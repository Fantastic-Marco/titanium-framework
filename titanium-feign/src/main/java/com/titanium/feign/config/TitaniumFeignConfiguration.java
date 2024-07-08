package com.titanium.feign.config;

import com.titanium.feign.interceptor.TitaniumUserRequestInterceptor;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(TitaniumFeignProperties.class)
public class TitaniumFeignConfiguration {

    @Bean
    @ConditionalOnProperty(name = "titanium.feign.enable", havingValue = "true", matchIfMissing = false)
    public RequestInterceptor requestInterceptor() {
        log.info("added titanium user request interceptor");
        return new TitaniumUserRequestInterceptor();
    }
}
