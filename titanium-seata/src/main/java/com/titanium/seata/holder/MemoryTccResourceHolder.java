package com.titanium.seata.holder;

import cn.hutool.core.util.ObjectUtil;
import com.titanium.seata.enums.TccStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存资源管理器
 */
@Slf4j
public class MemoryTccResourceHolder implements TccResourceHolder {
    private static ConcurrentHashMap<String, Integer> resourceMap = new ConcurrentHashMap<>();

    /**
     * 将资源标记为已占用
     * 如果资源已经被回滚了，则直接返回false
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public Boolean prepare(String xid, Long branchId) {
        return updateResource(xid, branchId, TccStatusEnum.TRYING, TccStatusEnum.INIT);
    }

    /**
     * 将资源标记为已提交
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public Boolean commit(String xid, Long branchId) {
        //判断当前资源状态是否为可提交
        return updateResource(xid, branchId, TccStatusEnum.CONFIRM, TccStatusEnum.TRYING);
    }

    /**
     * 将资源标记为已回滚
     * 业务实现方需要考虑空回滚问题
     * 也就是cancel需要判断状态，如果状态为空回滚，则什么也不做
     * 如果并非空回滚，才可以将状态设置为cancel
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public Boolean cancel(String xid, Long branchId) {
        return updateResource(xid, branchId, TccStatusEnum.CANCEL, TccStatusEnum.TRYING);
    }

    /**
     * 获取当前资源状态
     * @param xid
     * @param branchId
     * @see com.titanium.seata.enums.TccStatusEnum
     */
    @Override
    public Integer getStatus(String xid, Long branchId) {
        return getResourceStatus(xid, branchId);
    }

    /**
     * 删除资源
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public Boolean delete(String xid, Long branchId) {
        return deleteResource(xid, branchId);
    }

    /**
     * 添加资源
     * @param xid
     * @param branchId
     * @param statusEnum
     */
    private void addResource(String xid, Long branchId, TccStatusEnum statusEnum) {
        String key = xid + "-" + branchId;
        resourceMap.put(key, statusEnum.getCode());
    }

    /**
     * 添加资源，如果已经存在则不添加
     * @param xid
     * @param branchId
     * @param statusEnum
     */
    private synchronized boolean addResourceIfNoAbsent(String xid, Long branchId, TccStatusEnum statusEnum) {
        String key = xid + "-" + branchId;
        if (resourceMap.containsKey(key)) {
            return false;
        }
        resourceMap.put(key, statusEnum.getCode());
        return true;
    }

    /**
     * 更新资源状态
     * @param xid
     * @param branchId
     * @param statusEnum
     * @param oldStatusEnum
     * @return
     */
    private synchronized boolean updateResource(String xid, Long branchId, TccStatusEnum statusEnum, TccStatusEnum oldStatusEnum) {
        String key = xid + "-" + branchId;
        if (ObjectUtil.equals(resourceMap.get(key), oldStatusEnum.getCode())) {
            resourceMap.put(key, statusEnum.getCode());
            return true;
        }
        return false;
    }

    /**
     * 更新资源状态
     * @param xid
     * @param branchId
     * @param statusEnum
     * @param expectedStatusEnum
     * @return
     */
    private synchronized boolean updateResource(String xid, Long branchId, TccStatusEnum statusEnum, Set<TccStatusEnum> expectedStatusEnum) {
        String key = xid + "-" + branchId;
        Set<Integer> expectStatusCodes = expectedStatusEnum.stream().map(TccStatusEnum::getCode).collect(Collectors.toSet());
        if (expectStatusCodes.contains(resourceMap.get(key))) {
            resourceMap.put(key, statusEnum.getCode());
            return true;
        }
        return false;
    }

    /**
     * 获取资源状态
     */
    private Integer getResourceStatus(String xid, Long branchId) {
        String key = xid + "-" + branchId;
        return resourceMap.getOrDefault(key, TccStatusEnum.INIT.getCode());
    }

    /**
     * 删除资源
     * 删除资源时，需要判断是否允许删除
     * 只有提交状态和回滚状态才能删除
     * @param xid
     * @param branchId
     * @return
     */
    private synchronized boolean deleteResource(String xid, Long branchId) {
        try {
            String key = xid + "-" + branchId;
            if (ObjectUtil.equals(resourceMap.get(key), TccStatusEnum.allowDeleteStatus())) {
                resourceMap.remove(key);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
