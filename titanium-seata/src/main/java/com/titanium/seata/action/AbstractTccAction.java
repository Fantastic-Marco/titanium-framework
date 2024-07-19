package com.titanium.seata.action;

import cn.hutool.core.util.ObjectUtil;
import com.titanium.common.user.UserContext;
import com.titanium.common.user.UserContextHolder;
import com.titanium.json.Json;
import com.titanium.seata.context.BusinessParamContext;
import com.titanium.seata.enums.TccStatusEnum;
import com.titanium.seata.holder.TccResourceHolder;
import io.seata.rm.DefaultResourceManager;
import io.seata.rm.tcc.TCCResource;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.resource.parser.TccRegisterResourceParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@Slf4j
public abstract class AbstractTccAction implements InitializingBean, EnvironmentAware {
    private static String USER_CONTEXT_KEY = "user_context";
    private Environment environment;
    private String applicationName;
    private TccResourceHolder holder;

    public AbstractTccAction(TccResourceHolder holder) {
        this.holder = holder;
    }

    /**
     * 准备
     * 锁定资源
     */
    public abstract boolean doTry(BusinessActionContext context, BusinessParamContext paramContext);

    /**
     * 确认
     */
    public abstract boolean doCommit(BusinessActionContext context);

    /**
     * 取消
     * 已处理空回滚
     */
    public abstract boolean doCancel(BusinessActionContext context);

    /**
     * 准备阶段
     * 自动设置用户信息，如果存在的话
     * 需要处理资源悬挂
     */
    public boolean prepare(BusinessActionContext context, BusinessParamContext paramContext) {
        String xid = context.getXid();
        Long branchId = context.getBranchId();
        Integer status = holder.getStatus(xid, branchId);
        if (ObjectUtil.equals(status, TccStatusEnum.INIT.getCode())) {
            log.info("xid {} branchId {} already exec, status {}", xid, branchId, status);
            return false;
        }
        log.info(" xid: {} branchId {} prepare, param: {}", xid, branchId, Json.serialize(paramContext));
        try {
            boolean prepared = doTry(context, paramContext);
            if (prepared) {
                //添加用户信息
                if (UserContextHolder.get() != null) {
                    context.addActionContext(USER_CONTEXT_KEY, Json.serialize(UserContextHolder.get()));
                }
                // 锁定资源
                return holder.prepare(xid, branchId);
            }
            return prepared;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 从全局事务上下文中获取用户信息
     * @param context
     * @return
     */
    public UserContext getUserContext(BusinessActionContext context) {
        if (context.getActionContext(USER_CONTEXT_KEY) == null) {
            return null;
        }
        String userContextStr = (String) context.getActionContext(USER_CONTEXT_KEY);
        return Json.deserialize(userContextStr, UserContext.class);
    }

    /**
     * 提交阶段
     * @param context
     * @return
     */
    public boolean commit(BusinessActionContext context) {
        String xid = context.getXid();
        Long branchId = context.getBranchId();
        Integer status = holder.getStatus(xid, branchId);
        if (!ObjectUtil.equals(status, TccStatusEnum.TRYING.getCode())) {
            log.info("xid {} branchId {} has not prepare, status {}", xid, branchId, status);
            return false;
        }
        try {
            log.info(" xid: {} branchId {} commit, param: {}", xid, branchId, Json.serialize(context));
            boolean prepared = doCommit(context);
            if (prepared) {
                return holder.commit(xid, branchId);
            }
            return prepared;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 回滚阶段
     * @param context
     * @return
     */
    public boolean cancel(BusinessActionContext context) {
        String xid = context.getXid();
        Long branchId = context.getBranchId();
        Integer status = holder.getStatus(xid, branchId);
        if (ObjectUtil.equals(status, TccStatusEnum.INIT.getCode())) {
            log.info("xid {} branchId {} has not prepare, status {},exec empty rollback", xid, branchId, status);
            return true;
        } else {
            log.info(" xid: {} branchId {} cancel, param: {}", xid, branchId, Json.serialize(context));
            boolean canceled = doCancel(context);
            if (canceled) {
                return holder.cancel(xid, branchId);
            }
            return canceled;
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.applicationName = environment.getProperty("spring.application.name");
    }

    /**
     * 注册分布式事务
     * @throws Exception
     * @see TccRegisterResourceParser 看官方是如何注册TCC资源的
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        TCCResource tccResource = new TCCResource();
        String actionName = applicationName + "-" + this.getClass().getSimpleName();
        tccResource.setActionName(actionName);
        tccResource.setTargetBean(this);
        tccResource.setPrepareMethod(this.getClass().getMethod("prepare"));
        tccResource.setCommitMethodName("commit");
        tccResource.setCommitMethod(this.getClass().getMethod("commit", BusinessActionContext.class));
        tccResource.setRollbackMethodName("cancel");
        tccResource.setRollbackMethod(this.getClass().getMethod("cancel", BusinessActionContext.class));
        // set argsClasses
        Class[] argClasses = {BusinessActionContext.class};
        tccResource.setCommitArgsClasses(argClasses);
        tccResource.setRollbackArgsClasses(argClasses);
        // set phase two method's keys
        String[] keys = {};
        tccResource.setPhaseTwoCommitKeys(keys);
        tccResource.setPhaseTwoRollbackKeys(keys);
        //registry tcc resource
        DefaultResourceManager.get().registerResource(tccResource);
        log.info("register action {} to the resource manager", actionName);
    }
}
