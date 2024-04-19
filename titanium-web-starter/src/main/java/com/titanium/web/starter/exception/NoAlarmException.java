package com.titanium.web.starter.exception;

/**
 * 无需告警的异常
 */
public class NoAlarmException extends RuntimeException {

    NoAlarmException(String message) {
        super(message);
    }

    NoAlarmException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NoAlarmException of(String message) {
        return new NoAlarmException(message);
    }

    public static NoAlarmException of(String message, Throwable cause) {
        return new NoAlarmException(message, cause);
    }

}
