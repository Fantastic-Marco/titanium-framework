package com.titanium.json.sensitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.titanium.common.annotation.Sensitive;
import com.titanium.common.enums.SensitiveType;
import com.titanium.util.DesensitizedUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class TitaniumSensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private SensitiveType type;

    private Integer prefixNoMaskLen;

    private Integer suffixNoMaskLen;

    private String maskStr;

    @Override
    public void serialize(final String origin, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        switch (type) {
            case CHINESE_NAME:
                jsonGenerator.writeString(DesensitizedUtils.chineseName(origin));
                break;
            case ID_CARD:
                jsonGenerator.writeString(DesensitizedUtils.idCardNum(origin));
                break;
            case FIXED_PHONE:
                jsonGenerator.writeString(DesensitizedUtils.fixedPhone(origin));
                break;
            case MOBILE_PHONE:
                jsonGenerator.writeString(DesensitizedUtils.mobilePhone(origin));
                break;
            case ADDRESS:
                jsonGenerator.writeString(DesensitizedUtils.address(origin));
                break;
            case EMAIL:
                jsonGenerator.writeString(DesensitizedUtils.email(origin));
                break;
            case BANK_CARD:
                jsonGenerator.writeString(DesensitizedUtils.bankCard(origin));
                break;
            case PASSWORD:
                jsonGenerator.writeString(DesensitizedUtils.password(origin));
                break;
            case KEY:
                jsonGenerator.writeString(DesensitizedUtils.key(origin));
                break;
            case CUSTOMER:
                jsonGenerator
                        .writeString(DesensitizedUtils.desValue(origin, prefixNoMaskLen, suffixNoMaskLen, maskStr));
                break;
            default:
                throw new IllegalArgumentException("Unknow sensitive type enum " + type);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializerProvider,
                                              final BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                Sensitive sensitive = beanProperty.getAnnotation(Sensitive.class);
                if (sensitive != null) {
                    return new TitaniumSensitiveSerializer(sensitive.type(), sensitive.prefixNoMaskLen(),
                            sensitive.suffixNoMaskLen(), sensitive.maskStr());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}
