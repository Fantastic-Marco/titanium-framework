package com.titanium.web.starter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 标记当前的接口为视图接口
 */
@Target(value = { ElementType.METHOD })
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface ViewPage {
    String value() default "";
}
