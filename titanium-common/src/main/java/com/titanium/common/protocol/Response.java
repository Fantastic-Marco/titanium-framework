package com.titanium.common.protocol;

import lombok.Data;

@Data
public class Response<T> {
    private String code;

    private String message;

    private boolean success;

    private T data;

    public static <T> Response<T> ok(T data) {
        Response<T> response = new Response<>();
        response.setCode(ResponseCodeEnum.OK.getCode());
        response.setMessage(ResponseCodeEnum.OK.getMessage());
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error(String message) {
        Response<T> response = new Response<>();
        response.setCode(ResponseCodeEnum.ERROR.getCode());
        response.setMessage(message);
        response.setSuccess(false);
        return response;
    }

    public static <T> Response<T> error(String code, String message) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setSuccess(false);
        return response;
    }

    public static <T> Response<T> of(String code, String message, boolean success, T data) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setSuccess(success);
        response.setData(data);
        return response;
    }
}
