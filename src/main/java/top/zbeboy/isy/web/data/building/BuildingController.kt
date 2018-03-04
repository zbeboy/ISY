package top.zbeboy.isy.web.data.building

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.Building
import top.zbeboy.isy.service.data.BuildingService
import top.zbeboy.isy.web.bean.data.building.BuildingBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.data.building.BuildingVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-07 .
 **/
@Controller
open class BuildingController {

    @Resource
    open lateinit var buildingService: BuildingService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon

    /**
     * 通过院id获取全部楼
     *
     * @param collegeId 院id
     * @return 院下全部楼
     */
    @RequestMapping(value = ["/user/buildings"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun buildings(@RequestParam("collegeId") collegeId: Int): AjaxUtils<Building> {
        val ajaxUtils = AjaxUtils.of<Building>()
        val buildings = ArrayList<Building>()
        val isDel: Byte = 0
        val building = Building(0, "请选择楼", isDel, 0)
        buildings.add(building)
        val buildingRecords = buildingService.findByCollegeIdAndIsDel(collegeId, isDel)
        buildingRecords.mapTo(buildings) { Building(it.buildingId, it.buildingName, it.buildingIsDel, it.collegeId) }
        return ajaxUtils.success().msg("获取楼数据成功！").listData(buildings)
    }

    /**
     * 楼数据
     *
     * @return 楼数据页面
     */
    @RequestMapping(value = ["/web/menu/data/building"], method = [(RequestMethod.GET)])
    fun buildingData(): String {
        return "web/data/building/building_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/building/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun buildingDatas(request: HttpServletRequest): DataTablesUtils<BuildingBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("building_id")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("building_name")
        headers.add("building_is_del")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<BuildingBean>(request, headers)
        val records = buildingService.findAllByPage(dataTablesUtils)
        var buildingBeens: List<BuildingBean> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            buildingBeens = records.into(BuildingBean::class.java)
        }
        dataTablesUtils.data = buildingBeens
        dataTablesUtils.setiTotalRecords(buildingService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(buildingService.countByCondition(dataTablesUtils).toLong())
        return dataTablesUtils
    }

    /**
     * 楼数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = ["/web/data/building/add"], method = [(RequestMethod.GET)])
    fun buildingAdd(modelMap: ModelMap): String {
        pageParamControllerCommon.currentUserRoleNameAndCollegeIdNoAdminPageParam(modelMap)
        return "web/data/building/building_add::#page-wrapper"
    }

    /**
     * 楼数据编辑
     *
     * @param id       楼id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/data/building/edit"], method = [(RequestMethod.GET)])
    fun buildingEdit(@RequestParam("id") id: Int, modelMap: ModelMap): String {
        val record = buildingService.findByIdRelation(id)
        return if (record.isPresent) {
            val buildingBean = record.get().into(BuildingBean::class.java)
            modelMap.addAttribute("building", buildingBean)
            modelMap.addAttribute("collegeId", buildingBean.collegeId)
            pageParamControllerCommon.currentUserRoleNamePageParam(modelMap)
            "web/data/building/building_edit::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "未查询到相关楼信息")
        }
    }

    /**
     * 保存时检验楼名是否重复
     *
     * @param name      楼名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/building/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("buildingName") name: String, @RequestParam(value = "collegeId") collegeId: Int): AjaxUtils<*> {
        val buildingName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(buildingName)) {
            val buildingRecords = buildingService.findByBuildingNameAndCollegeId(buildingName, collegeId)
            return if (ObjectUtils.isEmpty(buildingRecords)) {
                AjaxUtils.of<Any>().success().msg("楼名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("楼名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("缺失必要参数")
    }

    /**
     * 检验编辑时楼名重复
     *
     * @param id        楼id
     * @param name      楼名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/building/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("buildingId") id: Int, @RequestParam("buildingName") name: String, @RequestParam(value = "collegeId") collegeId: Int): AjaxUtils<*> {
        val buildingName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(buildingName)) {
            val buildingRecords = buildingService.findByBuildingNameAndCollegeIdNeBuildingId(buildingName, id, collegeId)
            return if (buildingRecords.isEmpty()) {
                AjaxUtils.of<Any>().success().msg("楼名不重复")
            } else {
                AjaxUtils.of<Any>().fail().msg("楼名重复")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("缺失必要参数")
    }

    /**
     * 保存楼信息
     *
     * @param buildingVo    楼
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/building/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun buildingSave(@Valid buildingVo: BuildingVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val building = Building()
            building.buildingIsDel = if (!ObjectUtils.isEmpty(buildingVo.buildingIsDel) && buildingVo.buildingIsDel == 1.toByte()) {
                1
            } else {
                0
            }
            building.buildingName = StringUtils.trimWhitespace(buildingVo.buildingName!!)
            building.collegeId = buildingVo.collegeId
            buildingService.save(building)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 保存楼更改
     *
     * @param buildingVo    楼
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/building/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun buildingUpdate(@Valid buildingVo: BuildingVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(buildingVo.buildingId)) {
            val building = buildingService.findById(buildingVo.buildingId!!)
            if (!ObjectUtils.isEmpty(building)) {
                building.buildingIsDel = if (!ObjectUtils.isEmpty(buildingVo.buildingIsDel) && buildingVo.buildingIsDel == 1.toByte()) {
                    1
                } else {
                    0
                }
                building.buildingName = StringUtils.trimWhitespace(buildingVo.buildingName!!)
                building.collegeId = buildingVo.collegeId
                buildingService.update(building)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }

    /**
     * 批量更改楼状态
     *
     * @param buildingIds 楼ids
     * @param isDel       is_del
     * @return true注销成功
     */
    @RequestMapping(value = ["/web/data/building/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun buildingUpdateDel(buildingIds: String, isDel: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(buildingIds) && SmallPropsUtils.StringIdsIsNumber(buildingIds)) {
            buildingService.updateIsDel(SmallPropsUtils.StringIdsToList(buildingIds), isDel)
            return AjaxUtils.of<Any>().success().msg("更改楼状态成功")
        }
        return AjaxUtils.of<Any>().fail().msg("更改楼状态失败")
    }
}