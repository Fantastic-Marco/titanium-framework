package com.titanium.web.starter.interceptor;

import cn.hutool.core.lang.Assert;
import com.titanium.common.tenant.TenantContext;
import com.titanium.common.tenant.TenantContextHolder;
import com.titanium.common.user.UserContext;
import com.titanium.common.user.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 租户拦截器
 * 被拦截的请求在SQL语句中自动加上租户ID
 */
public class TenantHandlerInterceptor implements HandlerInterceptor, Ordered {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserContext userContext = UserContextHolder.get();
        TenantContext context = new TenantContext();
        Assert.notNull(userContext, "用户上下文不能为空");
        Assert.notNull(userContext.getTenantId(), "租户ID不能为空");
        context.setTenantId(userContext.getTenantId());
        TenantContextHolder.set(context);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantContextHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        TenantContextHolder.clear();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
