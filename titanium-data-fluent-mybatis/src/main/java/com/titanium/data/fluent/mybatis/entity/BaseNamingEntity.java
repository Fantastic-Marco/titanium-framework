package com.titanium.data.fluent.mybatis.entity;

import cn.org.atool.fluent.mybatis.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class BaseNamingEntity extends BaseEntity{
    /**
     * 创建人名称
     */
    @TableField("creator_name")
    private String creatorName;

    /**
     * 修改人名称
     */
    @TableField("reviser_name")
    private String reviserName;
}
