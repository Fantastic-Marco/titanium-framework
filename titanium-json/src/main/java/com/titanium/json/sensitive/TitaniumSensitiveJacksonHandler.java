package com.titanium.json.sensitive;

import com.titanium.common.annotation.Sensitive;
import com.titanium.json.handler.TitaniumJsonAnnotationHandler;

import java.lang.annotation.Annotation;

public class TitaniumSensitiveJacksonHandler implements TitaniumJsonAnnotationHandler {
    /**
     * 返回注解类型
     * @return
     */
    @Override
    public Class<? extends Annotation> annotationClass() {
        return Sensitive.class;
    }

    /**
     * 返回序列化器
     * @return JsonSerializer.class 或者 JsonSerializer实例
     * @return
     */
    @Override
    public Object serializer() {
        return null;
    }
}
