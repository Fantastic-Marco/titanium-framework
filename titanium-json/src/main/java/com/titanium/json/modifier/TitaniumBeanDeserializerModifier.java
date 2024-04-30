package com.titanium.json.modifier;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 如果前端传了key没有传值（例如："orderType":null ），跳过反序列化流程, 使用实体类自定义的默认值.
 *
 */
public class TitaniumBeanDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
        List<SettableBeanProperty> properties = new ArrayList<>();

        Iterator<SettableBeanProperty> iterator = builder.getProperties();
        while (iterator.hasNext()) {
            SettableBeanProperty property = iterator.next();
            properties.add(property.withNullProvider(NullsConstantProvider.skipper()));
        }

        properties.forEach(property ->
                builder.addOrReplaceProperty(property, true)
        );
        return builder;
    }
}
