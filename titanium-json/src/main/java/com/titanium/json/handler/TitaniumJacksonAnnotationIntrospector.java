package com.titanium.json.handler;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TitaniumJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {
    private final List<TitaniumJsonAnnotationHandler> handlers;

    /**
     * 通过注解返回自定义注解序列化器
     * @param a 候选注解
     * @return  返回序列化器
     */
    @Override
    public Object findSerializer(Annotated a) {
        for (TitaniumJsonAnnotationHandler handler : handlers) {
            if (a.hasAnnotation(handler.annotationClass())) {
                Object serializer = handler.serializer();
                if (ObjectUtil.isNotNull(serializer)){
                    return serializer;
                }
            }
        }
        return super.findSerializer(a);
    }

    /**
     * 通过注解返回自定义注解反序列化器
     * @param a 候选注解
     * @return 返回反序列化器
     */
    @Override
    public Object findDeserializer(Annotated a) {
        for (TitaniumJsonAnnotationHandler handler : handlers) {
            if (a.hasAnnotation(handler.annotationClass())) {
                Object deserializer = handler.deserializer();
                if (ObjectUtil.isNotNull(deserializer)){
                    return deserializer;
                }
            }
        }
        return super.findDeserializer(a);
    }
}
