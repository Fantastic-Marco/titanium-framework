

package com.titanium.json.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.titanium.util.time.Times;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.titanium.common.constant.DatePatternConstants.NORM_DATETIME_PATTERN;


/**
 * 修改输出标准的时间戳
 * Serializer for Java 8 temporal {@link LocalDateTimeSerializer}.
 *
 * @author zhongym
 */
public class TitaniumLocalDateTimeSerializer extends LocalDateTimeSerializer {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);

    public TitaniumLocalDateTimeSerializer() {
        this(null);
    }

    public TitaniumLocalDateTimeSerializer(DateTimeFormatter f) {
        super(f);
    }

    private TitaniumLocalDateTimeSerializer(TitaniumLocalDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter f) {
        super(base, useTimestamp, null, f);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            writeTimestamp(value, g);
        } else {
            writeFormatter(value, g);
        }
    }


    @Override
    public void serializeWithType(LocalDateTime value, JsonGenerator g, SerializerProvider provider,
                                  TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, serializationShape(provider)));
        if (typeIdDef.valueShape == JsonToken.VALUE_NUMBER_INT) {
            writeTimestamp(value, g);
        } else {
            writeFormatter(value, g);

        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }


    @Override
    protected JsonToken serializationShape(SerializerProvider provider) {
        return useTimestamp(provider) ? JsonToken.VALUE_NUMBER_INT : JsonToken.VALUE_STRING;
    }

    @Override
    protected TitaniumLocalDateTimeSerializer withFormat(Boolean useTimestamp,
                                                         DateTimeFormatter dtf,
                                                         JsonFormat.Shape shape) {
        return new TitaniumLocalDateTimeSerializer(this, useTimestamp, dtf);
    }


    protected void writeTimestamp(LocalDateTime value, JsonGenerator g) throws IOException {
        g.writeNumber(Times.toMillis(value));
    }


    protected void writeFormatter(LocalDateTime value, JsonGenerator g) throws IOException {
        DateTimeFormatter dtf = _formatter;
        if (dtf == null) {
            dtf = DEFAULT_FORMATTER;
        }
        g.writeString(value.format(dtf));
    }
}
