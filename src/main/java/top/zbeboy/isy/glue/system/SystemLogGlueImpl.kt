package top.zbeboy.isy.glue.system

import com.alibaba.fastjson.JSONObject
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.data.domain.Page
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.elastic.pojo.SystemLogElastic
import top.zbeboy.isy.elastic.repository.SystemLogElasticRepository
import top.zbeboy.isy.glue.plugin.ElasticPlugin
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.system.log.SystemLogBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-10-31 .
 **/
@Repository("systemLogGlue")
open class SystemLogGlueImpl : ElasticPlugin<SystemLogBean>(), SystemLogGlue {

    @Resource
    lateinit open var systemLogElasticRepository: SystemLogElasticRepository

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<SystemLogBean>): ResultUtils<List<SystemLogBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<SystemLogBean>>()
        val systemLogElasticPage = systemLogElasticRepository.search(buildSearchQuery(search, dataTablesUtils, false))
        return resultUtils.data(dataBuilder(systemLogElasticPage)).totalElements(systemLogElasticPage.getTotalElements())
    }

    override fun countAll(): Long {
        return systemLogElasticRepository.count()
    }

    override fun save(systemLogElastic: SystemLogElastic) {
        systemLogElasticRepository.save(systemLogElastic)
    }

    /**
     * 构建新数据
     *
     * @param systemLogElasticPage 分页数据
     * @return 新数据
     */
    private fun dataBuilder(systemLogElasticPage: Page<SystemLogElastic>): List<SystemLogBean> {
        val systemLogs = ArrayList<SystemLogBean>()
        systemLogElasticPage.content.forEach { s ->
            val systemLogBean = SystemLogBean()
            systemLogBean.systemLogId = s.systemLogId
            systemLogBean.behavior = s.behavior
            systemLogBean.operatingTime = s.operatingTime
            systemLogBean.username = s.username
            systemLogBean.ipAddress = s.ipAddress
            val date = DateTimeUtils.timestampToDate(s.operatingTime)
            systemLogBean.operatingTimeNew = DateTimeUtils.formatDate(date)
            systemLogs.add(systemLogBean)
        }
        return systemLogs
    }

    /**
     * 系统日志全局搜索条件
     *
     * @param search 搜索参数
     * @return 搜索条件
     */
    override fun searchCondition(search: JSONObject): QueryBuilder? {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        if (!ObjectUtils.isEmpty(search)) {
            val username = StringUtils.trimWhitespace(search.getString("username"))
            val behavior = StringUtils.trimWhitespace(search.getString("behavior"))
            val ipAddress = StringUtils.trimWhitespace(search.getString("ipAddress"))
            if (StringUtils.hasLength(username)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("username", SQLQueryUtils.elasticLikeAllParam(username))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }

            if (StringUtils.hasLength(behavior)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("behavior", behavior)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(ipAddress)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("ipAddress", SQLQueryUtils.elasticLikeAllParam(ipAddress))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }
        }
        return boolqueryBuilder
    }

    /**
     * 系统日志排序
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder 查询器
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<SystemLogBean>, nativeSearchQueryBuilder: NativeSearchQueryBuilder): NativeSearchQueryBuilder? {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_log_id".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("username".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("behavior".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("behavior").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("behavior").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("operating_time".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("operatingTime").order(SortOrder.ASC).unmappedType("long"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("operatingTime").order(SortOrder.DESC).unmappedType("long"))
                }
            }

            if ("ip_address".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("ipAddress").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("ipAddress").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }
        return nativeSearchQueryBuilder
    }
}