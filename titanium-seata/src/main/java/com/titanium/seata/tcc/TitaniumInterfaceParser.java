package com.titanium.seata.tcc;

import com.google.common.collect.Sets;
import com.titanium.seata.action.AbstractTccAction;
import io.seata.integration.tx.api.interceptor.TxBeanParserUtils;
import io.seata.integration.tx.api.interceptor.handler.ProxyInvocationHandler;
import io.seata.integration.tx.api.interceptor.parser.DefaultResourceRegisterParser;
import io.seata.integration.tx.api.interceptor.parser.InterfaceParser;
import io.seata.integration.tx.api.remoting.RemotingDesc;
import io.seata.integration.tx.api.remoting.parser.DefaultRemotingParser;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class TitaniumInterfaceParser implements InterfaceParser {

    @Override
    public ProxyInvocationHandler parserInterfaceToProxy(Object target, String objectName) {
        try {
            if (target instanceof AbstractTccAction) {
                if (!TitaniumScannerChecker.check(target)) {
                    return null;
                }

                Set<String> methodsToProxy = Sets.newHashSet("prepare");
                TxBeanParserUtils.parserRemotingServiceInfo(target, objectName);
                RemotingDesc remotingDesc = DefaultRemotingParser.get().getRemotingBeanDesc(target);
                if (remotingDesc != null) {
                    // 注册资源 @See TitaniumTccRegisterResourceParser
                    if (remotingDesc.isService()) {
                        DefaultResourceRegisterParser.get().registerResource(target, objectName);
                    }
                    if (remotingDesc.isReference()) {
                        return new TitaniumTccActionInterceptorHandler(remotingDesc, methodsToProxy, TitaniumTccResourcerHolder.get(target));
                    }
                }
            }
        } catch (Exception e) {
            log.error("parser interface to proxy error", e);
        }
        return null;
    }


}
