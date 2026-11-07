package com.yunshu.oa.common;

import lombok.Data;
import java.util.List;

/** 分页包装：{total, rows, page, size} */
@Data
public class PageResult<T> {
    private long total;
    private List<T> rows;
    private int page;
    private int size;

    public static <T> PageResult<T> of(long total, List<T> rows, int page, int size) {
        PageResult<T> p = new PageResult<>();
        p.total = total; p.rows = rows; p.page = page; p.size = size; return p;
    }
}
