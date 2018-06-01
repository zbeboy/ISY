package top.zbeboy.isy.web.data.college

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.College
import top.zbeboy.isy.domain.tables.pojos.CollegeApplication
import top.zbeboy.isy.service.data.CollegeApplicationService
import top.zbeboy.isy.service.data.CollegeService
import top.zbeboy.isy.service.system.ApplicationService
import top.zbeboy.isy.web.bean.data.college.CollegeBean
import top.zbeboy.isy.web.bean.tree.TreeBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.data.college.CollegeVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-01 .
 **/
@Controller
open class CollegeController {

    @Resource
    open lateinit var collegeService: CollegeService

    @Resource
    open lateinit var collegeApplicationService: CollegeApplicationService

    @Resource
    open lateinit var applicationService: ApplicationService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    /**
     * 通过学校id获取全部院
     *
     * @param schoolId 学校id
     * @return 学校下全部院
     */
    @RequestMapping(value = ["/user/colleges"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun colleges(@RequestParam("schoolId") schoolId: Int): AjaxUtils<College> {
        val ajaxUtils = AjaxUtils.of<College>()
        val colleges = ArrayList<College>()
        val isDel: Byte = 0
        val college = College(0, "请选择院", null, null, isDel, 0)
        colleges.add(college)
        val collegeRecords = collegeService.findBySchoolIdAndIsDel(schoolId, isDel)
        collegeRecords.mapTo(colleges) { College(it.collegeId, it.collegeName, it.collegeAddress, it.collegeCode, it.collegeIsDel, it.schoolId) }
        return ajaxUtils.success().msg("获取院数据成功！").listData(colleges)
    }

    /**
     * 院数据
     *
     * @return 院数据页面
     */
    @RequestMapping(value = ["/web/menu/data/college"], method = [(RequestMethod.GET)])
    fun collegeData(): String {
        return "web/data/college/college_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/college/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun collegeDatas(request: HttpServletRequest): DataTablesUtils<CollegeBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("college_id")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("college_code")
        headers.add("college_address")
        headers.add("college_is_del")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<CollegeBean>(request, headers)
        val records = collegeService.findAllByPage(dataTablesUtils)
        var colleges: List<CollegeBean> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            colleges = records.into(CollegeBean::class.java)
        }
        dataTablesUtils.data = colleges
        dataTablesUtils.setiTotalRecords(collegeService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(collegeService.countByCondition(dataTablesUtils).toLong())
        return dataTablesUtils
    }

    /**
     * 院数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = ["/web/data/college/add"], method = [(RequestMethod.GET)])
    fun collegeAdd(): String {
        return "web/data/college/college_add::#page-wrapper"
    }

    /**
     * 院数据编辑
     *
     * @param id       院id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/data/college/edit"], method = [(RequestMethod.GET)])
    fun collegeEdit(@RequestParam("id") id: Int, modelMap: ModelMap): String {
        val college = collegeService.findById(id)
        return if(!ObjectUtils.isEmpty(college)){
            modelMap.addAttribute("college", college)
            "web/data/college/college_edit::#page-wrapper"
        } else methodControllerCommon.showTip(modelMap,"未查询到相关院信息")
    }

    /**
     * 保存时检验院名是否重复
     *
     * @param name         院名
     * @param schoolId    学校id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/college/save/valid/name"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValidName(@RequestParam("collegeName") name: String, @RequestParam("schoolId") schoolId: Int): AjaxUtils<*> {
        val collegeName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(collegeName)) {
            val collegeRecords = collegeService.findByCollegeNameAndSchoolId(collegeName, schoolId)
            return if (collegeRecords.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("院名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("院名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("院名不能为空")
    }

    /**
     * 检验院代码是否重复
     *
     * @param code      院代码
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/college/save/valid/code"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValidCode(@RequestParam("collegeCode") code: String): AjaxUtils<*> {
        val collegeCode = StringUtils.trimWhitespace(code)
        if (StringUtils.hasLength(collegeCode)) {
            val collegeRecords = collegeService.findByCollegeCode(collegeCode)
            return if (collegeRecords.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("院代码不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("院代码已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("院代码不能为空")
    }

    /**
     * 检验编辑时院名重复
     *
     * @param id          院id
     * @param name        院名
     * @param schoolId    学校id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/college/update/valid/name"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValidName(@RequestParam("collegeId") id: Int, @RequestParam("collegeName") name: String, @RequestParam("schoolId") schoolId: Int): AjaxUtils<*> {
        val collegeName = StringUtils.trimWhitespace(name)
        val collegeRecords = collegeService.findByCollegeNameAndSchoolIdNeCollegeId(collegeName, id, schoolId)
        return if (collegeRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("院名不重复")
        } else AjaxUtils.of<Any>().fail().msg("院名重复")

    }

    /**
     * 检验编辑时院代码重复
     *
     * @param id          院id
     * @param code       院代码
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/college/update/valid/code"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValidName(@RequestParam("collegeId") id: Int, @RequestParam("collegeCode") code: String): AjaxUtils<*> {
        val collegeCode = StringUtils.trimWhitespace(code)
        val collegeRecords = collegeService.findByCollegeCodeNeCollegeId(collegeCode, id)
        return if (collegeRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("院代码不重复")
        } else AjaxUtils.of<Any>().fail().msg("院代码重复")

    }

    /**
     * 保存院更改
     *
     * @param collegeVo     院
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/college/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun collegeUpdate(@Valid collegeVo: CollegeVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(collegeVo.collegeId)) {
            val college = collegeService.findById(collegeVo.collegeId!!)
            if (!ObjectUtils.isEmpty(college)) {
                var isDel: Byte? = 0
                if (!ObjectUtils.isEmpty(collegeVo.collegeIsDel) && collegeVo.collegeIsDel == 1.toByte()) {
                    isDel = 1
                }
                college.collegeIsDel = isDel
                college.collegeName = StringUtils.trimWhitespace(collegeVo.collegeName!!)
                college.collegeCode = StringUtils.trimWhitespace(collegeVo.collegeCode!!)
                college.collegeAddress = StringUtils.trimWhitespace(collegeVo.collegeAddress!!)
                college.schoolId = collegeVo.schoolId
                collegeService.update(college)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }

    /**
     * 保存院信息
     *
     * @param collegeVo     院
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/college/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun collegeSave(@Valid collegeVo: CollegeVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val college = College()
            var isDel: Byte? = 0
            if (null != collegeVo.collegeIsDel && collegeVo.collegeIsDel == 1.toByte()) {
                isDel = 1
            }
            college.collegeIsDel = isDel
            college.collegeName = StringUtils.trimWhitespace(collegeVo.collegeName!!)
            college.collegeCode = StringUtils.trimWhitespace(collegeVo.collegeCode!!)
            college.collegeAddress = StringUtils.trimWhitespace(collegeVo.collegeAddress!!)
            college.schoolId = collegeVo.schoolId
            collegeService.save(college)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 批量更改院状态
     *
     * @param collegeIds 院ids
     * @param isDel      is_del
     * @return true注销成功
     */
    @RequestMapping(value = ["/web/data/college/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun collegeUpdateDel(collegeIds: String, isDel: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(collegeIds) && SmallPropsUtils.StringIdsIsNumber(collegeIds)) {
            collegeService.updateIsDel(SmallPropsUtils.StringIdsToList(collegeIds), isDel)
            return AjaxUtils.of<Any>().success().msg("更改院状态成功")
        }
        return AjaxUtils.of<Any>().fail().msg("更改院状态失败")
    }

    /**
     * 应用挂载
     *
     * @return 应用挂载页面
     */
    @RequestMapping(value = ["/web/data/college/mount"], method = [(RequestMethod.GET)])
    fun collegeMount(@RequestParam("id") collegeId: Int, modelMap: ModelMap): String {
        val record = collegeService.findByIdRelation(collegeId)
        var college = College()
        if (record.isPresent) {
            college = record.get().into(CollegeBean::class.java)
        }
        modelMap.addAttribute("college", college)
        return "web/data/college/college_mount::#page-wrapper"
    }

    /**
     * 院与应用数据
     *
     * @param collegeId 院id
     * @return 数据
     */
    @RequestMapping(value = ["/web/data/college/application/data"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun collegeApplicationData(@RequestParam("collegeId") collegeId: Int): AjaxUtils<CollegeApplication> {
        val ajaxUtils = AjaxUtils.of<CollegeApplication>()
        val collegeApplicationRecords = collegeApplicationService.findByCollegeId(collegeId)
        var collegeApplications: List<CollegeApplication> = ArrayList()
        if (collegeApplicationRecords.isNotEmpty) {
            collegeApplications = collegeApplicationRecords.into(CollegeApplication::class.java)
        }
        return ajaxUtils.success().listData(collegeApplications)
    }

    /**
     * 更新应用挂载
     *
     * @param collegeId      院id
     * @param applicationIds 应用ids
     * @return true 更新成功
     */
    @RequestMapping(value = ["/web/data/college/update/mount"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateMount(@RequestParam("collegeId") collegeId: Int, applicationIds: String): AjaxUtils<*> {
        if (collegeId > 0) {
            collegeApplicationService.deleteByCollegeId(collegeId)
            if (StringUtils.hasLength(applicationIds)) {
                val ids = SmallPropsUtils.StringIdsToStringList(applicationIds)
                ids.forEach { id ->
                    val collegeApplication = CollegeApplication(id, collegeId)
                    collegeApplicationService.save(collegeApplication)
                }
            }
        }
        return AjaxUtils.of<Any>().success().msg("更新成功")
    }

    /**
     * 数据json
     *
     * @return json
     */
    @RequestMapping(value = ["/web/data/college/application/json"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun applicationJson(): AjaxUtils<TreeBean> {
        val ajaxUtils = AjaxUtils.of<TreeBean>()
        val treeBeens = applicationService.getApplicationJson("0")
        return ajaxUtils.success().listData(treeBeens)
    }
}