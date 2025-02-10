package com.titanium.seata.action;

import cn.hutool.core.util.ObjectUtil;
import com.titanium.common.user.UserContext;
import com.titanium.common.user.UserContextHolder;
import com.titanium.json.Json;
import com.titanium.seata.constants.TccConstants;
import com.titanium.seata.context.BusinessParamContext;
import com.titanium.seata.enums.TccStatusEnum;
import com.titanium.seata.holder.TccResourceHolder;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.interceptor.TccActionInterceptorHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTccAction {
    private static String USER_CONTEXT_KEY = "user_context";
    private TccResourceHolder holder;

    public AbstractTccAction(TccResourceHolder holder) {
        this.holder = holder;
    }

    /**
     * 准备
     * 锁定资源
     */
    public abstract boolean doPrepare(BusinessActionContext context, BusinessParamContext paramContext);

    /**
     * 确认
     */
    public abstract boolean doCommit(BusinessActionContext context);

    /**
     * 取消
     * 已处理空回滚
     */
    public abstract boolean doRollback(BusinessActionContext context);

    /**
     * 准备阶段
     * 自动设置用户信息，如果存在的话
     * 需要处理资源悬挂
     * 该方法命名需要和 @see{com.titanium.seata.constants.TccConstants.PREPARE_METHOD} 一致
     * @see TccActionInterceptorHandler
     * #doInvoke(InvocationWrapper invocation)
     */
    public boolean prepare(BusinessActionContext context, BusinessParamContext paramContext) {
        String xid = context.getXid();
        Long branchId = context.getBranchId();
        Integer status = holder.getStatus(xid, branchId);
        if (ObjectUtil.notEqual(status, TccStatusEnum.INIT.getCode())) {
            log.info("xid {} branchId {} already exec, status {}", xid, branchId, status);
            return false;
        }
        log.info(" xid: {} branchId {} prepare, param: {}", xid, branchId, Json.serialize(paramContext));
        try {
            boolean prepared = doPrepare(context, paramContext);
            if (prepared) {
                //添加用户信息
                if (UserContextHolder.get() != null) {
                    context.addActionContext(TccConstants.USER_CONTEXT_KEY, Json.serialize(UserContextHolder.get()));
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
        if (context.getActionContext(TccConstants.USER_CONTEXT_KEY) == null) {
            return null;
        }
        String userContextStr = (String) context.getActionContext(TccConstants.USER_CONTEXT_KEY);
        // 获取用户信息
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
     * 空回滚处理
     * @param context
     * @return
     */
    public boolean rollback(BusinessActionContext context) {
        String xid = context.getXid();
        Long branchId = context.getBranchId();
        Integer status = holder.getStatus(xid, branchId);
        // 解决空回滚
        if (ObjectUtil.equals(status, TccStatusEnum.INIT.getCode())) {
            log.info("xid {} branchId {} has not prepare, status {},exec empty rollback", xid, branchId, status);
            return true;
        } else {
            log.info(" xid: {} branchId {} rollback, param: {}", xid, branchId, Json.serialize(context));
            boolean canceled = doRollback(context);
            if (canceled) {
                return holder.cancel(xid, branchId);
            }
            return canceled;
        }
    }
}
