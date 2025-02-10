package com.titanium.seata.tcc;

import com.titanium.seata.action.AbstractTccAction;
import io.seata.integration.tx.api.interceptor.parser.RegisterResourceParser;
import io.seata.rm.DefaultResourceManager;
import io.seata.rm.tcc.TCCResource;
import lombok.extern.slf4j.Slf4j;

/**
 * SPI 扩展
 * io.seata.integration.tx.api.interceptor.parser.RegisterResourceParser
 * 注册RM信息到seata-server
 */
@Slf4j
public class TitaniumTccRegisterResourceParser implements RegisterResourceParser {

    @Override
    public void registerResource(Object target, String beanName) {
        if (target instanceof AbstractTccAction) {
            if (TitaniumScannerChecker.check(target)) {
                registerResource(target);
            }
        }
    }

    private void registerResource(Object target) {
        TCCResource tccResource = TitaniumTccResourceHolder.get(target);
        //registry tcc resource
        DefaultResourceManager.get().registerResource(tccResource);
        log.info("register action {} to the resource manager", tccResource.getActionName());
    }

}