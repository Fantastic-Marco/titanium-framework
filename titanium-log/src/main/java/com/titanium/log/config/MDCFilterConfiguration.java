package com.titanium.log.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import jakarta.servlet.Filter;

/**
 * MDC过滤器配置类
 * 仅在存在Servlet API时注册MDC过滤器
 */
@Configuration
@ConditionalOnClass(Filter.class)
public class MDCFilterConfiguration {

    /**
     * 注册MDC过滤器
     * 
     * @return FilterRegistrationBean实例
     */
    @Bean
    public FilterRegistrationBean<MDCFilterWrapper> mdcFilter() {
        FilterRegistrationBean<MDCFilterWrapper> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MDCFilterWrapper());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}