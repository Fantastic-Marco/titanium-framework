package com.titanium.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface DistributionLock {

    /**
     * key 前缀
     * @return
     */
    String value() default "";

    /**
     * key 拼接的字段
     * 支持SpEL表达式
     */
    String[] spels() default {};

    /**
     * 锁等待时间
     * @return
     */
    int waitTime() default 10;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
