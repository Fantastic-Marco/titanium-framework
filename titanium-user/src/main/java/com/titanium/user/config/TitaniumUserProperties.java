package com.titanium.user.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "titanium.user")
public class TitaniumUserProperties {

    /**
     * 是否开启认证
     */
    private boolean enabled = true;
}
