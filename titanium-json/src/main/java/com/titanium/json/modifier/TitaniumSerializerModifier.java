package com.titanium.json.modifier;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.titanium.json.serializer.TitaniumNullSerializer;
import com.titanium.util.Types;

import java.util.Collection;
import java.util.List;


/**
 * 修改属性 null值序列化器
 * <p>
 * 作用于所有序列化过程, 提供空值序列化器.
 * <p>
 * 1. 数字｜字符串 ""
 * 2. 布尔 false
 * 3. 集合 []
 * 4. 其它空对象 null
 *
 */

public class TitaniumSerializerModifier extends BeanSerializerModifier {


    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        beanProperties.forEach(property -> {
            Class<?> typeClass = property.getType().getRawClass();

            if (Types.isNumeric(typeClass) ||
                    Types.isInstanceOf(typeClass, String.class)) {
                property.assignNullSerializer(TitaniumNullSerializer.STRING_DEFAULT_SERIALIZER);
                return;
            }

            if (Types.isBoolean(typeClass)) {
                property.assignNullSerializer(TitaniumNullSerializer.BOOL_DEFAULT_SERIALIZER);
                return;
            }

            if (Types.isInstanceOf(typeClass, Collection.class)) {
                property.assignNullSerializer(TitaniumNullSerializer.COLLECTION_DEFAULT_SERIALIZER);
                return;
            }

            property.assignNullSerializer(TitaniumNullSerializer.OBJECT_DEFAULT_SERIALIZER);
        });
        return beanProperties;
    }
}
