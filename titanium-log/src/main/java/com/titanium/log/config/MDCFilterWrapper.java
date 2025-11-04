package com.titanium.log.config;

import com.titanium.tracing.extractor.TraceIdExtractor;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * MDC过滤器包装类，实现Servlet Filter接口
 */
public class MDCFilterWrapper implements Filter {
    
    @Autowired(required = false)
    private TraceIdExtractor traceIdExtractor;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化操作
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            // 尝试获取traceId并放入MDC
            String traceId = getTraceId();
            if (traceId != null) {
                MDC.put("traceId", traceId);
            }
            
            // 继续处理请求
            chain.doFilter(request, response);
        } finally {
            // 清理MDC中的traceId
            MDC.remove("traceId");
        }
    }
    
    @Override
    public void destroy() {
        // 销毁操作
    }
    
    /**
     * 获取当前traceId
     * 
     * @return traceId，如果不存在则返回null
     */
    private String getTraceId() {
        if (traceIdExtractor != null) {
            return traceIdExtractor.getCurrentTraceId();
        }
        return null;
    }
}