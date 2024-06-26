package com.titanium.web.starter.interceptor;

import cn.hutool.json.JSONUtil;
import com.titanium.common.annotation.SkipAuth;
import com.titanium.common.user.UserContext;
import com.titanium.common.user.UserContextHolder;
import com.titanium.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class UserHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //校验JWT，成功则放置令牌
        try {
            // 校验是否需要跳过权限校验
            if (handler instanceof HandlerMethod) {
                HandlerMethod method = (HandlerMethod) handler;
                SkipAuth skipAuth = method.getMethodAnnotation(SkipAuth.class);
                if (skipAuth != null) {
                    return true;
                }
                //看控制器是否需要跳过权限校验
                if(method.getBeanType().isAnnotationPresent(SkipAuth.class)){
                    return true;
                }
            }
            // 校验token
            String authorization = request.getHeader("Authorization");
            if (authorization == null) {
                // 未登录,禁止访问
                throw new RuntimeException("认证信息缺失");
            }
            Jws<Claims> claimsJws = JwtUtil.decodeJwt(authorization.toString(), JwtUtil.SECRET_KEY);
            String subject = claimsJws.getPayload().getSubject();
            UserContext userContext = JSONUtil.toBean(subject, UserContext.class);
            UserContextHolder.set(userContext);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("认证信息已过期");
        } catch (SignatureException e) {
            throw new RuntimeException("认证信息解析异常");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("认证信息格式错误");
        } catch (Exception e) {
            throw new RuntimeException("认证信息校验失败");
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        UserContextHolder.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        UserContextHolder.clear();
    }
}
