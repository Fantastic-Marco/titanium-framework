package com.titanium.data.mybatis.plus.encrypt.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.titanium.data.mybatis.plus.encrypt.Encrypted;
import com.titanium.data.mybatis.plus.encrypt.encrytor.IEncryptor;
import com.titanium.data.mybatis.plus.encrypt.properties.EncryptProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

@Slf4j
@Intercepts({
        @Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class}),
})
public class EncryptUpdateInnerInterceptor extends EncryptionBaseInterceptor implements Interceptor {

    public EncryptUpdateInnerInterceptor(EncryptProperties encryptProp, IEncryptor encryptor) {
        super(encryptProp, encryptor);
    }


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        if (isInsert(mappedStatement)) {
            Object entity = args[1];
            if (entity instanceof Encrypted) {
                this.encrypt((Encrypted) entity);
            }
        }
        if (isUpdate(mappedStatement)) {
            Object entity = args[1];
            if (entity instanceof MapperMethod.ParamMap) {
                Object value = ((MapperMethod.ParamMap) entity).getOrDefault("param1", null);
                if (ObjectUtil.isNotNull(value) && value instanceof Encrypted) {
                    this.encrypt((Encrypted) value);
                }
            }
        }
        return invocation.proceed();
    }
}
