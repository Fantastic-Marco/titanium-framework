package com.titanium.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Bool {
    YES(1),
    NO(0);

    private int code;

    public static Bool fromBoolean(Boolean flag) {
        return flag ? YES : NO;
    }

    public static Integer codeFromBoolean(Boolean flag) {
        return flag ? YES.getCode() : NO.getCode();
    }

    public Boolean toBoolean() {
        return this == YES;
    }
}
