package com.titanium.seata.tcc;

import io.seata.common.exception.FrameworkException;
import io.seata.integration.tx.api.remoting.Protocols;
import io.seata.integration.tx.api.remoting.RemotingDesc;
import io.seata.integration.tx.api.remoting.parser.AbstractedRemotingParser;
import org.springframework.aop.framework.AopProxyUtils;

/**
 * 本地TCC远程调用解析器
 * 用于解析本地TCC服务，判断Bean是否为TCC服务或引用，并获取相应的远程调用描述信息
 */
public class TitaniumLocalTCCRemotingParser extends AbstractedRemotingParser {
    /**
     * 判断是否为引用Bean
     * @param bean     Bean对象
     * @param beanName Bean名称
     * @return true=是引用Bean，false=不是引用Bean
     * @throws FrameworkException 框架异常
     */
    @Override
    public boolean isReference(Object bean, String beanName) throws FrameworkException {
        return TitaniumScannerChecker.check(bean);
    }

    /**
     * 判断是否为服务Bean
     * @param bean     Bean对象
     * @param beanName Bean名称
     * @return true=是服务Bean，false=不是服务Bean
     * @throws FrameworkException 框架异常
     */
    @Override
    public boolean isService(Object bean, String beanName) throws FrameworkException {
        return TitaniumScannerChecker.check(bean);
    }

    /**
     * 获取远程调用Bean的信息
     * @param bean     Bean对象
     * @param beanName Bean名称
     * @return 远程调用描述信息
     * @throws FrameworkException 框架异常
     */
    @Override
    public RemotingDesc getServiceDesc(Object bean, String beanName) throws FrameworkException {
        // 判断是否为远程调用Bean
        if (!this.isRemoting(bean, beanName)) {
            return null;
        }
        
        // 创建远程调用描述对象
        RemotingDesc remotingDesc = new RemotingDesc();
        
        // 设置是否为引用
        remotingDesc.setReference(this.isReference(bean, beanName));
        
        // 设置是否为服务
        remotingDesc.setService(this.isService(bean, beanName));
        
        // 设置协议类型为JVM内部调用
        remotingDesc.setProtocol(Protocols.IN_JVM);
        
        // 设置服务类
        remotingDesc.setServiceClass(AopProxyUtils.ultimateTargetClass(bean));
        
        // 设置服务类名
        remotingDesc.setServiceClassName(remotingDesc.getServiceClass().getName());
        
        // 设置目标Bean
        remotingDesc.setTargetBean(bean);
        
        return remotingDesc;
    }

    /**
     * 获取远程调用协议类型
     * @return 协议类型
     */
    @Override
    public short getProtocol() {
        // 返回JVM内部调用协议
        return Protocols.IN_JVM;
    }
}