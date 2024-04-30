package com.titanium.json.deserializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.titanium.util.time.Times;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.titanium.common.constant.DatePatternConstants.NORM_DATETIME_PATTERN;


/**
 * @author zhongym
 */
public class TitaniumLocalDateTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalDateTime> {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);
    private final boolean isJsonFormat;

    public TitaniumLocalDateTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public TitaniumLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        this(formatter, false);
    }

    public TitaniumLocalDateTimeDeserializer(DateTimeFormatter formatter, boolean isJsonFormat) {
        super(LocalDateTime.class, formatter);
        this.isJsonFormat = isJsonFormat;
    }

    /**
     * Since 2.10
     */
    protected TitaniumLocalDateTimeDeserializer(TitaniumLocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
        this.isJsonFormat = base.isJsonFormat;
    }

    @Override
    protected TitaniumLocalDateTimeDeserializer withDateFormat(DateTimeFormatter formatter) {
        return new TitaniumLocalDateTimeDeserializer(formatter, true);
    }

    @Override
    protected TitaniumLocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new TitaniumLocalDateTimeDeserializer(this, leniency);
    }

    @Override
    protected TitaniumLocalDateTimeDeserializer withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateTimeText = parser.getValueAsString().trim();
        if (StrUtil.isBlank(dateTimeText)) {
            return null;
        }
        try {
            // 使用@JsonFormat注解
            if (isJsonFormat) {
                return LocalDateTime.parse(dateTimeText, _formatter);
            }
            // 时间戳
            Long timestamp = parseLong(dateTimeText);
            if (timestamp != null) {
                return Times.toLocalDateTime(timestamp);
            }
            // 默认日期格式
            return LocalDateTime.parse(dateTimeText, _formatter);
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
