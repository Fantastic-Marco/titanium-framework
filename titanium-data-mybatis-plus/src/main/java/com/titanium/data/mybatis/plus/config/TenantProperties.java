package com.titanium.data.mybatis.plus.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantProperties {
    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 需要拦截表名
     */
    private List<String> tables;

    /**
     * 字段名
     */
    private String column = "tenant_id";
}
