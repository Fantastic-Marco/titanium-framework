package com.titanium.tracing.skywalking;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import jakarta.servlet.Filter;

/**
 * Skywalking MDC配置类
 * 仅在存在Servlet API时注册Skywalking MDC过滤器
 */
@Configuration
@ConditionalOnClass(Filter.class)
public class SkywalkingMDCConfiguration {

    /**
     * 注册Skywalking MDC过滤器
     * 
     * @return FilterRegistrationBean实例
     */
    @Bean
    public FilterRegistrationBean<SkywalkingMDCFilter> skywalkingMDCFilter() {
        FilterRegistrationBean<SkywalkingMDCFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SkywalkingMDCFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 略低于默认MDC过滤器
        return registrationBean;
    }
}