package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.DepartmentDao
import top.zbeboy.isy.domain.tables.pojos.Department
import top.zbeboy.isy.domain.tables.records.DepartmentRecord
import top.zbeboy.isy.elastic.repository.OrganizeElasticRepository
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.department.DepartmentBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-02 .
 **/
@Service("departmentService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class DepartmentServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<DepartmentBean>(), DepartmentService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var departmentDao: DepartmentDao

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var organizeElasticRepository: OrganizeElasticRepository

    override fun findByCollegeIdAndIsDel(collegeId: Int, b: Byte?): Result<DepartmentRecord> {
        return create.selectFrom<DepartmentRecord>(DEPARTMENT)
                .where(DEPARTMENT.DEPARTMENT_IS_DEL.eq(b).and(DEPARTMENT.COLLEGE_ID.eq(collegeId)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(department: Department) {
        departmentDao.insert(department)
    }

    override fun update(department: Department) {
        departmentDao.update(department)
        val records = organizeElasticRepository.findByDepartmentId(department.departmentId!!)
        records.forEach { organizeElastic ->
            organizeElastic.departmentId = department.departmentId
            organizeElastic.departmentName = department.departmentName
            organizeElasticRepository.delete(organizeElastic)
            organizeElasticRepository.save(organizeElastic)
        }
    }

    override fun updateIsDel(ids: List<Int>, isDel: Byte?) {
        for (id in ids) {
            create.update<DepartmentRecord>(DEPARTMENT).set<Byte>(DEPARTMENT.DEPARTMENT_IS_DEL, isDel).where(DEPARTMENT.DEPARTMENT_ID.eq(id)).execute()
        }
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<DepartmentBean>): Result<Record> {
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildDepartmentCondition()
        if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.select()
                        .from(DEPARTMENT)
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
                pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
                records = selectJoinStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(DEPARTMENT)
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition)
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                records = selectConditionStep.fetch()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(DEPARTMENT)
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a)
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                records = selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(DEPARTMENT)
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition!!.and(a))
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                records = selectConditionStep.fetch()
            }
        }
        return records
    }

    override fun countAll(): Int {
        val roleCondition = buildDepartmentCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            statisticsAll(create, DEPARTMENT)
        } else {
            val count = create.selectCount()
                    .from(DEPARTMENT)
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(roleCondition)
                    .fetchOne()
            count.value1()
        }
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<DepartmentBean>): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildDepartmentCondition()
        if (ObjectUtils.isEmpty(a)) {
            count = if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.selectCount()
                        .from(DEPARTMENT)
                selectJoinStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(DEPARTMENT)
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(roleCondition)
                selectConditionStep.fetchOne()
            }
        } else {
            count = if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(DEPARTMENT)
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a)
                selectConditionStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(DEPARTMENT)
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition!!.and(a))
                selectConditionStep.fetchOne()
            }
        }
        return count.value1()
    }

    override fun findByDepartmentNameAndCollegeId(departmentName: String, collegeId: Int): Result<DepartmentRecord> {
        return create.selectFrom<DepartmentRecord>(DEPARTMENT)
                .where(DEPARTMENT.DEPARTMENT_NAME.eq(departmentName).and(DEPARTMENT.COLLEGE_ID.eq(collegeId)))
                .fetch()
    }

    override fun findByDepartmentNameAndCollegeIdNeDepartmentId(departmentName: String, departmentId: Int, collegeId: Int): Result<DepartmentRecord> {
        return create.selectFrom<DepartmentRecord>(DEPARTMENT)
                .where(DEPARTMENT.DEPARTMENT_NAME.eq(departmentName).and(DEPARTMENT.DEPARTMENT_ID.ne(departmentId)).and(DEPARTMENT.COLLEGE_ID.eq(collegeId)))
                .fetch()
    }

    override fun findByIdRelation(id: Int): Optional<Record> {
        return create.select()
                .from(DEPARTMENT)
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(DEPARTMENT.DEPARTMENT_ID.eq(id))
                .fetchOptional()
    }

    override fun findById(id: Int): Department {
        return departmentDao.findById(id)
    }

    /**
     * 系数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<DepartmentBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search!!.getString("schoolName"))
            val collegeName = StringUtils.trimWhitespace(search.getString("collegeName"))
            val departmentName = StringUtils.trimWhitespace(search.getString("departmentName"))
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName))
            }

            if (StringUtils.hasLength(collegeName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName))
                } else {
                    a = a!!.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)))
                }
            }

            if (StringUtils.hasLength(departmentName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(departmentName))
                } else {
                    a = a!!.and(DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(departmentName)))
                }
            }

        }
        return a
    }

    /**
     * 系数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<DepartmentBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("department_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_ID.asc()
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_ID.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = DEPARTMENT.DEPARTMENT_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = DEPARTMENT.DEPARTMENT_ID.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = DEPARTMENT.DEPARTMENT_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = DEPARTMENT.DEPARTMENT_ID.desc()
                }
            }

            if ("department_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.asc()
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.desc()
                }
            }

            if ("department_is_del".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_IS_DEL.asc()
                    sortField[1] = DEPARTMENT.DEPARTMENT_ID.asc()
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_IS_DEL.desc()
                    sortField[1] = DEPARTMENT.DEPARTMENT_ID.desc()
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildDepartmentCondition(): Condition? {
        var condition: Condition? = null // 分权限显示用户数据
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 管理员或其它角色,除系统
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val collegeId = roleService.getRoleCollegeId(record)
            condition = COLLEGE.COLLEGE_ID.eq(collegeId)
        }
        return condition
    }

}