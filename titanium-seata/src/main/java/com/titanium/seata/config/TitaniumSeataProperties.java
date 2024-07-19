package com.titanium.seata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "titanium.seata")
public class TitaniumSeataProperties {

    /**
     * 资源持有者类型
     */
    private String holderType = "memory";
}
