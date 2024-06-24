package com.titanium.web.starter.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.titanium.common.annotation.SkipAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用于标记内部接口，
 */
public class SkipAuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            HandlerMethod method = (HandlerMethod) handler;
            SkipAuth skipAuth = method.getMethodAnnotation(SkipAuth.class);
            return ObjectUtil.isNotNull(skipAuth);
        }else{
            return false;
        }
    }
}
