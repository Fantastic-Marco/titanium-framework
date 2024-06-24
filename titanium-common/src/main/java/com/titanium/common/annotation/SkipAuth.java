package com.titanium.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于标记跳过认证
 */
@Target(value = {ElementType.METHOD})
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface SkipAuth {
}
