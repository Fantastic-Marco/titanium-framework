package com.titanium.user.config;

import com.titanium.user.filter.TitaniumUserRequestFilter;
import com.titanium.user.interceptor.TitaniumUserRequestInterceptor;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(TitaniumUserProperties.class)
public class TitaniumUserConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "titanium.user", name = "enabled")
    @ConditionalOnClass(FeignAutoConfiguration.class)
    public RequestInterceptor requestInterceptor() {
        return new TitaniumUserRequestInterceptor();
    }
}
