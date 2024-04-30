package com.titanium.json.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;


public class TitaniumLocalDateSerializer extends LocalDateSerializer {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(NORM_DATE_PATTERN);

    public TitaniumLocalDateSerializer() {
    }

    public TitaniumLocalDateSerializer(DateTimeFormatter formatter) {
        super(formatter);
    }

    public TitaniumLocalDateSerializer(LocalDateSerializer base, Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        super(base, useTimestamp, dtf, shape);
    }

    @Override
    public void serialize(LocalDate date, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            //转换成时间戳
            writeTimestamp(date, g);
        } else {
            //转换成字符串
            writeFormatter(date, g);
        }
    }

    @Override
    public void serializeWithType(LocalDate value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
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
    protected LocalDateSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return super.withFormat(useTimestamp, dtf, shape);
    }

    protected void writeTimestamp(LocalDate value, JsonGenerator g) throws IOException {
        //转换成时间戳
        g.writeNumber(value.toEpochDay());
    }


    protected void writeFormatter(LocalDate value, JsonGenerator g) throws IOException {
        DateTimeFormatter dtf = _formatter;
        if (dtf == null) {
            dtf = DEFAULT_FORMATTER;
        }
        g.writeString(value.format(dtf));
    }
}
