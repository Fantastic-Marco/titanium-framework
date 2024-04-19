package com.titanium.web.starter.exception;

/**
 * 需要告警的异常
 * 例如发送邮件，短信等
 */
public class AlarmException extends RuntimeException {

    AlarmException(String message) {
        super(message);
    }

    AlarmException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AlarmException of(String message) {
        return new AlarmException(message);
    }

    public static AlarmException of(String message, Throwable cause) {
        return new AlarmException(message, cause);
    }

}
