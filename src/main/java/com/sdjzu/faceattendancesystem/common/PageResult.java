package com.sdjzu.faceattendancesystem.common;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 分页响应结果
 */
@Data
@ToString
public class PageResult<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 每页数量
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据列表
     */
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public PageResult(Long total, List<T> records, Integer current, Integer size) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }
}
