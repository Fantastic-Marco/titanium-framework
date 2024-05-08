package com.titanium.json.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.titanium.json.serializer.TitaniumToStringSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TitaniumNumberModule extends SimpleModule {

    private static final long serialVersionUID = -8746439547680282437L;

    /**
     * 指定序列化规则
     */
    public TitaniumNumberModule() {
        super("TitaniumNumberModule");
        //======================= 数字类型序列化成string ===========================
        this.addSerializer(Byte.class, TitaniumToStringSerializer.instance);
        this.addSerializer(byte.class, TitaniumToStringSerializer.instance);
        this.addSerializer(Short.class, TitaniumToStringSerializer.instance);
        this.addSerializer(short.class, TitaniumToStringSerializer.instance);
        this.addSerializer(Float.class, TitaniumToStringSerializer.instance);
        this.addSerializer(float.class, TitaniumToStringSerializer.instance);
        this.addSerializer(Double.class, TitaniumToStringSerializer.instance);
        this.addSerializer(double.class, TitaniumToStringSerializer.instance);
        this.addSerializer(Integer.class, TitaniumToStringSerializer.instance);
        this.addSerializer(int.class, TitaniumToStringSerializer.instance);
        this.addSerializer(Long.class, TitaniumToStringSerializer.instance);
        this.addSerializer(long.class, TitaniumToStringSerializer.instance);
        this.addSerializer(BigInteger.class, TitaniumToStringSerializer.instance);
        this.addSerializer(BigDecimal.class, TitaniumToStringSerializer.instance);
    }

}
