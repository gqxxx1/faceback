package com.sdjzu.faceattendancesystem.common;

import lombok.Data;

/**
 * 分页请求参数
 */
@Data
public class PageRequest {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页数量
     */
    private Integer size = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向：asc/desc
     */
    private String sortOrder;
}
