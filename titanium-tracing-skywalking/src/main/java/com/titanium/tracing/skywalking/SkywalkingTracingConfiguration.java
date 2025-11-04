package com.titanium.tracing.skywalking;

import com.titanium.tracing.extractor.TraceIdExtractor;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Skywalking分布式追踪配置类
 * 提供与Skywalking服务器的集成
 */
@Configuration
public class SkywalkingTracingConfiguration {
    
    /**
     * 配置TraceId在MDC中的自动注入和清除
     */
    @PostConstruct
    public void setupMDCIntegration() {
        // 注册MDC处理器以确保traceId被正确放入MDC中
    }
    
    /**
     * 提供MDCSkywalkingTraceIdHandler Bean
     * 
     * @return MDCSkywalkingTraceIdHandler实例
     */
    @Bean
    public MDCSkywalkingTraceIdHandler mdcSkywalkingTraceIdHandler() {
        return new MDCSkywalkingTraceIdHandler();
    }
    
    /**
     * 获取当前TraceId的工具方法
     * 
     * @return 当前TraceId，如果不存在则返回null
     */
    @Bean
    public com.titanium.tracing.extractor.TraceIdExtractor skywalkingTraceIdExtractor() {
        return new TraceIdExtractor();
    }
    
    public static class TraceIdExtractor implements com.titanium.tracing.extractor.TraceIdExtractor {
        @Override
        public String getCurrentTraceId() {
            try {
                // 从Skywalking获取traceId
                return TraceContext.traceId();
            } catch (Exception e) {
                // 如果无法获取traceId，则返回null
                return null;
            }
        }
    }
}