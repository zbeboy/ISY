package top.zbeboy.isy.service.platform

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.RoleDao
import top.zbeboy.isy.domain.tables.pojos.College
import top.zbeboy.isy.domain.tables.pojos.Department
import top.zbeboy.isy.domain.tables.pojos.Role
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord
import top.zbeboy.isy.domain.tables.records.RoleRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.platform.role.RoleBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-13 .
 **/
@Service("roleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class RoleServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<RoleBean>(), RoleService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var roleDao: RoleDao

    @Resource
    open lateinit var usersService: UsersService

    override fun findByRoleEnName(roleEnName: String): Role {
        return roleDao.fetchOne(ROLE.ROLE_EN_NAME, roleEnName)
    }

    override fun findByRoleNameAndRoleType(roleName: String, roleType: Int): Result<Record> {
        return create.select()
                .from(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName).and(ROLE.ROLE_TYPE.eq(roleType)))
                .fetch()
    }

    override fun findByRoleNameAndRoleTypeNeRoleId(roleName: String, roleType: Int, roleId: String): Result<Record> {
        return create.select()
                .from(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName).and(ROLE.ROLE_TYPE.eq(roleType)).and(ROLE.ROLE_ID.ne(roleId)))
                .fetch()
    }

    override fun findById(id: String): Role {
        return roleDao.findById(id)
    }

    override fun findByRoleIdRelation(roleId: String): Optional<Record> {
        return create.select()
                .from(ROLE)
                .leftJoin(COLLEGE_ROLE)
                .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                .leftJoin(COLLEGE)
                .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .leftJoin(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(ROLE.ROLE_ID.eq(roleId))
                .fetchOptional()
    }

    override fun findByRoleNameAndCollegeId(roleName: String, collegeId: Int): Result<Record> {
        return create.select()
                .from(ROLE)
                .leftJoin(COLLEGE_ROLE)
                .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                .where(ROLE.ROLE_NAME.eq(roleName).and(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId)))
                .fetch()
    }

    override fun findByRoleNameAndCollegeIdNeRoleId(roleName: String, collegeId: Int, roleId: String): Result<Record> {
        return create.select()
                .from(ROLE)
                .leftJoin(COLLEGE_ROLE)
                .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                .where(ROLE.ROLE_NAME.eq(roleName).and(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId)).and(ROLE.ROLE_ID.ne(roleId)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(role: Role) {
        roleDao.insert(role)
    }

    override fun update(role: Role) {
        roleDao.update(role)
    }

    override fun deleteById(id: String) {
        roleDao.deleteById(id)
    }

    override fun findInRoleId(ids: List<String>): Result<RoleRecord> {
        return create.selectFrom<RoleRecord>(ROLE)
                .where(ROLE.ROLE_ID.`in`(ids))
                .fetch()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<RoleBean>, roleBean: RoleBean): Result<Record>? {
        var records: Result<Record>? = null
        val defaultRoles = getDefaultRoles()
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                val selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                records = selectConditionStep.fetch()
            } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                val users = usersService.userFromSession
                val record = usersService.findUserSchoolInfo(users)
                val collegeId = getRoleCollegeId(record)
                val selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                records = selectConditionStep.fetch()
            }
        } else {
            // 分权限显示用户数据
            if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                val selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(a).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                records = selectConditionStep.fetch()
            } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                val users = usersService.userFromSession
                val record = usersService.findUserSchoolInfo(users)
                val collegeId = getRoleCollegeId(record)
                val selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                records = selectConditionStep.fetch()
            }
        }
        return records
    }

    override fun countAll(roleBean: RoleBean): Int {
        // 分权限显示用户数据
        if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            val defaultRoles = getDefaultRoles()
            val count = create.selectCount()
                    .from(ROLE)
                    .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                    .fetchOne()
            return count.value1()
        } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            val users = usersService.userFromSession
            val record = usersService.findUserSchoolInfo(users)
            val collegeId = getRoleCollegeId(record)
            val count = create.selectCount()
                    .from(ROLE)
                    .leftJoin(COLLEGE_ROLE)
                    .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                    .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                    .fetchOne()
            return count.value1()
        }
        return 0
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<RoleBean>, roleBean: RoleBean): Int {
        var count: Record1<Int>? = null
        val a = searchCondition(dataTablesUtils)
        val defaultRoles = getDefaultRoles()
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                val selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                count = selectConditionStep.fetchOne()
            } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                val users = usersService.userFromSession
                val record = usersService.findUserSchoolInfo(users)
                val collegeId = getRoleCollegeId(record)
                val selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                count = selectConditionStep.fetchOne()
            }
        } else {
            // 分权限显示用户数据
            if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                val selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(a).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                count = selectConditionStep.fetchOne()
            } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                val users = usersService.userFromSession
                val record = usersService.findUserSchoolInfo(users)
                val collegeId = getRoleCollegeId(record)
                val selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a).and(ROLE.ROLE_TYPE.eq(roleBean.roleType)))
                count = selectConditionStep.fetchOne()
            }
        }
        return if (!ObjectUtils.isEmpty(count)) {
            count!!.value1()
        } else 0
    }

    override fun findByRoleNameNotExistsCollegeRole(roleName: String): Result<RoleRecord> {
        val select = create.selectFrom<CollegeRoleRecord>(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
        return create.selectFrom<RoleRecord>(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName)).andNotExists(select).fetch()
    }

    override fun findByRoleNameNotExistsCollegeRoleNeRoleId(roleName: String, roleId: String): Result<RoleRecord> {
        val select = create.selectFrom<CollegeRoleRecord>(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
        return create.selectFrom<RoleRecord>(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName).and(ROLE.ROLE_ID.ne(roleId))).andNotExists(select).fetch()
    }

    override fun isCurrentUserInRole(role: String): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication
        if (authentication != null) {
            if (authentication.principal is UserDetails) {
                val springSecurityUser = authentication.principal as UserDetails
                return springSecurityUser.authorities.contains(SimpleGrantedAuthority(role))
            }
        }
        return false
    }

    override fun getRoleCollegeId(record: Optional<Record>): Int {
        var collegeId = 0
        if (record.isPresent) {
            val college = record.get().into(College::class.java)
            if (!ObjectUtils.isEmpty(college)) {
                collegeId = college.collegeId!!
            }
        }
        return collegeId
    }

    override fun getRoleDepartmentId(record: Optional<Record>): Int {
        var departmentId = 0
        if (record.isPresent) {
            val department = record.get().into(Department::class.java)
            if (!ObjectUtils.isEmpty(department)) {
                departmentId = department.departmentId!!
            }
        }
        return departmentId
    }

    /**
     * 获取系统默认角色
     *
     * @return 默认角色
     */
    private fun getDefaultRoles(): List<String> {
        val defaultRoles = ArrayList<String>()
        defaultRoles.add(Workbook.SYSTEM_AUTHORITIES)
        return defaultRoles
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<RoleBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search.getString("schoolName"))
            val collegeName = StringUtils.trimWhitespace(search.getString("collegeName"))
            val roleName = StringUtils.trimWhitespace(search.getString("roleName"))
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName))
            }

            if (StringUtils.hasLength(collegeName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName))
                } else {
                    a!!.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)))
                }
            }

            if (StringUtils.hasLength(roleName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    ROLE.ROLE_NAME.like(SQLQueryUtils.likeAllParam(roleName))
                } else {
                    a!!.and(ROLE.ROLE_NAME.like(SQLQueryUtils.likeAllParam(roleName)))
                }
            }
        }
        return a
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<RoleBean>, selectConditionStep: SelectConditionStep<Record>, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("role_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ROLE.ROLE_NAME.asc()
                    sortField[1] = ROLE.ROLE_ID.asc()
                } else {
                    sortField[0] = ROLE.ROLE_NAME.desc()
                    sortField[1] = ROLE.ROLE_ID.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = ROLE.ROLE_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = ROLE.ROLE_ID.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = ROLE.ROLE_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = ROLE.ROLE_ID.desc()
                }
            }

            if ("role_en_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ROLE.ROLE_EN_NAME.asc()
                    sortField[1] = ROLE.ROLE_ID.asc()
                } else {
                    sortField[0] = ROLE.ROLE_EN_NAME.desc()
                    sortField[1] = ROLE.ROLE_ID.desc()
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}