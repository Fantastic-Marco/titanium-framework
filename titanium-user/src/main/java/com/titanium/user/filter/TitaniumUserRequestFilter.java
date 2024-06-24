package com.titanium.user.filter;

import cn.hutool.json.JSONUtil;
import com.titanium.common.user.UserContext;
import com.titanium.common.user.UserContextHolder;
import com.titanium.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "titanium.user", name = "enabled",havingValue = "true")
public class TitaniumUserRequestFilter extends OncePerRequestFilter {

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //校验JWT，成功则放置令牌
        try {
            String authorization = request.getHeader("Authorization");
            if (authorization == null) {
                // 未登录,往下一个过滤器
                filterChain.doFilter(request, response);
            }
            Jws<Claims> claimsJws = JwtUtil.decodeJwt(authorization.toString(), JwtUtil.SECRET_KEY);
            String subject = claimsJws.getPayload().getSubject();
            UserContext userContext = JSONUtil.toBean(subject, UserContext.class);
            UserContextHolder.set(userContext);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("token已过期");
        } catch (SignatureException e) {
            throw new RuntimeException("token解析异常");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("token格式错误");
        } catch (Exception e) {
            throw new RuntimeException("token校验失败");
        }
    }
}
