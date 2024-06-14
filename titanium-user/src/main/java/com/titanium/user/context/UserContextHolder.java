package com.titanium.user.context;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class UserContextHolder {
    private final ThreadLocal<UserContext> userContextHolder = new ThreadLocal<>();

    /**
     * 设置用户信息上下文
     * @param context
     */
    public void set(UserContext context) {
        userContextHolder.set(context);
    }

    /**
     * 获取用户信息上下文
     * @return
     */
    public UserContext get() {
        return userContextHolder.get();
    }

    /**
     * 清除用户信息上下文
     */
    public void clear() {
        userContextHolder.remove();
    }
}
