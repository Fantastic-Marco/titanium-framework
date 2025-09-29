package com.titanium.seata.tcc;

import io.seata.common.Constants;
import io.seata.core.context.RootContext;
import io.seata.core.model.BranchType;
import io.seata.integration.tx.api.interceptor.ActionInterceptorHandler;
import io.seata.integration.tx.api.interceptor.InvocationWrapper;
import io.seata.integration.tx.api.interceptor.SeataInterceptorPosition;
import io.seata.integration.tx.api.interceptor.TwoPhaseBusinessActionParam;
import io.seata.integration.tx.api.interceptor.handler.AbstractProxyInvocationHandler;
import io.seata.integration.tx.api.remoting.RemotingDesc;
import io.seata.rm.tcc.TCCResource;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TCC操作拦截器处理器
 * 用于拦截TCC操作方法，在全局事务上下文中处理TCC的Try阶段操作
 */
@Slf4j
public class TitaniumTccActionInterceptorHandler extends AbstractProxyInvocationHandler {
    /**
     * Seata操作拦截器处理器
     */
    private ActionInterceptorHandler actionInterceptorHandler = new ActionInterceptorHandler();

    /**
     * 需要被代理的方法集合
     */
    private Set<String> methodsToProxy;
    
    /**
     * 远程调用描述信息
     */
    private RemotingDesc remotingDesc;
    
    /**
     * TCC资源对象
     */
    private TCCResource tccResource;

    /**
     * 构造方法
     * @param remotingDesc 远程调用描述信息
     * @param methodsToProxy 需要被代理的方法集合
     * @param tccResource TCC资源对象
     */
    public TitaniumTccActionInterceptorHandler(RemotingDesc remotingDesc, Set<String> methodsToProxy, TCCResource tccResource) {
        this.methodsToProxy = methodsToProxy;
        this.remotingDesc = remotingDesc;
        this.tccResource = tccResource;
    }

    /**
     * 执行方法拦截调用
     * @param invocation 调用包装器
     * @return 方法执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Override
    protected Object doInvoke(InvocationWrapper invocation) throws Throwable {
        // 判断是否在全局事务中且不在Saga分支中
        if (!RootContext.inGlobalTransaction() || RootContext.inSagaBranch()) {
            // 不在事务中，或此拦截器被禁用，创建新的全局事务并开始
            GlobalTransaction defaultGlobalTransaction = GlobalTransactionContext.createNew();
            defaultGlobalTransaction.begin();
        }
        
        // 获取调用方法
        Method method = invocation.getMethod();

        // Try阶段方法处理
        // 保存XID
        String xid = RootContext.getXID();
        // 保存之前的分支类型
        BranchType previousBranchType = RootContext.getBranchType();
        // 如果不是TCC分支类型，则绑定为TCC分支类型
        if (BranchType.TCC != previousBranchType) {
            RootContext.bindBranchType(BranchType.TCC);
        }
        
        try {
            // 创建两阶段业务操作参数
            TwoPhaseBusinessActionParam businessActionParam = new TwoPhaseBusinessActionParam();
            businessActionParam.setActionName(tccResource.getActionName());
            businessActionParam.setDelayReport(false);
            businessActionParam.setUseCommonFence(false);
            businessActionParam.setBranchType(BranchType.TCC);
            
            // 创建业务操作上下文映射
            Map<String, Object> businessActionContextMap = new HashMap<>(4);
            // 设置提交方法名
            businessActionContextMap.put(Constants.COMMIT_METHOD, tccResource.getCommitMethodName());
            // 设置回滚方法名
            businessActionContextMap.put(Constants.ROLLBACK_METHOD, tccResource.getRollbackMethodName());
            // 设置操作名称
            businessActionContextMap.put(Constants.ACTION_NAME, tccResource.getActionName());
            // 设置是否使用通用隔离
            businessActionContextMap.put(Constants.USE_COMMON_FENCE, false);
            businessActionParam.setBusinessActionContext(businessActionContextMap);
            
            // 处理TCC切面并返回业务结果
            return actionInterceptorHandler.proceed(method, invocation.getArguments(), xid, businessActionParam,
                    invocation::proceed);
        } catch (Exception e) {
            log.error("TCC操作拦截处理异常", e);
            return invocation.proceed();
        } finally {
            // 如果之前不是TCC分支类型，则解绑分支类型
            if (BranchType.TCC != previousBranchType) {
                RootContext.unbindBranchType();
            }
            // 从MDC中移除分支ID
            MDC.remove(RootContext.MDC_KEY_BRANCH_ID);
        }
    }

    /**
     * 获取需要被代理的方法集合
     * @return 需要被代理的方法集合
     */
    @Override
    public Set<String> getMethodsToProxy() {
        return methodsToProxy;
    }

    /**
     * 获取拦截器位置
     * @return 拦截器位置，此处为任意位置
     */
    @Override
    public SeataInterceptorPosition getPosition() {
        return SeataInterceptorPosition.Any;
    }
}