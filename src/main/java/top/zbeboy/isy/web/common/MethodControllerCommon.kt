package top.zbeboy.isy.web.common

import org.springframework.stereotype.Component
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-07 .
 **/
@Component
open class MethodControllerCommon {

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    /**
     * 根据角色获取院id
     *
     * @param collegeId 页面院id
     */
    fun roleCollegeId(collegeId: Int?): Int? {
        return if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 管理员或其它角色
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            roleService.getRoleCollegeId(record)
        } else {
            collegeId
        }
    }
}