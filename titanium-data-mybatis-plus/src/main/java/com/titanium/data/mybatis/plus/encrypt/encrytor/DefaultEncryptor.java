package com.titanium.data.mybatis.plus.encrypt.encrytor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.titanium.data.mybatis.plus.encrypt.properties.EncryptProperties;
import org.springframework.stereotype.Component;


/**
 * 默认加密器
 * 使用AES算法加密为16进制字符串
 */
public class DefaultEncryptor implements IEncryptor {
    private EncryptProperties encryptProperties;

    public DefaultEncryptor(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    /**
     * @param plainText
     * @return
     */
    @Override
    public String encrypt(String plainText) {
        String key = encryptProperties.getKey();
        switch (encryptProperties.getAlgorithm()) {
            case "AES":
                return SecureUtil.aes(key.getBytes()).encryptHex(plainText);
            default:
                // 默认使用AES加密
                return SecureUtil.aes(key.getBytes()).encryptHex(plainText);
        }
    }

    /**
     * @param cipherText
     * @return
     */
    @Override
    public String decrypt(String cipherText) {
        String key = encryptProperties.getKey();
        if (ObjectUtil.equals(encryptProperties.getAlgorithm(), SymmetricAlgorithm.AES.getValue())) {
            return SecureUtil.aes(key.getBytes()).decryptStr(cipherText);
        }
        // 默认使用AES
        return SecureUtil.aes(key.getBytes()).decryptStr(cipherText);
    }

    public static void main(String[] args) {
        String key = "b8dd183a8f6d38b1766f9a4e76c76e73";
        String plainText = "marco520";
        String encryptedStr = SecureUtil.aes(key.getBytes()).encryptHex(plainText);
        System.out.println(encryptedStr);
    }
}
