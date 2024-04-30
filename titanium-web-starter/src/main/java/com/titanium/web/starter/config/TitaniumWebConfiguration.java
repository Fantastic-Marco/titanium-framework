package com.titanium.web.starter.config;

import com.titanium.web.starter.advice.ApiLogHandler;
import com.titanium.web.starter.advice.TitaniumResponseHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(TitaniumWebProperties.class)
public class TitaniumWebConfiguration implements WebMvcConfigurer {
    @Resource
    private TitaniumWebProperties titaniumWebProperties;
    @Resource
    private TitaniumResponseHandler titaniumResponseHandler;

    @Bean
    public ApiLogHandler apiLogHandler() {
        return new ApiLogHandler(titaniumWebProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Titanium-Web-Starter add log handler");
        registry.addInterceptor(apiLogHandler());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> newConverters = new ArrayList<>();
        //设置为最高优先级
        newConverters.add(0,titaniumResponseHandler);
        newConverters.addAll(converters);
        converters.clear();
        converters.addAll(newConverters);
    }
}