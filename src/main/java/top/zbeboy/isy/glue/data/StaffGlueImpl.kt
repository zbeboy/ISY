package top.zbeboy.isy.glue.data

import com.alibaba.fastjson.JSONObject
import org.elasticsearch.index.query.BoolQueryBuilder
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
import top.zbeboy.isy.elastic.pojo.StaffElastic
import top.zbeboy.isy.elastic.repository.StaffElasticRepository
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-09 .
 **/
@Repository("staffGlue")
open class StaffGlueImpl : StaffGlue {

    @Resource
    open lateinit var staffElasticRepository: StaffElasticRepository

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    override fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): ResultUtils<List<StaffBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<StaffBean>>()
        val boolqueryBuilder = buildStaffExistsAuthoritiesCondition()
        boolqueryBuilder.must(searchCondition(search))
        val nativeSearchQueryBuilder = NativeSearchQueryBuilder().withQuery(boolqueryBuilder)
        val staffElasticPage = staffElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build())
        return resultUtils.data(dataBuilder(staffElasticPage)).totalElements(staffElasticPage.totalElements)
    }

    override fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): ResultUtils<List<StaffBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<StaffBean>>()
        val boolqueryBuilder = buildStaffNoExistsAuthoritiesCondition()
        boolqueryBuilder.must(searchCondition(search))
        val nativeSearchQueryBuilder = NativeSearchQueryBuilder().withQuery(boolqueryBuilder)
        val staffElasticPage = staffElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build())
        return resultUtils.data(dataBuilder(staffElasticPage)).totalElements(staffElasticPage.totalElements)
    }

    override fun countAllExistsAuthorities(): Long {
        return when {
            roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) -> {
                val list = ArrayList<Int>()
                list.add(ElasticBook.SYSTEM_AUTHORITIES)
                list.add(ElasticBook.NO_AUTHORITIES)
                staffElasticRepository.countByAuthoritiesNotIn(list)
            }
            roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) -> {
                val users = usersService.getUserFromSession()
                val record = usersService.findUserSchoolInfo(users!!)
                val collegeId = roleService.getRoleCollegeId(record)
                staffElasticRepository.countByAuthoritiesAndCollegeId(ElasticBook.HAS_AUTHORITIES, collegeId)
            }
            else -> {
                val users = usersService.getUserFromSession()
                val record = usersService.findUserSchoolInfo(users!!)
                val departmentId = roleService.getRoleDepartmentId(record)
                staffElasticRepository.countByAuthoritiesAndDepartmentIdAndUsernameNot(ElasticBook.HAS_AUTHORITIES, departmentId, users.username)
            }
        }
    }

    override fun countAllNotExistsAuthorities(): Long {
        return when {
            roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) -> staffElasticRepository.countByAuthorities(ElasticBook.NO_AUTHORITIES)
            roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) -> {
                val users = usersService.getUserFromSession()
                val record = usersService.findUserSchoolInfo(users!!)
                val collegeId = roleService.getRoleCollegeId(record)
                staffElasticRepository.countByAuthoritiesAndCollegeId(ElasticBook.NO_AUTHORITIES, collegeId)
            }
            else -> {
                val users = usersService.getUserFromSession()
                val record = usersService.findUserSchoolInfo(users!!)
                val departmentId = roleService.getRoleDepartmentId(record)
                staffElasticRepository.countByAuthoritiesAndDepartmentIdAndUsernameNot(ElasticBook.NO_AUTHORITIES, departmentId, users.username)
            }
        }
    }

    /**
     * 构建新数据
     *
     * @param staffElasticPage 分页数据
     * @return 新数据
     */
    private fun dataBuilder(staffElasticPage: Page<StaffElastic>): List<StaffBean> {
        val staffs = ArrayList<StaffBean>()
        for (staffElastic in staffElasticPage.content) {
            val staffBean = StaffBean()
            staffBean.staffId = staffElastic.getStaffId()
            staffBean.staffNumber = staffElastic.staffNumber
            staffBean.birthday = staffElastic.birthday
            staffBean.sex = staffElastic.sex
            staffBean.idCard = staffElastic.idCard
            staffBean.familyResidence = staffElastic.familyResidence
            staffBean.politicalLandscapeId = staffElastic.politicalLandscapeId
            staffBean.politicalLandscapeName = staffElastic.politicalLandscapeName
            staffBean.nationId = staffElastic.nationId
            staffBean.nationName = staffElastic.nationName
            staffBean.academicTitleId = staffElastic.academicTitleId
            staffBean.academicTitleName = staffElastic.academicTitleName
            staffBean.post = staffElastic.post
            staffBean.schoolId = staffElastic.schoolId
            staffBean.schoolName = staffElastic.schoolName
            staffBean.collegeId = staffElastic.collegeId
            staffBean.collegeName = staffElastic.collegeName
            staffBean.departmentId = staffElastic.departmentId
            staffBean.departmentName = staffElastic.departmentName
            staffBean.username = staffElastic.username
            staffBean.enabled = staffElastic.enabled
            staffBean.realName = staffElastic.realName
            staffBean.mobile = staffElastic.mobile
            staffBean.avatar = staffElastic.avatar
            staffBean.verifyMailbox = staffElastic.verifyMailbox
            staffBean.langKey = staffElastic.langKey
            staffBean.joinDate = staffElastic.joinDate
            staffBean.roleName = staffElastic.roleName
            staffs.add(staffBean)
        }
        return staffs
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
            val school = StringUtils.trimWhitespace(search!!.getString("school"))
            val college = StringUtils.trimWhitespace(search.getString("college"))
            val department = StringUtils.trimWhitespace(search.getString("department"))
            val post = StringUtils.trimWhitespace(search.getString("post"))
            val staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"))
            val username = StringUtils.trimWhitespace(search.getString("username"))
            val mobile = StringUtils.trimWhitespace(search.getString("mobile"))
            val idCard = StringUtils.trimWhitespace(search.getString("idCard"))
            val realName = StringUtils.trimWhitespace(search.getString("realName"))
            val sex = StringUtils.trimWhitespace(search.getString("sex"))

            if (StringUtils.hasLength(school)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("schoolName", school)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(college)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("collegeName", college)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(department)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("departmentName", department)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(post)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("post", department)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(staffNumber)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("staffNumber", SQLQueryUtils.elasticLikeAllParam(staffNumber))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }

            if (StringUtils.hasLength(username)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("username", SQLQueryUtils.elasticLikeAllParam(username))
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(mobile)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("mobile", SQLQueryUtils.elasticLikeAllParam(mobile))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }

            if (StringUtils.hasLength(idCard)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("idCard", SQLQueryUtils.elasticLikeAllParam(idCard))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }

            if (StringUtils.hasLength(realName)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("realName", realName)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(sex)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("sex", sex)
                boolqueryBuilder.must(matchQueryBuilder)
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
    fun sortCondition(dataTablesUtils: DataTablesUtils<StaffBean>, nativeSearchQueryBuilder: NativeSearchQueryBuilder): NativeSearchQueryBuilder {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        if (StringUtils.hasLength(orderColumnName)) {
            if ("staff_number".equals(orderColumnName!!, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("staffNumber").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("staffNumber").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("real_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("realName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("realName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("username".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("mobile".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("mobile").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("mobile").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("id_card".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("idCard").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("idCard").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("schoolName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("schoolName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("collegeName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("collegeName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("department_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("departmentName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("departmentName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("academic_title_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("academicTitleName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("academicTitleName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("post".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("post").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("post").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("sex".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sex").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sex").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("birthday".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("birthday").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("birthday").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("nation_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("nationName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("nationName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("politicalLandscape_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("politicalLandscapeName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("politicalLandscapeName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("family_residence".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("familyResidence").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("familyResidence").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("enabled".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("enabled").order(SortOrder.ASC).unmappedType("byte"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("enabled").order(SortOrder.DESC).unmappedType("byte"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("lang_key".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("langKey").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("langKey").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("join_date".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("joinDate").order(SortOrder.ASC).unmappedType("date"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("joinDate").order(SortOrder.DESC).unmappedType("date"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
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
    fun pagination(dataTablesUtils: DataTablesUtils<StaffBean>): PageRequest {
        return PageRequest(dataTablesUtils.extraPage, dataTablesUtils.length)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildStaffExistsAuthoritiesCondition(): BoolQueryBuilder {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.SYSTEM_AUTHORITIES))
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {// 管理员
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val collegeId = roleService.getRoleCollegeId(record)
            boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.HAS_AUTHORITIES))
            boolqueryBuilder.must(QueryBuilders.termQuery("collegeId", collegeId))
        } else {
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val departmentId = roleService.getRoleDepartmentId(record)
            boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.HAS_AUTHORITIES))
            boolqueryBuilder.must(QueryBuilders.termQuery("departmentId", departmentId))
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("username", users.username))
        }
        return boolqueryBuilder
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildStaffNoExistsAuthoritiesCondition(): BoolQueryBuilder {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {// 管理员
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val collegeId = roleService.getRoleCollegeId(record)
            boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
            boolqueryBuilder.must(QueryBuilders.termQuery("collegeId", collegeId))
        } else {
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val departmentId = roleService.getRoleDepartmentId(record)
            boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
            boolqueryBuilder.must(QueryBuilders.termQuery("departmentId", departmentId))
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("username", users.username))
        }
        return boolqueryBuilder
    }
}