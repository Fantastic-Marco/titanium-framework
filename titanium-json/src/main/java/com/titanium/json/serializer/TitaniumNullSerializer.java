package com.titanium.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;

import java.io.IOException;


/**
 * null 值序列化器
 * <p>
 * 1. 字符串|数字 ""
 * 2. 布尔 false
 * 3. 集合 []
 * 4. 其它空对象 null
 *
 */
public class TitaniumNullSerializer {

    public static final JsonSerializer<Object> STRING_DEFAULT_SERIALIZER =
            new JsonSerializer<>() {
                @Override
                public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString("");
                }
            };

    public static final JsonSerializer<Object> BOOL_DEFAULT_SERIALIZER =
            new JsonSerializer<>() {
                @Override
                public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeBoolean(false);
                }
            };

    public static final JsonSerializer<Object> COLLECTION_DEFAULT_SERIALIZER =
            new JsonSerializer<>() {
                @Override
                public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeStartArray();
                    gen.writeEndArray();
                }
            };

    public static final JsonSerializer<Object> OBJECT_DEFAULT_SERIALIZER =
            NullSerializer.instance;

}