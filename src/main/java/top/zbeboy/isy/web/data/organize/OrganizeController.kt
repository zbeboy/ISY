package top.zbeboy.isy.web.data.organize

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
import top.zbeboy.isy.domain.tables.pojos.Organize
import top.zbeboy.isy.elastic.pojo.OrganizeElastic
import top.zbeboy.isy.glue.data.OrganizeGlue
import top.zbeboy.isy.service.common.CommonControllerMethodService
import top.zbeboy.isy.service.data.DepartmentService
import top.zbeboy.isy.service.data.OrganizeService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean
import top.zbeboy.isy.web.common.PageParamCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.SelectUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.data.organize.OrganizeVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-03 .
 **/
@Controller
open class OrganizeController {

    @Resource
    open lateinit var organizeService: OrganizeService

    @Resource
    open lateinit var pageParamCommon: PageParamCommon

    @Resource
    open lateinit var commonControllerMethodService: CommonControllerMethodService

    @Resource
    open lateinit var organizeGlue: OrganizeGlue

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var departmentService: DepartmentService

    /**
     * 通过专业id获取全部年级
     *
     * @param scienceId 专业id
     * @return 专业下全部年级
     */
    @RequestMapping(value = ["/user/grades"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun grades(@RequestParam("scienceId") scienceId: Int): AjaxUtils<SelectUtils> {
        val ajaxUtils = AjaxUtils.of<SelectUtils>()
        val grades = ArrayList<SelectUtils>()
        val selectUtils = SelectUtils(0, "0", "请选择年级", true)
        grades.add(selectUtils)
        val gradeRecord = organizeService.findByScienceIdAndDistinctGradeAndIsDel(scienceId, 0)
        gradeRecord.mapTo(grades) { SelectUtils(0, it.getValue("grade").toString(), it.getValue("grade").toString(), false) }
        return ajaxUtils.success().msg("获取年级数据成功！").listData(grades)
    }

    /**
     * 通过系id获取全部年级
     *
     * @param departmentId 系id
     * @return 系下全部年级
     */
    @RequestMapping(value = ["/user/department/grades"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun departmentGrades(@RequestParam("departmentId") departmentId: Int): AjaxUtils<SelectUtils> {
        val ajaxUtils = AjaxUtils.of<SelectUtils>()
        val grades = ArrayList<SelectUtils>()
        val selectUtils = SelectUtils(0, "0", "请选择年级", true)
        grades.add(selectUtils)
        val gradeRecord = organizeService.findByDepartmentIdAndDistinctGrade(departmentId)
        gradeRecord.mapTo(grades) { SelectUtils(0, it.getValue("grade").toString(), it.getValue("grade").toString(), false) }
        return ajaxUtils.success().msg("获取年级数据成功！").listData(grades)
    }

    /**
     * 通过年级和专业id获取全部班级
     *
     * @param grade     年级
     * @param scienceId 专业id
     * @return 年级下全部班级
     */
    @RequestMapping(value = ["/user/organizes"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun organizes(@RequestParam("grade") grade: String, @RequestParam("scienceId") scienceId: Int): AjaxUtils<Organize> {
        val ajaxUtils = AjaxUtils.of<Organize>()
        val organizes = ArrayList<Organize>()
        val organize = Organize(0, "请选择班级", 0, 0, "")
        organizes.add(organize)
        val organizeRecords = organizeService.findByGradeAndScienceId(StringUtils.trimWhitespace(grade), scienceId)
        organizeRecords.mapTo(organizes) { Organize(it.organizeId, it.organizeName, it.organizeIsDel, it.scienceId, it.grade) }
        return ajaxUtils.success().msg("获取班级数据成功！").listData(organizes)
    }

    /**
     * 班级数据
     *
     * @return 班级数据页面
     */
    @RequestMapping(value = ["/web/menu/data/organize"], method = [(RequestMethod.GET)])
    fun organizeData(): String {
        return "web/data/organize/organize_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/organize/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun organizeDatas(request: HttpServletRequest): DataTablesUtils<OrganizeBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("organize_id")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("department_name")
        headers.add("science_name")
        headers.add("grade")
        headers.add("organize_name")
        headers.add("organize_is_del")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<OrganizeBean>(request, headers)
        val resultUtils = organizeGlue.findAllByPage(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(organizeGlue.countAll())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }

    /**
     * 班级数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = ["/web/data/organize/add"], method = [(RequestMethod.GET)])
    fun organizeAdd(modelMap: ModelMap): String {
        pageParamCommon.currentUserRoleNameAndCollegeIdAndDepartmentIdPageParam(modelMap)
        return "web/data/organize/organize_add::#page-wrapper"
    }

    /**
     * 班级数据编辑
     *
     * @param id       班级id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/data/organize/edit"], method = [(RequestMethod.GET)])
    fun organizeEdit(@RequestParam("id") id: Int, modelMap: ModelMap): String {
        val record = organizeService.findByIdRelation(id)
        return if (record.isPresent) {
            modelMap.addAttribute("organize", record.get().into(OrganizeBean::class.java))
            pageParamCommon.currentUserRoleNameAndCollegeIdAndDepartmentIdPageParam(modelMap)
            "web/data/organize/organize_edit::#page-wrapper"
        } else {
            commonControllerMethodService.showTip(modelMap, "未查询到相关班级信息")
        }
    }

    /**
     * 保存时检验班级名是否重复
     *
     * @param organizeName 班级名
     * @param scienceId    专业id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/organize/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("organizeName") organizeName: String, @RequestParam("scienceId") scienceId: Int): AjaxUtils<*> {
        if (StringUtils.hasLength(organizeName)) {
            val scienceRecords = organizeService.findByOrganizeNameAndScienceId(organizeName, scienceId)
            return if (ObjectUtils.isEmpty(scienceRecords)) {
                AjaxUtils.of<Any>().success().msg("班级名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("班级名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("班级名不能为空")
    }

    /**
     * 检验编辑时班级名重复
     *
     * @param id           班级id
     * @param organizeName 班级名
     * @param scienceId    专业id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/organize/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("organizeId") id: Int, @RequestParam("organizeName") organizeName: String, @RequestParam("scienceId") scienceId: Int): AjaxUtils<*> {
        val organizeRecords = organizeService.findByOrganizeNameAndScienceIdNeOrganizeId(organizeName, id, scienceId)
        return if (organizeRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("班级名不重复")
        } else AjaxUtils.of<Any>().fail().msg("班级名重复")

    }

    /**
     * 保存班级信息
     *
     * @param organizeVo    班级
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/organize/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun organizeSave(@Valid organizeVo: OrganizeVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val organizeElastic = OrganizeElastic()
            organizeElastic.organizeName = organizeVo.organizeName
            organizeElastic.organizeIsDel = if (!ObjectUtils.isEmpty(organizeVo.organizeIsDel) && organizeVo.organizeIsDel == 1.toByte()) {
                1
            } else {
                0
            }
            organizeElastic.scienceId = organizeVo.scienceId
            organizeElastic.grade = organizeVo.grade
            organizeElastic.departmentId = organizeVo.departmentId
            organizeElastic.scienceName = organizeVo.scienceName

            // 非系统角色用户
            if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
                val departmentRecord = departmentService.findByIdRelation(organizeVo.departmentId!!)
                if (departmentRecord.isPresent) {
                    val organizeBean = departmentRecord.get().into(OrganizeBean::class.java)
                    organizeElastic.schoolId = organizeBean.schoolId
                    organizeElastic.schoolName = organizeBean.schoolName
                    organizeElastic.collegeId = organizeBean.collegeId
                    organizeElastic.collegeName = organizeBean.collegeName
                    organizeElastic.departmentName = organizeBean.departmentName
                    organizeService.save(organizeElastic)
                    ajaxUtils.success().msg("保存成功")
                } else {
                    ajaxUtils.fail().msg("未查询到相关系信息")
                }
            } else {
                organizeElastic.schoolId = organizeVo.schoolId
                organizeElastic.schoolName = organizeVo.schoolName
                organizeElastic.collegeId = organizeVo.collegeId
                organizeElastic.collegeName = organizeVo.collegeName
                organizeElastic.departmentName = organizeVo.departmentName
                organizeService.save(organizeElastic)
                ajaxUtils.success().msg("保存成功")
            }
        } else {
            ajaxUtils.fail().msg("填写信息错误，请检查")
        }
        return ajaxUtils
    }

    /**
     * 保存班级更改
     *
     * @param organizeVo    班级
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/organize/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun organizeUpdate(@Valid organizeVo: OrganizeVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(organizeVo.organizeId)) {
            val organize = organizeService.findById(organizeVo.organizeId!!)
            if (!ObjectUtils.isEmpty(organize)) {
                organize.organizeIsDel = if (!ObjectUtils.isEmpty(organizeVo.organizeIsDel) && organizeVo.organizeIsDel == 1.toByte()) {
                    1
                } else {
                    0
                }
                organize.organizeName = organizeVo.organizeName
                organize.scienceId = organizeVo.scienceId
                organize.grade = organizeVo.grade
                organizeService.update(organize)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }

    /**
     * 批量更改班级状态
     *
     * @param organizeIds 班级ids
     * @param isDel       is_del
     * @return true注销成功
     */
    @RequestMapping(value = ["/web/data/organize/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun organizeUpdateDel(organizeIds: String, isDel: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(organizeIds) && SmallPropsUtils.StringIdsIsNumber(organizeIds)) {
            organizeService.updateIsDel(SmallPropsUtils.StringIdsToList(organizeIds), isDel)
            return AjaxUtils.of<Any>().success().msg("更改班级状态成功")
        }
        return AjaxUtils.of<Any>().fail().msg("更改班级状态失败")
    }
}