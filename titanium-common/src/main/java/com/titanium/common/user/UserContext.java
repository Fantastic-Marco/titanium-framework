package com.titanium.common.user;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户上下文，继承自HashMap，可扩展字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext{
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
