package com.titanium.seata.tcc;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.titanium.seata.context.BusinessParamContext;
import io.seata.rm.tcc.TCCResource;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TitaniumTccResourcerHolder {
    private static Map<Object, TCCResource> caches = new ConcurrentHashMap<>();

    public static void put(Object key) {
        try {
            caches.put(key, getTCCResource(key));
        } catch (Exception e) {
            log.error("TCCResourceHolder put error", e);
        }
    }

    public static TCCResource get(Object key) {
        TCCResource tccResource = caches.get(key);
        Assert.notNull(tccResource, "TCCResourceHolder get error");
        return tccResource;
    }

    private static TCCResource getTCCResource(Object target) throws Exception {
        TCCResource tccResource = new TCCResource();
        String applicationName = SpringUtil.getApplicationName();
        String actionName = applicationName + "-" + target.getClass().getSimpleName();
        tccResource.setActionName(actionName);
        tccResource.setAppName(applicationName);
        tccResource.setTargetBean(target);
        tccResource.setPrepareMethod(target.getClass().getMethod("prepare", BusinessActionContext.class, BusinessParamContext.class));
        tccResource.setCommitMethodName("commit");
        tccResource.setCommitMethod(target.getClass().getMethod("commit", BusinessActionContext.class));
        tccResource.setRollbackMethodName("rollback");
        tccResource.setRollbackMethod(target.getClass().getMethod("rollback", BusinessActionContext.class));
        // set argsClasses
        Class[] argClasses = {BusinessActionContext.class};
        tccResource.setCommitArgsClasses(argClasses);
        tccResource.setRollbackArgsClasses(argClasses);
        // set phase two method's keys
        String[] keys = {};
        tccResource.setPhaseTwoCommitKeys(keys);
        tccResource.setPhaseTwoRollbackKeys(keys);
        return tccResource;
    }
}
