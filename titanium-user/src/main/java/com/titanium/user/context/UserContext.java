package com.titanium.user.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContext {
    /**
     * authorization
     */
    private String authorization;

    /**
     * 用户Id
     */
    private Long userId;

    /***
     * 用户名
     */
    private String userName;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 租户Id
     */
    private Long tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 系统code
     */
    private String systemCode;

    /**
     * 租户code
     */
    private String tenantCode;

    /**
     * 租户用户Id
     */
    private Long tenantUserId;

    /**
     * 租户用户名称
     */
    private String tenantUserName;


}
