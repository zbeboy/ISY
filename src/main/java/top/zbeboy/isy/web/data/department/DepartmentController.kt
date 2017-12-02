package top.zbeboy.isy.web.data.department

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
import top.zbeboy.isy.domain.tables.pojos.Department
import top.zbeboy.isy.service.common.CommonControllerMethodService
import top.zbeboy.isy.service.data.DepartmentService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.web.bean.data.department.DepartmentBean
import top.zbeboy.isy.web.common.PageParamCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.data.department.DepartmentVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-02 .
 **/
@Controller
open class DepartmentController {

    @Resource
    open lateinit var departmentService: DepartmentService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var pageParamCommon: PageParamCommon

    @Resource
    open lateinit var commonControllerMethodService: CommonControllerMethodService

    /**
     * 根据院id获取全部系
     *
     * @param collegeId 院id
     * @return 院下全部系
     */
    @RequestMapping(value = ["/user/departments"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun departments(@RequestParam("collegeId") collegeId: Int): AjaxUtils<Department> {
        val ajaxUtils = AjaxUtils.of<Department>()
        val departments = ArrayList<Department>()
        val isDel: Byte = 0
        val department = Department(0, "请选择系", isDel, 0)
        departments.add(department)
        val departmentRecords = departmentService.findByCollegeIdAndIsDel(collegeId, isDel)
        departmentRecords.mapTo(departments) { Department(it.departmentId, it.departmentName, it.departmentIsDel, it.collegeId) }
        return ajaxUtils.success().msg("获取系数据成功！").listData(departments)
    }

    /**
     * 系数据
     *
     * @return 系数据页面
     */
    @RequestMapping(value = ["/web/menu/data/department"], method = [(RequestMethod.GET)])
    fun departmentData(): String {
        return "web/data/department/department_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/department/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun departmentDatas(request: HttpServletRequest): DataTablesUtils<DepartmentBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("department_id")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("department_name")
        headers.add("department_is_del")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<DepartmentBean>(request, headers)
        val records = departmentService.findAllByPage(dataTablesUtils)
        var departmentBeen: List<DepartmentBean> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            departmentBeen = records.into(DepartmentBean::class.java)
        }
        dataTablesUtils.data = departmentBeen
        dataTablesUtils.setiTotalRecords(departmentService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(departmentService.countByCondition(dataTablesUtils).toLong())
        return dataTablesUtils
    }

    /**
     * 系数据添加
     *
     * @param modelMap 页面对象
     * @return 添加页面
     */
    @RequestMapping(value = ["/web/data/department/add"], method = [(RequestMethod.GET)])
    fun departmentAdd(modelMap: ModelMap): String {
        pageParamCommon.currentUserRoleNamePageParam(modelMap)
        return "web/data/department/department_add::#page-wrapper"
    }

    /**
     * 系数据编辑
     *
     * @param id       系id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/data/department/edit"], method = [(RequestMethod.GET)])
    fun departmentEdit(@RequestParam("id") id: Int, modelMap: ModelMap): String {
        val record = departmentService.findByIdRelation(id)
        if (record.isPresent) {
            modelMap.addAttribute("department", record.get().into(DepartmentBean::class.java))
        } else {
            commonControllerMethodService.showTip(modelMap, "未查询到相关系信息")
        }
        pageParamCommon.currentUserRoleNamePageParam(modelMap)
        return "web/data/department/department_edit::#page-wrapper"
    }

    /**
     * 保存时检验系名是否重复
     *
     * @param name      系名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/department/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("departmentName") name: String, @RequestParam(value = "collegeId", defaultValue = "0") collegeId: Int): AjaxUtils<*> {
        val tempCollegeId = roleCollegeId(collegeId)
        val departmentName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(departmentName) && tempCollegeId > 0) {
            val departmentRecords = departmentService.findByDepartmentNameAndCollegeId(departmentName, tempCollegeId)
            return if (ObjectUtils.isEmpty(departmentRecords)) {
                AjaxUtils.of<Any>().success().msg("系名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("系名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("缺失必要参数")
    }

    /**
     * 检验编辑时系名重复
     *
     * @param id        系id
     * @param name      系名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/department/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("departmentId") id: Int, @RequestParam("departmentName") name: String, @RequestParam(value = "collegeId", defaultValue = "0") collegeId: Int): AjaxUtils<*> {
        val tempCollegeId = roleCollegeId(collegeId)
        val departmentName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(departmentName) && tempCollegeId > 0) {
            val departmentRecords = departmentService.findByDepartmentNameAndCollegeIdNeDepartmentId(departmentName, id, tempCollegeId)
            return if (departmentRecords.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("系名不重复")
            } else {
                AjaxUtils.of<Any>().fail().msg("系名重复")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("缺失必要参数")
    }

    /**
     * 保存系信息
     *
     * @param departmentVo  系
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/department/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun departmentSave(@Valid departmentVo: DepartmentVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val department = Department()
            department.departmentIsDel = if (!ObjectUtils.isEmpty(departmentVo.departmentIsDel) && departmentVo.departmentIsDel == 1.toByte()) {
                1
            } else {
                0
            }
            department.departmentName = StringUtils.trimWhitespace(departmentVo.departmentName)
            val collegeId = saveOrUpdateCollegeId(departmentVo)
            return if (collegeId > 0) {
                department.collegeId = collegeId
                departmentService.save(department)
                AjaxUtils.of<Any>().success().msg("保存成功")
            } else {
                AjaxUtils.of<Any>().fail().msg("保存失败，缺失必要参数")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 保存系更改
     *
     * @param departmentVo  系
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/department/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun departmentUpdate(@Valid departmentVo: DepartmentVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(departmentVo.departmentId)) {
            val department = departmentService.findById(departmentVo.departmentId!!)
            if (!ObjectUtils.isEmpty(department)) {
                department.departmentIsDel = if (!ObjectUtils.isEmpty(departmentVo.departmentIsDel) && departmentVo.departmentIsDel == 1.toByte()) {
                    1
                } else {
                    0
                }
                department.departmentName = departmentVo.departmentName
                val collegeId = saveOrUpdateCollegeId(departmentVo)
                return if (collegeId > 0) {
                    department.collegeId = collegeId
                    departmentService.update(department)
                    AjaxUtils.of<Any>().success().msg("更改成功")
                } else {
                    AjaxUtils.of<Any>().fail().msg("更新失败，缺失必要参数")
                }
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }

    /**
     * 保存或更新时获取院id
     *
     * @param departmentVo 系
     * @return 院id
     */
    private fun saveOrUpdateCollegeId(departmentVo: DepartmentVo): Int {
        return if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 管理员或其它角色
            roleCollegeId(0)
        } else {
            departmentVo.collegeId!!
        }
    }

    /**
     * 根据角色获取院id
     *
     * @param collegeId 页面院id
     */
    private fun roleCollegeId(collegeId: Int): Int {
        return if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 管理员或其它角色
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            roleService.getRoleCollegeId(record)
        } else {
            collegeId
        }
    }

    /**
     * 批量更改系状态
     *
     * @param departmentIds 系ids
     * @param isDel         is_del
     * @return true注销成功
     */
    @RequestMapping(value = ["/web/data/department/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun departmentUpdateDel(departmentIds: String, isDel: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(departmentIds) && SmallPropsUtils.StringIdsIsNumber(departmentIds)) {
            departmentService.updateIsDel(SmallPropsUtils.StringIdsToList(departmentIds), isDel)
            return AjaxUtils.of<Any>().success().msg("更改系状态成功")
        }
        return AjaxUtils.of<Any>().fail().msg("更改系状态失败")
    }
}