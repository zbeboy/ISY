package top.zbeboy.isy.web.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lenovo on 2016-01-22.
 * 分页数据
 */
public class PaginationUtils {

    private final Logger log = LoggerFactory.getLogger(PaginationUtils.class);

    private int totalPages;// 总页数
    private int totalDatas;// 数据总数
    private int pageNum;// 当前页
    private int pageSize;// 每页大小
    private int displayedPages;// 显示按钮数
    private String searchParams;// 搜索参数

    public PaginationUtils() {
    }

    public PaginationUtils(int totalPages, int totalDatas, int pageNum, int pageSize, int displayedPages, String searchParams) {
        this.totalPages = totalPages;
        this.totalDatas = totalDatas;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.displayedPages = displayedPages;
        this.searchParams = searchParams;
    }

    public int getTotalPages() {
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
        return displayedPages;
    }

    public void setDisplayedPages(int displayedPages) {
        if (totalPages > 3 || totalPages == 1) {
            displayedPages = 3;
        } else {
            displayedPages = totalPages;
        }
        this.displayedPages = displayedPages;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public String toString() {
        return "PaginationUtils{" +
                "totalPages=" + totalPages +
                ", totalDatas=" + totalDatas +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", displayedPages=" + displayedPages +
                ", searchParams='" + searchParams + '\'' +
                '}';
    }
}
