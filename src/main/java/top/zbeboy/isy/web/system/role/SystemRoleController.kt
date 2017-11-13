package top.zbeboy.isy.web.system.role

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.RoleApplication
import top.zbeboy.isy.service.data.ElasticSyncService
import top.zbeboy.isy.service.platform.RoleApplicationService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.system.ApplicationService
import top.zbeboy.isy.web.bean.platform.role.RoleBean
import top.zbeboy.isy.web.bean.tree.TreeBean
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.ArrayList
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

import top.zbeboy.isy.domain.Tables.ROLE

/**
 * Created by zbeboy 2017-11-13 .
 **/
@Controller
open class SystemRoleController {

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var roleApplicationService: RoleApplicationService

    @Resource
    open lateinit var elasticSyncService: ElasticSyncService

    @Resource
    open lateinit var applicationService: ApplicationService

    /**
     * 系统角色页面
     *
     * @return 页面
     */
    @RequestMapping(value = "/web/menu/system/role", method = arrayOf(RequestMethod.GET))
    fun platformRole(): String {
        return "web/system/role/system_role::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/system/role/data", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun roleDatas(request: HttpServletRequest): DataTablesUtils<RoleBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("role_name")
        headers.add("role_en_name")
        headers.add("operator")
        val otherCondition = RoleBean()
        otherCondition.roleType = 1
        val dataTablesUtils = DataTablesUtils<RoleBean>(request, headers)
        val records = roleService.findAllByPage(dataTablesUtils, otherCondition)
        val roleBeens = ArrayList<RoleBean>()
        if (!ObjectUtils.isEmpty(records) && records!!.isNotEmpty) {
            for (record in records) {
                val roleBean = RoleBean()
                roleBean.roleId = record.getValue<String>(ROLE.ROLE_ID)
                roleBean.roleName = record.getValue<String>(ROLE.ROLE_NAME)
                roleBean.roleEnName = record.getValue<String>(ROLE.ROLE_EN_NAME)
                roleBeens.add(roleBean)
            }
        }
        dataTablesUtils.data = roleBeens
        dataTablesUtils.setiTotalRecords(roleService.countAll(otherCondition).toLong())
        dataTablesUtils.setiTotalDisplayRecords(roleService.countByCondition(dataTablesUtils, otherCondition).toLong())
        return dataTablesUtils
    }

    /**
     * 角色数据编辑
     *
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/system/role/edit", method = arrayOf(RequestMethod.GET))
    fun roleEdit(@RequestParam("id") roleId: String, modelMap: ModelMap): String {
        val record = roleService.findByRoleIdRelation(roleId)
        val roleBean = RoleBean()
        if (record.isPresent) {
            val temp = record.get()
            roleBean.roleId = temp.getValue<String>(ROLE.ROLE_ID)
            roleBean.roleName = temp.getValue<String>(ROLE.ROLE_NAME)
            roleBean.roleEnName = temp.getValue<String>(ROLE.ROLE_EN_NAME)
        }
        modelMap.addAttribute("role", roleBean)
        return "web/system/role/system_role_edit::#page-wrapper"
    }

    /**
     * 更新时检验角色名
     *
     * @param name   角色名
     * @param roleId 角色id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/system/role/update/valid", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun updateValid(@RequestParam("roleName") name: String, @RequestParam("roleId") roleId: String): AjaxUtils<*> {
        val roleName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(roleName)) {
            val records = roleService.findByRoleNameAndRoleTypeNeRoleId(name, 1, roleId)
            return if (records.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("角色名不重复")
            } else {
                AjaxUtils.of<Any>().fail().msg("角色名重复")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("角色名不能为空")
    }

    /**
     * 更新角色
     *
     * @param roleId         角色id
     * @param roleName       角色名
     * @param applicationIds 应用ids
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/system/role/update", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun roleUpdate(@RequestParam("roleId") roleId: String, @RequestParam("roleName") roleName: String, applicationIds: String): AjaxUtils<*> {
        val role = roleService.findById(roleId)
        val oldRoleName = role.roleName
        role.roleName = roleName
        roleService.update(role)
        roleApplicationService.deleteByRoleId(roleId)
        roleApplicationService.batchSaveRoleApplication(applicationIds, roleId)
        elasticSyncService.systemRoleNameUpdate(oldRoleName)
        return AjaxUtils.of<Any>().success().msg("更新成功")
    }

    /**
     * 获取角色id 下的 应用id
     *
     * @param roleId 角色id
     * @return 应用
     */
    @RequestMapping(value = "/web/system/role/application/data", method = arrayOf(RequestMethod.POST))
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
     * @return json
     */
    @RequestMapping(value = "/web/system/role/application/json", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun applicationJson(): AjaxUtils<TreeBean> {
        val ajaxUtils = AjaxUtils.of<TreeBean>()
        val treeBeens = applicationService.getApplicationJson("0")
        return ajaxUtils.success().listData(treeBeens)
    }
}