package com.titanium.data.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@MappedSuperclass
@AllArgsConstructor
@SuppressWarnings("serial")
public class NamedBaseEntity extends BaseEntity {

    /**
     * 创建人名称
     */
    @Basic
    @Column(name = "creator_name")
    private String creatorName;

    /**
     * 修改人名称
     */
    @Basic
    @Column(name = "reviser_name")
    private String reviserName;

}
