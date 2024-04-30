package com.titanium.json.serializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.titanium.common.annotation.JsonNumberFormat;

/**
 * 默认将所有Number类型输出为string格式
 * <p>
 *
 * <pre>
 * 1、可以通过下面注解将Number按原始类型输出（覆盖booster默认行为）
 *
 * {@code
 *     @JsonFormat(shape = JsonFormat.Shape.NUMBER)
 *     private Long total;
 * }
 * 或者
 * {@code
 *     @JsonNumberFormat
 *     private Long total;
 * }
 *
 * 2、对@JsonNumberFormat注解自定义数字格式
 * {@code
 *     @JsonNumberFormat(pattern = "#.00")
 *     private BigDecimal formatBigDecimal = new BigDecimal("100.0001");
 * }
 *
 * </pre>
 *
 */
public class TitaniumToStringSerializer extends ToStringSerializer implements ContextualSerializer {
    public final static TitaniumToStringSerializer instance = new TitaniumToStringSerializer();

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
                                              BeanProperty property) {
        // 支持@JsonFormat 输出Number类型
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format != null) {
            switch (format.getShape()) {
                case NUMBER:
                case NUMBER_FLOAT:
                case NUMBER_INT:
                    return NumberSerializer.instance;
                default:
            }
        }
        // 支持@JsonNumberFormat输出Number类型、自定义格式化Number类型
        if (property != null) {
            JsonNumberFormat annotation = property.getAnnotation(JsonNumberFormat.class);
            if (annotation != null) {
                // 优先使用自定义格式
                String pattern = annotation.pattern();
                if (StrUtil.isNotBlank(pattern)) {
                    return new TitaniumNumberFormatSerializer(pattern);
                }
                // 输出Number类型
                if (annotation.style() == JsonNumberFormat.Style.NUMBER) {
                    return NumberSerializer.instance;
                }
            }
        }
        // 输出String类型
        return this;
    }
}
