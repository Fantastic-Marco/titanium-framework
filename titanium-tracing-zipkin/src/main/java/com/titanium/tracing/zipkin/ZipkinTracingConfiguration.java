package com.titanium.tracing.zipkin;

import brave.propagation.TraceContext;
import brave.propagation.CurrentTraceContext;
import com.titanium.tracing.config.TracingConfiguration;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

/**
 * Zipkin分布式追踪配置类
 * 提供与Zipkin服务器的集成
 */
@Configuration
public class ZipkinTracingConfiguration {
    
    private final Optional<CurrentTraceContext> currentTraceContext;
    
    public ZipkinTracingConfiguration(Optional<CurrentTraceContext> currentTraceContext) {
        this.currentTraceContext = currentTraceContext;
    }
    
    /**
     * 配置TraceId在MDC中的自动注入和清除
     */
    @PostConstruct
    public void setupMDCIntegration() {
        // Zipkin会自动将traceId放入MDC中，供日志系统使用
    }
    
    /**
     * 获取当前TraceId的工具方法
     * 
     * @return 当前TraceId，如果不存在则返回null
     */
    @Bean
    public TracingConfiguration.TraceIdExtractor traceIdExtractor() {
        return new TraceIdExtractor(currentTraceContext);
    }
    
    public static class TraceIdExtractor implements TracingConfiguration.TraceIdExtractor {
        private final Optional<CurrentTraceContext> currentTraceContext;
        
        public TraceIdExtractor(Optional<CurrentTraceContext> currentTraceContext) {
            this.currentTraceContext = currentTraceContext;
        }
        
        @Override
        public String getCurrentTraceId() {
            return currentTraceContext
                .map(ctx -> ctx.get())
                .map(context -> context.traceIdString())
                .orElse(null);
        }
    }
}