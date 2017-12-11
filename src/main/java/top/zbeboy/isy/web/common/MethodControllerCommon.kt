package top.zbeboy.isy.web.common

import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.elastic.config.ElasticBook
import top.zbeboy.isy.elastic.repository.StaffElasticRepository
import top.zbeboy.isy.elastic.repository.StudentElasticRepository
import top.zbeboy.isy.elastic.repository.UsersElasticRepository
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.CommonControllerMethodService
import top.zbeboy.isy.service.data.*
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-12-07 .
 **/
@Component
open class MethodControllerCommon {

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var departmentService: DepartmentService

    @Resource
    open lateinit var buildingService: BuildingService

    @Resource
    open lateinit var collegeRoleService: CollegeRoleService

    @Resource
    open lateinit var authoritiesService: AuthoritiesService

    @Resource
    open lateinit var usersElasticRepository: UsersElasticRepository

    @Resource
    open lateinit var studentElasticRepository: StudentElasticRepository

    @Resource
    open lateinit var staffElasticRepository: StaffElasticRepository

    @Resource
    open lateinit var commonControllerMethodService: CommonControllerMethodService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var staffService: StaffService

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

    /**
     * 通过毕业设计发布 生成楼数据
     *
     * @param graduationDesignRelease 毕业设计发布
     * @return 楼
     */
    fun generateBuildFromGraduationDesignRelease(graduationDesignRelease: GraduationDesignRelease): List<Building> {
        val buildings = ArrayList<Building>()
        val isDel: Byte = 0
        val building = Building(0, "请选择楼", isDel, 0)
        buildings.add(building)
        val record = departmentService.findByIdRelation(graduationDesignRelease.departmentId!!)
        if (record.isPresent) {
            val college = record.get().into(College::class.java)
            val buildingRecords = buildingService.findByCollegeIdAndIsDel(college.collegeId!!, isDel)
            buildingRecords.mapTo(buildings) { Building(it.buildingId, it.buildingName, it.buildingIsDel, it.collegeId) }
        }
        return buildings
    }

    /**
     * 组装提示信息
     *
     * @param modelMap 页面对象
     * @param tip      提示内容
     */
    fun showTip(modelMap: ModelMap, tip: String): String {
        modelMap.addAttribute("showTip", true)
        modelMap.addAttribute("tip", tip)
        modelMap.addAttribute("showButton", true)
        modelMap.addAttribute("buttonText", "返回上一页")
        return Workbook.TIP_PAGE
    }

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
                    val usersElastic = usersElasticRepository.findOne(username)
                    val roleEnNames = ArrayList<String>()
                    val stringBuilder = StringBuilder()
                    roleList.forEach { role ->
                        val authorities = Authorities(username, role)
                        authoritiesService.save(authorities)
                        val tempRole = roleService.findByRoleEnName(role)
                        roleEnNames.add(tempRole.roleEnName)
                        stringBuilder.append(tempRole.roleName).append(" ")
                    }
                    when {
                        roleEnNames.contains(Workbook.SYSTEM_AUTHORITIES) -> usersElastic.authorities = ElasticBook.SYSTEM_AUTHORITIES
                        roleEnNames.contains(Workbook.ADMIN_AUTHORITIES) -> usersElastic.authorities = ElasticBook.ADMIN_AUTHORITIES
                        else -> usersElastic.authorities = ElasticBook.HAS_AUTHORITIES
                    }
                    usersElastic.roleName = stringBuilder.toString().trim { it <= ' ' }
                    usersElasticRepository.delete(username)
                    usersElasticRepository.save(usersElastic)
                    val usersType = cacheManageService.findByUsersTypeId(users.usersTypeId!!)
                    if (usersType.usersTypeName == Workbook.STUDENT_USERS_TYPE) {
                        val studentElastic = studentElasticRepository.findByUsername(username)
                        studentElastic.authorities = usersElastic.authorities
                        studentElastic.roleName = usersElastic.roleName
                        studentElasticRepository.deleteByUsername(username)
                        studentElasticRepository.save(studentElastic)
                    } else if (usersType.usersTypeName == Workbook.STAFF_USERS_TYPE) {
                        val staffElastic = staffElasticRepository.findByUsername(username)
                        staffElastic.authorities = usersElastic.authorities
                        staffElastic.roleName = usersElastic.roleName
                        staffElasticRepository.deleteByUsername(username)
                        staffElasticRepository.save(staffElastic)
                    }
                    val curUsers = usersService.getUserFromSession()
                    val notify = "您的权限已变更为" + usersElastic.roleName + " ，请登录查看。"
                    commonControllerMethodService.sendNotify(users, curUsers, "权限变更", notify, request)
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

    /**
     * 更新用户状态
     *
     * @param userIds ids
     * @param enabled 状态
     * @return true 成功 false 失败
     */
    fun usersUpdateEnabled(userIds: String, enabled: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(userIds)) {
            usersService.updateEnabled(SmallPropsUtils.StringIdsToStringList(userIds), enabled)
            return AjaxUtils.of<Any>().success().msg("注销用户成功")
        }
        return AjaxUtils.of<Any>().fail().msg("注销用户失败")
    }

    /**
     * 删除无角色关联的用户
     *
     * @param userIds 用户账号
     * @return true 成功 false 失败
     */
    fun deleteUsers(userIds: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (StringUtils.hasLength(userIds)) {
            val ids = SmallPropsUtils.StringIdsToStringList(userIds)
            loop@ for (id in ids) {
                val authoritiesRecords = authoritiesService.findByUsername(id)
                if (!ObjectUtils.isEmpty(authoritiesRecords) && !authoritiesRecords.isEmpty()) {
                    ajaxUtils.fail().msg("用户'$id'存在角色关联，无法删除")
                    break
                } else {
                    val users = usersService.findByUsername(id)
                    if (!ObjectUtils.isEmpty(users)) {
                        val usersType = cacheManageService.findByUsersTypeId(users!!.usersTypeId)
                        when (usersType.usersTypeName) {
                            Workbook.STUDENT_USERS_TYPE  // 学生
                            -> {
                                studentService.deleteByUsername(id)
                                usersService.deleteById(id)
                                ajaxUtils.success().msg("删除用户成功")
                            }
                            Workbook.STAFF_USERS_TYPE  // 教职工
                            -> {
                                staffService.deleteByUsername(id)
                                usersService.deleteById(id)
                                ajaxUtils.success().msg("删除用户成功")
                            }
                            else -> {
                                ajaxUtils.fail().msg("未获取到用户'$id'类型")
                                break@loop
                            }
                        }
                    } else {
                        ajaxUtils.fail().msg("未查询到用户'$id'")
                        break
                    }
                }
            }
        } else {
            ajaxUtils.fail().msg("用户账号为空")
        }
        return ajaxUtils
    }
}