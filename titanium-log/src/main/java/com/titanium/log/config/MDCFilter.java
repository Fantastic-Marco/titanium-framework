package com.titanium.log.config;

import com.titanium.tracing.extractor.TraceIdExtractor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

/**
 * MDC过滤器，确保在每个请求中将traceId放入MDC中
 */
public class MDCFilter {
    
    @Autowired(required = false)
    private TraceIdExtractor traceIdExtractor;
    
    /**
     * 获取当前traceId
     * 
     * @return traceId，如果不存在则返回null
     */
    public String getTraceId() {
        if (traceIdExtractor != null) {
            return traceIdExtractor.getCurrentTraceId();
        }
        return null;
    }
}