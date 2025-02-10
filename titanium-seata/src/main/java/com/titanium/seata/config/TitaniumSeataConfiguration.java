package com.titanium.seata.config;

import com.titanium.seata.constants.TitaniumSeataConstants;
import com.titanium.seata.holder.MemoryTccResourceHolder;
import com.titanium.seata.holder.MysqlTccResourceHolder;
import com.titanium.seata.holder.TccResourceHolder;
import com.titanium.seata.tcc.TitaniumScannerChecker;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
    @ConditionalOnProperty(prefix = TitaniumSeataConstants.TITANIUM_SEATA_PREFIX, name = TitaniumSeataConstants.HOLDER_TYPE_PROP_NAME, havingValue = "mysql")
    @ConditionalOnClass(name = "io.seata.rm.datasource.DataSourceProxy")
    @ConditionalOnMissingBean
    TccResourceHolder mysqlTccResourceHolder(DataSourceProxy proxy) {
        log.info("mysql tcc resource holder enabled");
        return new MysqlTccResourceHolder(proxy);
    }

    /**
     * 内存分布式事务资源存储
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = TitaniumSeataConstants.TITANIUM_SEATA_PREFIX, name =TitaniumSeataConstants.HOLDER_TYPE_PROP_NAME, havingValue = "memory")
    @ConditionalOnMissingBean
    TccResourceHolder memoryTccResourceHolder() {
        log.info("memory tcc resource holder enabled");
        return new MemoryTccResourceHolder();
    }

    /**
     * 自定义 tcc bean 检查器
     * @return
     */
    @Bean
    @ConditionalOnBean(TccResourceHolder.class)
    @ConditionalOnMissingBean
    TitaniumScannerChecker titaniumScannerChecker() {
        log.info("titanium seata scanner checker enabled");
        return new TitaniumScannerChecker();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
