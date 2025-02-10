package com.titanium.seata.config;

import cn.hutool.core.lang.Assert;
import com.titanium.seata.constants.TccConstants;
import com.titanium.seata.constants.TitaniumSeataConstants;
import com.titanium.seata.holder.RedisTccResourceHolder;
import com.titanium.seata.holder.TccResourceHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class TitaniumSeataTccRedisHolderConfiguration {
    /**
     * Redis 分布式事务资源存储
     * @param redisTemplate
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = TitaniumSeataConstants.TITANIUM_SEATA_PREFIX, name = TitaniumSeataConstants.HOLDER_TYPE_PROP_NAME, havingValue = TccConstants.TCC_REDIS_HOLDER)
    TccResourceHolder redisTccResourceHolder(RedisTemplate<?, ?> redisTemplate) {
        log.info("redis tcc resource holder enabled");
        String applicationName = System.getProperty("spring.application.name");
        Assert.notNull(applicationName, "spring.application.name must not be null");
        return new RedisTccResourceHolder(redisTemplate, applicationName);
    }
}
