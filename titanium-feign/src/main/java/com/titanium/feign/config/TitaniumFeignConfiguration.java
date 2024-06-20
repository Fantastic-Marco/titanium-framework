package com.titanium.feign.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(TitaniumFeignProperties.class)
public class TitaniumFeignConfiguration {
}
