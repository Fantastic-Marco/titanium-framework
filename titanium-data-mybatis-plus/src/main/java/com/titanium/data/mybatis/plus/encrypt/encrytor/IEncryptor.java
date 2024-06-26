package com.titanium.data.mybatis.plus.encrypt.encrytor;

/**
 * 加密器
 */
public interface IEncryptor {

    /**
     * 加密
     * @param plainText
     * @return
     */
    String encrypt(String plainText);

    /**
     * 解密
     * @param cipherText
     * @return
     */
    String decrypt(String cipherText);
}
