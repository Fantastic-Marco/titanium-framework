package com.titanium.web.starter.config;

import com.titanium.json.config.TitaniumJsonConfiguration;
import com.titanium.web.starter.advice.ApiLogHandler;
import com.titanium.web.starter.advice.TitaniumMessageConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(TitaniumWebProperties.class)
@AutoConfigureAfter(TitaniumJsonConfiguration.class)
@ConditionalOnWebApplication
@Import(TitaniumMessageConverter.class)
public class TitaniumWebConfiguration implements WebMvcConfigurer {
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private TitaniumWebProperties titaniumWebProperties;
    @Resource
    private TitaniumMessageConverter titaniumMessageConverter;

    @Bean
    @ConditionalOnProperty(name = "titanium.web.api-logging", havingValue = "true", matchIfMissing = false)
    public ApiLogHandler apiLogHandler() {
        log.info("Titanium-Web-Starter add log handler");
        return new ApiLogHandler(titaniumWebProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //认证拦截器
        if (titaniumWebProperties.getWebInterceptor().isEnable()) {
            registry.addInterceptor(createInterceptor(titaniumWebProperties.getWebInterceptor().getInterceptor()))
                    .addPathPatterns(titaniumWebProperties.getWebInterceptor().getIncludePatterns())
                    .excludePathPatterns(titaniumWebProperties.getWebInterceptor().getExcludePatterns())
                    .order(titaniumWebProperties.getWebInterceptor().getOrder());
            log.info("Titanium-Web-Starter add web interceptor");
        }
        //租户拦截器
        if (titaniumWebProperties.getTenantInterceptor().isEnable()) {
            registry.addInterceptor(createInterceptor(titaniumWebProperties.getTenantInterceptor().getInterceptor()))
                    .addPathPatterns(titaniumWebProperties.getTenantInterceptor().getIncludePatterns())
                    .excludePathPatterns(titaniumWebProperties.getTenantInterceptor().getExcludePatterns())
                    .order(titaniumWebProperties.getTenantInterceptor().getOrder());
            log.info("Titanium-Web-Starter add tenant interceptor");
        }
        //额外拦截器
        for (InterceptorProperties interceptor : titaniumWebProperties.getAdditionalInterceptors()) {
            if (interceptor.isEnable()) {
                registry.addInterceptor(createInterceptor(interceptor.getInterceptor()))
                        .addPathPatterns(interceptor.getIncludePatterns())
                        .excludePathPatterns(interceptor.getExcludePatterns())
                        .order(interceptor.getOrder());
                log.info("Titanium-Web-Starter add {}", interceptor.getInterceptor().getName());
            }
        }
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> newConverters = new ArrayList<>();
        //设置为最高优先级
        newConverters.add(0, titaniumMessageConverter);
        newConverters.addAll(converters);
        converters.clear();
        converters.addAll(newConverters);
    }

    private HandlerInterceptor createInterceptor(Class<? extends HandlerInterceptor> interceptor) {
        return applicationContext.getAutowireCapableBeanFactory()
                .createBean(interceptor);
    }
}
