package com.titanium.tracing.zipkin;

import brave.Span;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import org.slf4j.MDC;

/**
 * 将TraceId放入MDC的SpanHandler
 * 确保日志中可以正确显示TraceId
 */
public class MDCSpanHandler extends SpanHandler {
    
    @Override
    public boolean handlesAbandoned() {
        return true; // 处理被放弃的span
    }
    
    public boolean begin(TraceContext context, Span span, TraceContext parent) {
        // 在span开始时将traceId放入MDC
        MDC.put("traceId", context.traceIdString());
        return true;
    }
    
    public boolean end(TraceContext context, Span span, Cause cause) {
        // 在span结束时清理MDC中的traceId
        MDC.remove("traceId");
        return true;
    }
}