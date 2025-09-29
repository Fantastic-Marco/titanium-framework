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

/**
 * 框架自定义的接口解析器
 * 用于解析TCC操作接口并创建相应的代理处理器
 */
@Slf4j
public class TitaniumInterfaceParser implements InterfaceParser {

    /**
     * 将目标对象解析为代理对象
     * @param target 目标对象
     * @param objectName 对象名称
     * @return 代理调用处理器，如果不需要代理则返回null
     */
    @Override
    public ProxyInvocationHandler parserInterfaceToProxy(Object target, String objectName) {
        try {
            // 检查目标对象是否为TCC操作类的实例
            if (target instanceof AbstractTccAction) {
                // 检查目标对象是否需要被处理
                if (!TitaniumScannerChecker.check(target)) {
                    return null;
                }

                // 定义需要被代理的方法集合，这里主要是TCC的prepare方法
                Set<String> methodsToProxy = Sets.newHashSet("prepare");
                
                // 解析远程服务信息
                TxBeanParserUtils.parserRemotingServiceInfo(target, objectName);
                
                // 获取远程服务描述信息
                RemotingDesc remotingDesc = DefaultRemotingParser.get().getRemotingBeanDesc(target);
                
                if (remotingDesc != null) {
                    // 注册资源 @See TitaniumTccRegisterResourceParser
                    if (remotingDesc.isService()) {
                        // 如果是服务提供方，则注册资源
                        DefaultResourceRegisterParser.get().registerResource(target, objectName);
                    }
                    if (remotingDesc.isReference()) {
                        // 如果是服务消费方，则创建TCC操作拦截器处理器
                        return new TitaniumTccActionInterceptorHandler(remotingDesc, methodsToProxy, TitaniumTccResourceHolder.get(target));
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析接口为代理对象时发生错误", e);
        }
        return null;
    }


}