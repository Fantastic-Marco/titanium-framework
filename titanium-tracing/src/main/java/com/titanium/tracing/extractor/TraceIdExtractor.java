package com.titanium.tracing.extractor;

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