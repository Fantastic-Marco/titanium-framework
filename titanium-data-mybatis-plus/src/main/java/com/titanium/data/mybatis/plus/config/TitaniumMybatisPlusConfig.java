package com.titanium.data.mybatis.plus.config;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecoratorProperties;
import com.github.gavlyukovskiy.boot.jdbc.decorator.p6spy.P6SpyConfiguration;
import com.github.gavlyukovskiy.boot.jdbc.decorator.p6spy.P6SpyProperties;
import com.p6spy.engine.spy.P6SpyDriver;
import com.titanium.data.mybatis.plus.encrypt.encrytor.DefaultEncryptor;
import com.titanium.data.mybatis.plus.encrypt.encrytor.IEncryptor;
import com.titanium.data.mybatis.plus.encrypt.interceptor.EncryptQueryInterceptor;
import com.titanium.data.mybatis.plus.encrypt.interceptor.EncryptUpdateInnerInterceptor;
import com.titanium.data.mybatis.plus.encrypt.interceptor.EncryptionResultInterceptor;
import com.titanium.data.mybatis.plus.encrypt.properties.EncryptProperties;
import com.titanium.data.mybatis.plus.log.TitaniumSqlStdoutLogger;
import com.titanium.data.mybatis.plus.repository.AdvancedSqlInjector;
import com.titanium.data.mybatis.plus.tenant.TenantInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Configuration
@EnableConfigurationProperties({TitaniumMybatisPlusProperties.class, DataSourceProperties.class, DataSourceDecoratorProperties.class})
public class TitaniumMybatisPlusConfig {
    @Resource
    private TitaniumMybatisPlusProperties properties;
    @Autowired(required = false)
    private DataSourceDecoratorProperties dataSourceDecoratorProperties;

    /**
     * 初始化sql打印
     */
    @PostConstruct
    public void initSqlPrint() {
        dataSourceDecoratorProperties = Optional.ofNullable(dataSourceDecoratorProperties).orElseGet(() -> new DataSourceDecoratorProperties());
        dataSourceDecoratorProperties.setEnabled(properties.isSqlLogEnabled());
        P6SpyProperties p6SpyProperties = new P6SpyProperties();
        p6SpyProperties.setEnableLogging(properties.isSqlLogEnabled());
        p6SpyProperties.setLogFormat("execute time:%(executionTime) || sql:%(sql)");
        p6SpyProperties.setLogging(P6SpyProperties.P6SpyLogging.CUSTOM);
        p6SpyProperties.setCustomAppenderClass(TitaniumSqlStdoutLogger.class.getName());
        dataSourceDecoratorProperties.setP6spy(p6SpyProperties);
    }

    /**
     * 数据源
     * @param datasourceProperties
     * @return
     * @see com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecoratorAutoConfiguration
     */
    @Bean
    @ConditionalOnClass(DataSource.class)
    public DataSource dataSource(DataSourceProperties datasourceProperties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(datasourceProperties.getUrl());
        dataSource.setDriverClassName(datasourceProperties.getDriverClassName());
        dataSource.setUsername(datasourceProperties.getUsername());
        dataSource.setPassword(datasourceProperties.getPassword());
        return dataSource;
    }

    /**
     * 默认 SqlsessionFactory
     * 使用MybatisPlus的SqlSessionFactory
     * @param mybatisProperties
     * @param dataSource
     * @param encryptor
     * @return
     * @throws Exception
     */
    @Bean
    @ConditionalOnMissingBean(SqlSessionFactoryBean.class)
    public SqlSessionFactory sqlSessionFactory(
            MybatisProperties mybatisProperties,
            DataSource dataSource,
            @Autowired(required = false) IEncryptor encryptor
    ) throws Exception {
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
     * 默认加密器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "titanium.data.mybatis-plus.encrypt", name = "enabled", havingValue = "true", matchIfMissing = false)
    public IEncryptor encryptor() {
        return new DefaultEncryptor(properties.getEncrypt());
    }

    /**
     * 自定义Id生成器
     * @return
     */
    @Bean
    public IdentifierGenerator idGenerator() {
        return new TitaniumIdentifierGenerator();
    }

    /**
     * 插件管理
     * 乐观锁拦截器
     * 防止全表删除拦截器
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //乐观锁插件
        if (properties.isOptimisticLockerEnabled()) {
            interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        }
        //防止全表更新插件
        if (properties.isBlockAttackEnabled()) {
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        }
        //租户拦截插件
        if (ObjectUtil.isNotNull(properties.getTenant()) && properties.getTenant().isEnabled()) {
            TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
            tenantInterceptor.setTenantLineHandler(new TenantInnerInterceptor(properties.getTenant()));
        }
        return interceptor;
    }

    public static void main(String[] args) {
        System.out.println(P6SpyDriver.class.getName());
    }


}
