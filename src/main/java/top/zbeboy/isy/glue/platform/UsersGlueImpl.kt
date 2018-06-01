package top.zbeboy.isy.glue.platform

import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang.math.NumberUtils
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.elastic.config.ElasticBook
import top.zbeboy.isy.elastic.pojo.UsersElastic
import top.zbeboy.isy.elastic.repository.UsersElasticRepository
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.platform.users.UsersBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-19 .
 **/
@Repository("usersGlue")
open class UsersGlueImpl : UsersGlue {
    @Resource
    open lateinit var usersElasticRepository: UsersElasticRepository

    @Resource
    open lateinit var roleService: RoleService

    override fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): ResultUtils<List<UsersBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<UsersBean>>()
        val boolqueryBuilder = QueryBuilders.boolQuery()
        boolqueryBuilder.must(searchCondition(search))
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.SYSTEM_AUTHORITIES))
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
        } else {
            boolqueryBuilder.must(QueryBuilders.matchQuery("authorities", ElasticBook.HAS_AUTHORITIES))
        }
        val nativeSearchQueryBuilder = NativeSearchQueryBuilder().withQuery(boolqueryBuilder)
        val usersElasticPage = usersElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build())
        return resultUtils.data(dataBuilder(usersElasticPage)).totalElements(usersElasticPage.totalElements)
    }

    override fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): ResultUtils<List<UsersBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<UsersBean>>()
        val nativeSearchQueryBuilder = NativeSearchQueryBuilder().withQuery(prepositionCondition(search, ElasticBook.NO_AUTHORITIES))
        val usersElasticPage = usersElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build())
        return resultUtils.data(dataBuilder(usersElasticPage)).totalElements(usersElasticPage.totalElements)
    }

    override fun countAllExistsAuthorities(): Long {
        return if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            val list = ArrayList<Int>()
            list.add(ElasticBook.SYSTEM_AUTHORITIES)
            list.add(ElasticBook.NO_AUTHORITIES)
            usersElasticRepository.countByAuthoritiesNotIn(list)
        } else {
            usersElasticRepository.countByAuthorities(ElasticBook.HAS_AUTHORITIES)
        }
    }

    override fun countAllNotExistsAuthorities(): Long {
        return usersElasticRepository.countByAuthorities(ElasticBook.NO_AUTHORITIES)
    }

    /**
     * 若有其它条件
     *
     * @param search      条件内容
     * @param authorities 详见：ElasticBook
     * @return 其它条件
     */
    private fun prepositionCondition(search: JSONObject?, authorities: Int): QueryBuilder {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        boolqueryBuilder.must(searchCondition(search))
        boolqueryBuilder.must(QueryBuilders.termQuery("authorities", authorities))
        return boolqueryBuilder
    }

    /**
     * 构建新数据
     *
     * @param usersElasticPage 分页数据
     * @return 新数据
     */
    private fun dataBuilder(usersElasticPage: Page<UsersElastic>): List<UsersBean> {
        val userses = ArrayList<UsersBean>()
        for (usersElastic in usersElasticPage.content) {
            val usersBean = UsersBean()
            usersBean.realName = usersElastic.realName
            usersBean.username = usersElastic.username
            usersBean.mobile = usersElastic.mobile
            usersBean.roleName = usersElastic.roleName
            usersBean.usersTypeName = usersElastic.usersTypeName
            usersBean.enabled = usersElastic.enabled
            usersBean.verifyMailbox = usersElastic.verifyMailbox
            usersBean.langKey = usersElastic.langKey
            usersBean.joinDate = usersElastic.joinDate
            userses.add(usersBean)
        }
        return userses
    }

    /**
     * 查询条件
     *
     * @param search 搜索条件
     * @return 查询条件
     */
    fun searchCondition(search: JSONObject?): QueryBuilder {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        if (!ObjectUtils.isEmpty(search)) {
            val realName = StringUtils.trimWhitespace(search!!.getString("realName"))
            val username = StringUtils.trimWhitespace(search.getString("username"))
            val mobile = StringUtils.trimWhitespace(search.getString("mobile"))
            val usersType = StringUtils.trimWhitespace(search.getString("usersType"))
            if (StringUtils.hasLength(realName)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("realName", SQLQueryUtils.elasticLikeAllParam(realName))
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(username)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("username", SQLQueryUtils.elasticLikeAllParam(username))
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(mobile)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("mobile", SQLQueryUtils.elasticLikeAllParam(mobile))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }

            if (StringUtils.hasLength(usersType)) {
                val usersTypeId = NumberUtils.toInt(usersType)
                if (usersTypeId > 0) {
                    val matchQueryBuilder = QueryBuilders.matchQuery("usersTypeId", usersTypeId)
                    boolqueryBuilder.must(matchQueryBuilder)
                }
            }
        }
        return boolqueryBuilder
    }

    /**
     * 排序方式
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder builer
     */
    fun sortCondition(dataTablesUtils: DataTablesUtils<UsersBean>, nativeSearchQueryBuilder: NativeSearchQueryBuilder): NativeSearchQueryBuilder {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        if (StringUtils.hasLength(orderColumnName)) {
            if ("username".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("mobile".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("mobile.keyword").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("mobile.keyword").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("real_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("realName.keyword").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("realName.keyword").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("role_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("roleName.keyword").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("roleName.keyword").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("users_type_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("usersTypeName.keyword").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("usersTypeName.keyword").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("enabled".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("enabled").order(SortOrder.ASC).unmappedType("byte"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("enabled").order(SortOrder.DESC).unmappedType("byte"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("lang_key".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("langKey.keyword").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("langKey.keyword").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("join_date".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("joinDate").order(SortOrder.ASC).unmappedType("date"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("joinDate").order(SortOrder.DESC).unmappedType("date"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username.keyword").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }
        return nativeSearchQueryBuilder
    }

    /**
     * 分页方式
     *
     * @param dataTablesUtils datatables工具类
     */
    fun pagination(dataTablesUtils: DataTablesUtils<UsersBean>): PageRequest {
        return PageRequest.of(dataTablesUtils.extraPage, dataTablesUtils.length)
    }
}