package com.titanium.data.mybatis.plus.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

public class TitaniumRepository<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
}
