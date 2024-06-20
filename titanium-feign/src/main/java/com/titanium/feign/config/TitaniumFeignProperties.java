package com.titanium.feign.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "titanium.feign")
public class TitaniumFeignProperties {
    /**
     * 启用feign
     */
    private boolean enable = true;
}
