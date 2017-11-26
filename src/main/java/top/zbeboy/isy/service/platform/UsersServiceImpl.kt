package top.zbeboy.isy.service.platform

import org.apache.commons.lang3.math.NumberUtils
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.UsersDao
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord
import top.zbeboy.isy.domain.tables.records.UsersRecord
import top.zbeboy.isy.elastic.config.ElasticBook
import top.zbeboy.isy.elastic.pojo.UsersElastic
import top.zbeboy.isy.elastic.repository.StaffElasticRepository
import top.zbeboy.isy.elastic.repository.StudentElasticRepository
import top.zbeboy.isy.elastic.repository.UsersElasticRepository
import top.zbeboy.isy.security.MyUserImpl
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.bean.platform.users.UsersBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-19 .
 **/
@Service("usersService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class UsersServiceImpl @Autowired constructor(dslContext: DSLContext) : UsersService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var usersDao: UsersDao

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersElasticRepository: UsersElasticRepository

    @Resource
    open lateinit var studentElasticRepository: StudentElasticRepository

    @Resource
    open lateinit var staffElasticRepository: StaffElasticRepository

    override fun findByUsername(username: String): Users? {
        return usersDao.findById(username)
    }

    override fun findByJoinDateAndVerifyMailbox(joinDate: Date, verifyMailbox: Byte?): Result<UsersRecord> {
        return create.selectFrom<UsersRecord>(USERS)
                .where(USERS.JOIN_DATE.le(DateTimeUtils.utilDateToSqlDate(joinDate)).and(USERS.VERIFY_MAILBOX.eq(verifyMailbox)))
                .fetch()
    }

    override fun getUserFromSession(): Users? {
        val principal = SecurityContextHolder.getContext().authentication.principal
        var users: Users? = null
        if (!ObjectUtils.isEmpty(principal) && principal is MyUserImpl) {
            users = principal.getUsers()
        }
        return users
    }

    override fun findUserSchoolInfo(users: Users): Optional<Record> {
        var record = Optional.empty<Record>()
        if (!ObjectUtils.isEmpty(users)) {
            val userTypeId = users.usersTypeId!!
            val usersType = cacheManageService.findByUsersTypeId(userTypeId)
            if (usersType.usersTypeName == Workbook.STUDENT_USERS_TYPE) {// 学生
                record = studentService.findByUsernameRelation(users.username)
            } else if (usersType.usersTypeName == Workbook.STAFF_USERS_TYPE) {// 教职工
                record = staffService.findByUsernameRelation(users.username)
            }
        }
        return record
    }

    override fun findByMobile(mobile: String): List<Users> {
        return usersDao.fetchByMobile(mobile)
    }

    override fun findByMobileNeUsername(mobile: String, username: String): Result<UsersRecord> {
        return create.selectFrom<UsersRecord>(USERS)
                .where(USERS.MOBILE.eq(mobile).and(USERS.USERNAME.ne(username)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(users: Users) {
        usersDao.insert(users)
        val usersElastic = UsersElastic()
        usersElastic.username = users.username
        usersElastic.password = users.password
        usersElastic.enabled = users.enabled
        usersElastic.usersTypeId = users.usersTypeId
        usersElastic.usersTypeName = cacheManageService.findByUsersTypeId(users.usersTypeId!!).usersTypeName
        usersElastic.realName = users.realName
        usersElastic.mobile = users.mobile
        usersElastic.avatar = users.avatar
        usersElastic.verifyMailbox = users.verifyMailbox
        usersElastic.mailboxVerifyCode = users.mailboxVerifyCode
        usersElastic.passwordResetKey = users.passwordResetKey
        usersElastic.mailboxVerifyValid = users.mailboxVerifyValid
        usersElastic.passwordResetKeyValid = users.passwordResetKeyValid
        usersElastic.langKey = users.langKey
        usersElastic.joinDate = users.joinDate
        usersElastic.authorities = ElasticBook.NO_AUTHORITIES
        usersElasticRepository.save(usersElastic)
    }

    override fun update(users: Users) {
        usersDao.update(users)
        val usersElastic = usersElasticRepository.findOne(users.username)
        usersElastic.password = users.password
        usersElastic.enabled = users.enabled
        usersElastic.usersTypeId = users.usersTypeId
        usersElastic.realName = users.realName
        usersElastic.mobile = users.mobile
        usersElastic.avatar = users.avatar
        usersElastic.verifyMailbox = users.verifyMailbox
        usersElastic.mailboxVerifyCode = users.mailboxVerifyCode
        usersElastic.passwordResetKey = users.passwordResetKey
        usersElastic.mailboxVerifyValid = users.mailboxVerifyValid
        usersElastic.passwordResetKeyValid = users.passwordResetKeyValid
        usersElastic.langKey = users.langKey
        usersElastic.joinDate = users.joinDate
        usersElasticRepository.delete(usersElastic)
        usersElasticRepository.save(usersElastic)
        val usersType = cacheManageService.findByUsersTypeId(users.usersTypeId!!)
        if (usersType.usersTypeName == Workbook.STUDENT_USERS_TYPE) {
            val studentElastic = studentElasticRepository.findByUsername(users.username)
            studentElastic.enabled = users.enabled
            studentElastic.realName = users.realName
            studentElastic.mobile = users.mobile
            studentElastic.avatar = users.avatar
            studentElastic.langKey = users.langKey
            studentElastic.joinDate = users.joinDate
            studentElastic.verifyMailbox = users.verifyMailbox
            studentElasticRepository.delete(studentElastic)
            studentElasticRepository.save(studentElastic)
        } else if (usersType.usersTypeName == Workbook.STAFF_USERS_TYPE) {
            val staffElastic = staffElasticRepository.findByUsername(users.username)
            staffElastic.enabled = users.enabled
            staffElastic.realName = users.realName
            staffElastic.mobile = users.mobile
            staffElastic.avatar = users.avatar
            staffElastic.langKey = users.langKey
            staffElastic.joinDate = users.joinDate
            staffElastic.verifyMailbox = users.verifyMailbox
            staffElasticRepository.delete(staffElastic)
            staffElasticRepository.save(staffElastic)
        }
    }

    override fun updateEnabled(ids: List<String>, enabled: Byte?) {
        ids.forEach { id ->
            create.update<UsersRecord>(USERS).set<Byte>(USERS.ENABLED, enabled).where(USERS.USERNAME.eq(id)).execute()
            val usersElastic = usersElasticRepository.findOne(id)
            usersElastic.enabled = enabled
            usersElasticRepository.delete(usersElastic)
            usersElasticRepository.save(usersElastic)
            val usersType = cacheManageService.findByUsersTypeId(usersElastic.usersTypeId!!)
            if (usersType.usersTypeName == Workbook.STUDENT_USERS_TYPE) {
                val studentElastic = studentElasticRepository.findByUsername(usersElastic.username)
                studentElastic.enabled = usersElastic.enabled
                studentElasticRepository.delete(studentElastic)
                studentElasticRepository.save(studentElastic)
            } else if (usersType.usersTypeName == Workbook.STAFF_USERS_TYPE) {
                val staffElastic = staffElasticRepository.findByUsername(usersElastic.username)
                staffElastic.enabled = usersElastic.enabled
                staffElasticRepository.delete(staffElastic)
                staffElasticRepository.save(staffElastic)
            }
        }
    }

    override fun deleteById(username: String) {
        usersDao.deleteById(username)
        usersElasticRepository.delete(username)
    }

    override fun validSCDSOIsDel(users: Users): Boolean {
        // 检验学校等信息是否已被注销
        val usersType = cacheManageService.findByUsersTypeId(users.usersTypeId!!)
        var isDel = true
        if (!ObjectUtils.isEmpty(usersType)) {
            when (usersType.usersTypeName) {
                Workbook.STUDENT_USERS_TYPE // 学生
                -> isDel = validSCDSOForStudentIsDel(users)
                Workbook.STAFF_USERS_TYPE // 教职工
                -> isDel = validSCDSOForStaffIsDel(users)
                Workbook.SYSTEM_USERS_TYPE // 系统
                -> isDel = false
                else -> isDel = false
            }
        }
        return isDel
    }

    /**
     * 检验学生学校状态
     *
     * @param users 用户对象
     * @return true or false
     */
    private fun validSCDSOForStudentIsDel(users: Users): Boolean {
        val isNotDel: Byte = 0
        var isDel = true
        val studentRecord = studentService.findByUsernameRelation(users.username)
        if (studentRecord.isPresent) {
            val studentBean = studentRecord.get().into(StudentBean::class.java)
            if (studentBean.organizeIsDel == isNotDel &&
                    studentBean.scienceIsDel == isNotDel &&
                    studentBean.departmentIsDel == isNotDel &&
                    studentBean.collegeIsDel == isNotDel &&
                    studentBean.schoolIsDel == isNotDel) {
                isDel = false
            }
        }
        return isDel
    }

    /**
     * 检验教职工学校状态
     *
     * @param users 用户对象
     * @return true or false
     */
    private fun validSCDSOForStaffIsDel(users: Users): Boolean {
        val isNotDel: Byte = 0
        var isDel = true
        val staffRecord = staffService.findByUsernameRelation(users.username)
        if (staffRecord.isPresent) {
            val staffBean = staffRecord.get().into(StaffBean::class.java)
            if (staffBean.departmentIsDel == isNotDel &&
                    staffBean.collegeIsDel == isNotDel &&
                    staffBean.schoolIsDel == isNotDel) {
                isDel = false
            }
        }
        return isDel
    }

    override fun findByUsernameWithRole(username: String): Result<Record1<String>> {
        return create.select<String>(ROLE.ROLE_NAME)
                .from(USERS)
                .leftJoin(AUTHORITIES)
                .on(USERS.USERNAME.eq(AUTHORITIES.USERNAME))
                .leftJoin(ROLE)
                .on(AUTHORITIES.AUTHORITY.eq(ROLE.ROLE_EN_NAME))
                .where(USERS.USERNAME.eq(username))
                .fetch()
    }

    override fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): Result<Record> {
        val users = getUserFromSession()
        val select = existsAuthoritiesSelect()
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(USERS)
                    .join(USERS_TYPE)
                    .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                    .whereExists(select).and(USERS.USERNAME.ne(users!!.username))
            sortCondition(dataTablesUtils, selectConditionStep)
            pagination(dataTablesUtils, selectConditionStep)
            records = selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(USERS)
                    .join(USERS_TYPE)
                    .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                    .where(a).andExists(select).and(USERS.USERNAME.ne(users!!.username))
            sortCondition(dataTablesUtils, selectConditionStep)
            pagination(dataTablesUtils, selectConditionStep)
            records = selectConditionStep.fetch()
        }
        return records
    }

    override fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): Result<Record> {
        val select = create.selectFrom<AuthoritiesRecord>(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(USERS)
                    .join(USERS_TYPE)
                    .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                    .whereNotExists(select)
            sortCondition(dataTablesUtils, selectConditionStep)
            pagination(dataTablesUtils, selectConditionStep)
            records = selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(USERS)
                    .join(USERS_TYPE)
                    .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                    .where(a).andNotExists(select)
            sortCondition(dataTablesUtils, selectConditionStep)
            pagination(dataTablesUtils, selectConditionStep)
            records = selectConditionStep.fetch()
        }
        return records
    }

    override fun countAllExistsAuthorities(): Int {
        val users = getUserFromSession()
        val select = existsAuthoritiesSelect()
        val count = create.selectCount()
                .from(USERS)
                .whereExists(select).and(USERS.USERNAME.ne(users!!.username))
                .fetchOne()
        return count.value1()
    }

    override fun countAllNotExistsAuthorities(): Int {
        val select = create.selectFrom<AuthoritiesRecord>(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val count = create.selectCount()
                .from(USERS)
                .whereNotExists(select)
                .fetchOne()
        return count.value1()
    }

    override fun countByConditionExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): Int {
        val users = getUserFromSession()
        val select = existsAuthoritiesSelect()
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.selectCount()
                    .from(USERS)
                    .whereExists(select).and(USERS.USERNAME.ne(users!!.username))
            count = selectConditionStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(USERS)
                    .join(USERS_TYPE)
                    .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                    .where(a).andExists(select).and(USERS.USERNAME.ne(users!!.username))
            count = selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun countByConditionNotExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): Int {
        val select = create.selectFrom<AuthoritiesRecord>(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.selectCount()
                    .from(USERS)
                    .whereNotExists(select)
            count = selectConditionStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(USERS)
                    .join(USERS_TYPE)
                    .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                    .where(a).andNotExists(select)
            count = selectConditionStep.fetchOne()
        }
        return count.value1()

    }

    /**
     * 组装exists条件
     *
     * @return select
     */
    override fun existsAuthoritiesSelect(): Select<AuthoritiesRecord> {
        return if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            create.selectFrom<AuthoritiesRecord>(AUTHORITIES)
                    .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME).and(AUTHORITIES.AUTHORITY.ne(Workbook.SYSTEM_AUTHORITIES)))
        } else {
            create.selectFrom<AuthoritiesRecord>(AUTHORITIES)
                    .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME).and(AUTHORITIES.AUTHORITY.ne(Workbook.SYSTEM_AUTHORITIES)).and(AUTHORITIES.AUTHORITY.ne(Workbook.ADMIN_AUTHORITIES)))
        }
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    fun searchCondition(dataTablesUtils: DataTablesUtils<UsersBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val username = StringUtils.trimWhitespace(search!!.getString("username"))
            val mobile = StringUtils.trimWhitespace(search.getString("mobile"))
            val usersType = StringUtils.trimWhitespace(search.getString("usersType"))
            if (StringUtils.hasLength(username)) {
                a = USERS.USERNAME.like(SQLQueryUtils.likeAllParam(username))
            }

            if (StringUtils.hasLength(mobile)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.MOBILE.like(SQLQueryUtils.likeAllParam(mobile))
                } else {
                    a = a!!.and(USERS.MOBILE.like(SQLQueryUtils.likeAllParam(mobile)))
                }
            }

            if (StringUtils.hasLength(usersType)) {
                val usersTypeId = NumberUtils.toInt(usersType)
                if (usersTypeId > 0) {
                    if (ObjectUtils.isEmpty(a)) {
                        a = USERS_TYPE.USERS_TYPE_ID.eq(usersTypeId)
                    } else {
                        a = a!!.and(USERS_TYPE.USERS_TYPE_ID.eq(usersTypeId))
                    }
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
    fun sortCondition(dataTablesUtils: DataTablesUtils<UsersBean>, selectConditionStep: SelectConditionStep<Record>) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
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

            if ("role_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ROLE.ROLE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = ROLE.ROLE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("users_type_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS_TYPE.USERS_TYPE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS_TYPE.USERS_TYPE_NAME.desc()
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
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    fun pagination(dataTablesUtils: DataTablesUtils<UsersBean>, selectConditionStep: SelectConditionStep<Record>) {
        val start = dataTablesUtils.start
        val length = dataTablesUtils.length
        selectConditionStep.limit(start, length)
    }

}