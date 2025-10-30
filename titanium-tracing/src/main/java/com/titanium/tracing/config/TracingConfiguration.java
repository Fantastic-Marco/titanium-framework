package com.titanium.tracing.config;

import org.springframework.context.annotation.Configuration;

/**
 * 分布式追踪基础配置类
 * 提供分布式追踪的抽象层，具体实现由不同的追踪系统提供
 */
@Configuration
public class TracingConfiguration {
    
    /**
     * TraceId提取器接口
     */
    public interface TraceIdExtractor {
        /**
         * 获取当前TraceId
         * 
         * @return 当前TraceId，如果不存在则返回null
         */
        String getCurrentTraceId();
    }
}