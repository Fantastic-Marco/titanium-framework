package com.titanium.web.starter.advice;

import com.titanium.web.starter.annotation.ApiLog;
import com.titanium.web.starter.config.TitaniumWebProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

@Slf4j
public class ApiLogHandler implements HandlerInterceptor {
    private TitaniumWebProperties webConfigProperties;

    public ApiLogHandler(TitaniumWebProperties webConfigProperties) {
        this.webConfigProperties = webConfigProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (webConfigProperties.isEnableApiLogging() || handler.getClass().isAnnotationPresent(ApiLog.class)) {
            logRequestDetails(request,response,handler);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void logRequestDetails(HttpServletRequest request, HttpServletResponse response, Object handler) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Caller IP: ").append(request.getRemoteAddr())
                .append(" [").append(request.getMethod()).append("]-[")
                .append(request.getRequestURI()).
                append("]");

        // 获取请求参数
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames.hasMoreElements()) {
            logMessage.append(",Parameters: ");
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                logMessage.append(paramName).append(": ").append(paramValue);
            }
        }

        // 获取响应状态码
        log.info(logMessage.toString());
    }


}
