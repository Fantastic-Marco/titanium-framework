package com.titanium.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.DecimalFormat;

public class TitaniumNumberFormatSerializer extends StdSerializer<Number> {
    private final DecimalFormat format;

    public TitaniumNumberFormatSerializer(String pattern) {
        super(Number.class);
        this.format = new DecimalFormat(pattern);
    }

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(format.format(value));
    }

}
