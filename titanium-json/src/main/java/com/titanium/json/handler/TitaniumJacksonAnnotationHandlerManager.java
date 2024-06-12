package com.titanium.json.handler;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.titanium.json.sensitive.TitaniumSensitiveJacksonHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@Slf4j
public class TitaniumJacksonAnnotationHandlerManager {
    private static List<TitaniumJsonAnnotationHandler> handlers = new ArrayList<>();

    static {
        // spi加载自定义注解处理器
        ServiceLoader<TitaniumJsonAnnotationHandler> loader = ServiceLoader.load(TitaniumJsonAnnotationHandler.class);
        for (TitaniumJsonAnnotationHandler handler : loader) {
            log.info("TitaniumJsonAnnotationHandler ——> " + handler.getClass().getSimpleName());
            handlers.add(handler);
        }
        handlers.add(new TitaniumSensitiveJacksonHandler());
    }

    /**
     * 通过注解返回自定义注解序列化器
     * @param a 候选注解
     * @return 返回序列化器
     */
    public static Object findSerializer(Annotated a) {
        for (TitaniumJsonAnnotationHandler handler : handlers) {
            if (a.hasAnnotation(handler.annotationClass())) {
                Object serializer = handler.serializer();
                if (ObjectUtil.isNotNull(serializer)) {
                    return serializer;
                }
            }
        }
        return null;
    }

    /**
     * 通过注解返回自定义注解反序列化器
     * @param a 候选注解
     * @return 返回反序列化器
     */
    public static Object findDeserializer(Annotated a) {
        for (TitaniumJsonAnnotationHandler handler : handlers) {
            if (a.hasAnnotation(handler.annotationClass())) {
                Object deserializer = handler.deserializer();
                if (ObjectUtil.isNotNull(deserializer)) {
                    return deserializer;
                }
            }
        }
        return null;
    }
}
