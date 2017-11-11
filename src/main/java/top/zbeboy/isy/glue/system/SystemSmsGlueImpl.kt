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
import top.zbeboy.isy.elastic.pojo.SystemSmsElastic
import top.zbeboy.isy.elastic.repository.SystemSmsElasticRepository
import top.zbeboy.isy.glue.plugin.ElasticPlugin
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.system.sms.SystemSmsBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.ArrayList
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Repository("systemSmsGlue")
open class SystemSmsGlueImpl : ElasticPlugin<SystemSmsBean>(),SystemSmsGlue{

    @Resource
    open lateinit var systemSmsElasticRepository: SystemSmsElasticRepository

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<SystemSmsBean>): ResultUtils<List<SystemSmsBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<SystemSmsBean>>()
        val systemSmsElasticPage = systemSmsElasticRepository.search(buildSearchQuery(search, dataTablesUtils, false))
        return resultUtils.data(dataBuilder(systemSmsElasticPage)).totalElements(systemSmsElasticPage.totalElements)
    }

    override fun countAll(): Long {
        return systemSmsElasticRepository.count()
    }

    override fun save(systemSmsElastic: SystemSmsElastic) {
        systemSmsElasticRepository.save(systemSmsElastic)
    }

    /**
     * 构建新数据
     *
     * @param systemSmsElasticPage 分页数据
     * @return 新数据
     */
    private fun dataBuilder(systemSmsElasticPage: Page<SystemSmsElastic>): List<SystemSmsBean> {
        val systemSmses = ArrayList<SystemSmsBean>()
        for (systemSmsElastic in systemSmsElasticPage.content) {
            val systemSmsBean = SystemSmsBean()
            systemSmsBean.systemSmsId = systemSmsElastic.systemSmsId
            systemSmsBean.sendTime = systemSmsElastic.sendTime
            systemSmsBean.acceptPhone = systemSmsElastic.acceptPhone
            systemSmsBean.sendCondition = systemSmsElastic.sendCondition
            val date = DateTimeUtils.timestampToDate(systemSmsElastic.sendTime)
            systemSmsBean.sendTimeNew = DateTimeUtils.formatDate(date)
            systemSmses.add(systemSmsBean)
        }
        return systemSmses
    }

    /**
     * 系统短信全局搜索条件
     *
     * @param search 搜索参数
     * @return 搜索条件
     */
    override fun searchCondition(search: JSONObject): QueryBuilder? {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        if (!ObjectUtils.isEmpty(search)) {
            val acceptPhone = StringUtils.trimWhitespace(search.getString("acceptPhone"))
            if (StringUtils.hasLength(acceptPhone)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("acceptPhone", SQLQueryUtils.elasticLikeAllParam(acceptPhone))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }
        }
        return boolqueryBuilder
    }

    /**
     * 系统短信排序
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder 查询器
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<SystemSmsBean>, nativeSearchQueryBuilder: NativeSearchQueryBuilder): NativeSearchQueryBuilder? {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_sms_id".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("send_time".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.ASC).unmappedType("long"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.DESC).unmappedType("long"))
                }
            }

            if ("accept_phone".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptPhone").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptPhone").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("send_condition".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }
        return nativeSearchQueryBuilder
    }
}