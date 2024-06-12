package com.titanium.web.starter.advice;

import com.titanium.web.starter.annotation.ViewPage;
import com.titanium.web.starter.protocol.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 返回true表示此Advice对所有带有@ResponseBody的方法都生效
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        //如果当前接口被标记为视图，那不做任何处理，直接返回
        if (isViewResponse(returnType)){
            log.info("当前接口被标记为视图，不做任何处理");
            return body;
        }else {
            if (body instanceof Response<?>) {
                // 如果Controller方法已经返回了ResponseVO类型，则无需再次包装
                return body;
            } else {
                // 封装响应体
                Response<Object> objectResponse = Response.ok(body);

                // 设置响应内容类型和编码
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);

                try {
                    // 将封装后的响应体转换为JSON字符串写入响应体
                    return objectResponse;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize response", e);
                }
            }
        }
    }

    private Boolean isViewResponse(MethodParameter returnType) {
        return Arrays.stream(returnType.getMethodAnnotations()).anyMatch(a -> a.annotationType().equals(ViewPage.class));
    }
}
