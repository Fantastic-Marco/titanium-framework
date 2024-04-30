package com.titanium.json.deserializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.titanium.util.time.Times;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static com.titanium.common.constant.DatePatternConstants.NORM_TIME_PATTERN;

public class TitaniumLocalTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalTime> {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(NORM_TIME_PATTERN);
    private final boolean isJsonFormat;

    public TitaniumLocalTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public TitaniumLocalTimeDeserializer(DateTimeFormatter formatter) {
        this(formatter, false);
    }

    public TitaniumLocalTimeDeserializer(DateTimeFormatter formatter, boolean isJsonFormat) {
        super(LocalTime.class, formatter);
        this.isJsonFormat = isJsonFormat;
    }

    /**
     * Since 2.10
     */
    protected TitaniumLocalTimeDeserializer(TitaniumLocalTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
        this.isJsonFormat = base.isJsonFormat;
    }

    @Override
    protected TitaniumLocalTimeDeserializer withDateFormat(DateTimeFormatter formatter) {
        return new TitaniumLocalTimeDeserializer(formatter, true);
    }

    @Override
    protected TitaniumLocalTimeDeserializer withLeniency(Boolean leniency) {
        return new TitaniumLocalTimeDeserializer(this, leniency);
    }

    @Override
    protected TitaniumLocalTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateTimeText = parser.getValueAsString().trim();
        if (Strings.isBlank(dateTimeText)) {
            return null;
        }
        try {
            // 使用@JsonFormat注解
            if (isJsonFormat) {
                return LocalTime.parse(dateTimeText, _formatter);
            }
            // 时间戳
            Long timestamp = parseLong(dateTimeText);
            if (timestamp != null) {
                return Times.toLocalTime(timestamp);
            }
            // 默认日期格式
            return LocalTime.parse(dateTimeText, _formatter);
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
