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

    /**
     * 是否打印日志
     */
    private boolean log = false;

    /**
     * 打印Curl
     */
    private boolean logCurl = false;
}
