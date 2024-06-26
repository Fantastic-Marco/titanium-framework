package com.titanium.data.mybatis.plus.encrypt.interceptor;

import cn.hutool.core.util.StrUtil;
import com.titanium.data.mybatis.plus.encrypt.Encrypted;
import com.titanium.data.mybatis.plus.encrypt.annotation.Encrypt;
import com.titanium.data.mybatis.plus.encrypt.encrytor.IEncryptor;
import com.titanium.data.mybatis.plus.encrypt.properties.EncryptProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class EncryptionBaseInterceptor {
    /**
     * 加密类
     */
    private final IEncryptor encryptor;

    private EncryptProperties encryptProperties;

    public EncryptionBaseInterceptor(EncryptProperties encryptProp, IEncryptor encryptor) {
        this.encryptProperties = encryptProp;
        this.encryptor = encryptor;
    }

    public String encrypt(String str) {
        if (!encryptProperties.isEnabled()) {
            return str;
        }
        return encryptor.encrypt(str);
    }

    public String decrypt(String str) {
        if (!encryptProperties.isEnabled()) {
            return str;
        }
        return encryptor.decrypt(str);
    }

    public void encrypt(Field field, Object object) throws IllegalAccessException {
        String fieldStr = getFieldStr(field, object);
        if (fieldStr != null) {
            field.setAccessible(true);
            field.set(object, encrypt(fieldStr));
        }
    }

    public void decrypt(Field field, Object object) throws IllegalAccessException {
        String fieldStr = getFieldStr(field, object);
        if (fieldStr != null) {
            field.setAccessible(true);
            field.set(object, decrypt(fieldStr));
        }
    }

    public void encrypt(Encrypted object) throws IllegalAccessException {
        for (Field field : object.getEncryptFields()) {
            if (isEncryptField(field, object)) {
                this.encrypt(field, object);
            }
        }
    }

    public void decrypt(Encrypted object) throws IllegalAccessException {
        for (Field field : object.getEncryptFields()) {
            if (isEncryptField(field, object)) {
                this.decrypt(field, object);
            }
        }
    }


    public String getFieldStr(Field field, Object object) throws IllegalAccessException {
        Object val = field.get(object);
        if (val == null || StrUtil.isBlank(val.toString())) {
            return null;
        }
        return val.toString();
    }


    public static boolean isEncryptField(Field field) {
        return field.isAnnotationPresent(Encrypt.class);
    }

    public static boolean isEncryptField(Field field, Object obj) throws IllegalAccessException {
        return isEncryptField(field) && field.get(obj) instanceof String;
    }

    public static boolean isInsertOrUpdate(MappedStatement mappedStatement) {
        SqlCommandType type = mappedStatement.getSqlCommandType();
        return SqlCommandType.UPDATE == type || SqlCommandType.INSERT == type;
    }

    public static boolean isInsert(MappedStatement mappedStatement) {
        SqlCommandType type = mappedStatement.getSqlCommandType();
        return SqlCommandType.INSERT == type;
    }

    public static boolean isUpdate(MappedStatement mappedStatement) {
        SqlCommandType type = mappedStatement.getSqlCommandType();
        return SqlCommandType.UPDATE == type;
    }

    public static boolean isSelect(MappedStatement mappedStatement) {
        SqlCommandType type = mappedStatement.getSqlCommandType();
        return SqlCommandType.SELECT == type;
    }

    public String getMapperClassPath(MappedStatement mappedStatement) {
        String classPath = mappedStatement.getId();
        return classPath.substring(0, classPath.lastIndexOf("."));
    }

    public Class<?> getEncryptClass(MappedStatement mappedStatement) {
        String id = mappedStatement.getId();
        // 假设 id 格式为 "com.example package.EntityMapper.insertOrUpdateEntity"
        int end = id.indexOf("Mapper") + 6;
        if (end > 0) {
            String entityClassName = id.substring(0, end);
            try {
                Class<?> mapperClass = Class.forName(entityClassName);
                if (mapperClass != null) {
                    var superInterfaces = mapperClass.getGenericInterfaces();
                    if (superInterfaces != null && superInterfaces.length > 0) {
                        for (var mapperInterface : superInterfaces) {
                            Type[] actualTypeArguments = ((ParameterizedType)mapperInterface).getActualTypeArguments();
                            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                                var entityClass = (Class<?>) actualTypeArguments[0];
                                if (entityClass != null) {
                                    Type[] genericInterfaces = entityClass.getGenericInterfaces();
                                    if (genericInterfaces != null && genericInterfaces.length > 0) {
                                        for (Type genericInterface : genericInterfaces) {
                                            if (genericInterface instanceof ParameterizedType) {
                                                ParameterizedType entityInterface = (ParameterizedType) genericInterface;
                                                if (entityInterface.getRawType() == Encrypted.class) {
                                                    return entityClass;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return null;
            } catch (Exception e) {
                log.error("Failed to load entity class: {}", entityClassName, e);
                return null;
            }
        } else {
            log.warn("Invalid MappedStatement ID format. Could not extract entity class name: {}", id);
            return null;
        }
    }

    public Set<String> encryptFieldSet(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Encrypt.class))
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

}
