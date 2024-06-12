package com.titanium.data.fluent.mybatis.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "titanium.data.fluent.mybatis")
public class TitaniumFluentMybatisProperties {
    private String basePackage;
    private String mapperLocations;
}
