package com.titanium.seata.config;

import cn.hutool.core.lang.Assert;
import com.titanium.seata.holder.MemoryTccResourceHolder;
import com.titanium.seata.holder.MysqlTccResourceHolder;
import com.titanium.seata.holder.RedisTccResourceHolder;
import com.titanium.seata.holder.TccResourceHolder;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@EnableConfigurationProperties(TitaniumSeataProperties.class)
public class TitaniumSeataConfiguration implements EnvironmentAware {
    private Environment environment;

    /**
     * Mysql 分布式事务资源存储
     * @param proxy
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "titanium.seata", name = "holder-type", havingValue = "mysql")
    @ConditionalOnClass(name = "io.seata.rm.datasource.DataSourceProxy")
    TccResourceHolder mysqlTccResourceHolder(DataSourceProxy proxy) {
        log.info("mysql tcc resource holder enabled");
        return new MysqlTccResourceHolder(proxy);
    }

    /**
     * Redis 分布式事务资源存储
     * @param redisTemplate
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "titanium.seata", name = "holder-type", havingValue = "redis")
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    TccResourceHolder redisTccResourceHolder(RedisTemplate redisTemplate) {
        log.info("redis tcc resource holder enabled");
        String applicationName = environment.getProperty("spring.application.name");
        Assert.notNull(applicationName, "spring.application.name must not be null");
        return new RedisTccResourceHolder(redisTemplate, applicationName);
    }

    /**
     * 内存分布式事务资源存储
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "titanium.seata", name = "holder-type", havingValue = "memory")
    TccResourceHolder memoryTccResourceHolder() {
        log.info("memory tcc resource holder enabled");
        return new MemoryTccResourceHolder();
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
