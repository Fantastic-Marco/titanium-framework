package com.titanium.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.titanium.util.time.Times;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TitaniumLocalTimeSerializer extends LocalTimeSerializer {
    private static DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TitaniumLocalTimeSerializer() {
    }

    public TitaniumLocalTimeSerializer(DateTimeFormatter f) {
        super(f);
    }

    public TitaniumLocalTimeSerializer(LocalTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter f) {
        super(base, useTimestamp, useNanoseconds, f);
    }

    @Override
    public void serialize(LocalTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeString(value.format(LOCAL_TIME_FORMATTER));
    }
}
