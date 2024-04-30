package com.titanium.common.annotation;

import java.lang.annotation.*;

/**
 * 支持Number类型输出格式化特殊处理
 *
 * <pre>
 * 1、Number类型按原始类型输出（覆盖默认按string输出的规则）
 * {@code
 *    @JsonNumberFormat
 *    private int value;
 * }
 *
 * 2、对Number类型按自定义格式输出: 100.0001-> 100.00
 * {@code
 *     @JsonNumberFormat(pattern = "#.00")
 *     private BigDecimal formatBigDecimal = new BigDecimal("100.0001");
 * }
 *
 * </pre>
 *
 * @see java.text.NumberFormat
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JsonNumberFormat {

    /**
     * 输出格式
     * <p>
     * style 和 pattern二选一，优先取pattern
     */
    Style style() default Style.NUMBER;

    /**
     * 用于格式化字段的自定义模式
     * <p>
     * style 和 pattern二选一，优先取pattern
     */
    String pattern() default "";


    enum Style {

        /**
         * json默认输出
         */
        DEFAULT,

        /**
         * 按普通数字类型输出
         */
        NUMBER
    }

}