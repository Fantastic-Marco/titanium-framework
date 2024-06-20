package com.titanium.data.mybatis.plus.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.titanium.data.mybatis.plus.repository.AdvancedSqlInjector;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableConfigurationProperties({TitaniumMybatisPlusProperties.class, DataSourceProperties.class})
public class TitaniumMybatisPlusConfig {

    @Bean
    @ConditionalOnClass(DataSource.class)
    public DataSource dataSource(DataSourceProperties properties) {
        log.info("init datasource ");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean(SqlSessionFactoryBean.class)
    public SqlSessionFactory sqlSessionFactory(MybatisProperties properties, DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = SqlSessionFactoryBeanBuilder.getSqlSessionFactoryBean(properties, dataSource);
//        bean.setPlugins(
//                new EncryptQueryInterceptor(encryptProperties, encryptor),
//                new EncryptionResultInterceptor(encryptProperties, encryptor),
//                new EncryptUpdateInnerInterceptor(encryptProperties, encryptor)
//        );
        return bean.getObject();
    }


}
