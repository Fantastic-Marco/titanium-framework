package com.titanium.tracing.zipkin;

import brave.handler.SpanHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import brave.Tracing;
import brave.TracingCustomizer;

/**
 * Brave定制化配置类
 * 确保MDCSpanHandler被正确添加到Brave的追踪系统中
 */
@Configuration
@ConditionalOnClass(TracingCustomizer.class)
public class BraveCustomizerConfiguration {
    
    /**
     * 创建TracingCustomizer Bean，用于添加MDCSpanHandler到Brave追踪系统中
     * 
     * @return TracingCustomizer实例
     */
    @Bean
    TracingCustomizer tracingCustomizer() {
        return new TracingCustomizer() {
            @Override
            public void customize(Tracing.Builder builder) {
                // 直接创建一个新的MDCSpanHandler实例，避免循环依赖
                builder.addSpanHandler(new MDCSpanHandler());
            }
        };
    }
}