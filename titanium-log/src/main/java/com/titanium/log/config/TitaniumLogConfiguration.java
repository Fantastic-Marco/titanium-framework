package com.titanium.log.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import com.titanium.tracing.config.TracingConfiguration;

/**
 * 日志配置类，用于配置日志相关的功能
 */
@Configuration
public class TitaniumLogConfiguration {

    /**
     * 获取当前TraceId的工具方法
     * 优先使用 tracing 模块提供的 TraceIdExtractor
     * 
     * @return 当前TraceId，如果不存在则返回null
     */
    @Bean
    public TraceIdExtractor traceIdExtractor(ObjectProvider<TracingConfiguration.TraceIdExtractor> tracingTraceIdExtractor) {
        // 如果 tracing 模块存在且提供了 TraceIdExtractor，则使用它
        TracingConfiguration.TraceIdExtractor extractor = tracingTraceIdExtractor.getIfAvailable();
        if (extractor != null) {
            return new TraceIdExtractorAdapter(extractor);
        }
        // 否则使用默认实现，从MDC中获取TraceId
        return new DefaultTraceIdExtractor();
    }
    
    /**
     * 适配器类，用于适配 tracing 模块的 TraceIdExtractor
     */
    private static class TraceIdExtractorAdapter implements TraceIdExtractor {
        private final TracingConfiguration.TraceIdExtractor extractor;
        
        public TraceIdExtractorAdapter(TracingConfiguration.TraceIdExtractor extractor) {
            this.extractor = extractor;
        }
        
        @Override
        public String getCurrentTraceId() {
            return extractor.getCurrentTraceId();
        }
    }
    
    /**
     * 默认的TraceId提取器实现
     * 从MDC中获取TraceId（如果其他追踪系统直接放入MDC）
     */
    private static class DefaultTraceIdExtractor implements TraceIdExtractor {
        @Override
        public String getCurrentTraceId() {
            // 从MDC中获取traceId，如果存在的话
            return MDC.get("traceId");
        }
    }
    
    /**
     * TraceId提取器接口
     */
    public interface TraceIdExtractor {
        String getCurrentTraceId();
    }
}