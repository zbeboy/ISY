package top.zbeboy.isy.web.data.science

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.Science
import top.zbeboy.isy.service.data.ScienceService
import top.zbeboy.isy.web.bean.data.science.ScienceBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.data.science.ScienceVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-03 .
 **/
@Controller
open class ScienceController {

    @Resource
    open lateinit var scienceService: ScienceService

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    /**
     * 通过系id获取全部专业
     *
     * @param departmentId 系id
     * @return 系下全部专业
     */
    @RequestMapping(value = ["/user/sciences"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun sciences(@RequestParam("departmentId") departmentId: Int): AjaxUtils<Science> {
        val ajaxUtils = AjaxUtils.of<Science>()
        val sciences = ArrayList<Science>()
        val isDel: Byte = 0
        val science = Science(0, "请选择专业", null, isDel, 0)
        sciences.add(science)
        val scienceRecords = scienceService.findByDepartmentIdAndIsDel(departmentId, isDel)
        scienceRecords.mapTo(sciences) { Science(it.scienceId, it.scienceName, it.scienceCode, it.scienceIsDel, it.departmentId) }
        return ajaxUtils.success().msg("获取专业数据成功！").listData(sciences)
    }

    /**
     * 通过年级与系id获取全部专业
     *
     * @param grade 年级
     * @return 年级下全部专业
     */
    @RequestMapping(value = ["/user/grade/sciences"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun gradeSciences(@RequestParam("grade") grade: String, @RequestParam("departmentId") departmentId: Int): AjaxUtils<Science> {
        val ajaxUtils = AjaxUtils.of<Science>()
        val scienceRecords = scienceService.findByGradeAndDepartmentId(grade, departmentId)
        val sciences = scienceRecords.into(Science::class.java)
        return ajaxUtils.success().msg("获取专业数据成功！").listData(sciences)
    }

    /**
     * 专业数据
     *
     * @return 专业数据页面
     */
    @RequestMapping(value = ["/web/menu/data/science"], method = [(RequestMethod.GET)])
    fun scienceData(): String {
        return "web/data/science/science_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/science/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun scienceDatas(request: HttpServletRequest): DataTablesUtils<ScienceBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("science_id")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("department_name")
        headers.add("science_name")
        headers.add("science_code")
        headers.add("science_is_del")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<ScienceBean>(request, headers)
        val records = scienceService.findAllByPage(dataTablesUtils)
        var scienceBeen: List<ScienceBean> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            scienceBeen = records.into(ScienceBean::class.java)
        }
        dataTablesUtils.data = scienceBeen
        dataTablesUtils.setiTotalRecords(scienceService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(scienceService.countByCondition(dataTablesUtils).toLong())

        return dataTablesUtils
    }

    /**
     * 专业数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = ["/web/data/science/add"], method = [(RequestMethod.GET)])
    fun scienceAdd(modelMap: ModelMap): String {
        pageParamControllerCommon.currentUserRoleNameAndCollegeIdAndDepartmentIdPageParam(modelMap)
        return "web/data/science/science_add::#page-wrapper"
    }

    /**
     * 专业数据编辑
     *
     * @param id       专业id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/data/science/edit"], method = [(RequestMethod.GET)])
    fun scienceEdit(@RequestParam("id") id: Int, modelMap: ModelMap): String {
        val record = scienceService.findByIdRelation(id)
        return if (record.isPresent) {
            modelMap.addAttribute("science", record.get().into(ScienceBean::class.java))
            pageParamControllerCommon.currentUserRoleNameAndCollegeIdAndDepartmentIdPageParam(modelMap)
            "web/data/science/science_edit::#page-wrapper"
        } else methodControllerCommon.showTip(modelMap, "未查询到相关专业信息")

    }

    /**
     * 保存时检验专业名是否重复
     *
     * @param name          专业名
     * @param departmentId 系id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/science/save/valid/name"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValidName(@RequestParam("scienceName") name: String, @RequestParam("departmentId") departmentId: Int): AjaxUtils<*> {
        val scienceName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(scienceName)) {
            val scienceRecords = scienceService.findByScienceNameAndDepartmentId(scienceName, departmentId)
            return if (ObjectUtils.isEmpty(scienceRecords)) {
                AjaxUtils.of<Any>().success().msg("专业名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("专业名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("专业名不能为空")
    }

    /**
     * 保存时检验专业代码是否重复
     *
     * @param code      专业代码
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/science/save/valid/code"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValidCode(@RequestParam("scienceCode") code: String): AjaxUtils<*> {
        val scienceCode = StringUtils.trimWhitespace(code)
        if (StringUtils.hasLength(scienceCode)) {
            val scienceRecords = scienceService.findByScienceCode(scienceCode)
            return if (ObjectUtils.isEmpty(scienceRecords)) {
                AjaxUtils.of<Any>().success().msg("专业代码不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("专业代码已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("专业代码不能为空")
    }

    /**
     * 检验编辑时专业名重复
     *
     * @param id           专业id
     * @param name          专业名
     * @param departmentId 系id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/science/update/valid/name"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValidName(@RequestParam("scienceId") id: Int, @RequestParam("scienceName") name: String, @RequestParam("departmentId") departmentId: Int): AjaxUtils<*> {
        val scienceName = StringUtils.trimWhitespace(name)
        val scienceRecords = scienceService.findByScienceNameAndDepartmentIdNeScienceId(scienceName, id, departmentId)
        return if (scienceRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("专业名不重复")
        } else AjaxUtils.of<Any>().fail().msg("专业名重复")

    }

    /**
     * 检验编辑时专业代码重复
     *
     * @param id          专业id
     * @param code      专业代码
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/science/update/valid/code"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValidCode(@RequestParam("scienceId") id: Int, @RequestParam("scienceCode") code: String): AjaxUtils<*> {
        val scienceCode = StringUtils.trimWhitespace(code)
        val scienceRecords = scienceService.findByScienceCodeNeScienceId(scienceCode, id)
        return if (scienceRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("专业代码不重复")
        } else AjaxUtils.of<Any>().fail().msg("专业代码重复")

    }

    /**
     * 保存专业信息
     *
     * @param scienceVo     专业
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/science/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun scienceSave(@Valid scienceVo: ScienceVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val science = Science()
            science.scienceIsDel = if (!ObjectUtils.isEmpty(scienceVo.scienceIsDel) && scienceVo.scienceIsDel == 1.toByte()) {
                1
            } else {
                0
            }
            science.scienceName = StringUtils.trimWhitespace(scienceVo.scienceName)
            science.scienceCode = StringUtils.trimWhitespace(scienceVo.scienceCode)
            science.departmentId = scienceVo.departmentId
            scienceService.save(science)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 保存专业更改
     *
     * @param scienceVo     专业
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/science/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun scienceUpdate(@Valid scienceVo: ScienceVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(scienceVo.scienceId)) {
            val science = scienceService.findById(scienceVo.scienceId!!)
            if (!ObjectUtils.isEmpty(science)) {
                science.scienceIsDel = if (!ObjectUtils.isEmpty(scienceVo.scienceIsDel) && scienceVo.scienceIsDel == 1.toByte()) {
                    1
                } else {
                    0
                }
                science.scienceName = StringUtils.trimWhitespace(scienceVo.scienceName)
                science.scienceCode = StringUtils.trimWhitespace(scienceVo.scienceCode)
                science.departmentId = scienceVo.departmentId
                scienceService.update(science)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }

    /**
     * 批量更改专业状态
     *
     * @param scienceIds 专业ids
     * @param isDel      is_del
     * @return true注销成功
     */
    @RequestMapping(value = ["/web/data/science/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun scienceUpdateDel(scienceIds: String, isDel: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(scienceIds) && SmallPropsUtils.StringIdsIsNumber(scienceIds)) {
            scienceService.updateIsDel(SmallPropsUtils.StringIdsToList(scienceIds), isDel)
            return AjaxUtils.of<Any>().success().msg("更改专业状态成功")
        }
        return AjaxUtils.of<Any>().fail().msg("更改专业状态失败")
    }
}