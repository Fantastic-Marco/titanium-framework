package com.titanium.web.starter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TitaniumWebProperties.class)
public class TitaniumWebConfiguration {
}
