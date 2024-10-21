package com.titanium.data.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
public class BaseEntity implements Serializable {
    /**
     * 创建人Id
     */
    @Basic
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    /**
     * 创建时间
     */
    @Basic
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    /**
     * 更新人Id
     */
    @Basic
    @Column(name = "reviser_id")
    private Long reviserId;

    /**
     * 更新时间
     */
    @Basic
    @Column(name = "revised_time")
    private LocalDateTime revisedTime;

    /**
     * 是否删除 0-未删除，1-已删除
     */
    @Basic
    @Column(name = "deleted", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Integer deleted;
}
