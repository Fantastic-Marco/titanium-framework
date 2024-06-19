package com.titanium.data.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity{
    /**
     * 创建人Id
     */
    @TableField(value = "creator_id", jdbcType = JdbcType.BIGINT, fill = FieldFill.INSERT)
    private Long creatorId;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", jdbcType = JdbcType.TIME, fill = FieldFill.INSERT)
    private Long createdTime;

    /**
     * 更新人Id
     */
    @TableField(value = "reviser_id", jdbcType = JdbcType.BIGINT, fill = FieldFill.UPDATE)
    private Long reviserId;

    /**
     * 更新时间
     */
    @TableField(value = "revised_time", jdbcType = JdbcType.TIME, fill = FieldFill.UPDATE)
    private Long revisedTime;

    /**
     * 是否删除 0-未删除，1-已删除
     */
    @TableLogic
    @TableField(value = "deleted", jdbcType = JdbcType.TINYINT, fill = FieldFill.INSERT)
    private Integer deleted;
}
