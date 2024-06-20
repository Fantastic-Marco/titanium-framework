package com.titanium.user.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
     * 租户用户Id
     */
    private Long tenantUserId;

    /**
     * 角色名
     */
    private List<String> authorities;

}
