package com.titanium.common.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageReq implements Serializable {
    /**
     * 当前页码
     */
    private Integer page = 1;

    /**
     * 分页大小
     */
    private Integer size = 10;
}
