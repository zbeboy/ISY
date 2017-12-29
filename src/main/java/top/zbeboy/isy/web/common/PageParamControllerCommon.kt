package top.zbeboy.isy.web.common

import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-27 .
 **/
@Component
open class PageParamControllerCommon {

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var cacheManageService: CacheManageService

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
            val collegeId = cacheManageService.getRoleCollegeId(users!!)
            modelMap.addAttribute("collegeId", collegeId)
        }
    }

    /**
     * 当前用户的角色名与院id
     *
     * @param modelMap 页面对象
     */
    fun currentUserRoleNameAndCollegeIdNoAdminPageParam(modelMap: ModelMap) {
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME)
        } else {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME)
            val users = usersService.getUserFromSession()
            val collegeId = cacheManageService.getRoleCollegeId(users!!)
            modelMap.addAttribute("collegeId", collegeId)
        }
    }

    /**
     * 当前用户的角色名,院id与系id
     *
     * @param modelMap 页面对象
     */
    fun currentUserRoleNameAndCollegeIdAndDepartmentIdPageParam(modelMap: ModelMap) {
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME)
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME)
            val users = usersService.getUserFromSession()
            val collegeId = cacheManageService.getRoleCollegeId(users!!)
            modelMap.addAttribute("collegeId", collegeId)
        } else {
            val users = usersService.getUserFromSession()
            val departmentId = cacheManageService.getRoleDepartmentId(users!!)
            modelMap.addAttribute("departmentId", departmentId)
        }
    }

    /**
     * 当前用户的角色名
     *
     * @param modelMap 页面对象
     */
    fun currentUserRoleNamePageParam(modelMap: ModelMap) {
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME)
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME)
        }
    }

    /**
     * 当前用户的角色名和类型
     *
     * @param modelMap 页面对象
     */
    fun currentUserRoleNameAndTypeNamePageParam(modelMap: ModelMap) {
        val users = usersService.getUserFromSession()
        val usersType = cacheManageService.findByUsersTypeId(users!!.usersTypeId!!)
        modelMap.addAttribute("usersTypeName", usersType.usersTypeName)
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME)
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME)
        }
    }
}