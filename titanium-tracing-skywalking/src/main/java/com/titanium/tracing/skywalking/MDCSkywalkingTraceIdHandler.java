package com.titanium.tracing.skywalking;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;

/**
 * 将Skywalking TraceId放入MDC的处理器
 * 确保日志中可以正确显示TraceId
 */
public class MDCSkywalkingTraceIdHandler {
    
    /**
     * 设置当前traceId到MDC中
     */
    public void setupMDC() {
        // 尝试获取当前traceId并放入MDC
        try {
            String traceId = TraceContext.traceId();
            if (traceId != null && !traceId.isEmpty() && !"Ignored_Trace".equals(traceId)) {
                MDC.put("traceId", traceId);
            }
        } catch (Exception e) {
            // 忽略异常，可能Skywalking未启用
        }
    }
    
    /**
     * 清理MDC中的traceId
     */
    public void cleanupMDC() {
        // 清理MDC中的traceId
        MDC.remove("traceId");
    }
}