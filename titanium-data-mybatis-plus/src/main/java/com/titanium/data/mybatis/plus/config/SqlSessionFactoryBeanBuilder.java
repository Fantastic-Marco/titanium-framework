package com.titanium.data.mybatis.plus.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.titanium.data.mybatis.plus.autofill.TitaniumMetaObjectHandler;
import com.titanium.data.mybatis.plus.repository.AdvancedSqlInjector;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自定义SqlSessionFactoryBean构建器
 * @see com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration#sqlSessionFactory
 */
@Slf4j
public class SqlSessionFactoryBeanBuilder {
    public static MybatisSqlSessionFactoryBean getSqlSessionFactoryBean(
            MybatisProperties mybatisProperties,
            DataSource dataSource
    ) {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        bean.setTypeHandlersPackage(mybatisProperties.getTypeHandlersPackage());

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(false);
        configuration.setAggressiveLazyLoading(true);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setUseColumnLabel(true);
        configuration.setUseGeneratedKeys(true);
        configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
        configuration.setDefaultStatementTimeout(30000);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(JdbcType.OTHER);
        configuration.setLocalCacheScope(LocalCacheScope.SESSION);
        configuration.setSafeRowBoundsEnabled(true);
        bean.setConfiguration(configuration);

        GlobalConfig globalConfig = new GlobalConfig();
        //设置自动填充策略
        globalConfig.setMetaObjectHandler(new TitaniumMetaObjectHandler());
        //Sql注入器
        globalConfig.setSqlInjector(new AdvancedSqlInjector());
        bean.setGlobalConfig(globalConfig);
        PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        if (mybatisProperties.getMapperLocations() != null && mybatisProperties.getMapperLocations().length > 0) {
            List<Resource> resources = new ArrayList<>();

            for (int i = 0; i < mybatisProperties.getMapperLocations().length; ++i) {
                try {
                    Collections.addAll(resources, resourceResolver.getResources(mybatisProperties.getMapperLocations()[i]));
                } catch (IOException var8) {
                    log.warn("创建SqlSessionFactoryBean过程读取资源异常", var8);
                }
            }

            Resource[] resourcesArr = new Resource[resources.size()];
            bean.setMapperLocations(resources.toArray(resourcesArr));
        }
        return bean;
    }
}
