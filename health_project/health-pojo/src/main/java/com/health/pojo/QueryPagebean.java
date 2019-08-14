package com.health.pojo;

import java.io.Serializable;
import java.util.List;

public class QueryPagebean<T> implements Serializable {
    private Integer currentPage;//页码
    private Integer pageSize;//每页记录数
    private T queryString;//查询条件
    private List list;//id的数组

    public QueryPagebean(Integer currentPage, Integer pageSize, T queryString, List list) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.queryString = queryString;
        this.list = list;
    }


    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public T getQueryString() {
        return queryString;
    }

    public void setQueryString(T queryString) {
        this.queryString = queryString;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
