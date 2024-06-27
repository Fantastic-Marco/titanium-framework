package com.titanium.data.mybatis.plus.config;

import com.titanium.data.mybatis.plus.encrypt.properties.EncryptProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "titanium.data.mybatis-plus")
public class TitaniumMybatisPlusProperties {
    /**
     * 是否开启sql日志
     * 使用p6spy实现
     * 具有性能损耗，不建议生产环境使用
     */
    private boolean sqlLogEnabled = false;

    /**
     * 乐观锁开关
     */
    private boolean optimisticLockerEnabled = false;

    /**
     * 防止全表删除/更新
     */
    private boolean blockAttackEnabled = true;

    /**
     * 加密配置
     */
    private EncryptProperties encrypt;

    /**
     * 租户配置
     */
    private TenantProperties tenant;

}

