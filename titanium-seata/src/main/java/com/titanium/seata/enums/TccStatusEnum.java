package com.titanium.seata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum TccStatusEnum {

    INIT(0, "未执行"),

    TRYING(1, "执行中"),

    CONFIRM(2, "确认"),

    CANCEL(3, "取消");


    private int code;
    private String desc;

    public static Set<TccStatusEnum> allowCancelStatus() {
        return Set.of(INIT, TRYING);
    }

    public static Set<TccStatusEnum> allowDeleteStatus() {
        return Set.of(CONFIRM, CANCEL);
    }

    public static String[] allowDeleteStatusCodes() {
        String[] array = allowDeleteStatus()
                .stream()
                .map(TccStatusEnum::getCode)
                .map(String::valueOf)
                .collect(Collectors.toList())
                .toArray(String[]::new);
        return array;
    }
}
