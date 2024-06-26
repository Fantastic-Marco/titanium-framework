package com.titanium.data.redis.cache;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Mybaits 二级缓存 实现
 * 往Mapper上加上@CacheNamespace(implementation= TitaniumMybatisRedisCache.class,eviction=TitaniumMybatisRedisCache.class)
 */
@Slf4j
public class TitaniumMybatisRedisCache implements Cache {
    private RedisTemplate redisTemplate;
    private final String id;

    public TitaniumMybatisRedisCache(String id) {
        if (ObjectUtil.isNull(id)) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    public RedisTemplate getRedisTemplate() {
        if (ObjectUtil.isNull(redisTemplate)) {
            redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        }
        return redisTemplate;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(@NonNull Object key, @NonNull Object value) {
        log.debug("TitaniumMybatisRedisCache id {},put key:{},value {}", id, key, JSONUtil.toJsonStr(value));
        getRedisTemplate().opsForHash().put(id, key.toString(), value);
    }

    @Override
    public Object getObject(@NonNull Object key) {
        log.debug("TitaniumMybatisRedisCache id {},get key:{}", id, key);
        try {
            return getRedisTemplate().opsForHash().get(id, key.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Object removeObject(@NonNull Object key) {
        log.debug("TitaniumMybatisRedisCache id {},remove key:{}", id, key);
        return getRedisTemplate().opsForHash().delete(id, key.toString());
    }

    @Override
    public void clear() {
        log.debug("TitaniumMybatisRedisCache clear id {}", id);
        getRedisTemplate().delete(id);
    }

    @Override
    public int getSize() {
        int size = getRedisTemplate().opsForHash().size(id).intValue();
        log.debug("TitaniumMybatisRedisCache get id {} size {}", id, size);
        return size;
    }
}
