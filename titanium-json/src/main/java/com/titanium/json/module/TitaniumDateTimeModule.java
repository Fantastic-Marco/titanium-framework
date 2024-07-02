package com.titanium.json.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.titanium.json.deserializer.TitaniumDateDeserializer;
import com.titanium.json.deserializer.TitaniumLocalDateDeserializer;
import com.titanium.json.deserializer.TitaniumLocalDateTimeDeserializer;
import com.titanium.json.deserializer.TitaniumLocalTimeDeserializer;
import com.titanium.json.serializer.TitaniumLocalDateSerializer;
import com.titanium.json.serializer.TitaniumLocalDateTimeSerializer;
import com.titanium.json.serializer.TitaniumLocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class TitaniumDateTimeModule extends SimpleModule {

    /**
     * Constructors that should only be used for non-reusable
     * convenience modules used by app code: "real" modules should
     * use actual name and version number information.
     */
    public TitaniumDateTimeModule() {
        super("TitaniumDateTimeModule");
        // ======================= 时间规则 ===============================
        this.addSerializer(Date.class, DateSerializer.instance);
        this.addDeserializer(Date.class, new TitaniumDateDeserializer());

        this.addSerializer(LocalDate.class, new TitaniumLocalDateSerializer());
        this.addDeserializer(LocalDate.class, new TitaniumLocalDateDeserializer());

        this.addSerializer(LocalTime.class, new TitaniumLocalTimeSerializer());
        this.addDeserializer(LocalTime.class, new TitaniumLocalTimeDeserializer());

        this.addSerializer(LocalDateTime.class, new TitaniumLocalDateTimeSerializer(TitaniumLocalDateTimeSerializer.DEFAULT_FORMATTER));
        this.addDeserializer(LocalDateTime.class, new TitaniumLocalDateTimeDeserializer());
    }
}
