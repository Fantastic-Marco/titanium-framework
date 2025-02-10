package com.titanium.seata.config;

import com.titanium.seata.constants.TitaniumSeataConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = TitaniumSeataConstants.TITANIUM_SEATA_PREFIX)
public class TitaniumSeataProperties {

    /**
     * 资源持有者类型
     */
    private String holderType = "memory";
}
