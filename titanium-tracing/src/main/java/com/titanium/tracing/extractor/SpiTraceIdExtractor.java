package com.titanium.tracing.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 基于SPI机制的TraceId提取器
 * 通过Java的ServiceLoader机制加载具体的TraceIdExtractor实现
 */
public class SpiTraceIdExtractor implements TraceIdExtractor, ApplicationContextAware {
    
    private static final Logger logger = LoggerFactory.getLogger(SpiTraceIdExtractor.class);
    
    private TraceIdExtractor delegate;
    private ApplicationContext applicationContext;
    
    public SpiTraceIdExtractor() {
        loadDelegateExtractor();
    }
    
    private void loadDelegateExtractor() {
        try {
            ServiceLoader<TraceIdExtractor> serviceLoader = ServiceLoader.load(TraceIdExtractor.class);
            Iterator<TraceIdExtractor> iterator = serviceLoader.iterator();
            
            if (iterator.hasNext()) {
                this.delegate = iterator.next();
                logger.debug("Loaded TraceIdExtractor implementation: {}", delegate.getClass().getName());
                
                // 如果实现了ApplicationContextAware接口，则注入ApplicationContext
                if (delegate instanceof ApplicationContextAware && applicationContext != null) {
                    ((ApplicationContextAware) delegate).setApplicationContext(applicationContext);
                }
            } else {
                logger.debug("No TraceIdExtractor implementation found via SPI");
            }
        } catch (Exception e) {
            logger.warn("Failed to load TraceIdExtractor via SPI", e);
        }
    }
    
    /**
     * 检查是否存在有效的SPI实现
     * 
     * @return 如果存在有效的SPI实现返回true，否则返回false
     */
    public boolean hasValidImplementation() {
        return delegate != null && delegate.getCurrentTraceId() != null;
    }
    
    @Override
    public String getCurrentTraceId() {
        if (delegate != null) {
            return delegate.getCurrentTraceId();
        }
        return null;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        // 如果delegate已经加载，重新设置applicationContext
        if (delegate instanceof ApplicationContextAware && applicationContext != null) {
            ((ApplicationContextAware) delegate).setApplicationContext(applicationContext);
        }
    }
}