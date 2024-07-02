package com.titanium.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.titanium.json.handler.TitaniumJacksonAnnotationIntrospector;
import com.titanium.json.module.TitaniumDateTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.titanium.common.constant.DatePatternConstants.ASIA_SHANGHAI;

@Slf4j
@UtilityClass
public class Json {
    private static ObjectMapper objectMapper;

    public static ObjectMapper getSource() {
        return objectMapper;
    }

    /**
     * 序列化对象
     *
     * @param value 值
     * @return Json 字符串
     */
    public static String serialize(Object value) {
        // 序列化
        try {
            return getSource().writeValueAsString(value);

        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 序列化对象并美化 Json 字符串
     *
     * @param value 值
     * @return Json 字符串
     */
    public static String serializeWithPrettyPrint(Object value) {
        // 序列化并美化 Json 格式
        try {
            return getSource().writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Json 反序列化为对象
     *
     * @param string      Json 字符串
     * @param classObject 反序列化的类型
     * @param <T>         类型
     * @return 反序列化后的对象
     */
    public static <T> T deserialize(String string, Class<T> classObject) {
        // 反序列化
        try {
            return getSource().readValue(string, classObject);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 行为与 {@link #deserialize(String, Class)} 一致.
     * 反序列化类型使用 TypeReference 定义, 以便处理泛型类型.
     *
     * @return 反序列化后的对象
     */
    public static <T> T deserialize(String string, TypeReference<T> reference) {
        // 反序列化
        try {
            return getSource().readValue(string, reference);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 反序列化列表
     *
     * @return 反序列化后的对象
     */
    public static <T> List<T> deserializeList(String json, Class<T> clazz) {
        // 反序列化
        try {
            return getSource().readerForListOf(clazz).readValue(json);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }


    static {
        objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .setLocale(Locale.CHINA)
                .setTimeZone(TimeZone.getTimeZone(ASIA_SHANGHAI))
                // 输出时间戳
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                // 禁用时间输出纳秒
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                //接受空字符串作为空对象
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                // 注解拦截器
                .setAnnotationIntrospector(new TitaniumJacksonAnnotationIntrospector())
                // 加载依赖模块
                .findAndRegisterModules()
                // BoosterModule
                .registerModule(new TitaniumDateTimeModule());
    }


}
