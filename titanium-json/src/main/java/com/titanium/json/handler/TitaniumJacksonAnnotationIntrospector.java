package com.titanium.json.handler;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.Annotated;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class TitaniumJacksonAnnotationIntrospector extends AnnotationIntrospector {

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    /**
     * 通过注解返回自定义注解序列化器
     * @param a 候选注解
     * @return 返回序列化器
     */
    @Override
    public Object findSerializer(Annotated a) {
        return Optional.ofNullable(TitaniumJacksonAnnotationHandlerManager.findSerializer(a))
                .orElse(super.findSerializer(a));
    }

    /**
     * 通过注解返回自定义注解反序列化器
     * @param a 候选注解
     * @return 返回反序列化器
     */
    @Override
    public Object findDeserializer(Annotated a) {
        return Optional.ofNullable(TitaniumJacksonAnnotationHandlerManager.findDeserializer(a))
                .orElse(super.findDeserializer(a));
    }
}
