package com.health.pojo;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable{
    private Long total;//总页数
    private List rows;//当前页数据集

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
