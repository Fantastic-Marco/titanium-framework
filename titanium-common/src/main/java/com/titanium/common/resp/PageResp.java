package com.titanium.common.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResp<T> implements Serializable {
    /**
     * 总数
     */
    private Long total;

    /**
     * 当前页数
     */
    private Integer page;

    /**
     * 分页列表
     */
    private List<T> list;
}
