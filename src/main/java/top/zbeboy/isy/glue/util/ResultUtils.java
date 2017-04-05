package top.zbeboy.isy.glue.util;

/**
 * Created by lenovo on 2017-03-28.
 */
public class ResultUtils<T> {
    private T data;
    private boolean isSearch;
    private long totalElements;

    public ResultUtils<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResultUtils<T> isSearch(boolean isSearch) {
        this.isSearch = isSearch;
        return this;
    }

    public ResultUtils<T> totalElements(long totalElements) {
        this.totalElements = totalElements;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSearch() {
        return isSearch;
    }

    public void setSearch(boolean search) {
        isSearch = search;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
