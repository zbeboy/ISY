package top.zbeboy.isy.web.data.school

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.School
import top.zbeboy.isy.service.data.SchoolService
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.data.school.SchoolVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-01 .
 **/
@Controller
open class SchoolController {

    @Resource
    open lateinit var schoolService: SchoolService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    /**
     * 获取全部学校
     *
     * @return 全部学校 json
     */
    @RequestMapping(value = ["/user/schools"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun schools(): AjaxUtils<School> {
        val ajaxUtils = AjaxUtils.of<School>()
        val schools = ArrayList<School>()
        val isDel: Byte = 0
        val school = School(0, "请选择学校", isDel)
        schools.add(school)
        val schoolRecords = schoolService.findByIsDel(isDel)
        schoolRecords.mapTo(schools) { School(it.schoolId, it.schoolName, it.schoolIsDel) }
        return ajaxUtils.success().msg("获取学校数据成功！").listData(schools)
    }

    /**
     * 学校数据
     *
     * @return 学校数据页面
     */
    @RequestMapping(value = ["/web/menu/data/school"], method = [(RequestMethod.GET)])
    fun schoolData(): String {
        return "web/data/school/school_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/school/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun schoolDatas(request: HttpServletRequest): DataTablesUtils<School> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("school_id")
        headers.add("school_name")
        headers.add("school_is_del")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<School>(request, headers)
        val records = schoolService.findAllByPage(dataTablesUtils)
        var schools: List<School> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            schools = records.into(School::class.java)
        }
        dataTablesUtils.data = schools
        dataTablesUtils.setiTotalRecords(schoolService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(schoolService.countByCondition(dataTablesUtils).toLong())
        return dataTablesUtils
    }

    /**
     * 学校数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = ["/web/data/school/add"], method = [(RequestMethod.GET)])
    fun schoolAdd(): String {
        return "web/data/school/school_add::#page-wrapper"
    }

    /**
     * 学校数据编辑
     *
     * @param id       学校id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/data/school/edit"], method = [(RequestMethod.GET)])
    fun schoolEdit(@RequestParam("id") id: Int, modelMap: ModelMap): String {
        val school = schoolService.findById(id)
        return if (!ObjectUtils.isEmpty(school)) {
            modelMap.addAttribute("school", school)
            "web/data/school/school_edit::#page-wrapper"
        } else methodControllerCommon.showTip(modelMap, "未查询到相关学校信息")
    }

    /**
     * 保存时检验学校名是否重复
     *
     * @param name      学校名
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/school/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("schoolName") name: String): AjaxUtils<*> {
        val schoolName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(schoolName)) {
            val schools = schoolService.findBySchoolName(schoolName)
            return if (ObjectUtils.isEmpty(schools)) {
                AjaxUtils.of<Any>().success().msg("学校名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("学校名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("学校名不能为空")
    }

    /**
     * 保存学校信息
     *
     * @param schoolVo      学校
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/school/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun schoolSave(@Valid schoolVo: SchoolVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val school = School()
            var isDel: Byte? = 0
            if (null != schoolVo.schoolIsDel && schoolVo.schoolIsDel == 1.toByte()) {
                isDel = 1
            }
            school.schoolIsDel = isDel
            school.schoolName = StringUtils.trimWhitespace(schoolVo.schoolName)
            schoolService.save(school)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 检验编辑时学校名重复
     *
     * @param id         学校id
     * @param name       学校名
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/school/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("schoolId") id: Int, @RequestParam("schoolName") name: String): AjaxUtils<*> {
        val schoolName = StringUtils.trimWhitespace(name)
        val schoolRecords = schoolService.findBySchoolNameNeSchoolId(schoolName, id)
        return if (schoolRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("学校名不重复")
        } else AjaxUtils.of<Any>().fail().msg("学校名重复")

    }

    /**
     * 保存学校更改
     *
     * @param schoolVo      学校
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/school/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun schoolUpdate(@Valid schoolVo: SchoolVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(schoolVo.schoolId)) {
            val school = schoolService.findById(schoolVo.schoolId!!)
            if (!ObjectUtils.isEmpty(school)) {
                var isDel: Byte? = 0
                if (!ObjectUtils.isEmpty(schoolVo.schoolIsDel) && schoolVo.schoolIsDel == 1.toByte()) {
                    isDel = 1
                }
                school.schoolIsDel = isDel
                school.schoolName = StringUtils.trimWhitespace(schoolVo.schoolName)
                schoolService.update(school)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }

    /**
     * 批量更改学校状态
     *
     * @param schoolIds 学校ids
     * @param isDel     is_del
     * @return true注销成功
     */
    @RequestMapping(value = ["/web/data/school/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun schoolUpdateDel(schoolIds: String, isDel: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(schoolIds) && SmallPropsUtils.StringIdsIsNumber(schoolIds)) {
            schoolService.updateIsDel(SmallPropsUtils.StringIdsToList(schoolIds), isDel)
            return AjaxUtils.of<Any>().success().msg("更改学校状态成功")
        }
        return AjaxUtils.of<Any>().fail().msg("更改学校状态失败")
    }
}