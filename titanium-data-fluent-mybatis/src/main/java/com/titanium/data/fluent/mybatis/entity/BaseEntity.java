package com.titanium.data.fluent.mybatis.entity;

import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.base.IEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity implements IEntity {
    /**
     * 是否删除（0:否，1:是）
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建人Id
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Long createdTime;

    /**
     * 修改人Id
     */
    @TableField("reviser_id")
    private Long reviserId;

    /**
     * 修改时间
     */
    @TableField("revised_time")
    private Long revisedTime;
}
