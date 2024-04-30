package com.titanium.json.handler;

import java.lang.annotation.Annotation;

/**
 * 自定义 Json 注解处理
 * @see com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
 */
public interface TitaniumJsonAnnotationHandler {
    /**
     * 返回注解类型
     *
     * @return
     */
    Class<? extends Annotation> annotationClass();

    /**
     * 返回序列化器
     * @return JsonSerializer.class 或者 JsonSerializer实例
     * @return
     */
    default Object serializer(){return null;}


    /**
     * 返回反序列化器
     * @return JsonDeserializer.class 或者 JsonDeserializer实例
     * @return
     */
    default Object deserializer(){return null;}
}
