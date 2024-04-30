package com.titanium.json.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.titanium.json.modifier.TitaniumBeanDeserializerModifier;
import com.titanium.json.modifier.TitaniumSerializerModifier;

public class TitaniumNullValueModule extends SimpleModule {

    private static final long serialVersionUID = 6836907286485519754L;

    public TitaniumNullValueModule() {
        super("TitaniumNullValueModule");
        // null 值处理
        this.setSerializerModifier(new TitaniumSerializerModifier());
        this.setDeserializerModifier(new TitaniumBeanDeserializerModifier());
    }
}
