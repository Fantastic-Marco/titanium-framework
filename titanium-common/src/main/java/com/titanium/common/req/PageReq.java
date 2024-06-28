package com.titanium.common.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "当前页码不能为空")
    @Min(value = 1, message = "当前页码不能小于1")
    private Integer page = 1;

    /**
     * 分页大小
     */
    @NotNull(message = "分页大小不能为空")
    @Min(value = 1, message = "分页大小不能小于1")
    private Integer size = 10;
}
