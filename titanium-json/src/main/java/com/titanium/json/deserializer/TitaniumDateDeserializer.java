package com.titanium.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;

public class TitaniumDateDeserializer extends DateDeserializers.DateDeserializer{
    private static final SimpleDateFormat DEFAULT_FORMATTER = new SimpleDateFormat(NORM_DATETIME_PATTERN);
    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return super.deserialize(p, ctxt);
        } catch (Exception e) {
            // 兼容原framework
            try {
                return DEFAULT_FORMATTER.parse(p.getValueAsString());
            } catch (Exception ex) {
                // 抛出原始错误
                throw e;
            }
        }
    }
}
