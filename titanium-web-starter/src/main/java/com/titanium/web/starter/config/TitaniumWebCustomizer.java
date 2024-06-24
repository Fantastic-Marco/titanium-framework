package com.titanium.web.starter.config;

public interface TitaniumWebCustomizer {

    /**
     * 覆盖web配置
     * @param properties
     */
    void customize(TitaniumWebProperties properties);
}
