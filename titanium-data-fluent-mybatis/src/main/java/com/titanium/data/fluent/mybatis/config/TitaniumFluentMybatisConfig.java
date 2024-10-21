package com.titanium.data.fluent.mybatis.config;

import cn.org.atool.fluent.mybatis.spring.MapperFactory;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableConfigurationProperties({TitaniumFluentMybatisProperties.class, DataSourceProperties.class})
public class TitaniumFluentMybatisConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        //seata 数据库代理
        if (ClassUtils.isPresent("io.seata.rm.datasource.DataSourceProxy", this.getClass().getClassLoader())) {
            log.info("seata datasource proxy enabled");
            return new DataSourceProxy(dataSource);
        }
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource, TitaniumFluentMybatisProperties properties) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean;
    }

    @Bean
    public MapperFactory mapperFactory() {
        return new MapperFactory();
    }

}
