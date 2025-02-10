package com.titanium.util.concurrent;

import com.titanium.common.tenant.TenantContextHolder;
import com.titanium.common.user.UserContextHolder;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 线程工具
 * 提供线程执行方法
 * 在线程执行之后，清除用户上下文和租户上下文
 */
@UtilityClass
public class ThreadUtil {

    public static CompletableFuture<Void> exec(Executor executor, Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
//                log.error(e.getMessage(), e);
                throw  e;
            } finally {
                UserContextHolder.clear();
                TenantContextHolder.clear();
            }
        }, executor);

    }

    public static <T> CompletableFuture<T> exec(ExecutorService executor, Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
//                log.error(e.getMessage(), e);
                throw  e;
//                return null;
            } finally {
                UserContextHolder.clear();
                TenantContextHolder.clear();
            }
        }, executor);
    }



    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        //数组流 1，2，3 执行线程，并且等待线程执行完成，join
        List<CompletableFuture<Integer>> futures = Stream.of(1, 2, 3)
                .map(i -> exec(executor,
                        () -> testCallError(i)
                )).collect(Collectors.toList());
        List<Integer> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
//        futures.stream().allMatch(f -> f.isDone() || f.isCompletedExceptionally());
        for (int i = 0; i < result.size(); i++) {
            Integer resultItem = result.get(i);
            System.out.println("result: " + resultItem);
        }
        System.out.println("end");
        executor.shutdown();
    }

    private static void testRun(Integer i) {
        try {
            TimeUnit.SECONDS.sleep(new Random().nextInt(5));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("hello " + i);
    }

    private static void testRunError(Integer i) {
        if (i % 2 == 0) throw new RuntimeException("error" + i);
        try {
            TimeUnit.SECONDS.sleep(new Random().nextInt(5));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("hello " + i);
    }

    private static Integer testCallError(Integer i) {
        int random = new Random().nextInt(5);
        if (i % 2 == 0) throw new RuntimeException("error" + i);
        try {
            TimeUnit.SECONDS.sleep(random);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("hello " + i + " sleep time " + random + " s");
        return i;
    }

}
