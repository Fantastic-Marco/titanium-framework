package com.titanium.seata.holder;

/**
 * TCC 资源持有者
 * 用于管理分布式事务中
 * @see io.seata.core.model.BranchStatus
 */
public interface TccResourceHolder {

    /**
     * 将资源标记为已占用
     * @param xid
     * @return
     */
    Boolean prepare(String xid, Long branchId);

    /**
     * 将资源标记为已提交
     * @param xid
     * @return
     */
    Boolean commit(String xid, Long branchId);

    /**
     * 将资源标记为已回滚
     * @param xid
     * @return
     */
    Boolean cancel(String xid, Long branchId);

    /**
     * 获取当前资源状态
     * @see com.titanium.seata.enums.TccStatusEnum
     */
    Integer getStatus(String xid, Long branchId);

    /**
     * 删除资源
     * @param xid
     * @return
     */
    Boolean delete(String xid, Long branchId);
}
