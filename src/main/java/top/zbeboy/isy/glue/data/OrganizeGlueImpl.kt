package top.zbeboy.isy.glue.data

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
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.elastic.pojo.OrganizeElastic
import top.zbeboy.isy.elastic.repository.OrganizeElasticRepository
import top.zbeboy.isy.glue.plugin.ElasticPlugin
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-03 .
 **/
@Repository("organizeGlue")
open class OrganizeGlueImpl : ElasticPlugin<OrganizeBean>(), OrganizeGlue {

    @Resource
    open lateinit var organizeElasticRepository: OrganizeElasticRepository

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<OrganizeBean>): ResultUtils<List<OrganizeBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<OrganizeBean>>()
        // 分权限显示用户数据
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            val organizeElasticPage = organizeElasticRepository.search(buildSearchQuery(search, dataTablesUtils, false))
            resultUtils.data(dataBuilder(organizeElasticPage)).totalElements(organizeElasticPage.totalElements)
        } else {
            val organizeElasticPage = organizeElasticRepository.search(buildSearchQuery(search, dataTablesUtils, true))
            resultUtils.data(dataBuilder(organizeElasticPage)).totalElements(organizeElasticPage.totalElements)
        }
        return resultUtils
    }

    override fun countAll(): Long {
        return if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            organizeElasticRepository.count()
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            val users = usersService.getUserFromSession()
            val collegeId = cacheManageService.getRoleCollegeId(users!!)
            organizeElasticRepository.countByCollegeId(collegeId)
        } else { // 其它学校自由角色
            val users = usersService.getUserFromSession()
            val departmentId = cacheManageService.getRoleDepartmentId(users!!)
            organizeElasticRepository.countByDepartmentId(departmentId)
        }
    }

    /**
     * 构建新数据
     *
     * @param organizeElasticPage 分页数据
     * @return 新数据
     */
    private fun dataBuilder(organizeElasticPage: Page<OrganizeElastic>): List<OrganizeBean> {
        val organizes = ArrayList<OrganizeBean>()
        for (organizeElastic in organizeElasticPage.content) {
            val organizeBean = OrganizeBean()
            organizeBean.schoolName = organizeElastic.schoolName
            organizeBean.collegeName = organizeElastic.collegeName
            organizeBean.departmentName = organizeElastic.departmentName
            organizeBean.scienceName = organizeElastic.scienceName
            organizeBean.schoolId = organizeElastic.schoolId!!
            organizeBean.collegeId = organizeElastic.collegeId!!
            organizeBean.scienceId = organizeElastic.scienceId
            organizeBean.departmentId = organizeElastic.departmentId!!
            organizeBean.organizeId = organizeElastic.getOrganizeId()
            organizeBean.organizeName = organizeElastic.organizeName
            organizeBean.organizeIsDel = organizeElastic.organizeIsDel
            organizeBean.grade = organizeElastic.grade
            organizes.add(organizeBean)
        }
        return organizes
    }

    /**
     * 搜索前置条件
     *
     * @param search 搜索内容
     * @return 前置
     */
    override fun prepositionCondition(search: JSONObject?): QueryBuilder? {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        boolqueryBuilder.must(searchCondition(search))
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {// 管理员
            val users = usersService.getUserFromSession()
            val collegeId = cacheManageService.getRoleCollegeId(users!!)
            boolqueryBuilder.must(QueryBuilders.termQuery("collegeId", collegeId))
        } else {// 其它学校自由角色
            val users = usersService.getUserFromSession()
            val departmentId = cacheManageService.getRoleDepartmentId(users!!)
            boolqueryBuilder.must(QueryBuilders.termQuery("departmentId", departmentId))
        }
        return boolqueryBuilder
    }

    /**
     * 班级全局搜索条件
     *
     * @param search 搜索参数
     * @return 搜索条件
     */
    override fun searchCondition(search: JSONObject?): QueryBuilder? {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search!!.getString("schoolName"))
            val collegeName = StringUtils.trimWhitespace(search.getString("collegeName"))
            val departmentName = StringUtils.trimWhitespace(search.getString("departmentName"))
            val scienceName = StringUtils.trimWhitespace(search.getString("scienceName"))
            val grade = StringUtils.trimWhitespace(search.getString("grade"))
            val organizeName = StringUtils.trimWhitespace(search.getString("organizeName"))

            if (StringUtils.hasLength(schoolName)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("schoolName", schoolName)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(collegeName)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("collegeName", collegeName)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(departmentName)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("departmentName", departmentName)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(scienceName)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("scienceName", scienceName)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(grade)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("grade", SQLQueryUtils.elasticLikeAllParam(grade))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }

            if (StringUtils.hasLength(organizeName)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("organizeName", organizeName)
                boolqueryBuilder.must(matchQueryBuilder)
            }
        }
        return boolqueryBuilder
    }

    /**
     * 班级排序
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder 查询器
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<OrganizeBean>, nativeSearchQueryBuilder: NativeSearchQueryBuilder): NativeSearchQueryBuilder? {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        if (StringUtils.hasLength(orderColumnName)) {
            if ("organize_id".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("schoolName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("schoolName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("collegeName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("collegeName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("department_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("departmentName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("departmentName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("science_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("scienceName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("scienceName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("grade".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("grade").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("grade").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("organize_is_del".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeIsDel").order(SortOrder.ASC).unmappedType("byte"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeIsDel").order(SortOrder.DESC).unmappedType("byte"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }
        return nativeSearchQueryBuilder
    }
}