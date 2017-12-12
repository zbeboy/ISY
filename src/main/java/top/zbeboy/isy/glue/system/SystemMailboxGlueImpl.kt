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
import top.zbeboy.isy.elastic.pojo.SystemMailboxElastic
import top.zbeboy.isy.elastic.repository.SystemMailboxElasticRepository
import top.zbeboy.isy.glue.plugin.ElasticPlugin
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Repository("systemMailboxGlue")
open class SystemMailboxGlueImpl : ElasticPlugin<SystemMailboxBean>(), SystemMailboxGlue {

    @Resource
    open lateinit var systemMailboxElasticRepository: SystemMailboxElasticRepository

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<SystemMailboxBean>): ResultUtils<List<SystemMailboxBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<SystemMailboxBean>>()
        val systemMailboxElasticPage = systemMailboxElasticRepository.search(buildSearchQuery(search, dataTablesUtils, false))
        return resultUtils.data(dataBuilder(systemMailboxElasticPage)).totalElements(systemMailboxElasticPage.totalElements)
    }

    override fun countAll(): Long {
        return systemMailboxElasticRepository.count()
    }

    override fun save(systemMailboxElastic: SystemMailboxElastic) {
        systemMailboxElasticRepository.save(systemMailboxElastic)
    }

    /**
     * 构建新数据
     *
     * @param systemMailboxElasticPage 分页数据
     * @return 新数据
     */
    private fun dataBuilder(systemMailboxElasticPage: Page<SystemMailboxElastic>): List<SystemMailboxBean> {
        val systemMailboxes = ArrayList<SystemMailboxBean>()
        for (systemMailboxElastic in systemMailboxElasticPage.content) {
            val systemMailboxBean = SystemMailboxBean()
            systemMailboxBean.systemMailboxId = systemMailboxElastic.systemMailboxId
            systemMailboxBean.sendTime = systemMailboxElastic.sendTime
            systemMailboxBean.acceptMail = systemMailboxElastic.acceptMail
            systemMailboxBean.sendCondition = systemMailboxElastic.sendCondition
            val date = DateTimeUtils.timestampToDate(systemMailboxElastic.sendTime!!)
            systemMailboxBean.sendTimeNew = DateTimeUtils.formatDate(date)
            systemMailboxes.add(systemMailboxBean)
        }
        return systemMailboxes
    }

    /**
     * 系统邮件全局搜索条件
     *
     * @param search 搜索参数
     * @return 搜索条件
     */
    override fun searchCondition(search: JSONObject?): QueryBuilder? {
        val bluerBuilder = QueryBuilders.boolQuery()
        if (!ObjectUtils.isEmpty(search)) {
            val acceptMail = StringUtils.trimWhitespace(search!!.getString("acceptMail"))
            if (StringUtils.hasLength(acceptMail)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("acceptMail", SQLQueryUtils.elasticLikeAllParam(acceptMail))
                bluerBuilder.must(wildcardQueryBuilder)
            }
        }
        return bluerBuilder
    }

    /**
     * 系统邮件排序
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder 查询器
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<SystemMailboxBean>, nativeSearchQueryBuilder: NativeSearchQueryBuilder): NativeSearchQueryBuilder? {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        if (StringUtils.hasLength(orderColumnName)) {

            if ("system_mailbox_id".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("send_time".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.ASC).unmappedType("long"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.DESC).unmappedType("long"))
                }
            }

            if ("accept_mail".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptMail").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptMail").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("send_condition".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

        }
        return nativeSearchQueryBuilder
    }

}