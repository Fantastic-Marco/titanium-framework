package com.titanium.common.user;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户上下文，继承自HashMap，可扩展字段
 */
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class UserContext extends HashMap<String, Object> {
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

    public void setAuthorization(String authorization) {
        put("authorization", authorization);
    }

    public String getAuthorization() {
        return (String) get("authorization");
    }

    public void setUserId(Long userId) {
        put("userId", userId);
    }

    public Long getUserId() {
        return (Long) get("userId");
    }

    public void setUserName(String userName) {
        put("userName", userName);
    }

    public String getUserName() {
        return (String) get("userName");
    }

    public void setTenantId(Long tenantId) {
        put("tenantId", tenantId);
    }

    public Long getTenantId() {
        return (Long) get("tenantId");
    }

    public void setTenantName(String tenantName) {
        put("tenantName", tenantName);
    }

    public String getTenantName() {
        return (String) get("tenantName");
    }

    public void setSystemCode(String systemCode) {
        put("systemCode", systemCode);
    }

    public String getSystemCode() {
        return (String) get("systemCode");
    }

    public void setTenantUserId(Long tenantUserId) {
        put("tenantUserId", tenantUserId);
    }

    public Long getTenantUserId() {
        return (Long) get("tenantUserId");
    }

    public void setAuthorities(List<String> authorities) {
        put("authorities", authorities);
    }

    public List<String> getAuthorities() {
        return (List<String>) get("authorities");
    }

}
