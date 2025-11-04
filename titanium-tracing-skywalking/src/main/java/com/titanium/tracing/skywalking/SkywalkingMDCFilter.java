package com.titanium.tracing.skywalking;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Skywalking MDC过滤器
 * 确保在每个请求中将Skywalking traceId放入MDC中
 */
public class SkywalkingMDCFilter implements Filter {
    
    @Autowired(required = false)
    private MDCSkywalkingTraceIdHandler mdcHandler;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化操作
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            // 设置traceId到MDC中
            if (mdcHandler != null) {
                mdcHandler.setupMDC();
            }
            
            // 继续处理请求
            chain.doFilter(request, response);
        } finally {
            // 清理MDC中的traceId
            if (mdcHandler != null) {
                mdcHandler.cleanupMDC();
            }
        }
    }
    
    @Override
    public void destroy() {
        // 销毁操作
    }
}