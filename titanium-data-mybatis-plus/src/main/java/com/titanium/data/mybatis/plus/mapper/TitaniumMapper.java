package com.titanium.data.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

public interface TitaniumMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入 仅适用于mysql
     * 对于默认值填充有影响
     * @param entityList 实体列表
     * @return 影响行数
     */
    int insertBatchSomeColumn(Collection<T> entityList);

}
