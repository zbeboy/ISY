package top.zbeboy.isy.web.data.schoolroom

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.Schoolroom
import top.zbeboy.isy.service.data.SchoolroomService
import top.zbeboy.isy.web.bean.data.schoolroom.SchoolroomBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.data.schoolroom.SchoolroomVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-08 .
 **/
@Controller
open class SchoolroomController {

    @Resource
    open lateinit var schoolroomService: SchoolroomService

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    /**
     * 通过楼id获取全部教室
     *
     * @param buildingId 楼id
     * @return 楼下全部教室
     */
    @RequestMapping(value = ["/user/schoolrooms"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun schoolrooms(@RequestParam("buildingId") buildingId: Int): AjaxUtils<Schoolroom> {
        val ajaxUtils = AjaxUtils.of<Schoolroom>()
        val schoolrooms = ArrayList<Schoolroom>()
        val isDel: Byte = 0
        val schoolroom = Schoolroom(0, 0, "请选择教室", isDel)
        schoolrooms.add(schoolroom)
        val schoolroomRecords = schoolroomService.findByBuildingIdAndIsDel(buildingId, isDel)
        schoolroomRecords.mapTo(schoolrooms) { Schoolroom(it.schoolroomId, it.buildingId, it.buildingCode, it.schoolroomIsDel) }
        return ajaxUtils.success().msg("获取楼数据成功！").listData(schoolrooms)
    }

    /**
     * 教室数据
     *
     * @return 教室数据页面
     */
    @RequestMapping(value = ["/web/menu/data/schoolroom"], method = [(RequestMethod.GET)])
    fun scienceData(): String {
        return "web/data/schoolroom/schoolroom_data::#page-wrapper"
    }

    /**
     * 教室数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = ["/web/data/schoolroom/add"], method = [(RequestMethod.GET)])
    fun schoolroomAdd(modelMap: ModelMap): String {
        pageParamControllerCommon.currentUserRoleNameAndCollegeIdNoAdminPageParam(modelMap)
        return "web/data/schoolroom/schoolroom_add::#page-wrapper"
    }

    /**
     * 教室数据编辑
     *
     * @param id       专业id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/data/schoolroom/edit"], method = [(RequestMethod.GET)])
    fun schoolroomEdit(@RequestParam("id") id: Int, modelMap: ModelMap): String {
        val record = schoolroomService.findByIdRelation(id)
        return if (record.isPresent) {
            modelMap.addAttribute("schoolroom", record.get().into(SchoolroomBean::class.java))
            pageParamControllerCommon.currentUserRoleNameAndCollegeIdNoAdminPageParam(modelMap)
            "web/data/schoolroom/schoolroom_edit::#page-wrapper"
        } else methodControllerCommon.showTip(modelMap, "未查询到相关教室信息")
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/schoolroom/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun schoolroomDatas(request: HttpServletRequest): DataTablesUtils<SchoolroomBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("schoolroom_id")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("building_name")
        headers.add("building_code")
        headers.add("schoolroom_is_del")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<SchoolroomBean>(request, headers)
        val records = schoolroomService.findAllByPage(dataTablesUtils)
        var schoolroomBeen: List<SchoolroomBean> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            schoolroomBeen = records.into(SchoolroomBean::class.java)
        }
        dataTablesUtils.data = schoolroomBeen
        dataTablesUtils.setiTotalRecords(schoolroomService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(schoolroomService.countByCondition(dataTablesUtils).toLong())

        return dataTablesUtils
    }

    /**
     * 保存时检验教室是否重复
     *
     * @param code 教室名
     * @param buildingId   楼id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/schoolroom/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("buildingCode") code: String, @RequestParam("buildingId") buildingId: Int): AjaxUtils<*> {
        val buildingCode = StringUtils.trimWhitespace(code)
        if (StringUtils.hasLength(buildingCode)) {
            val schoolroomRecords = schoolroomService.findByBuildingCodeAndBuildingId(buildingCode, buildingId)
            return if (ObjectUtils.isEmpty(schoolroomRecords)) {
                AjaxUtils.of<Any>().success().msg("教室不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("教室已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("教室不能为空")
    }

    /**
     * 检验编辑时教室重复
     *
     * @param id           教室id
     * @param code 教室
     * @param buildingId   楼id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/schoolroom/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("schoolroomId") id: Int, @RequestParam("buildingCode") code: String, @RequestParam("buildingId") buildingId: Int): AjaxUtils<*> {
        val buildingCode = StringUtils.trimWhitespace(code)
        val schoolroomRecords = schoolroomService.findByBuildingCodeAndBuildingIdNeSchoolroomId(buildingCode, id, buildingId)
        return if (schoolroomRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("教室不重复")
        } else AjaxUtils.of<Any>().fail().msg("教室重复")

    }

    /**
     * 保存教室信息
     *
     * @param schoolroomVo  教室
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/schoolroom/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun scienceSave(@Valid schoolroomVo: SchoolroomVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val schoolroom = Schoolroom()
            schoolroom.schoolroomIsDel = if (!ObjectUtils.isEmpty(schoolroomVo.schoolroomIsDel) && schoolroomVo.schoolroomIsDel == 1.toByte()) {
                1
            } else {
                0
            }
            schoolroom.buildingCode = StringUtils.trimWhitespace(schoolroomVo.buildingCode)
            schoolroom.buildingId = schoolroomVo.buildingId
            schoolroomService.save(schoolroom)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 保存教室更改
     *
     * @param schoolroomVo  教室
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/schoolroom/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun scienceUpdate(@Valid schoolroomVo: SchoolroomVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(schoolroomVo.schoolroomId)) {
            val schoolroom = schoolroomService.findById(schoolroomVo.schoolroomId!!)
            if (!ObjectUtils.isEmpty(schoolroom)) {
                schoolroom.schoolroomIsDel = if (!ObjectUtils.isEmpty(schoolroomVo.schoolroomIsDel) && schoolroomVo.schoolroomIsDel == 1.toByte()) {
                    1
                } else {
                    0
                }
                schoolroom.buildingCode = StringUtils.trimWhitespace(schoolroomVo.buildingCode)
                schoolroom.buildingId = schoolroomVo.buildingId
                schoolroomService.update(schoolroom)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }

    /**
     * 批量更改教室状态
     *
     * @param schoolroomIds 教室ids
     * @param isDel         is_del
     * @return true注销成功
     */
    @RequestMapping(value = ["/web/data/schoolroom/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun scienceUpdateDel(schoolroomIds: String, isDel: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(schoolroomIds) && SmallPropsUtils.StringIdsIsNumber(schoolroomIds)) {
            schoolroomService.updateIsDel(SmallPropsUtils.StringIdsToList(schoolroomIds), isDel)
            return AjaxUtils.of<Any>().success().msg("更改教室状态成功")
        }
        return AjaxUtils.of<Any>().fail().msg("更改教室状态失败")
    }
}