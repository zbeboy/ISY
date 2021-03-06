package top.zbeboy.isy.web.platform.role

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.CollegeRole
import top.zbeboy.isy.domain.tables.pojos.Role
import top.zbeboy.isy.domain.tables.pojos.RoleApplication
import top.zbeboy.isy.service.data.CollegeRoleService
import top.zbeboy.isy.service.platform.RoleApplicationService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.ApplicationService
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.service.util.RandomUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.platform.role.RoleBean
import top.zbeboy.isy.web.bean.tree.TreeBean
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-11-16 .
 **/
@Controller
open class RoleController {

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var authoritiesService: AuthoritiesService

    @Resource
    open lateinit var roleApplicationService: RoleApplicationService

    @Resource
    open lateinit var collegeRoleService: CollegeRoleService

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon

    @Resource
    open lateinit var applicationService: ApplicationService

    /**
     * 平台角色页面
     *
     * @return 页面
     */
    @RequestMapping(value = ["/web/menu/platform/role"], method = [(RequestMethod.GET)])
    fun platformRole(): String {
        return "web/platform/role/role_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/platform/role/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun roleDatas(request: HttpServletRequest): DataTablesUtils<RoleBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("role_name")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("role_en_name")
        headers.add("allow_agent")
        headers.add("operator")
        val otherCondition = RoleBean()
        otherCondition.roleType = 2
        val dataTablesUtils = DataTablesUtils<RoleBean>(request, headers)
        val records = roleService.findAllByPage(dataTablesUtils, otherCondition)
        val roleBeens = roleService.dealDataRelation(records)
        dataTablesUtils.data = roleBeens
        dataTablesUtils.setiTotalRecords(roleService.countAll(otherCondition).toLong())
        dataTablesUtils.setiTotalDisplayRecords(roleService.countByCondition(dataTablesUtils, otherCondition).toLong())
        return dataTablesUtils
    }

    /**
     * 角色数据添加
     *
     * @param modelMap 页面对象
     * @return 添加页面
     */
    @RequestMapping(value = ["/web/platform/role/add"], method = [(RequestMethod.GET)])
    fun roleAdd(modelMap: ModelMap): String {
        pageParamControllerCommon.currentUserRoleNameAndCollegeIdPageParam(modelMap)
        return "web/platform/role/role_add::#page-wrapper"
    }

    /**
     * 角色数据编辑
     *
     * @param roleId   角色id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/platform/role/edit"], method = [(RequestMethod.GET)])
    fun roleEdit(@RequestParam("id") roleId: String, modelMap: ModelMap): String {
        val record = roleService.findByRoleIdRelation(roleId)
        val roleBean = roleService.dealDataRelationSingle(record)
        modelMap.addAttribute("role", roleBean)
        pageParamControllerCommon.currentUserRoleNameAndCollegeIdPageParam(modelMap)
        return "web/platform/role/role_edit::#page-wrapper"
    }

    /**
     * 保存时检验角色是否重复
     *
     * @param name      角色名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/platform/role/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("roleName") name: String, @RequestParam(value = "collegeId") collegeId: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val roleName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(roleName)) {
            val records = roleService.findByRoleNameAndCollegeId(roleName, collegeId)
            return if (records.isEmpty()) {
                ajaxUtils.success().msg("角色名不重复")
            } else {
                ajaxUtils.fail().msg("角色名重复")
            }
        } else {
            ajaxUtils.fail().msg("角色名不能为空")
        }
        return ajaxUtils
    }

    /**
     * 更新时检验角色名
     *
     * @param name      角色名
     * @param collegeId 院id
     * @param roleId    角色id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/platform/role/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("roleName") name: String, @RequestParam(value = "collegeId") collegeId: Int,
                    @RequestParam("roleId") roleId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val roleName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(roleName)) {
            val records = roleService.findByRoleNameAndCollegeIdNeRoleId(roleName, collegeId, roleId)
            return if (records.isEmpty()) {
                ajaxUtils.success().msg("角色名不重复")
            } else {
                ajaxUtils.fail().msg("角色名重复")
            }
        } else {
            ajaxUtils.fail().msg("角色名不能为空")
        }
        return ajaxUtils
    }

    /**
     * 保存角色
     *
     * @param collegeId      院id
     * @param roleName       角色名
     * @param applicationIds 应用ids
     * @param allowAgent 允许代理该角色
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/platform/role/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleSave(@RequestParam(value = "collegeId") collegeId: Int, @RequestParam("roleName") roleName: String,
                 allowAgent: Byte, applicationIds: String): AjaxUtils<*> {
        val role = Role()
        val roleId = UUIDUtils.getUUID()
        role.roleId = roleId
        role.roleName = StringUtils.trimAllWhitespace(roleName)
        role.roleEnName = "ROLE_" + RandomUtils.generateRoleEnName().toUpperCase()
        role.roleType = 2
        roleService.save(role)
        saveOrUpdate(collegeId, applicationIds, roleId, allowAgent)
        return AjaxUtils.of<Any>().success().msg("保存成功")
    }

    /**
     * 更新角色
     *
     * @param roleId         角色id
     * @param collegeId      院id
     * @param roleName       角色名
     * @param applicationIds 应用ids
     * @param allowAgent 允许代理该角色
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/platform/role/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleUpdate(@RequestParam("roleId") roleId: String, @RequestParam(value = "collegeId") collegeId: Int,
                   @RequestParam("roleName") roleName: String, allowAgent: Byte, applicationIds: String): AjaxUtils<*> {
        val role = roleService.findById(roleId)
        role.roleName = StringUtils.trimAllWhitespace(roleName)
        roleService.update(role)
        // 用户可能同时更改菜单
        roleApplicationService.deleteByRoleId(roleId)
        // 当是系统角色时，可能改变这个角色到其它院下
        collegeRoleService.deleteByRoleId(roleId)
        saveOrUpdate(collegeId, applicationIds, roleId, allowAgent)
        return AjaxUtils.of<Any>().success().msg("更新成功")
    }

    /**
     * 保存或更新与角色相关的表
     *
     * @param collegeId      院id
     * @param applicationIds 应用ids
     * @param roleId         角色id
     * @param allowAgent 允许代理该角色
     */
    private fun saveOrUpdate(collegeId: Int, applicationIds: String, roleId: String, allowAgent: Byte) {
        roleApplicationService.batchSaveRoleApplication(applicationIds, roleId)
        if (collegeId > 0) {
            val collegeRole = CollegeRole(roleId, collegeId, allowAgent)
            collegeRoleService.save(collegeRole)
        }
    }

    /**
     * 删除角色
     *
     * @param roleId 角色id
     * @return true成功
     */
    @RequestMapping(value = ["/web/platform/role/delete"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleDelete(@RequestParam("roleId") roleId: String): AjaxUtils<*> {
        val record = roleService.findByRoleIdRelation(roleId)
        val roleBean = roleService.dealDataRelationSingle(record)
        collegeRoleService.deleteByRoleId(roleId)
        roleApplicationService.deleteByRoleId(roleId)
        authoritiesService.deleteByAuthorities(roleBean.roleEnName!!)
        roleService.deleteById(roleId)
        return AjaxUtils.of<Any>().success().msg("删除成功")
    }

    /**
     * 操作角色代理
     *
     * @param roleId 角色id
     * @param allowAgent 允许代理该角色
     * @return 消息
     */
    @RequestMapping(value = ["/web/platform/role/agent"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleAgent(@RequestParam("roleId") roleId: String, @RequestParam("allowAgent") allowAgent: Byte): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val record = collegeRoleService.findByRoleId(roleId)
        if (record.isPresent) {
            val collegeRole = record.get().into(CollegeRole::class.java)
            collegeRole.allowAgent = allowAgent
            collegeRoleService.update(collegeRole)
            ajaxUtils.success().msg("操作角色代理成功")
        } else {
            ajaxUtils.fail().msg("未查询到该角色，操作角色代理失败")
        }
        return ajaxUtils
    }

    /**
     * 获取角色id 下的 应用id
     *
     * @param roleId 角色id
     * @return 应用
     */
    @RequestMapping(value = ["/web/platform/role/application/data"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleApplicationData(@RequestParam("roleId") roleId: String): AjaxUtils<RoleApplication> {
        val ajaxUtils = AjaxUtils.of<RoleApplication>()
        val roleApplicationRecords = roleApplicationService.findByRoleId(roleId)
        var roleApplications: List<RoleApplication> = ArrayList()
        if (roleApplicationRecords.isNotEmpty) {
            roleApplications = roleApplicationRecords.into(RoleApplication::class.java)
        }
        return ajaxUtils.success().listData(roleApplications)
    }

    /**
     * 数据json
     *
     * @param collegeId 院id
     * @return json
     */
    @RequestMapping(value = ["/web/platform/role/application/json"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun applicationJson(@RequestParam("collegeId") collegeId: Int): AjaxUtils<TreeBean> {
        val ajaxUtils = AjaxUtils.of<TreeBean>()
        val treeBeens = applicationService.getApplicationJsonByCollegeId("0", collegeId)
        return ajaxUtils.success().listData(treeBeens)
    }
}