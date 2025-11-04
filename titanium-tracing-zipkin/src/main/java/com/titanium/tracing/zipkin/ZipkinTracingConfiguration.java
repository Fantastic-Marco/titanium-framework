package com.titanium.tracing.zipkin;

import brave.Tracer;
import brave.handler.SpanHandler;
import brave.propagation.CurrentTraceContext;
import com.titanium.tracing.extractor.TraceIdExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * Zipkin分布式追踪配置类
 * 提供与Zipkin服务器的集成
 */
@Slf4j
@Configuration
public class ZipkinTracingConfiguration {
    
    private final Optional<CurrentTraceContext> currentTraceContext;
    private final Optional<Tracer> tracer;
    private final SpanHandler mdcSpanHandler;
    
    public ZipkinTracingConfiguration(Optional<CurrentTraceContext> currentTraceContext, 
                                     Optional<Tracer> tracer,
                                     @Autowired(required = false) SpanHandler mdcSpanHandler) {
        this.currentTraceContext = currentTraceContext;
        this.tracer = tracer;
        this.mdcSpanHandler = mdcSpanHandler;
    }
    
    /**
     * 配置TraceId在MDC中的自动注入和清除
     */
    @PostConstruct
    public void setupMDCIntegration() {
        // 注册MDCSpanHandler以确保traceId被正确放入MDC中
    }
    
    /**
     * 获取当前TraceId的工具方法
     * 
     * @return 当前TraceId，如果不存在则返回null
     */
    @Bean
    public com.titanium.tracing.extractor.TraceIdExtractor TraceIdExtractor() {
        log.info("titanium tracing zipkin started");
        return new TraceIdExtractor(currentTraceContext);
    }
    
    /**
     * 提供MDCSpanHandler Bean
     * 
     * @return MDCSpanHandler实例
     */
    @Bean
    public SpanHandler mdcSpanHandler() {
        return new MDCSpanHandler();
    }
    
    public static class TraceIdExtractor implements com.titanium.tracing.extractor.TraceIdExtractor, org.springframework.context.ApplicationContextAware {
        private Optional<CurrentTraceContext> currentTraceContext;
        private org.springframework.context.ApplicationContext applicationContext;
        
        public TraceIdExtractor(Optional<CurrentTraceContext> currentTraceContext) {
            this.currentTraceContext = currentTraceContext;
        }
        
        // 为SPI机制提供的无参构造函数
        public TraceIdExtractor() {
            this(Optional.empty());
        }
        
        @Override
        public String getCurrentTraceId() {
            // 延迟初始化currentTraceContext
            if (currentTraceContext == null && applicationContext != null) {
                try {
                    CurrentTraceContext ctx = applicationContext.getBean(CurrentTraceContext.class);
                    this.currentTraceContext = Optional.of(ctx);
                } catch (Exception e) {
                    // 如果无法获取CurrentTraceContext，则使用空的Optional
                    this.currentTraceContext = Optional.empty();
                }
            }
            
            return currentTraceContext
                .map(ctx -> ctx.get())
                .map(context -> context.traceIdString())
                .orElse(null);
        }
        
        @Override
        public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }
    }
}