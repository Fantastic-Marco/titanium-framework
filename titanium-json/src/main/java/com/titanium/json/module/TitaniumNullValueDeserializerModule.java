package com.titanium.json.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.titanium.json.modifier.TitaniumBeanDeserializerModifier;

public class TitaniumNullValueDeserializerModule extends SimpleModule {
    private static final long serialVersionUID = 5152667684594518832L;

    public TitaniumNullValueDeserializerModule() {
        super("TitaniumNullValueDeserializerModule");
        // null 值处理
        this.setDeserializerModifier(new TitaniumBeanDeserializerModifier());
    }
}
