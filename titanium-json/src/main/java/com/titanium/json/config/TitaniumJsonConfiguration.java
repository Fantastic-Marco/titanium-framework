package com.titanium.json.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.titanium.json.handler.TitaniumJacksonAnnotationIntrospector;
import com.titanium.json.handler.TitaniumJsonAnnotationHandler;
import com.titanium.json.module.*;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Configuration
@EnableConfigurationProperties(TitaniumJsonProperties.class)
public class TitaniumJsonConfiguration {

    /**
     * 相对于直接生成一个ObjectMapper来说，以这种方式来追加module会比较节省性能
     * 并且直接声明ObjectMapper 会违反 “Don't Repeat Yourself”（DRY）原则
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer(TitaniumJsonProperties properties, List<TitaniumJsonAnnotationHandler> handlers) {
        return builder -> {
            builder.locale(Locale.CHINA);
            builder.timeZone(TimeZone.getTimeZone(properties.getTimezone()));
            // 输出时间戳
            builder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // 禁用时间输出纳秒
            builder.featuresToDisable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
            builder.featuresToDisable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
            // 空字符串处理
            builder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            // 注解拦截器
            builder.annotationIntrospector(fn -> new TitaniumJacksonAnnotationIntrospector(handlers));
            // BoosterModule
            builder.modulesToInstall(getModules(properties));
        };
    }

    /**
     * 获取模块
     * 后续如果有拓展别的模块，需要在这里注册
     * @param properties
     * @return
     */
    private Module[] getModules(TitaniumJsonProperties properties) {
        final var modules = new ArrayList<Module>();
        modules.add(new TitaniumDateTimeModule());
        if (properties.getNumberToString()) {
            modules.add(new TitaniumLongModule());
        } else {
            modules.add(new TitaniumNumberModule());
            modules.add(new TitaniumNullValueSerializerModule());
        }
        modules.add(new TitaniumNullValueDeserializerModule());
        return modules.toArray(Module[]::new);
    }
}
