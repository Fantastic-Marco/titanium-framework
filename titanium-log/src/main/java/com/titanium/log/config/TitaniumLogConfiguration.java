package com.titanium.log.config;

import com.titanium.tracing.config.SpiTraceIdExtractorConfiguration;
import com.titanium.tracing.config.TracingConfiguration;
import com.titanium.tracing.extractor.TraceIdExtractor;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志配置类，用于配置日志相关的功能
 */
@Configuration
@AutoConfigureAfter(SpiTraceIdExtractorConfiguration.class) // 确保在TracingConfiguration之后加载
public class TitaniumLogConfiguration {

    /**
     * 获取当前TraceId的工具方法
     * 优先使用 tracing 模块提供的 TraceIdExtractor
     * 
     * @return 当前TraceId，如果不存在则返回null
     */
    @Bean
    @ConditionalOnMissingBean(TraceIdExtractor.class)
    public TraceIdExtractor traceIdExtractor(ObjectProvider<TraceIdExtractor> tracingTraceIdExtractor) {
        // 如果 tracing 模块存在且提供了 TraceIdExtractor，则使用它
        TraceIdExtractor extractor = tracingTraceIdExtractor.getIfAvailable();
        if (extractor != null) {
            return extractor;
        }
        // 否则使用默认实现，从MDC中获取TraceId
        return new DefaultTraceIdExtractor();
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
}