package top.zbeboy.isy.web.platform.common

import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.Authorities
import top.zbeboy.isy.domain.tables.pojos.College
import top.zbeboy.isy.domain.tables.pojos.Role
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.data.CollegeRoleService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-12-18 .
 **/
@Component
open class RoleMethodControllerCommon {

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var collegeRoleService: CollegeRoleService

    @Resource
    open lateinit var authoritiesService: AuthoritiesService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var cacheManageService: CacheManageService

    /**
     * 获取设置角色时的角色数据
     *
     * @param username 被设置人的账号
     * @return 角色数据
     */
    fun getRoleData(username: String): ArrayList<Role> {
        // 根据此用户账号查询院下所有角色
        val users = usersService.findByUsername(username)
        val roles = ArrayList<Role>()
        if (!ObjectUtils.isEmpty(users)) {
            var isSystemAuthorities = false
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
                roles.add(roleService.findByRoleEnName(Workbook.ADMIN_AUTHORITIES))
                roles.add(roleService.findByRoleEnName(Workbook.OPS_AUTHORITIES))
                isSystemAuthorities = true
            }
            val record = usersService.findUserSchoolInfo(users!!)
            if (record.isPresent) {
                val college = record.get().into(College::class.java)
                val collegeRoleRecords = if (isSystemAuthorities || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                    collegeRoleService.findByCollegeId(college.collegeId!!)
                } else {
                    collegeRoleService.findByCollegeIdAndAllowAgent(college.collegeId!!, 1)
                }
                if (!ObjectUtils.isEmpty(collegeRoleRecords) && !collegeRoleRecords.isEmpty()) {
                    val roleIds = ArrayList<String>()
                    collegeRoleRecords.forEach { role -> roleIds.add(role.roleId) }
                    val roleRecords = roleService.findInRoleId(roleIds)
                    roles.addAll(roleRecords.into(Role::class.java))
                }
            }
        }
        return roles
    }

    /**
     * 保存角色数据
     */
    fun roleSave(username: String, roles: String, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (StringUtils.hasLength(roles)) {
            val users = usersService.findByUsername(username)
            if (!ObjectUtils.isEmpty(users)) {
                if (!ObjectUtils.isEmpty(users!!.verifyMailbox) && users.verifyMailbox > 0) {
                    val roleList = SmallPropsUtils.StringIdsToStringList(roles)
                    // 禁止非系统用户 提升用户权限到系统或管理员级别权限
                    if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) && (roleList.contains(Workbook.ADMIN_AUTHORITIES) || roleList.contains(Workbook.SYSTEM_AUTHORITIES))) {
                        return ajaxUtils.fail().msg("禁止非系统用户角色提升用户权限到系统或管理员级别权限")
                    }
                    authoritiesService.deleteByUsername(username)
                    roleList.forEach { role ->
                        val authorities = Authorities(username, role)
                        authoritiesService.save(authorities)
                    }
                    val curUsers = usersService.getUserFromSession()
                    methodControllerCommon.sendNotify(users, curUsers!!, "权限变更", "您的权限已发生变更，请登录查看。", request)
                    ajaxUtils.success().msg("更改用户角色成功")
                } else {
                    ajaxUtils.fail().msg("该用户未激活账号")
                }
            } else {
                ajaxUtils.fail().msg("未查询到该用户信息")
            }
        } else {
            ajaxUtils.fail().msg("用户角色参数异常")
        }
        return ajaxUtils
    }
}