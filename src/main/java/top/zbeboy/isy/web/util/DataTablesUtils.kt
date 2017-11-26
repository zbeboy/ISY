package top.zbeboy.isy.web.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.util.ObjectUtils
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-11-26 .
 **/
open class DataTablesUtils<T> {
    /*
    返回的数据
     */
    var data: List<T>? = null

    var draw: Int = 0

    /*
    数据总数
     */
    var iTotalRecords: Long = 0

    /*
    过滤条件下的总数
     */
    var iTotalDisplayRecords: Long = 0

    /*
    从哪页开始
     */
    var start: Int = 0

    /*
    页大小
     */
    var length: Int = 0

    /*
    哪列排序 是索引
     */
    var orderColumn: Int = 0

    /*
    列
     */
    var headers: List<String>? = null

    /*
    哪列排序 是数据库对应列名
     */
    var orderColumnName: String? = null

    /*
    asc or desc
     */
    var orderDir = "asc"

    /*
    当开启过滤时，过滤参数
     */
    var searchValue: String? = null

    /*
    额外搜索参数
     */
    var extraSearch: String? = null

    /*
    当前页号
     */
    var extraPage: Int = 0

    /*
    object extraSearch
     */
    var search: JSONObject? = null

    companion object {
        @JvmStatic
        fun <T> of(): DataTablesUtils<T> {
            return DataTablesUtils()
        }
    }

    constructor()

    constructor(request: HttpServletRequest, headers: List<String>) {
        val startParam = request.getParameter("start")
        val lengthParam = request.getParameter("length")
        val orderColumnParam = request.getParameter("order[0][column]")
        val orderDirParam = request.getParameter("order[0][dir]")
        val searchValueParam = request.getParameter("search[value]")
        val extraSearchParam = request.getParameter("extra_search")
        val extraPage = request.getParameter("extra_page")
        val dramParam = request.getParameter("draw")

        if (NumberUtils.isNumber(startParam)) {
            this.start = NumberUtils.toInt(startParam)
        }

        if (NumberUtils.isNumber(lengthParam)) {
            this.length = NumberUtils.toInt(lengthParam)
        }

        if (NumberUtils.isNumber(orderColumnParam)) {
            this.orderColumn = NumberUtils.toInt(orderColumnParam)
        }

        if (!ObjectUtils.isEmpty(headers) && !headers.isEmpty() && headers.size > this.orderColumn) {
            this.orderColumnName = headers[this.orderColumn]
            this.headers = headers
        }

        if (StringUtils.isNotBlank(orderDirParam)) {
            this.orderDir = orderDirParam
        }

        if (StringUtils.isNotBlank(searchValueParam)) {
            this.searchValue = searchValueParam
        }

        if (StringUtils.isNotBlank(extraSearchParam)) {
            this.extraSearch = extraSearchParam
            this.search = JSON.parseObject(extraSearchParam)
        }

        if (NumberUtils.isNumber(extraPage)) {
            this.extraPage = NumberUtils.toInt(extraPage)
        }

        if (NumberUtils.isNumber(dramParam)) {
            this.draw = NumberUtils.toInt(dramParam)
        }

    }
}