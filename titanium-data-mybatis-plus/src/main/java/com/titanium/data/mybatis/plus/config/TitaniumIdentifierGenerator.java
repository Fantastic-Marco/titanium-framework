package com.titanium.data.mybatis.plus.config;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

/**
 * 默认的Id生成器
 * 使自增主键使用雪花算法
 * 并且提供手动输入Id也能插入同类型的id
 */
public class TitaniumIdentifierGenerator implements IdentifierGenerator {
    /**
     * 生成Id
     * @param entity 实体
     * @return id
     */
    @Override
    public Number nextId(Object entity) {
        return IdUtil.getSnowflakeNextId();
    }

    /**
     * 生成uuid
     * @param entity 实体
     * @return uuid
     */
    @Override
    public String nextUUID(Object entity) {
        return IdUtil.getSnowflakeNextIdStr();
    }
}
