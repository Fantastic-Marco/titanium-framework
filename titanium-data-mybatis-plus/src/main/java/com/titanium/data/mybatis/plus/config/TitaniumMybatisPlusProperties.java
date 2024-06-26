package com.titanium.data.mybatis.plus.config;

import com.titanium.data.mybatis.plus.encrypt.properties.EncryptProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "titanium.data.mybatis-plus")
public class TitaniumMybatisPlusProperties {

    private EncryptProperties encrypt;
}

