package com.iori.util;

import java.util.List;

public class PageUtil<T> {
    private Integer current;
    private Integer size;
    private Integer count;
    private Integer total;
    private List<T> data;

    public PageUtil() {
    }

    public PageUtil(Integer current, Integer size, Integer count, Integer total, List<T> data) {
        this.current = current;
        this.size = size;
        this.count = count;
        this.total = total;
        this.data = data;
    }



    @Override
    public String toString() {
        return "PageUtil{" +
                "current=" + current +
                ", size=" + size +
                ", count=" + count +
                ", total=" + total +
                ", data=" + data +
                '}';
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
