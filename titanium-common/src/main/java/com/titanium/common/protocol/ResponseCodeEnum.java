package com.titanium.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCodeEnum {
    OK("200", "success"),
    ERROR("500", "server-error"),
    NO_ALARM("400", "param-error"),
    NO_PERMISSION("401", "no-permission"),
    NO_LOGIN("403", "no-login"),
    NO_LOGIN_OR_PERMISSION("403", "no-login-or-permission"),
    NO_LOGIN_OR_PERMISSION_OR_ALARM("403", "no-login-or-permission-or-alarm"),
    NO_LOGIN_OR_PERMISSION_OR_ALARM_OR_NO_PERMISSION("403", "no-login-or-permission-or-alarm-or-no-permission"),
    ;

    private String code;
    private String message;
}
