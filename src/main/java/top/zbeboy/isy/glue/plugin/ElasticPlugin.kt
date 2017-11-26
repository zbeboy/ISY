package top.zbeboy.isy.glue.plugin

import com.alibaba.fastjson.JSONObject
import org.elasticsearch.index.query.QueryBuilder
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.data.elasticsearch.core.query.SearchQuery
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-11-12 .
 **/
open class ElasticPlugin<T> {

    /**
     * 若有其它条件可 覆盖实现
     *
     * @param search 条件内容
     * @return 其它条件
     */
    open fun prepositionCondition(search: JSONObject?): QueryBuilder? {
        return null
    }

    /**
     * 构建查询语句
     *
     * @param search          搜索条件
     * @param dataTablesUtils datatables工具类
     * @param hasPreposition  是否有前置条件
     * @return 条件
     */
    protected fun buildSearchQuery(search: JSONObject?, dataTablesUtils: DataTablesUtils<T>, hasPreposition: Boolean): SearchQuery {
        val nativeSearchQueryBuilder: NativeSearchQueryBuilder
        if (hasPreposition) {
            nativeSearchQueryBuilder = NativeSearchQueryBuilder().withQuery(prepositionCondition(search))
        } else {
            nativeSearchQueryBuilder = NativeSearchQueryBuilder().withQuery(searchCondition(search))
        }
        return sortCondition(dataTablesUtils, nativeSearchQueryBuilder)!!.withPageable(pagination(dataTablesUtils)).build()
    }

    /**
     * 查询条件，需要自行覆盖
     *
     * @param search 搜索条件
     * @return 查询条件
     */
    open fun searchCondition(search: JSONObject?): QueryBuilder? {
        return null
    }

    /**
     * 排序方式，需要自行覆盖
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder builer
     */
    open fun sortCondition(dataTablesUtils: DataTablesUtils<T>, nativeSearchQueryBuilder: NativeSearchQueryBuilder): NativeSearchQueryBuilder? {
        return null
    }

    /**
     * 分页方式
     *
     * @param dataTablesUtils datatables工具类
     */
    open fun pagination(dataTablesUtils: DataTablesUtils<T>): PageRequest {
        return PageRequest(dataTablesUtils.extraPage, dataTablesUtils.length)
    }

}