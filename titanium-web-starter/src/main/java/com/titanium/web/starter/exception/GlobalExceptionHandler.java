package com.titanium.web.starter.exception;

import com.titanium.web.starter.protocol.Response;
import com.titanium.web.starter.protocol.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * 无需报警异常处理
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(value = NoAlarmException.class)
    public final ResponseEntity<Object> handleNoAlarmException(NoAlarmException e, WebRequest request) {
        log.error("no alarm exception: {}", e.getMessage());
        log.error(e.getMessage(),e);
        Response response = Response.error(ResponseCodeEnum.ERROR.getCode(), e.getMessage());

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 根据实际情况选择合适的HTTP状态码
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * 报警异常处理
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(value = AlarmException.class)
    public final ResponseEntity<Object> handleNoAlarmException(AlarmException e, WebRequest request) {
        log.error("alarm exception: {}", e.getMessage());
        log.error(e.getMessage(),e);
        //TODO 报警处理,发送邮件,短信等

        Response response = Response.error(ResponseCodeEnum.ERROR.getCode(), e.getMessage());

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 根据实际情况选择合适的HTTP状态码
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * 其他异常处理
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public final ResponseEntity<Object> handleNoAlarmException(RuntimeException e, WebRequest request) {
        log.error("runtime exception: {}", e.getMessage());
        log.error(e.getMessage(),e);
        Response response = Response.error(ResponseCodeEnum.ERROR.getCode(), e.getMessage());

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 根据实际情况选择合适的HTTP状态码
        return new ResponseEntity<>(response, httpStatus);
    }





}
