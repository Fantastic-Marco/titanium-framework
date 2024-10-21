package com.titanium.data.jpa.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;

@Slf4j
@Configuration
//开启审计
@EnableJpaAuditing
@EnableTransactionManagement
@EnableConfigurationProperties({
        DataSourceProperties.class,
})
public class TitaniumJpaConfig {
    /**
     * 数据源
     * @param datasourceProperties
     * @return
     * com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecoratorAutoConfiguration
     */
    @Bean
    @ConditionalOnClass(DataSource.class)
    public DataSource dataSource(DataSourceProperties datasourceProperties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(datasourceProperties.getUrl());
        dataSource.setDriverClassName(datasourceProperties.getDriverClassName());
        dataSource.setUsername(datasourceProperties.getUsername());
        dataSource.setPassword(datasourceProperties.getPassword());
        //seata 数据库代理
        if (ClassUtils.isPresent("io.seata.rm.datasource.DataSourceProxy", this.getClass().getClassLoader())) {
            log.info("seata datasource proxy enabled");
            return new DataSourceProxy(dataSource);
        }
        return dataSource;
    }
}
