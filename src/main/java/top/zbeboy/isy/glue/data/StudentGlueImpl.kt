package top.zbeboy.isy.glue.data

import com.alibaba.fastjson.JSONObject
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.jooq.SQL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.elastic.config.ElasticBook
import top.zbeboy.isy.elastic.pojo.StudentElastic
import top.zbeboy.isy.elastic.repository.StudentElasticRepository
import top.zbeboy.isy.glue.common.MethodGlueCommon
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.ArrayList
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-11 .
 **/
@Repository("studentGlue")
open class StudentGlueImpl : StudentGlue {

    @Resource
    open lateinit var studentElasticRepository: StudentElasticRepository

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var methodGlueCommon: MethodGlueCommon

    @Resource
    open lateinit var cacheManageService: CacheManageService

    override fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): ResultUtils<List<StudentBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<StudentBean>>()
        val boolqueryBuilder = buildStudentExistsAuthoritiesCondition()
        boolqueryBuilder.must(searchCondition(search))
        val nativeSearchQueryBuilder = NativeSearchQueryBuilder().withQuery(boolqueryBuilder)
        val studentElasticPage = studentElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build())
        return resultUtils.data(dataBuilder(studentElasticPage)).totalElements(studentElasticPage.totalElements)
    }

    override fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): ResultUtils<List<StudentBean>> {
        val search = dataTablesUtils.search
        val resultUtils = ResultUtils<List<StudentBean>>()
        val boolqueryBuilder = buildStudentNoExistsAuthoritiesCondition()
        boolqueryBuilder.must(searchCondition(search))
        val nativeSearchQueryBuilder = NativeSearchQueryBuilder().withQuery(boolqueryBuilder)
        val studentElasticPage = studentElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build())
        return resultUtils.data(dataBuilder(studentElasticPage)).totalElements(studentElasticPage.totalElements)
    }

    override fun countAllExistsAuthorities(): Long {
        return when {
            roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) -> {
                val list = ArrayList<Int>()
                list.add(ElasticBook.SYSTEM_AUTHORITIES)
                list.add(ElasticBook.NO_AUTHORITIES)
                studentElasticRepository.countByAuthoritiesNotIn(list)
            }
            roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) -> {
                val users = usersService.getUserFromSession()
                val collegeId = cacheManageService.getRoleCollegeId(users!!)
                studentElasticRepository.countByAuthoritiesAndCollegeId(ElasticBook.HAS_AUTHORITIES, collegeId)
            }
            else -> {
                val users = usersService.getUserFromSession()
                val departmentId = cacheManageService.getRoleDepartmentId(users!!)
                studentElasticRepository.countByAuthoritiesAndDepartmentIdAndUsernameNot(ElasticBook.HAS_AUTHORITIES, departmentId, users.username)
            }
        }
    }

    override fun countAllNotExistsAuthorities(): Long {
        return when {
            roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) -> studentElasticRepository.countByAuthorities(ElasticBook.NO_AUTHORITIES)
            roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) -> {
                val users = usersService.getUserFromSession()
                val collegeId = cacheManageService.getRoleCollegeId(users!!)
                studentElasticRepository.countByAuthoritiesAndCollegeId(ElasticBook.NO_AUTHORITIES, collegeId)
            }
            else -> {
                val users = usersService.getUserFromSession()
                val departmentId = cacheManageService.getRoleDepartmentId(users!!)
                studentElasticRepository.countByAuthoritiesAndDepartmentIdAndUsernameNot(ElasticBook.NO_AUTHORITIES, departmentId, users.username)
            }
        }
    }

    /**
     * 构建新数据
     *
     * @param studentElasticPage 分页数据
     * @return 新数据
     */
    private fun dataBuilder(studentElasticPage: Page<StudentElastic>): List<StudentBean> {
        val students = ArrayList<StudentBean>()
        for (studentElastic in studentElasticPage.content) {
            val studentBean = StudentBean()
            studentBean.studentId = studentElastic.getStudentId()
            studentBean.studentNumber = studentElastic.studentNumber
            studentBean.birthday = studentElastic.birthday
            studentBean.sex = studentElastic.sex
            studentBean.idCard = studentElastic.idCard
            studentBean.familyResidence = studentElastic.familyResidence
            studentBean.politicalLandscapeId = studentElastic.politicalLandscapeId
            studentBean.politicalLandscapeName = studentElastic.politicalLandscapeName
            studentBean.nationId = studentElastic.nationId
            studentBean.nationName = studentElastic.nationName
            studentBean.dormitoryNumber = studentElastic.dormitoryNumber
            studentBean.parentName = studentElastic.parentName
            studentBean.parentContactPhone = studentElastic.parentContactPhone
            studentBean.placeOrigin = studentElastic.placeOrigin
            studentBean.schoolId = studentElastic.schoolId
            studentBean.schoolName = studentElastic.schoolName
            studentBean.collegeId = studentElastic.collegeId
            studentBean.collegeName = studentElastic.collegeName
            studentBean.departmentId = studentElastic.departmentId
            studentBean.departmentName = studentElastic.departmentName
            studentBean.scienceId = studentElastic.scienceId
            studentBean.scienceName = studentElastic.scienceName
            studentBean.grade = studentElastic.grade
            studentBean.organizeId = studentElastic.organizeId
            studentBean.organizeName = studentElastic.organizeName
            studentBean.username = studentElastic.username
            studentBean.enabled = studentElastic.enabled
            studentBean.realName = studentElastic.realName
            studentBean.mobile = studentElastic.mobile
            studentBean.avatar = studentElastic.avatar
            studentBean.verifyMailbox = studentElastic.verifyMailbox
            studentBean.langKey = studentElastic.langKey
            studentBean.joinDate = studentElastic.joinDate
            studentBean.roleName = studentElastic.roleName
            students.add(studentBean)
        }
        return students
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
            val science = StringUtils.trimWhitespace(search.getString("science"))
            val grade = StringUtils.trimWhitespace(search.getString("grade"))
            val organize = StringUtils.trimWhitespace(search.getString("organize"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
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

            if (StringUtils.hasLength(science)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("scienceName", science)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(grade)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("grade", SQLQueryUtils.elasticLikeAllParam(grade))
                boolqueryBuilder.must(wildcardQueryBuilder)
            }

            if (StringUtils.hasLength(organize)) {
                val matchQueryBuilder = QueryBuilders.matchPhraseQuery("organizeName", organize)
                boolqueryBuilder.must(matchQueryBuilder)
            }

            if (StringUtils.hasLength(studentNumber)) {
                val wildcardQueryBuilder = QueryBuilders.wildcardQuery("studentNumber", SQLQueryUtils.elasticLikeAllParam(studentNumber))
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
    fun sortCondition(dataTablesUtils: DataTablesUtils<StudentBean>, nativeSearchQueryBuilder: NativeSearchQueryBuilder): NativeSearchQueryBuilder {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_number".equals(orderColumnName!!, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("studentNumber").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("studentNumber").order(SortOrder.DESC).unmappedType("string"))
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

            if ("science_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("scienceName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("scienceName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("grade".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("grade").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("grade").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeName").order(SortOrder.DESC).unmappedType("string"))
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

            if ("dormitory_number".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("dormitoryNumber").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("dormitoryNumber").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("place_origin".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("placeOrigin").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("placeOrigin").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("parent_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("parentName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("parentName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                }
            }

            if ("parent_contact_phone".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("parentContactPhone").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("parentContactPhone").order(SortOrder.DESC).unmappedType("string"))
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
    fun pagination(dataTablesUtils: DataTablesUtils<StudentBean>): PageRequest {
        return PageRequest(dataTablesUtils.extraPage, dataTablesUtils.length)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildStudentExistsAuthoritiesCondition(): BoolQueryBuilder {
        return methodGlueCommon.buildExistsAuthoritiesCondition()
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildStudentNoExistsAuthoritiesCondition(): BoolQueryBuilder {
        return methodGlueCommon.buildNoExistsAuthoritiesCondition()
    }
}