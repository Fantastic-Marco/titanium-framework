package com.titanium.json.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "titanium.json")
public class TitaniumJsonProperties {

    /**
     * 时区
     */
    private String timezone = "Asia/Shanghai";

    /**
     * 数字转字符串
     */
    private Boolean numberToString = true;
}
