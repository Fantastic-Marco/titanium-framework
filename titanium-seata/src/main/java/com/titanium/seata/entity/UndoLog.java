package com.titanium.seata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndoLog {
    /**
     * id
     */
    private Long id;

    /**
     * 分布式事务全局唯一ID
     */
    private String xid;

    /**
     * 事务的分支ID
     */
    private Long branchId;

    /**
     * 内容
     */
    private String context;

    /**
     * 回滚信息
     */
    private String rollbackInfo;

    /**
     * 状态
     */
    private Integer logStatus;

    /**
     * 创建时间
     */
    private LocalDateTime logCreated;

    /**
     * 更新时间
     */
    private LocalDateTime logModified;

}
