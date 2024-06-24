package com.titanium.web.starter.advice;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.titanium.web.starter.protocol.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;


@Slf4j
//@ControllerAdvice
public class TitaniumMessageConverter extends MappingJackson2HttpMessageConverter implements InitializingBean {

    public TitaniumMessageConverter(ObjectMapper objectMapper) {
        super();
        setObjectMapper(objectMapper);
        // 设置支持的媒体类型，例如 application/json
        super.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return true;
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if (object instanceof Response) {
            // 已经是统一响应对象，直接调用父类方法序列化
            super.writeInternal(object, type, outputMessage);
        }  else if(object instanceof ProblemDetail){
            // 如果Controller方法已经返回了ProblemDetail类型，则无需再次包装
            super.writeInternal(object, type, outputMessage);
        }else {
            // 非统一响应对象，包装后再序列化
            Response<Object> wrappedResponse = Response.ok(object);
            super.writeInternal(wrappedResponse, type, outputMessage);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("added global response handler");
    }
}
