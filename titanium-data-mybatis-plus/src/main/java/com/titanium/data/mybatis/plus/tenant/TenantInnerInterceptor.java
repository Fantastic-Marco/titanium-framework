package com.titanium.data.mybatis.plus.tenant;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.titanium.common.tenant.TenantContext;
import com.titanium.common.tenant.TenantContextHolder;
import com.titanium.data.mybatis.plus.config.TenantProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

/**
 * 租户拦截器
 */
public class TenantInnerInterceptor implements TenantLineHandler {
    private TenantProperties properties;

    public TenantInnerInterceptor(TenantProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取租户 ID 值表达式，只支持单个 ID 值
     * <p>
     * @return 租户 ID 值表达式
     */
    @Override
    public Expression getTenantId() {
        // 获取当前用户的租户
        TenantContext tenantContext = TenantContextHolder.get();
        if (ObjectUtil.isNotNull(tenantContext)) {
            Long tenantId = TenantContextHolder.get().getTenantId();
            // 返回租户ID的表达式，LongValue 是 JSQLParser 中表示 bigint 类型的 class
            return new LongValue(tenantId);
        } else {
            return null;
        }
    }

    /**
     * 获取租户字段名
     * <p>
     * 默认字段名叫: tenant_id
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getColumn();
    }

    /**
     * 根据表名判断是否忽略拼接多租户条件
     * <p>
     * 默认都要进行解析并拼接多租户条件
     * @param tableName 表名
     * @return 是否忽略, true:表示忽略，false:需要解析并拼接多租户条件
     */
    @Override
    public boolean ignoreTable(String tableName) {
        //如果没有租户信息，则不进行多租户处理
        if (ObjectUtil.isNull(TenantContextHolder.get())) {
            return true;
        }
        return properties.getTables()
                .stream().noneMatch(tableName::equalsIgnoreCase);
    }

    /**
     * 忽略插入租户字段逻辑
     * @param columns        插入字段
     * @param tenantIdColumn 租户 ID 字段
     * @return
     */
    @Override
    public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
        return TenantLineHandler.super.ignoreInsert(columns, tenantIdColumn);
    }
}
