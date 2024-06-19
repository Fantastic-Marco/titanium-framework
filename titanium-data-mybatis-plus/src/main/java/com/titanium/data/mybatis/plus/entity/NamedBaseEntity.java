package com.titanium.data.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.JdbcType;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class NamedBaseEntity extends BaseEntity {

    /**
     * 创建人名称
     */
    @TableField(value = "creator_name", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT)
    private String creatorName;

    /**
     * 修改人名称
     */
    @TableField(value = "reviser_name", jdbcType = JdbcType.VARCHAR, fill = FieldFill.UPDATE)
    private String reviserName;

}
