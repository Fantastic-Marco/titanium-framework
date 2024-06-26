package com.titanium.data.mybatis.plus.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.titanium.data.mybatis.plus.encrypt.encrytor.DefaultEncryptor;
import com.titanium.data.mybatis.plus.encrypt.encrytor.IEncryptor;
import com.titanium.data.mybatis.plus.encrypt.interceptor.EncryptQueryInterceptor;
import com.titanium.data.mybatis.plus.encrypt.interceptor.EncryptUpdateInnerInterceptor;
import com.titanium.data.mybatis.plus.encrypt.interceptor.EncryptionResultInterceptor;
import com.titanium.data.mybatis.plus.encrypt.properties.EncryptProperties;
import com.titanium.data.mybatis.plus.repository.AdvancedSqlInjector;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Objects;

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


    /**
     * 默认加密器
     * @param encryptProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "titanium.data.mybatis-plus", name = "encrypt.enabled", havingValue = "true")
    public IEncryptor encryptor(TitaniumMybatisPlusProperties encryptProperties) {
        return new DefaultEncryptor(encryptProperties.getEncrypt());
    }

    @Bean
    @ConditionalOnMissingBean(SqlSessionFactoryBean.class)
    public SqlSessionFactory sqlSessionFactory(TitaniumMybatisPlusProperties properties, MybatisProperties mybatisProperties, DataSource dataSource, IEncryptor encryptor) throws Exception {
        MybatisSqlSessionFactoryBean bean = SqlSessionFactoryBeanBuilder.getSqlSessionFactoryBean(mybatisProperties, dataSource);
        if (Objects.nonNull(encryptor)) {
            EncryptProperties encryptProperties = properties.getEncrypt();
            bean.setPlugins(
                    new EncryptQueryInterceptor(encryptProperties, encryptor),
                    new EncryptionResultInterceptor(encryptProperties, encryptor),
                    new EncryptUpdateInnerInterceptor(encryptProperties, encryptor)
            );
        }
        return bean.getObject();
    }

    /**
     * 自定义Id生成器
     * @return
     */
    @Bean
    public IdentifierGenerator idGenerator() {
        return new TitaniumIdentifierGenerator();
    }


}
