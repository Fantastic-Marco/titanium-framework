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

@Slf4j
public class TitaniumTccActionInterceptorHandler extends AbstractProxyInvocationHandler {
    private ActionInterceptorHandler actionInterceptorHandler = new ActionInterceptorHandler();

    private Set<String> methodsToProxy;
    private RemotingDesc remotingDesc;
    private TCCResource tccResource;

    public TitaniumTccActionInterceptorHandler(RemotingDesc remotingDesc, Set<String> methodsToProxy, TCCResource tccResource) {
        this.methodsToProxy = methodsToProxy;
        this.remotingDesc = remotingDesc;
        this.tccResource = tccResource;
    }

    @Override
    protected Object doInvoke(InvocationWrapper invocation) throws Throwable {
        if (!RootContext.inGlobalTransaction() || RootContext.inSagaBranch()) {
            //not in transaction, or this interceptor is disabled
            GlobalTransaction defaultGlobalTransaction = GlobalTransactionContext.createNew();
            defaultGlobalTransaction.begin();
        }
        Method method = invocation.getMethod();

        //try method
        //save the xid
        String xid = RootContext.getXID();
        //save the previous branchType
        BranchType previousBranchType = RootContext.getBranchType();
        //if not TCC, bind TCC branchType
        if (BranchType.TCC != previousBranchType) {
            RootContext.bindBranchType(BranchType.TCC);
        }
        try {
            TwoPhaseBusinessActionParam businessActionParam = new TwoPhaseBusinessActionParam();
            businessActionParam.setActionName(tccResource.getActionName());
            businessActionParam.setDelayReport(false);
            businessActionParam.setUseCommonFence(false);
            businessActionParam.setBranchType(BranchType.TCC);
            Map<String, Object> businessActionContextMap = new HashMap<>(4);
            //the phase two method name
            businessActionContextMap.put(Constants.COMMIT_METHOD, tccResource.getCommitMethodName());
            businessActionContextMap.put(Constants.ROLLBACK_METHOD, tccResource.getRollbackMethodName());
            businessActionContextMap.put(Constants.ACTION_NAME, tccResource.getActionName());
            businessActionContextMap.put(Constants.USE_COMMON_FENCE, false);
            businessActionParam.setBusinessActionContext(businessActionContextMap);
            //Handler the TCC Aspect, and return the business result
            return actionInterceptorHandler.proceed(method, invocation.getArguments(), xid, businessActionParam,
                    invocation::proceed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return invocation.proceed();
        } finally {
            //if not TCC, unbind branchType
            if (BranchType.TCC != previousBranchType) {
                RootContext.unbindBranchType();
            }
            //MDC remove branchId
            MDC.remove(RootContext.MDC_KEY_BRANCH_ID);
        }
    }

    @Override
    public Set<String> getMethodsToProxy() {
        return methodsToProxy;
    }

    @Override
    public SeataInterceptorPosition getPosition() {
        return SeataInterceptorPosition.Any;
    }
}
