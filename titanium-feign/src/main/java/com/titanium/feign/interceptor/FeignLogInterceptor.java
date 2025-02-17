package com.titanium.feign.interceptor;

import com.titanium.feign.client.BufferingFeignClientResponse;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Collection;
import java.util.Map;

/**
 * 日志打印切面
 * feign.Client.Response execute(Request request, Options options) throws IOException;
 */
@Slf4j
@Aspect
public class FeignLogInterceptor {

    @Pointcut("execution(* feign.Client.execute(..))")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Exception {
        //解析参数
        Object[] args = joinPoint.getArgs();
        Request request = (Request) args[0];
        StringBuilder sb = new StringBuilder("[log started]\r\n");
        sb.append(request.httpMethod()).append(" ").append(request.url()).append("\r\n");
        CombineHeaders(sb, request.headers()); // 请求Header
        CombineBody(sb, request.body());

        long costTime = -1;
        String errorMessage = null;
        BufferingFeignClientResponse response = null;
        long begin = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed(args);
            response = new BufferingFeignClientResponse((Response)result );
            costTime = (System.currentTimeMillis() - begin);
        } catch (Throwable exp) {
            costTime = (System.currentTimeMillis() - begin);
            errorMessage = exp.getMessage();
        } finally {
            sb.append("\r\nResponse cost time(ms)： ").append(costTime);
            if (response != null) {
                sb.append("  status: ").append(response.status());
                sb.append("  reason: ").append(response.response.reason());
            }
            sb.append("\r\n");
            if (response != null) {
                CombineHeaders(sb, response.headers()); // 响应Header
                sb.append("Body:\r\n").append(response.body()).append("\r\n");
            }
            if (errorMessage != null) {
                sb.append("Exception:\r\n  ").append(errorMessage).append("\r\n");
            }
            sb.append("\r\n[log ended]");
            log.info(sb.toString());
        }
        return result;
    }

    private static void CombineHeaders(StringBuilder sb, Map<String, Collection<String>> headers) {
        if (headers != null && !headers.isEmpty()) {
            sb.append("Headers:\r\n");
            for (Map.Entry<String, Collection<String>> ob : headers.entrySet()) {
                for (String val : ob.getValue()) {
                    sb.append("  ").append(ob.getKey()).append(": ").append(val).append("\r\n");
                }
            }
        }
    }

    private static void CombineBody(StringBuilder sb, byte[] body) {
        if (body == null || body.length <= 0)
            return;
        sb.append("Body:\r\n").append(new String(body)).append("\r\n");
    }

}
