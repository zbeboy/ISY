package top.zbeboy.isy.test

import org.jooq.DSLContext
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import top.zbeboy.isy.Application
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.elastic.pojo.OrganizeElastic
import top.zbeboy.isy.elastic.pojo.StaffElastic
import top.zbeboy.isy.elastic.pojo.StudentElastic
import top.zbeboy.isy.elastic.pojo.UsersElastic
import top.zbeboy.isy.elastic.repository.*
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.system.AuthoritiesService
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [(Application::class)])
open class ElasticSyncData {
    @Qualifier("dsl")
    @Autowired
    open lateinit var create: DSLContext

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

    @Test
    fun cleanLog() {
        systemLogElasticRepository.deleteAll()
        systemMailboxElasticRepository.deleteAll()
        systemSmsElasticRepository.deleteAll()
    }

    @Test
    fun syncOrganizeData() {
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

        if (record!!.isNotEmpty) {
            val organizeElastics = record.into(OrganizeElastic::class.java)
            organizeElasticRepository.save<OrganizeElastic>(organizeElastics)
        }
    }

    @Test
    fun syncUsersData() {
        usersElasticRepository.deleteAll()
        val record = create.select()
                .from(USERS)
                .join(USERS_TYPE)
                .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                .fetch()
        val usersElastics = ArrayList<UsersElastic>()
        for (r in record!!) {
            val usersElastic = r.into(UsersElastic::class.java)
            val authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME))
            /**
             * -1 : 无权限
             * 0 :  有权限
             * 1 : 系统
             * 2 : 管理员
             * 3 : 运维
             */
            if (authoritiesRecords.isNotEmpty()) {
                var hasUse = false
                val stringBuilder = StringBuilder()
                for (a in authoritiesRecords) {
                    if (a.authority == Workbook.SYSTEM_AUTHORITIES) {
                        usersElastic.authorities = 1
                        hasUse = true
                    }
                    val tempRole = roleService.findByRoleEnName(a.authority)
                    stringBuilder.append(tempRole.roleName).append(" ")
                }

                if (!hasUse) {
                    for (a in authoritiesRecords) {
                        if (a.authority == Workbook.ADMIN_AUTHORITIES) {
                            usersElastic.authorities = 2
                            hasUse = true
                            break
                        }
                    }
                }

                if (!hasUse) {
                    for (a in authoritiesRecords) {
                        if (a.authority == Workbook.OPS_AUTHORITIES) {
                            usersElastic.authorities = 3
                            hasUse = true
                            break
                        }
                    }
                }
                if (!hasUse) {
                    usersElastic.authorities = 0
                }
                usersElastic.roleName = stringBuilder.toString().trim { it <= ' ' }
            } else {
                usersElastic.authorities = 99999
            }
            usersElastics.add(usersElastic)
        }
        usersElasticRepository.save(usersElastics)
    }

    @Test
    fun syncStudentData() {
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
        for (r in record!!) {
            val studentElastic = r.into(StudentElastic::class.java)
            val authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME))
            /**
             * -1 : 无权限
             * 0 :  有权限
             * 1 : 系统
             * 2 : 管理员
             */
            if (authoritiesRecords.isNotEmpty()) {
                var hasUse = false
                val stringBuilder = StringBuilder()
                for (a in authoritiesRecords) {
                    if (a.authority == Workbook.SYSTEM_AUTHORITIES) {
                        studentElastic.authorities = 1
                        hasUse = true
                    }
                    val tempRole = roleService.findByRoleEnName(a.authority)
                    stringBuilder.append(tempRole.roleName).append(" ")
                }

                if (!hasUse) {
                    for (a in authoritiesRecords) {
                        if (a.authority == Workbook.ADMIN_AUTHORITIES) {
                            studentElastic.authorities = 2
                            hasUse = true
                            break
                        }
                    }
                }

                if (!hasUse) {
                    for (a in authoritiesRecords) {
                        if (a.authority == Workbook.OPS_AUTHORITIES) {
                            studentElastic.authorities = 3
                            hasUse = true
                            break
                        }
                    }
                }
                if (!hasUse) {
                    studentElastic.authorities = 0
                }
                studentElastic.roleName = stringBuilder.toString().trim { it <= ' ' }
            } else {
                studentElastic.authorities = 99999
            }
            studentElastics.add(studentElastic)
        }
        studentElasticRepository.save(studentElastics)
    }

    @Test
    fun syncStaffData() {
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
        for (r in record!!) {
            val staffElastic = r.into(StaffElastic::class.java)
            val authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME))
            /**
             * -1 : 无权限
             * 0 :  有权限
             * 1 : 系统
             * 2 : 管理员
             */
            if (authoritiesRecords.isNotEmpty()) {
                var hasUse = false
                val stringBuilder = StringBuilder()
                for (a in authoritiesRecords) {
                    if (a.authority == Workbook.SYSTEM_AUTHORITIES) {
                        staffElastic.authorities = 1
                        hasUse = true
                    }
                    val tempRole = roleService.findByRoleEnName(a.authority)
                    stringBuilder.append(tempRole.roleName).append(" ")
                }

                if (!hasUse) {
                    for (a in authoritiesRecords) {
                        if (a.authority == Workbook.ADMIN_AUTHORITIES) {
                            staffElastic.authorities = 2
                            hasUse = true
                            break
                        }
                    }
                }

                if (!hasUse) {
                    for (a in authoritiesRecords) {
                        if (a.authority == Workbook.OPS_AUTHORITIES) {
                            staffElastic.authorities = 3
                            hasUse = true
                            break
                        }
                    }
                }
                if (!hasUse) {
                    staffElastic.authorities = 0
                }
                staffElastic.roleName = stringBuilder.toString().trim { it <= ' ' }
            } else {
                staffElastic.authorities = 99999
            }
            staffElastics.add(staffElastic)
        }
        staffElasticRepository.save(staffElastics)
    }
}