package com.titanium.json.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.titanium.json.serializer.TitaniumToStringSerializer;

public class TitaniumLongModule extends SimpleModule {
    private static final long serialVersionUID = 8480369023758949538L;

    /**
     * Constructors that should only be used for non-reusable
     * convenience modules used by app code: "real" modules should
     * use actual name and version number information.
     */
    public TitaniumLongModule() {
        super("TitaniumLongModule");
        //======================= Long类型序列化成string ===========================
        this.addSerializer(Long.class, TitaniumToStringSerializer.instance);
        this.addSerializer(long.class, TitaniumToStringSerializer.instance);
    }
}
