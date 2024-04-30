package com.titanium.json.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.titanium.json.modifier.TitaniumSerializerModifier;

public class TitaniumNullValueSerializerModule extends SimpleModule {

    private static final long serialVersionUID = -2735440970682465214L;

    public TitaniumNullValueSerializerModule() {
        super("TitaniumNullValueSerializerModule");
        // null 值处理
        this.setSerializerModifier(new TitaniumSerializerModifier());
    }
}
