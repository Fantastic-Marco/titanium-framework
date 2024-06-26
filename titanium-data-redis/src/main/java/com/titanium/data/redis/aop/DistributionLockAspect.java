package com.titanium.data.redis.aop;

import com.titanium.common.annotation.DistributionLock;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class DistributionLockAspect {
    @Resource
    private RedissonClient redissonClient;

    @Pointcut(value = "@annotation(com.titanium.common.annotation.DistributionLock)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        DistributionLock distributionLock = method.getAnnotation(DistributionLock.class);
        String lockKey = getLockKey(distributionLock, pjp.getArgs());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            log.debug("尝试获取锁:{}", lockKey);
            boolean tryLock = lock.tryLock(distributionLock.waitTime(), distributionLock.timeUnit());
            if (tryLock) {
                log.debug("获取锁成功:{}", lockKey);
                return pjp.proceed();
            } else {
                log.debug("获取锁失败:{}", lockKey);
                throw new RuntimeException("获取锁失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("获取锁失败");
        } finally {
            try {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private String getLockKey(DistributionLock distributionLock, Object[] args) {
        String keyPrefix = distributionLock.value();
        String[] spels = distributionLock.spels();
        if (keyPrefix.isEmpty()) {
            throw new RuntimeException("锁的key不能为空");
        }
        StringBuilder sb = new StringBuilder(keyPrefix);
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        if (args.length > 0 && spels.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (i < spels.length) {
                    Object value = spelExpressionParser.parseExpression(spels[i]).getValue(args[i]);
                    sb.append(":").append(value);
                }
            }
        }
        return sb.toString();
    }
}
