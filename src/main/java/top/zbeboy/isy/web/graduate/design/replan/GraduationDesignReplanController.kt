package top.zbeboy.isy.web.graduate.design.replan

import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.graduate.design.*
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseOrderBean
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.graduate.design.replan.*
import java.text.ParseException
import java.util.*
import javax.annotation.Resource
import javax.validation.Valid

/**
 * Created by zbeboy 2018-02-06 .
 **/
@Controller
open class GraduationDesignReplanController {

    private val log = LoggerFactory.getLogger(GraduationDesignReplanController::class.java)

    @Resource
    open lateinit var defenseArrangementService: DefenseArrangementService

    @Resource
    open lateinit var defenseTimeService: DefenseTimeService

    @Resource
    open lateinit var defenseGroupService: DefenseGroupService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var defenseOrderService: DefenseOrderService

    @Resource
    open lateinit var defenseRateService: DefenseRateService

    @Resource
    open lateinit var defenseGroupMemberService: DefenseGroupMemberService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon


    /**
     * 毕业设计答辩安排
     *
     * @return 毕业设计答辩安排页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/replan"], method = [(RequestMethod.GET)])
    fun replan(): String {
        return "web/graduate/design/replan/design_replan::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/replan/design/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun designDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils)
    }

    /**
     * 毕业设计答辩设置
     *
     * @return 毕业设计答辩设置页面
     */
    @RequestMapping(value = ["/web/graduate/design/replan/arrange"], method = [(RequestMethod.GET)])
    fun arrange(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            if (record.isPresent) {
                val defenseArrangement = record.get().into(DefenseArrangement::class.java)
                val defenseTimeRecord = defenseTimeService.findByDefenseArrangementId(defenseArrangement.defenseArrangementId)
                val defenseTimes = defenseTimeRecord.into(DefenseTime::class.java)
                modelMap.addAttribute("defenseArrangement", defenseArrangement)
                modelMap.addAttribute("defenseTimes", defenseTimes)
                "web/graduate/design/replan/design_replan_arrange_edit::#page-wrapper"
            } else {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                "web/graduate/design/replan/design_replan_arrange_add::#page-wrapper"
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 毕业设计答辩组管理
     *
     * @return 毕业设计答辩组管理页面
     */
    @RequestMapping(value = ["/web/graduate/design/replan/group"], method = [(RequestMethod.GET)])
    fun group(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            if (record.isPresent) {
                val defenseArrangement = record.get().into(DefenseArrangement::class.java)
                modelMap.addAttribute("defenseArrangement", defenseArrangement)
                "web/graduate/design/replan/design_replan_group::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "请先进行毕业设计答辩设置")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 毕业设计答辩教师分组
     *
     * @return 毕业设计答辩教师分组页面
     */
    @RequestMapping(value = ["/web/graduate/design/replan/divide"], method = [(RequestMethod.GET)])
    fun divide(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherCondition(errorBean)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            "web/graduate/design/replan/design_replan_divide::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 毕业设计答辩生成顺序
     *
     * @return 毕业设计答辩生成顺序页面
     */
    @RequestMapping(value = ["/web/graduate/design/replan/order"], method = [(RequestMethod.GET)])
    fun order(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            if (record.isPresent) {
                val defenseArrangement = record.get().into(DefenseArrangement::class.java)
                modelMap.addAttribute("defenseArrangement", defenseArrangement)
                "web/graduate/design/replan/design_replan_order::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "请先进行毕业设计答辩设置")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 毕业设计答辩顺序
     *
     * @return 毕业设计答辩顺序页面
     */
    @RequestMapping(value = ["/web/graduate/design/replan/order/look"], method = [(RequestMethod.GET)])
    fun orderLook(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("defenseGroupId") defenseGroupId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("defenseGroupId", defenseGroupId)
            "web/graduate/design/replan/design_replan_order_look::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 教师分组数据
     *
     * @param condition 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/replan/divide/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun divideData(condition: GraduationDesignTeacherBean): AjaxUtils<GraduationDesignTeacherBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTeacherBean>()
        var graduationDesignTeacherBeens: List<GraduationDesignTeacherBean> = ArrayList()
        val errorBean = graduationDesignConditionCommon.basicCondition(condition.graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherCondition(errorBean)
        if (!errorBean.isHasError()) {
            graduationDesignTeacherBeens = defenseGroupMemberService.findByGraduationDesignReleaseIdRelationForStaff(condition)
        }
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens)
    }

    /**
     * 添加组
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/replan/group/add"], method = [(RequestMethod.GET)])
    fun groupAdd(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            if (record.isPresent) {
                val defenseArrangement = record.get().into(DefenseArrangement::class.java)
                modelMap.addAttribute("defenseArrangement", defenseArrangement)
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                "web/graduate/design/replan/design_replan_group_add::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "请先进行毕业设计答辩设置")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 编辑组
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/replan/group/edit"], method = [(RequestMethod.GET)])
    fun groupEdit(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("defenseGroupId") defenseGroupId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val record = defenseGroupService.findByIdRelation(defenseGroupId)
            if (record.isPresent) {
                val defenseGroup = record.get().into(DefenseGroupBean::class.java)
                modelMap.addAttribute("defenseGroup", defenseGroup)
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                "web/graduate/design/replan/design_replan_group_edit::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "未查询到相关组信息")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 毕业设计安排保存
     *
     * @param graduationDesignReplanAddVo 数据
     * @param bindingResult               检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/arrange/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun arrangeSave(@Valid graduationDesignReplanAddVo: GraduationDesignReplanAddVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReplanAddVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val dayDefenseStartTime = graduationDesignReplanAddVo.dayDefenseStartTime
                val dayDefenseEndTime = graduationDesignReplanAddVo.dayDefenseEndTime
                if (dayDefenseStartTime!!.isNotEmpty() && dayDefenseStartTime.size == dayDefenseEndTime!!.size) {
                    val defenseArrangement = DefenseArrangement()
                    val defenseArrangementId = UUIDUtils.getUUID()
                    defenseArrangement.defenseArrangementId = defenseArrangementId
                    saveOrUpdateTime(defenseArrangement, graduationDesignReplanAddVo.paperTime!!, graduationDesignReplanAddVo.defenseTime!!)
                    defenseArrangement.intervalTime = graduationDesignReplanAddVo.intervalTime
                    defenseArrangement.defenseNote = graduationDesignReplanAddVo.defenseNote
                    defenseArrangement.graduationDesignReleaseId = graduationDesignReplanAddVo.graduationDesignReleaseId
                    defenseArrangementService.save(defenseArrangement)
                    saveDefenseTime(dayDefenseStartTime, dayDefenseEndTime, defenseArrangementId)
                    ajaxUtils.success().msg("保存成功")
                } else {
                    ajaxUtils.fail().msg("每日答辩时间设置有误")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 毕业设计安排更新
     *
     * @param graduationDesignReplanUpdateVo 数据
     * @param bindingResult                  检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/arrange/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun arrangeUpdate(@Valid graduationDesignReplanUpdateVo: GraduationDesignReplanUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReplanUpdateVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val dayDefenseStartTime = graduationDesignReplanUpdateVo.dayDefenseStartTime
                val dayDefenseEndTime = graduationDesignReplanUpdateVo.dayDefenseEndTime
                if (dayDefenseStartTime!!.isNotEmpty() && dayDefenseStartTime.size == dayDefenseEndTime!!.size) {
                    val defenseArrangement = defenseArrangementService.findById(graduationDesignReplanUpdateVo.defenseArrangementId!!)
                    if (!ObjectUtils.isEmpty(defenseArrangement)) {
                        saveOrUpdateTime(defenseArrangement, graduationDesignReplanUpdateVo.paperTime!!, graduationDesignReplanUpdateVo.defenseTime!!)
                        defenseArrangement.intervalTime = graduationDesignReplanUpdateVo.intervalTime
                        defenseArrangement.defenseNote = graduationDesignReplanUpdateVo.defenseNote
                        defenseArrangementService.update(defenseArrangement)
                        defenseTimeService.deleteByDefenseArrangementId(graduationDesignReplanUpdateVo.defenseArrangementId!!)
                        saveDefenseTime(dayDefenseStartTime, dayDefenseEndTime, graduationDesignReplanUpdateVo.defenseArrangementId!!)
                        ajaxUtils.success().msg("保存成功")
                    } else {
                        ajaxUtils.fail().msg("未查询到相关毕业设计答辩信息")
                    }
                } else {
                    ajaxUtils.fail().msg("每日答辩时间设置有误")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 毕业设计添加组
     *
     * @param defenseGroupAddVo 数据
     * @param bindingResult     检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/group/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun groupSave(@Valid defenseGroupAddVo: DefenseGroupAddVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(defenseGroupAddVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val record = defenseArrangementService.findByGraduationDesignReleaseId(defenseGroupAddVo.graduationDesignReleaseId!!)
                if (record.isPresent) {
                    val defenseGroup = DefenseGroup()
                    defenseGroup.defenseGroupId = UUIDUtils.getUUID()
                    defenseGroup.createTime = DateTimeUtils.getNow()
                    defenseGroup.defenseArrangementId = defenseGroupAddVo.defenseArrangementId
                    defenseGroup.defenseGroupName = defenseGroupAddVo.defenseGroupName
                    defenseGroup.schoolroomId = defenseGroupAddVo.schoolroomId
                    defenseGroup.note = defenseGroupAddVo.note
                    defenseGroupService.save(defenseGroup)
                    ajaxUtils.success().msg("保存成功")
                } else {
                    ajaxUtils.fail().msg("请先进行毕业设计答辩设置")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 毕业设计添加组
     *
     * @param defenseGroupUpdateVo 数据
     * @param bindingResult        检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/group/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun groupUpdate(@Valid defenseGroupUpdateVo: DefenseGroupUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(defenseGroupUpdateVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val defenseGroup = defenseGroupService.findById(defenseGroupUpdateVo.defenseGroupId!!)
                defenseGroup.defenseGroupName = defenseGroupUpdateVo.defenseGroupName
                defenseGroup.schoolroomId = defenseGroupUpdateVo.schoolroomId
                defenseGroup.note = defenseGroupUpdateVo.note
                defenseGroupService.update(defenseGroup)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 组管理数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseArrangementId      毕业答辩安排 id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/replan/group/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun groupData(@RequestParam("id") graduationDesignReleaseId: String,
                  @RequestParam("defenseArrangementId") defenseArrangementId: String): AjaxUtils<DefenseGroupBean> {
        val ajaxUtils = AjaxUtils.of<DefenseGroupBean>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("获取数据成功").listData(defenseGroupService.findByDefenseArrangementId(defenseArrangementId))
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 组数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/replan/divide/groups"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun groups(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String): AjaxUtils<DefenseGroupBean> {
        val ajaxUtils = AjaxUtils.of<DefenseGroupBean>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val defenseGroupBeens = ArrayList<DefenseGroupBean>()
            val defenseGroupBean = DefenseGroupBean()
            defenseGroupBean.defenseGroupId = ""
            defenseGroupBean.defenseGroupName = "请选择组"
            defenseGroupBeens.add(defenseGroupBean)
            val records = defenseGroupService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            if (records.isNotEmpty) {
                defenseGroupBeens.addAll(records.into(DefenseGroupBean::class.java))
            }
            ajaxUtils.success().msg("获取数据成功").listData(defenseGroupBeens)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 批量删除组
     *
     * @param defenseGroupIds 组ids
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/group/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun groupDel(@RequestParam("id") graduationDesignReleaseId: String, defenseGroupIds: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(defenseGroupIds)) {
                val ids = SmallPropsUtils.StringIdsToStringList(defenseGroupIds)
                ids.forEach { id ->
                    val defenseOrders = defenseOrderService.findByDefenseGroupId(id)
                    defenseOrders.forEach { order -> defenseRateService.deleteByDefenseOrderId(order.defenseOrderId) }
                    defenseOrderService.deleteByDefenseGroupId(id)
                    defenseGroupMemberService.deleteByDefenseGroupId(id)
                    defenseGroupService.deleteById(id)
                }
                ajaxUtils.success().msg("删除成功")
            } else {
                ajaxUtils.fail().msg("缺失参数")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 获取该教师组信息
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/divide/group"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun group(@RequestParam("graduationDesignTeacherId") graduationDesignTeacherId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        var defenseGroupMember = DefenseGroupMember()
        val defenseGroupMemberRecord = defenseGroupMemberService.findByGraduationDesignTeacherId(graduationDesignTeacherId)
        if (!ObjectUtils.isEmpty(defenseGroupMemberRecord)) {
            defenseGroupMember = defenseGroupMemberRecord.into(DefenseGroupMember::class.java)
        }
        return ajaxUtils.success().msg("获取数据成功").obj(defenseGroupMember)
    }

    /**
     * 保存设置
     *
     * @param defenseGroupMemberAddVo 数据
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/divide/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun divideSave(@Valid defenseGroupMemberAddVo: DefenseGroupMemberAddVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(defenseGroupMemberAddVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val defenseGroupMember = DefenseGroupMember()
                defenseGroupMember.graduationDesignTeacherId = defenseGroupMemberAddVo.graduationDesignTeacherId
                defenseGroupMember.defenseGroupId = defenseGroupMemberAddVo.defenseGroupId
                defenseGroupMember.note = defenseGroupMemberAddVo.note
                defenseGroupMemberService.saveOrUpdate(defenseGroupMember)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 设置组长
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @param defenseGroupId            组id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/divide/leader"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun divideLeader(@RequestParam("graduationDesignTeacherId") graduationDesignTeacherId: String,
                     @RequestParam("defenseGroupId") defenseGroupId: String,
                     @RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val defenseGroup = defenseGroupService.findById(defenseGroupId)
            if (!ObjectUtils.isEmpty(defenseGroup)) {
                defenseGroup.leaderId = graduationDesignTeacherId
                defenseGroupService.update(defenseGroup)
                ajaxUtils.success().msg("设置成功")
            } else {
                ajaxUtils.fail().msg("未查询到相关组信息")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 设置秘书
     *
     * @param secretaryId               用户账号
     * @param defenseGroupId            组id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/order/secretary"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun orderSecretary(@RequestParam("secretaryId") secretaryId: String,
                       @RequestParam("defenseGroupId") defenseGroupId: String,
                       @RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val defenseGroup = defenseGroupService.findById(defenseGroupId)
            if (!ObjectUtils.isEmpty(defenseGroup)) {
                val users = usersService.findByUsername(StringUtils.trimWhitespace(secretaryId))
                if (!ObjectUtils.isEmpty(users)) {
                    defenseGroup.secretaryId = users!!.username
                    defenseGroupService.update(defenseGroup)
                    ajaxUtils.success().msg("设置成功")
                } else {
                    ajaxUtils.fail().msg("未查询到该账号信息")
                }
            } else {
                ajaxUtils.fail().msg("未查询到相关组信息")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 生成顺序
     *
     * @param defenseGroupId            组id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseArrangementId      毕业设计答辩安排id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/replan/order/make"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun orderMake(@RequestParam("defenseGroupId") defenseGroupId: String,
                  @RequestParam("id") graduationDesignReleaseId: String,
                  @RequestParam("defenseArrangementId") defenseArrangementId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
            graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
            if (!errorBean.isHasError()) {
                // 安排情况
                val defenseArrangement = defenseArrangementService.findById(defenseArrangementId)
                if (!ObjectUtils.isEmpty(defenseArrangement)) {
                    // 删除之前顺序配置
                    defenseOrderService.deleteByDefenseGroupId(defenseGroupId)

                    var defenseTimes: List<DefenseTime> = ArrayList()
                    // 每日答辩时间
                    val defenseTimeRecord = defenseTimeService.findByDefenseArrangementId(defenseArrangementId)
                    if (defenseTimeRecord.isNotEmpty) {
                        defenseTimes = defenseTimeRecord.into(DefenseTime::class.java)
                    }

                    // 查询小组学生
                    val defenseGroupMemberBeens = defenseGroupMemberService.findByDefenseGroupIdAndGraduationDesignReleaseIdForStudent(defenseGroupId, graduationDesignReleaseId)

                    val format1 = "yyyy-MM-dd HH:mm:ss"
                    val format2 = "yyyy-MM-dd"
                    val format3 = "HH:mm"
                    // 切分时间
                    var defenseStartTime = DateTime(defenseArrangement.defenseStartTime.time)
                    // 学生计数器
                    var sortNum = 0
                    var startTime: Date
                    var endTime: Date
                    var dateTime: DateTime? = null
                    val defenseOrders = ArrayList<DefenseOrder>()
                    // 先循环日期
                    while (defenseStartTime.isBefore(defenseArrangement.defenseEndTime.time)) {
                        val startDate = DateTimeUtils.formatDate(defenseStartTime.toDate(), format2)
                        // 循环时段
                        for (defenseTime in defenseTimes) {
                            startTime = DateTimeUtils.formatDateTime(startDate + " " + defenseTime.dayDefenseStartTime + ":00", format1)
                            endTime = DateTimeUtils.formatDateTime(startDate + " " + defenseTime.dayDefenseEndTime + ":00", format1)
                            dateTime = DateTime(startTime.getTime())
                            // 循环学生
                            while (sortNum < defenseGroupMemberBeens.size) {
                                val defenseGroupMemberBean = defenseGroupMemberBeens[sortNum]
                                val defenseOrder = DefenseOrder()
                                defenseOrder.defenseOrderId = UUIDUtils.getUUID()
                                // 在该时段
                                if (dateTime!!.isBefore(endTime.getTime())) {
                                    defenseOrder.studentNumber = defenseGroupMemberBean.studentNumber
                                    defenseOrder.studentName = defenseGroupMemberBean.studentName
                                    defenseOrder.subject = defenseGroupMemberBean.subject
                                    defenseOrder.defenseDate = java.sql.Date(dateTime.toDate().time)
                                    defenseOrder.defenseTime = DateTimeUtils.formatDate(dateTime.toDate(), format3)
                                    defenseOrder.staffName = defenseGroupMemberBean.staffName
                                    defenseOrder.sortNum = sortNum
                                    defenseOrder.studentId = defenseGroupMemberBean.studentId
                                    defenseOrder.defenseGroupId = defenseGroupMemberBean.defenseGroupId
                                    defenseOrder.defenseStatus = 0
                                    defenseOrder.studentMobile = defenseGroupMemberBean.studentMobile
                                } else {
                                    break
                                }
                                defenseOrders.add(defenseOrder)
                                dateTime = dateTime.plusMinutes(defenseArrangement.intervalTime!!)
                                sortNum++
                            }
                        }
                        defenseStartTime = defenseStartTime.plusDays(1)
                    }

                    // 若已超过时间，但学生还有，补齐
                    if (sortNum < defenseGroupMemberBeens.size) {
                        while (sortNum < defenseGroupMemberBeens.size) {
                            val defenseGroupMemberBean = defenseGroupMemberBeens[sortNum]
                            val defenseOrder = DefenseOrder()
                            defenseOrder.defenseOrderId = UUIDUtils.getUUID()
                            // 在该时段
                            if (!ObjectUtils.isEmpty(dateTime)) {
                                defenseOrder.studentNumber = defenseGroupMemberBean.studentNumber
                                defenseOrder.studentName = defenseGroupMemberBean.studentName
                                defenseOrder.subject = defenseGroupMemberBean.subject
                                defenseOrder.defenseDate = java.sql.Date(dateTime!!.toDate().time)
                                defenseOrder.defenseTime = DateTimeUtils.formatDate(dateTime.toDate(), format3)
                                defenseOrder.staffName = defenseGroupMemberBean.staffName
                                defenseOrder.sortNum = sortNum
                                defenseOrder.studentId = defenseGroupMemberBean.studentId
                                defenseOrder.defenseGroupId = defenseGroupMemberBean.defenseGroupId
                                defenseOrder.defenseStatus = 0
                                defenseOrder.studentMobile = defenseGroupMemberBean.studentMobile
                                dateTime = dateTime.plusMinutes(defenseArrangement.intervalTime!!)
                            }
                            defenseOrders.add(defenseOrder)
                            sortNum++
                        }
                    }
                    if (defenseOrders.size > 0) {
                        defenseOrderService.save(defenseOrders)
                    }
                    ajaxUtils.success().msg("生成成功")
                } else {
                    ajaxUtils.fail().msg("未查询到相关毕业答辩安排")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } catch (e: ParseException) {
            log.error("Parse time error , error is ", e)
        }

        return ajaxUtils
    }

    /**
     * 查看及调整顺序
     *
     * @param condition 条件
     * @return 数据
     */
    @RequestMapping(value = ["/anyone/graduate/design/defense/order/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun orderLookData(condition: DefenseOrderBean): AjaxUtils<DefenseOrderBean> {
        val ajaxUtils = AjaxUtils.of<DefenseOrderBean>()
        var defenseOrderBeens: List<DefenseOrderBean> = ArrayList()
        val errorBean = graduationDesignConditionCommon.basicCondition(condition.graduationDesignReleaseId!!)
        if (!errorBean.isHasError()) {
            val records = defenseOrderService.findAll(condition)
            if (records.isNotEmpty) {
                defenseOrderBeens = records.into(DefenseOrderBean::class.java)
                // 以下为保护个人隐私，仅在答辩时间内显示个人电话
                var isEncrypt = true
                val defenseArrangementRecord = defenseArrangementService.findByGraduationDesignReleaseId(condition.graduationDesignReleaseId!!)
                if (defenseArrangementRecord.isPresent) {
                    val defenseArrangement = defenseArrangementRecord.get().into(DefenseArrangement::class.java)
                    // 答辩时间内显示
                    if (DateTimeUtils.timestampRangeDecide(defenseArrangement.defenseStartTime, defenseArrangement.defenseEndTime)) {
                        isEncrypt = false
                    }
                }
                if (isEncrypt) {
                    defenseOrderBeens.forEach { i -> i.studentMobile = "******" }
                }
            }
        }
        return ajaxUtils.success().msg("获取数据成功").listData(defenseOrderBeens)
    }

    /**
     * 调换顺序
     *
     * @param defenseOrderBean 数据
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/order/adjust"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun orderAdjust(defenseOrderBean: DefenseOrderBean): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(defenseOrderBean.graduationDesignReleaseId!!)
        if (!errorBean.isHasError()) {
            // 当前学生
            val defenseOrder = defenseOrderService.findById(defenseOrderBean.defenseOrderId)
            if (!ObjectUtils.isEmpty(defenseOrder)) {
                if (defenseOrder.sortNum != defenseOrderBean.sortNum) {
                    // 要与之调换的学生
                    val record = defenseOrderService.findBySortNumAndDefenseGroupId(defenseOrderBean.sortNum!!, defenseOrderBean.defenseGroupId)
                    if (!ObjectUtils.isEmpty(record)) {
                        val tempDefenseOrder = record.into(DefenseOrder::class.java)
                        // 进行调换
                        tempDefenseOrder.sortNum = defenseOrder.sortNum
                        val tempDefenseDate = tempDefenseOrder.defenseDate
                        val tempDefenseTime = tempDefenseOrder.defenseTime
                        tempDefenseOrder.defenseDate = defenseOrder.defenseDate
                        tempDefenseOrder.defenseTime = defenseOrder.defenseTime
                        defenseOrder.sortNum = defenseOrderBean.sortNum
                        defenseOrder.defenseDate = tempDefenseDate
                        defenseOrder.defenseTime = tempDefenseTime
                        defenseOrderService.update(tempDefenseOrder)
                        defenseOrderService.update(defenseOrder)
                        ajaxUtils.success().msg("调换成功")
                    } else {
                        ajaxUtils.fail().msg("未查询到与之调换的学生信息")
                    }
                } else {
                    ajaxUtils.fail().msg("序号一致，无法调换")
                }
            } else {
                ajaxUtils.fail().msg("未查询到当前学生信息")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 更新或保存时间
     *
     * @param defenseArrangement 毕业设计安排
     * @param paperDate          毕业论文评阅日期
     * @param defenseDate        答辩日期
     */
    private fun saveOrUpdateTime(defenseArrangement: DefenseArrangement, paperDate: String, defenseDate: String) {
        try {
            val format = "yyyy-MM-dd HH:mm:ss"
            val paperDateArr = DateTimeUtils.splitDateTime("至", paperDate)
            defenseArrangement.paperStartTime = DateTimeUtils.formatDateToTimestamp(paperDateArr[0], format)
            defenseArrangement.paperEndTime = DateTimeUtils.formatDateToTimestamp(paperDateArr[1], format)
            val defenseDateArr = DateTimeUtils.splitDateTime("至", defenseDate)
            defenseArrangement.defenseStartTime = DateTimeUtils.formatDateToTimestamp(defenseDateArr[0], format)
            defenseArrangement.defenseEndTime = DateTimeUtils.formatDateToTimestamp(defenseDateArr[1], format)
        } catch (e: ParseException) {
            log.error(" format time is exception.", e)
        }

    }

    /**
     * 保存每日答辩时间
     *
     * @param dayDefenseStartTime  开始时间
     * @param dayDefenseEndTime    结束时间
     * @param defenseArrangementId 毕业设置安排id
     */
    private fun saveDefenseTime(dayDefenseStartTime: Array<String>, dayDefenseEndTime: Array<String>, defenseArrangementId: String) {
        for (i in dayDefenseStartTime.indices) {
            val defenseTime = DefenseTime()
            defenseTime.defenseArrangementId = defenseArrangementId
            defenseTime.dayDefenseStartTime = dayDefenseStartTime[i]
            defenseTime.dayDefenseEndTime = dayDefenseEndTime[i]
            defenseTime.sortTime = i
            defenseTimeService.save(defenseTime)
        }
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/group/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun groupCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            if (record.isPresent) {
                ajaxUtils.success().msg("在条件范围，允许使用")
            } else {
                ajaxUtils.fail().msg("请先进行毕业设计答辩设置")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/divide/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun divideCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherCondition(errorBean)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/replan/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }
}