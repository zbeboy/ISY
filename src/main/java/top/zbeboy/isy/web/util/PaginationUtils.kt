package top.zbeboy.isy.web.util

/**
 * Created by zbeboy 2017-11-26 .
 **/
class PaginationUtils {
    private var totalPages: Int = 0// 总页数
    private var totalDatas: Int = 0// 数据总数
    private var pageNum: Int = 0// 当前页
    private var pageSize: Int = 0// 每页大小
    private var displayedPages: Int = 0// 显示按钮数
    private var searchParams: String? = null// 搜索参数


    fun getTotalPages(): Int {
        totalPages = if (totalDatas % pageSize == 0) totalDatas / pageSize else totalDatas / pageSize + 1
        return totalPages
    }

    fun setTotalPages(totalPages: Int) {
        this.totalPages = totalPages
    }

    fun getTotalDatas(): Int {
        return totalDatas
    }

    fun setTotalDatas(totalDatas: Int) {
        this.totalDatas = totalDatas
    }

    fun getPageNum(): Int {
        if (pageNum <= 0) {
            pageNum = 1
        }
        return pageNum
    }

    fun setPageNum(pageNum: Int) {
        this.pageNum = pageNum
    }

    fun getPageSize(): Int {
        return pageSize
    }

    fun setPageSize(pageSize: Int) {
        this.pageSize = pageSize
    }

    fun getDisplayedPages(): Int {
        if (totalPages > 3 || totalPages == 1) {
            displayedPages = 3
        } else {
            displayedPages = totalPages
        }
        return displayedPages
    }

    fun setDisplayedPages(displayedPages: Int) {
        this.displayedPages = displayedPages
    }

    fun getSearchParams(): String? {
        return searchParams
    }

    fun setSearchParams(searchParams: String) {
        this.searchParams = searchParams
    }
}