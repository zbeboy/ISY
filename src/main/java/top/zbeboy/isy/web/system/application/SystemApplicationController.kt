package top.zbeboy.isy.web.system.application

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.Application
import top.zbeboy.isy.domain.tables.pojos.RoleApplication
import top.zbeboy.isy.service.data.CollegeApplicationService
import top.zbeboy.isy.service.platform.RoleApplicationService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.system.ApplicationService
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.system.application.ApplicationBean
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.system.application.ApplicationVo
import java.util.ArrayList
import java.util.HashMap
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-11-17 .
 **/
@Controller
open class SystemApplicationController {

    @Resource
    open lateinit var applicationService: ApplicationService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var roleApplicationService: RoleApplicationService

    @Resource
    open lateinit var collegeApplicationService: CollegeApplicationService

    /**
     * 系统应用
     *
     * @return 系统应用页面
     */
    @RequestMapping(value = "/web/menu/system/application", method = arrayOf(RequestMethod.GET))
    fun systemLog(): String {
        return "web/system/application/system_application::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/system/application/data", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun applicationDatas(request: HttpServletRequest): DataTablesUtils<ApplicationBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("application_name")
        headers.add("application_en_name")
        headers.add("application_pid")
        headers.add("application_url")
        headers.add("icon")
        headers.add("application_sort")
        headers.add("application_code")
        headers.add("application_data_url_start_with")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<ApplicationBean>(request, headers)
        val records = applicationService.findAllByPage(dataTablesUtils)
        var applicationBeen: List<ApplicationBean> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            applicationBeen = records.into(ApplicationBean::class.java)
            applicationBeen.forEach { a ->
                if (a.applicationPid == "0") {
                    a.applicationPidName = "无"
                } else {
                    val application = applicationService.findById(a.applicationPid)
                    a.applicationPidName = application.applicationName
                }
            }
        }
        dataTablesUtils.data = applicationBeen
        dataTablesUtils.setiTotalRecords(applicationService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(applicationService.countByCondition(dataTablesUtils).toLong())
        return dataTablesUtils
    }

    /**
     * 应用添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/system/application/add", method = arrayOf(RequestMethod.GET))
    fun applicationAdd(): String {
        return "web/system/application/system_application_add::#page-wrapper"
    }

    /**
     * 应用更新
     *
     * @return 更新页面
     */
    @RequestMapping(value = "/web/system/application/edit", method = arrayOf(RequestMethod.GET))
    fun applicationEdit(@RequestParam("id") id: String, modelMap: ModelMap): String {
        val application = applicationService.findById(id)
        modelMap.addAttribute("sys_application", application)
        return "web/system/application/system_application_edit::#page-wrapper"
    }

    /**
     * 初始化添加页面数据
     *
     * @return 页面数据
     */
    @RequestMapping(value = "/web/system/application/init", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun init(): AjaxUtils<*> {
        // 一级与二级菜单
        val applicationPids = ArrayList<Application>()
        val application = Application()
        application.applicationId = "0"
        application.applicationName = "无"
        applicationPids.add(application)
        applicationPids.addAll(applicationService.findByPid("0"))
        val pids = ArrayList<String>()
        for (i in 1..applicationPids.size - 1) {
            pids.add(applicationPids[i].applicationId)
        }
        val applicationRecords = applicationService.findInPids(pids)
        if (applicationRecords.isNotEmpty) {
            val secondLevelIds = applicationRecords.into(Application::class.java)
            applicationPids.addAll(secondLevelIds)
        }

        val data = HashMap<String, Any>()
        data.put("applicationPids", applicationPids)
        return AjaxUtils.of<Any>().success().mapData(data)
    }

    /**
     * 检验保存时应用名是否重复
     *
     * @param applicationName 应用名
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/save/valid/name", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun saveValidName(@RequestParam("applicationName") applicationName: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationName)) {
            val applications = applicationService.findByApplicationName(applicationName)
            return if (ObjectUtils.isEmpty(applications) && applications.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("应用名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("应用名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("应用名不能为空")
    }

    /**
     * 检验更新时应用名是否重复
     *
     * @param applicationName 应用名
     * @param applicationId   应用id
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/update/valid/name", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun updateValidName(@RequestParam("applicationName") applicationName: String, @RequestParam("applicationId") applicationId: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationName)) {
            val applications = applicationService.findByApplicationNameNeApplicationId(applicationName, applicationId)
            return if (applications.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("应用名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("应用名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("应用名不能为空")
    }

    /**
     * 检验保存时应用英文名是否重复
     *
     * @param applicationEnName 应用英文名
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/save/valid/en_name", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun saveValidEnName(@RequestParam("applicationEnName") applicationEnName: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationEnName)) {
            val applications = applicationService.findByApplicationEnName(applicationEnName)
            return if (ObjectUtils.isEmpty(applications) && applications.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("应用英文名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("应用英文名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("应用英文名不能为空")
    }

    /**
     * 检验更新时应用英文名是否重复
     *
     * @param applicationEnName 应用英文名
     * @param applicationId     应用id
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/update/valid/en_name", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun updateValidEnName(@RequestParam("applicationEnName") applicationEnName: String, @RequestParam("applicationId") applicationId: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationEnName)) {
            val applications = applicationService.findByApplicationEnNameNeApplicationId(applicationEnName, applicationId)
            return if (applications.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("应用英文名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("应用英文名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("应用英文名不能为空")
    }

    /**
     * 检验保存时应用链接是否重复
     *
     * @param applicationUrl 应用链接
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/save/valid/url", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun saveValidUrl(@RequestParam("applicationUrl") applicationUrl: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationUrl)) {
            val applications = applicationService.findByApplicationUrl(applicationUrl)
            return if (ObjectUtils.isEmpty(applications) && applications.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("应用链接不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("应用链接已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("应用链接不能为空")
    }

    /**
     * 检验更新时应用链接是否重复
     *
     * @param applicationUrl 应用链接
     * @param applicationId  应用id
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/update/valid/url", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun updateValidUrl(@RequestParam("applicationUrl") applicationUrl: String, @RequestParam("applicationId") applicationId: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationUrl)) {
            val applications = applicationService.findByApplicationUrlNeApplicationId(applicationUrl, applicationId)
            return if (applications.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("应用链接不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("应用链接已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("应用链接不能为空")
    }

    /**
     * 检验保存时应用识别码是否重复
     *
     * @param applicationCode 应用识别码
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/save/valid/code", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun saveValidCode(@RequestParam("applicationCode") applicationCode: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationCode)) {
            val applications = applicationService.findByApplicationCode(applicationCode)
            return if (ObjectUtils.isEmpty(applications) && applications.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("应用识别码不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("应用识别码已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("应用识别码不能为空")
    }

    /**
     * 检验更新时应用识别码是否重复
     *
     * @param applicationCode 应用识别码
     * @param applicationId   应用id
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/update/valid/code", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun updateValidCode(@RequestParam("applicationCode") applicationCode: String, @RequestParam("applicationId") applicationId: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationCode)) {
            val applications = applicationService.findByApplicationCodeNeApplicationId(applicationCode, applicationId)
            return if (applications.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("应用识别码不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("应用识别码已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("应用识别码不能为空")
    }

    /**
     * 保存应用信息
     *
     * @param applicationVo 应用
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/system/application/save", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun applicationSave(@Valid applicationVo: ApplicationVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val application = Application()
            val applicationId = UUIDUtils.getUUID()
            application.applicationId = applicationId
            application.applicationName = applicationVo.applicationName
            application.applicationSort = applicationVo.applicationSort
            application.applicationPid = applicationVo.applicationPid
            application.applicationUrl = applicationVo.applicationUrl
            application.applicationCode = applicationVo.applicationCode
            application.applicationEnName = applicationVo.applicationEnName
            application.icon = applicationVo.icon
            application.applicationDataUrlStartWith = applicationVo.applicationDataUrlStartWith
            applicationService.save(application)
            val role = roleService.findByRoleEnName(Workbook.SYSTEM_AUTHORITIES)
            val roleApplication = RoleApplication(role.roleId, applicationId)
            roleApplicationService.save(roleApplication)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 更新应用信息
     *
     * @param applicationVo 应用
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/system/application/update", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun applicationUpdate(@Valid applicationVo: ApplicationVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(applicationVo.applicationId)) {
            val application = applicationService.findById(applicationVo.applicationId!!)
            application.applicationName = applicationVo.applicationName
            application.applicationSort = applicationVo.applicationSort
            application.applicationPid = applicationVo.applicationPid
            application.applicationUrl = applicationVo.applicationUrl
            application.applicationCode = applicationVo.applicationCode
            application.applicationEnName = applicationVo.applicationEnName
            application.icon = applicationVo.icon
            application.applicationDataUrlStartWith = applicationVo.applicationDataUrlStartWith
            applicationService.update(application)
            return AjaxUtils.of<Any>().success().msg("更新成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 批量删除应用
     *
     * @param applicationIds 应用ids
     * @return true删除成功
     */
    @RequestMapping(value = "/web/system/application/update/del", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun applicationUpdateDel(applicationIds: String): AjaxUtils<*> {
        if (StringUtils.hasLength(applicationIds)) {
            val ids = SmallPropsUtils.StringIdsToStringList(applicationIds)
            ids.forEach { id ->
                roleApplicationService.deleteByApplicationId(id)
                collegeApplicationService.deleteByApplicationId(id)
            }
            applicationService.deletes(ids)
            return AjaxUtils.of<Any>().success().msg("删除应用成功")
        }
        return AjaxUtils.of<Any>().fail().msg("删除应用失败")
    }
}