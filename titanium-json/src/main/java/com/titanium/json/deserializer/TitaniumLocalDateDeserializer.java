package com.titanium.json.deserializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.titanium.util.time.Times;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TitaniumLocalDateDeserializer extends LocalDateDeserializer {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private boolean isJsonFormat;
    public TitaniumLocalDateDeserializer() {

    }

    public TitaniumLocalDateDeserializer(DateTimeFormatter dtf) {
        super(dtf);
    }

    public TitaniumLocalDateDeserializer(LocalDateDeserializer base, DateTimeFormatter dtf) {
        super(base, dtf);
    }

    public TitaniumLocalDateDeserializer(LocalDateDeserializer base, Boolean leniency) {
        super(base, leniency);
        isJsonFormat = leniency;
    }

    public TitaniumLocalDateDeserializer(LocalDateDeserializer base, JsonFormat.Shape shape) {
        super(base, shape);
    }



    @Override
    protected LocalDateDeserializer withDateFormat(DateTimeFormatter dtf) {
        return super.withDateFormat(dtf);
    }

    @Override
    protected LocalDateDeserializer withLeniency(Boolean leniency) {
        return super.withLeniency(leniency);
    }

    @Override
    protected LocalDateDeserializer withShape(JsonFormat.Shape shape) {
        return super.withShape(shape);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<?> _withFormatOverrides(DeserializationContext ctxt, BeanProperty property, JsonFormat.Value formatOverrides) {
        return super._withFormatOverrides(ctxt, property, formatOverrides);
    }


    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateTimeText = parser.getValueAsString().trim();
        if (Strings.isBlank(dateTimeText)) {
            return null;
        }
        try {
            // 使用@JsonFormat注解
            if (isJsonFormat) {
                return LocalDate.parse(dateTimeText, _formatter);
            }
            // 时间戳
            Long timestamp = parseLong(dateTimeText);
            if (timestamp != null) {
                return Times.toLocalDate(timestamp);
            }
            // 默认日期格式
            return LocalDate.parse(dateTimeText, _formatter);
        } catch (DateTimeException e) {
            return _handleDateTimeException(context, e, dateTimeText);
        }
    }

    private Long parseLong(String dateTimeText) {
        try {
            return Long.parseLong(dateTimeText);
        } catch (Exception e) {
            return null;
        }
    }
}
