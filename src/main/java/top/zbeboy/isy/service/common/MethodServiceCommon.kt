package top.zbeboy.isy.service.common

import org.jooq.Condition
import org.springframework.stereotype.Component
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.Tables
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-07 .
 **/
@Component
open class MethodServiceCommon {

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    /**
     * 构建该角色查询条件
     */
    fun buildCollegeCondition(): Condition? {
        var condition: Condition? = null // 分权限显示用户数据
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 管理员或其它角色,除系统
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val collegeId = roleService.getRoleCollegeId(record)
            condition = Tables.COLLEGE.COLLEGE_ID.eq(collegeId)
        }
        return condition
    }

    /**
     * 构建该角色查询条件
     */
    fun buildDepartmentCondition(): Condition? {
        val condition: Condition? = null // 分权限显示用户数据
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {// 系统角色直接回避
            return condition
        }
        return if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {// 管理员
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val collegeId = roleService.getRoleCollegeId(record)
            Tables.COLLEGE.COLLEGE_ID.eq(collegeId)
        } else {// 其它学校自由角色
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val departmentId = roleService.getRoleDepartmentId(record)
            Tables.DEPARTMENT.DEPARTMENT_ID.eq(departmentId)
        }
    }
}