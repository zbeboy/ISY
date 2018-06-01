package top.zbeboy.isy.service.data

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.elastic.repository.*
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.system.AuthoritiesService

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord
import top.zbeboy.isy.elastic.config.ElasticBook
import top.zbeboy.isy.elastic.pojo.OrganizeElastic
import top.zbeboy.isy.elastic.pojo.StaffElastic
import top.zbeboy.isy.elastic.pojo.StudentElastic
import top.zbeboy.isy.elastic.pojo.UsersElastic
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Service("elasticSyncService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class ElasticSyncServiceImpl @Autowired constructor(dslContext: DSLContext) : ElasticSyncService {

    private val create: DSLContext = dslContext

    @Autowired
    open lateinit var organizeElasticRepository: OrganizeElasticRepository

    @Autowired
    open lateinit var usersElasticRepository: UsersElasticRepository

    @Autowired
    open lateinit var studentElasticRepository: StudentElasticRepository

    @Autowired
    open lateinit var staffElasticRepository: StaffElasticRepository

    @Autowired
    open lateinit var systemLogElasticRepository: SystemLogElasticRepository

    @Autowired
    open lateinit var systemMailboxElasticRepository: SystemMailboxElasticRepository

    @Autowired
    open lateinit var systemSmsElasticRepository: SystemSmsElasticRepository

    @Autowired
    open lateinit var authoritiesService: AuthoritiesService

    @Autowired
    open lateinit var roleService: RoleService


    @Async
    override fun cleanSystemLog() {
        systemLogElasticRepository.deleteAll()
    }

    @Async
    override fun cleanSystemMailbox() {
        systemMailboxElasticRepository.deleteAll()
    }

    @Async
    override fun cleanSystemSms() {
        systemSmsElasticRepository.deleteAll()
    }

    @Async
    override fun syncOrganizeData() {
        organizeElasticRepository.deleteAll()
        val record = create.select()
                .from(ORGANIZE)
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .fetch()

        if (record.isNotEmpty) {
            val organizeElastics = record.into(OrganizeElastic::class.java)
            organizeElasticRepository.saveAll(organizeElastics)
        }
    }

    @Async
    override fun syncUsersData() {
        usersElasticRepository.deleteAll()
        val record = create.select()
                .from(USERS)
                .join(USERS_TYPE)
                .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                .fetch()
        val usersElastics = ArrayList<UsersElastic>()
        for (r in record) {
            val usersElastic = r.into(UsersElastic::class.java)
            val authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME))
            val map = buildAuthoritiesAndRoleNameData(authoritiesRecords)
            usersElastic.authorities = map["authorities"] as Int
            usersElastic.roleName = map["roleName"] as String
            usersElastics.add(usersElastic)
        }
        if (!ObjectUtils.isEmpty(usersElastics) && usersElastics.size > 0) {
            usersElasticRepository.saveAll(usersElastics)
        }
    }

    @Async
    override fun syncStudentData() {
        studentElasticRepository.deleteAll()
        val record = create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .leftJoin(NATION)
                .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                .leftJoin(POLITICAL_LANDSCAPE)
                .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                .fetch()
        val studentElastics = ArrayList<StudentElastic>()
        for (r in record) {
            val studentElastic = r.into(StudentElastic::class.java)
            val authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME))
            val map = buildAuthoritiesAndRoleNameData(authoritiesRecords)
            studentElastic.authorities = map["authorities"] as Int
            studentElastic.roleName = map["roleName"] as String
            studentElastics.add(studentElastic)
        }
        if (!ObjectUtils.isEmpty(studentElastics) && studentElastics.size > 0) {
            studentElasticRepository.saveAll(studentElastics)
        }
    }

    @Async
    override fun syncStaffData() {
        staffElasticRepository.deleteAll()
        val record = create.select()
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
                .fetch()
        val staffElastics = ArrayList<StaffElastic>()
        for (r in record) {
            val staffElastic = r.into(StaffElastic::class.java)
            val authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME))
            val map = buildAuthoritiesAndRoleNameData(authoritiesRecords)
            staffElastic.authorities = map["authorities"] as Int
            staffElastic.roleName = map["roleName"] as String
            staffElastics.add(staffElastic)
        }
        if (!ObjectUtils.isEmpty(staffElastics) && staffElastics.size > 0) {
            staffElasticRepository.saveAll(staffElastics)
        }
    }

    @Async
    override fun collegeRoleNameUpdate(collegeId: Int, roleName: String) {
        // step1 : 更新该院下学生
        val studentElastics = studentElasticRepository.findByCollegeIdAndRoleNameLike(collegeId, roleName)
        refreshStudent(studentElastics)

        // step2 : 更新用户表
        val usersElastics = ArrayList<UsersElastic>()
        studentElastics.forEach { studentElastic ->
            val user = usersElasticRepository.findById(studentElastic.username!!)
            if (user.isPresent) {
                usersElastics.add(user.get())
            }
        }

        // step3 : 更新改院下教职工
        val staffElastics = staffElasticRepository.findByCollegeIdAndRoleNameLike(collegeId, roleName)
        refreshStaff(staffElastics)

        // step4 : 更新用户表
        staffElastics.forEach { staffElastic ->
            val user = usersElasticRepository.findById(staffElastic.username!!)
            if (user.isPresent) {
                usersElastics.add(user.get())
            }
        }

        refreshUsers(usersElastics)
    }

    @Async
    override fun systemRoleNameUpdate(roleName: String) {
        val staffElastics = ArrayList<StaffElastic>()
        val studentElastics = ArrayList<StudentElastic>()
        val usersElastics = usersElasticRepository.findByRoleNameLike(roleName)
        for (usersElastic in usersElastics) {
            // 根据用户类型刷新角色名
            if (usersElastic.usersTypeName == Workbook.STAFF_USERS_TYPE) {
                staffElastics.add(staffElasticRepository.findByUsername(usersElastic.username!!))
            } else if (usersElastic.usersTypeName == Workbook.STUDENT_USERS_TYPE) {
                studentElastics.add(studentElasticRepository.findByUsername(usersElastic.username!!))
            }
        }
        refreshUsers(usersElastics)
        refreshStaff(staffElastics)
        refreshStudent(studentElastics)
    }

    private fun refreshStudent(studentElastics: List<StudentElastic>) {
        if (!ObjectUtils.isEmpty(studentElastics) && studentElastics.isNotEmpty()) {
            studentElasticRepository.deleteAll(studentElastics)
            for (r in studentElastics) {
                val authoritiesRecords = authoritiesService.findByUsername(r.username!!)
                val roleName = buildRoleNameData(authoritiesRecords)
                r.roleName = roleName
            }
            studentElasticRepository.saveAll(studentElastics)
        }
    }

    private fun refreshStaff(staffElastics: List<StaffElastic>) {
        if (!ObjectUtils.isEmpty(staffElastics) && staffElastics.isNotEmpty()) {
            staffElasticRepository.deleteAll(staffElastics)
            for (r in staffElastics) {
                val authoritiesRecords = authoritiesService.findByUsername(r.username!!)
                val roleName = buildRoleNameData(authoritiesRecords)
                r.roleName = roleName
            }
            staffElasticRepository.saveAll(staffElastics)
        }
    }

    private fun refreshUsers(usersElastics: List<UsersElastic>) {
        if (!ObjectUtils.isEmpty(usersElastics) && usersElastics.isNotEmpty()) {
            usersElasticRepository.deleteAll(usersElastics)
            for (r in usersElastics) {
                val authoritiesRecords = authoritiesService.findByUsername(r.username!!)
                val roleName = buildRoleNameData(authoritiesRecords)
                r.roleName = roleName
            }
            usersElasticRepository.saveAll(usersElastics)
        }
    }

    /**
     * 构造权限与角色数据
     *
     * @param authoritiesRecords 权限数据
     * @return 数据
     */
    private fun buildAuthoritiesAndRoleNameData(authoritiesRecords: List<AuthoritiesRecord>): Map<String, Any> {
        val map = HashMap<String, Any>()
        var authorities = ElasticBook.HAS_AUTHORITIES
        if (!ObjectUtils.isEmpty(authoritiesRecords) && authoritiesRecords.isNotEmpty()) {
            var hasUse = false
            val stringBuilder = StringBuilder()
            for (a in authoritiesRecords) {
                if (a.authority == Workbook.SYSTEM_AUTHORITIES) {
                    authorities = ElasticBook.SYSTEM_AUTHORITIES
                    hasUse = true
                }
                val tempRole = roleService.findByRoleEnName(a.authority)
                if (!ObjectUtils.isEmpty(tempRole) && StringUtils.hasLength(tempRole.roleName)) {
                    stringBuilder.append(tempRole.roleName).append(" ")
                } else {
                    throw NullPointerException("Not exist role for authority : " + a.authority)
                }
            }

            if (!hasUse) {
                for (a in authoritiesRecords) {
                    if (a.authority == Workbook.ADMIN_AUTHORITIES) {
                        authorities = ElasticBook.ADMIN_AUTHORITIES
                        hasUse = true
                        break
                    }
                }
            }

            if (!hasUse) {
                for (a in authoritiesRecords) {
                    if (a.authority == Workbook.OPS_AUTHORITIES) {
                        authorities = ElasticBook.OPS_AUTHORITIES
                        break
                    }
                }
            }
            map.put("roleName", stringBuilder.toString().trim { it <= ' ' })
        } else {
            authorities = ElasticBook.NO_AUTHORITIES
            map.put("roleName", "")
        }
        map.put("authorities", authorities)
        return map
    }

    /**
     * 构造角色名
     *
     * @param authoritiesRecords 权限数据
     * @return 角色名
     */
    private fun buildRoleNameData(authoritiesRecords: List<AuthoritiesRecord>): String {
        val stringBuilder = StringBuilder()
        if (!ObjectUtils.isEmpty(authoritiesRecords) && authoritiesRecords.isNotEmpty()) {
            authoritiesRecords
                    .map { roleService.findByRoleEnName(it.authority) }
                    .forEach { stringBuilder.append(it.roleName).append(" ") }
        }
        return stringBuilder.toString().trim { it <= ' ' }
    }
}