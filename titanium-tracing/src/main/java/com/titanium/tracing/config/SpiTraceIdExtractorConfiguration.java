package com.titanium.tracing.config;

import com.titanium.tracing.extractor.SpiTraceIdExtractor;
import com.titanium.tracing.extractor.TraceIdExtractor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基于SPI机制的TraceIdExtractor自动配置类
 * 当没有其他TraceIdExtractor Bean存在时，通过SPI机制加载实现
 */
@Configuration
public class SpiTraceIdExtractorConfiguration {

    /**
     * 通过SPI机制提供TraceIdExtractor实现
     * 只有在没有其他TraceIdExtractor Bean时才会创建
     * 
     * @return TraceIdExtractor实现
     */
    @Bean
    @ConditionalOnMissingBean(TraceIdExtractor.class)
    public TraceIdExtractor traceIdExtractor() {
        SpiTraceIdExtractor spiExtractor = new SpiTraceIdExtractor();
        // 只有在SPI机制找到有效实现时才返回它
        if (spiExtractor.hasValidImplementation()) {
            return spiExtractor;
        }
        // 否则返回null，让其他配置类提供默认实现
        return null;
    }
}