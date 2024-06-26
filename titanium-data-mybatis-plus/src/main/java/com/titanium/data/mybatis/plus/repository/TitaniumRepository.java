package com.titanium.data.mybatis.plus.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.titanium.data.mybatis.plus.mapper.TitaniumMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
public abstract class TitaniumRepository<M extends TitaniumMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * 批量插入
     * 与Mybatis-Plus的saveBatch方法不同
     * 这个方法是直接拼接批量插入语句，而Mybatis-Plus的saveBatch方法是通过循环单条插入
     * 注意：这个方法不支持字段默认值，如果对字段默认值有要求，请使用saveBatch方法
     */
    int insertBatchSomeColumn(Collection<T> entityList) {
        return this.baseMapper.insertBatchSomeColumn(entityList);
    }

    /**
     * 流式操作
     * 提供计数日志
     */
    public void stream(QueryWrapper<T> queryWrapper, Consumer<T> consumer) {
        AtomicInteger count = new AtomicInteger();
        Long total = this.baseMapper.selectCount(queryWrapper);
        this.baseMapper.selectList(queryWrapper, resultContext -> {
            count.getAndIncrement();
            log.info("stream {}/{} is processing", count.get(), total);
            consumer.accept(resultContext.getResultObject());
        });
    }

    /**
     * 流式操作
     * 提供计数日志
     * @param page
     * @param queryWrapper
     * @param consumer
     */
    public void stream(Page<T> page, QueryWrapper<T> queryWrapper, Consumer<T> consumer) {
        AtomicInteger count = new AtomicInteger();
        Long total = this.baseMapper.selectCount(queryWrapper);
        this.baseMapper.selectList(page, queryWrapper, resultContext -> {
            count.getAndIncrement();
            log.info("stream {}/{} is processing", count.get(), total);
            consumer.accept(resultContext.getResultObject());
        });
    }

}
