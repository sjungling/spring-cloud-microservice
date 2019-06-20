package com.microweb.page;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private Long total;

    private int totalPage;

    private List<T> rows;

    public PageResult() {

    }

    public PageResult(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public PageResult(Long total, int totalPage, List<T> rows) {
        this.total = total;
        this.totalPage = totalPage;
        this.rows = rows;
    }
}