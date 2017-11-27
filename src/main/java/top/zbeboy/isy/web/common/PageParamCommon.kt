package top.zbeboy.isy.web.common

import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-27 .
 **/
@Component
open class PageParamCommon {

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    /**
     * 当前用户的角色名与院id
     *
     * @param modelMap 页面对象
     */
    fun currentUserRoleNameAndCollegeIdPageParam(modelMap: ModelMap) {
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME)
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME)
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val collegeId = roleService.getRoleCollegeId(record)
            modelMap.addAttribute("collegeId", collegeId)
        }
    }
}