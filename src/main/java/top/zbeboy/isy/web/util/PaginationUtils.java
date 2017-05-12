package top.zbeboy.isy.web.util;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by lenovo on 2016-01-22.
 * 分页数据
 */
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class PaginationUtils {

    private int totalPages;// 总页数
    private int totalDatas;// 数据总数
    private int pageNum;// 当前页
    private int pageSize;// 每页大小
    private int displayedPages;// 显示按钮数
    private String searchParams;// 搜索参数


    public int getTotalPages() {
        totalPages = totalDatas % pageSize == 0 ? totalDatas / pageSize : totalDatas / pageSize + 1;
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalDatas() {
        return totalDatas;
    }

    public void setTotalDatas(int totalDatas) {
        this.totalDatas = totalDatas;
    }

    public int getPageNum() {
        if (pageNum <= 0) {
            pageNum = 1;
        }
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getDisplayedPages() {
        if (totalPages > 3 || totalPages == 1) {
            displayedPages = 3;
        } else {
            displayedPages = totalPages;
        }
        return displayedPages;
    }

    public void setDisplayedPages(int displayedPages) {
        this.displayedPages = displayedPages;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }
}
