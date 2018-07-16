package top.zbeboy.isy.service.data

import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.StaffDao
import top.zbeboy.isy.domain.tables.pojos.Staff
import top.zbeboy.isy.domain.tables.records.StaffRecord
import top.zbeboy.isy.service.common.MethodServiceCommon
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.sql.Date
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-09 .
 **/
@Service("staffService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class StaffServiceImpl @Autowired constructor(dslContext: DSLContext) : StaffService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var staffDao: StaffDao

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var departmentService: DepartmentService

    @Resource
    open lateinit var politicalLandscapeService: PoliticalLandscapeService

    @Resource
    open lateinit var nationService: NationService

    @Resource
    open lateinit var academicTitleService: AcademicTitleService

    @Resource
    open lateinit var methodServiceCommon: MethodServiceCommon


    override fun findByIdRelation(id: Int): Optional<Record> {
        return create.select()
                .from(STAFF)
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(STAFF.STAFF_ID.eq(id))
                .fetchOptional()
    }

    override fun findByIdRelationForUsers(id: Int): Optional<Record> {
        return create.select()
                .from(STAFF)
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(STAFF.STAFF_ID.eq(id))
                .fetchOptional()
    }

    override fun findInIdsRelation(id: List<Int>): Result<Record> {
        return create.select()
                .from(STAFF)
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(STAFF.STAFF_ID.`in`(id))
                .fetch()
    }

    override fun findByStaffNumber(staffNumber: String): Staff? {
        return staffDao.fetchOneByStaffNumber(staffNumber)
    }

    override fun findByDepartmentIdAndEnabledAndVerifyMailboxExistsAuthoritiesRelation(departmentId: Int, b: Byte?, verifyMailbox: Byte?): Result<Record> {
        val authoritiesRecordSelect = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        return create.select()
                .from(STAFF)
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(STAFF.DEPARTMENT_ID.eq(departmentId).and(USERS.ENABLED.eq(b)).and(USERS.VERIFY_MAILBOX.eq(verifyMailbox))).andExists(authoritiesRecordSelect)
                .fetch()
    }

    override fun findByUsername(username: String): Staff {
        return staffDao.fetchOne(STAFF.USERNAME, username)
    }

    override fun findByStaffNumberNeUsername(username: String, staffNumber: String): Result<StaffRecord> {
        return create.selectFrom(STAFF)
                .where(STAFF.STAFF_NUMBER.eq(staffNumber).and(STAFF.USERNAME.ne(username)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(staff: Staff) {
        staffDao.insert(staff)
    }

    override fun update(staff: Staff) {
        staffDao.update(staff)
    }

    override fun findByUsernameRelation(username: String): Optional<Record> {
        return create.select()
                .from(STAFF)
                .join(DEPARTMENT)
                .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .leftJoin(NATION)
                .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                .leftJoin(POLITICAL_LANDSCAPE)
                .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                .leftJoin(ACADEMIC_TITLE)
                .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                .where(STAFF.USERNAME.eq(username))
                .fetchOptional()
    }

    override fun deleteByUsername(username: String) {
        create.deleteFrom(STAFF).where(STAFF.USERNAME.eq(username)).execute()
    }

    override fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): Result<Record18<String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, Byte, String, Date>>? {
        val select = usersService.existsAuthoritiesSelect()
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildStaffCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select(USERS.REAL_NAME, STAFF.STAFF_NUMBER, USERS.USERNAME, USERS.MOBILE,
                        DSL.listAgg(ROLE.ROLE_NAME, " ").withinGroupOrderBy(ROLE.ROLE_NAME).`as`("roleName"),
                        SCHOOL.SCHOOL_NAME, COLLEGE.COLLEGE_NAME, DEPARTMENT.DEPARTMENT_NAME, ACADEMIC_TITLE.ACADEMIC_TITLE_NAME,
                        STAFF.POST, STAFF.SEX, STAFF.BIRTHDAY, NATION.NATION_NAME, POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME,
                        STAFF.FAMILY_RESIDENCE, USERS.ENABLED, USERS.LANG_KEY, USERS.JOIN_DATE)
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .join(AUTHORITIES)
                        .on(USERS.USERNAME.eq(AUTHORITIES.USERNAME))
                        .join(ROLE)
                        .on(ROLE.ROLE_EN_NAME.eq(AUTHORITIES.AUTHORITY))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .leftJoin(ACADEMIC_TITLE)
                        .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                        .whereExists(select)
                        .groupBy(USERS.USERNAME)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select(USERS.REAL_NAME, STAFF.STAFF_NUMBER, USERS.USERNAME, USERS.MOBILE,
                        DSL.listAgg(ROLE.ROLE_NAME, " ").withinGroupOrderBy(ROLE.ROLE_NAME).`as`("roleName"),
                        SCHOOL.SCHOOL_NAME, COLLEGE.COLLEGE_NAME, DEPARTMENT.DEPARTMENT_NAME, ACADEMIC_TITLE.ACADEMIC_TITLE_NAME,
                        STAFF.POST, STAFF.SEX, STAFF.BIRTHDAY, NATION.NATION_NAME, POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME,
                        STAFF.FAMILY_RESIDENCE, USERS.ENABLED, USERS.LANG_KEY, USERS.JOIN_DATE)
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .join(AUTHORITIES)
                        .on(USERS.USERNAME.eq(AUTHORITIES.USERNAME))
                        .join(ROLE)
                        .on(ROLE.ROLE_EN_NAME.eq(AUTHORITIES.AUTHORITY))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .leftJoin(ACADEMIC_TITLE)
                        .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                        .where(roleCondition).andExists(select)
                        .groupBy(USERS.USERNAME)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select(USERS.REAL_NAME, STAFF.STAFF_NUMBER, USERS.USERNAME, USERS.MOBILE,
                        DSL.listAgg(ROLE.ROLE_NAME, " ").withinGroupOrderBy(ROLE.ROLE_NAME).`as`("roleName"),
                        SCHOOL.SCHOOL_NAME, COLLEGE.COLLEGE_NAME, DEPARTMENT.DEPARTMENT_NAME, ACADEMIC_TITLE.ACADEMIC_TITLE_NAME,
                        STAFF.POST, STAFF.SEX, STAFF.BIRTHDAY, NATION.NATION_NAME, POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME,
                        STAFF.FAMILY_RESIDENCE, USERS.ENABLED, USERS.LANG_KEY, USERS.JOIN_DATE)
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .join(AUTHORITIES)
                        .on(USERS.USERNAME.eq(AUTHORITIES.USERNAME))
                        .join(ROLE)
                        .on(ROLE.ROLE_EN_NAME.eq(AUTHORITIES.AUTHORITY))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .leftJoin(ACADEMIC_TITLE)
                        .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                        .where(a).andExists(select)
                        .groupBy(USERS.USERNAME)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select(USERS.REAL_NAME, STAFF.STAFF_NUMBER, USERS.USERNAME, USERS.MOBILE,
                        DSL.listAgg(ROLE.ROLE_NAME, " ").withinGroupOrderBy(ROLE.ROLE_NAME).`as`("roleName"),
                        SCHOOL.SCHOOL_NAME, COLLEGE.COLLEGE_NAME, DEPARTMENT.DEPARTMENT_NAME, ACADEMIC_TITLE.ACADEMIC_TITLE_NAME,
                        STAFF.POST, STAFF.SEX, STAFF.BIRTHDAY, NATION.NATION_NAME, POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME,
                        STAFF.FAMILY_RESIDENCE, USERS.ENABLED, USERS.LANG_KEY, USERS.JOIN_DATE)
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .join(AUTHORITIES)
                        .on(USERS.USERNAME.eq(AUTHORITIES.USERNAME))
                        .join(ROLE)
                        .on(ROLE.ROLE_EN_NAME.eq(AUTHORITIES.AUTHORITY))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .leftJoin(ACADEMIC_TITLE)
                        .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                        .where(roleCondition!!.and(a)).andExists(select)
                        .groupBy(USERS.USERNAME)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            }
        }
    }

    override fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): Result<Record> {
        val select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildStaffCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .whereNotExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .where(roleCondition).andNotExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .where(a).andNotExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .where(roleCondition!!.and(a)).andNotExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            }
        }
    }

    override fun countAllExistsAuthorities(): Int {
        val select = usersService.existsAuthoritiesSelect()
        val roleCondition = buildStaffCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            create.selectCount()
                    .from(STAFF)
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .whereExists(select)
                    .fetchOne().value1()
        } else {
            create.selectCount()
                    .from(STAFF)
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(DEPARTMENT)
                    .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(roleCondition).andExists(select)
                    .fetchOne().value1()
        }
    }

    override fun countAllNotExistsAuthorities(): Int {
        val select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val roleCondition = buildStaffCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            create.selectCount()
                    .from(STAFF)
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .whereNotExists(select)
                    .fetchOne().value1()
        } else {
            create.selectCount()
                    .from(STAFF)
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(DEPARTMENT)
                    .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(roleCondition).andNotExists(select)
                    .fetchOne().value1()
        }
    }

    override fun countByConditionExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): Int {
        val select = usersService.existsAuthoritiesSelect()
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildStaffCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .whereExists(select)
                selectConditionStep.fetchOne().value1()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(roleCondition).andExists(select)
                selectConditionStep.fetchOne().value1()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .leftJoin(ACADEMIC_TITLE)
                        .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                        .where(a).andExists(select)
                selectConditionStep.fetchOne().value1()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .leftJoin(ACADEMIC_TITLE)
                        .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                        .where(roleCondition!!.and(a)).andExists(select)
                selectConditionStep.fetchOne().value1()
            }
        }

    }

    override fun countByConditionNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): Int {
        val select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildStaffCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .whereNotExists(select)
                selectConditionStep.fetchOne().value1()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(roleCondition).andNotExists(select)
                selectConditionStep.fetchOne().value1()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .leftJoin(ACADEMIC_TITLE)
                        .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                        .where(a).andNotExists(select)
                selectConditionStep.fetchOne().value1()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .leftJoin(ACADEMIC_TITLE)
                        .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                        .where(roleCondition!!.and(a)).andNotExists(select)
                selectConditionStep.fetchOne().value1()
            }
        }
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    fun searchCondition(dataTablesUtils: DataTablesUtils<StaffBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val school = StringUtils.trimWhitespace(search!!.getString("school"))
            val college = StringUtils.trimWhitespace(search.getString("college"))
            val department = StringUtils.trimWhitespace(search.getString("department"))
            val post = StringUtils.trimWhitespace(search.getString("post"))
            val staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"))
            val username = StringUtils.trimWhitespace(search.getString("username"))
            val mobile = StringUtils.trimWhitespace(search.getString("mobile"))
            val realName = StringUtils.trimWhitespace(search.getString("realName"))
            if (StringUtils.hasLength(school)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(school))
            }

            if (StringUtils.hasLength(college)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(college))
                } else {
                    a!!.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(college)))
                }
            }

            if (StringUtils.hasLength(department)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(department))
                } else {
                    a!!.and(DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(department)))
                }
            }

            if (StringUtils.hasLength(post)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STAFF.POST.like(SQLQueryUtils.likeAllParam(post))
                } else {
                    a!!.and(STAFF.POST.like(SQLQueryUtils.likeAllParam(post)))
                }
            }

            if (StringUtils.hasLength(staffNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber))
                } else {
                    a!!.and(STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber)))
                }
            }

            if (StringUtils.hasLength(username)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(username))
                } else {
                    a!!.and(STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(username)))
                }
            }

            if (StringUtils.hasLength(mobile)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    USERS.MOBILE.like(SQLQueryUtils.likeAllParam(mobile))
                } else {
                    a!!.and(USERS.MOBILE.like(SQLQueryUtils.likeAllParam(mobile)))
                }
            }

            if (StringUtils.hasLength(realName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(realName))
                } else {
                    a!!.and(USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(realName)))
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
    fun sortCondition(dataTablesUtils: DataTablesUtils<StaffBean>, selectConditionStep: SelectHavingStep<*>) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("staff_number".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STAFF.STAFF_NUMBER.asc()
                } else {
                    sortField[0] = STAFF.STAFF_NUMBER.desc()
                }
            }

            if ("real_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("username".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.USERNAME.desc()
                }
            }

            if ("mobile".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = USERS.MOBILE.asc()
                } else {
                    sortField[0] = USERS.MOBILE.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("department_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("academic_title_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("post".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STAFF.POST.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STAFF.POST.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("birthday".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STAFF.BIRTHDAY.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STAFF.BIRTHDAY.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("nation_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = NATION.NATION_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = NATION.NATION_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("politicalLandscape_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }


            if ("family_residence".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STAFF.FAMILY_RESIDENCE.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STAFF.FAMILY_RESIDENCE.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("enabled".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.ENABLED.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.ENABLED.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("lang_key".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.LANG_KEY.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.LANG_KEY.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("join_date".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.JOIN_DATE.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.JOIN_DATE.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

        }
        if (!ObjectUtils.isEmpty(sortField)) {
            selectConditionStep.orderBy(*sortField!!)
        }
    }

    /**
     * 分页方式
     *
     * @param dataTablesUtils 工具类
     * @param selectConditionStep 条件
     */
    fun pagination(dataTablesUtils: DataTablesUtils<StaffBean>, selectConditionStep: SelectHavingStep<*>) {
        val start = dataTablesUtils.start
        val length = dataTablesUtils.length
        selectConditionStep.limit(start, length)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildStaffCondition(): Condition? {
        return methodServiceCommon.buildDepartmentAndUserCondition()
    }
}