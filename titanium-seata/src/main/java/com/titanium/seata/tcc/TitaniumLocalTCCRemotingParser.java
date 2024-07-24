package com.titanium.seata.tcc;

import io.seata.common.exception.FrameworkException;
import io.seata.integration.tx.api.remoting.Protocols;
import io.seata.integration.tx.api.remoting.RemotingDesc;
import io.seata.integration.tx.api.remoting.parser.AbstractedRemotingParser;
import org.springframework.aop.framework.AopProxyUtils;

public class TitaniumLocalTCCRemotingParser extends AbstractedRemotingParser {
    /**
     * if it is reference bean ?
     * @param bean     the bean
     * @param beanName the bean name
     * @return boolean boolean
     * @throws FrameworkException the framework exception
     */
    @Override
    public boolean isReference(Object bean, String beanName) throws FrameworkException {
        return TitaniumScannerChecker.check(bean);
    }

    /**
     * if it is service bean ?
     * @param bean     the bean
     * @param beanName the bean name
     * @return boolean boolean
     * @throws FrameworkException the framework exception
     */
    @Override
    public boolean isService(Object bean, String beanName) throws FrameworkException {
        return TitaniumScannerChecker.check(bean);
    }

    /**
     * get the remoting bean info
     * @param bean     the bean
     * @param beanName the bean name
     * @return service desc
     * @throws FrameworkException the framework exception
     */
    @Override
    public RemotingDesc getServiceDesc(Object bean, String beanName) throws FrameworkException {
        if (!this.isRemoting(bean, beanName)) {
            return null;
        }
        RemotingDesc remotingDesc = new RemotingDesc();
        remotingDesc.setReference(this.isReference(bean, beanName));
        remotingDesc.setService(this.isService(bean, beanName));
        remotingDesc.setProtocol(Protocols.IN_JVM);
        remotingDesc.setServiceClass(AopProxyUtils.ultimateTargetClass(bean));
        remotingDesc.setServiceClassName(remotingDesc.getServiceClass().getName());
        remotingDesc.setTargetBean(bean);
        return remotingDesc;
    }

    /**
     * the remoting protocol
     * @return protocol
     */
    @Override
    public short getProtocol() {
        return Protocols.IN_JVM;
    }
}
