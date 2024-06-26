package com.titanium.data.mybatis.plus.encrypt.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptProperties {
    /**
     * 是否开启加密
     */
    private boolean enabled;

    /**
     * 加密key
     */
    private String key;

    /**
     * 加密算法
     * @see cn.hutool.crypto.symmetric.SymmetricAlgorithm
     */
    private String algorithm;
}
