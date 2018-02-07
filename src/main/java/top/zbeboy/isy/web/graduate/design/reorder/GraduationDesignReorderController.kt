package top.zbeboy.isy.web.graduate.design.reorder

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
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.graduate.design.*
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.bean.graduate.design.reorder.DefenseRateBean
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.vo.graduate.design.reorder.DefenseOrderVo
import java.util.*
import javax.annotation.Resource
import javax.validation.Valid

/**
 * Created by zbeboy 2018-02-07 .
 **/
@Controller
open class GraduationDesignReorderController {

    @Resource
    open lateinit var defenseArrangementService: DefenseArrangementService

    @Resource
    open lateinit var defenseTimeService: DefenseTimeService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var defenseGroupService: DefenseGroupService

    @Resource
    open lateinit var defenseGroupMemberService: DefenseGroupMemberService

    @Resource
    open lateinit var defenseOrderService: DefenseOrderService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var defenseRateService: DefenseRateService

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon


    /**
     * 毕业答辩顺序
     *
     * @return 毕业答辩顺序页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/reorder"], method = [(RequestMethod.GET)])
    fun reorder(): String {
        return "web/graduate/design/reorder/design_reorder::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/design/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun designDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils)
    }

    /**
     * 毕业答辩安排
     *
     * @return 毕业答辩安排页面
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/arrange"], method = [(RequestMethod.GET)])
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
                "web/graduate/design/reorder/design_reorder_arrange::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "未进行毕业设计答辩设置")
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
    @RequestMapping(value = ["/web/graduate/design/reorder/order"], method = [(RequestMethod.GET)])
    fun orderLook(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("defenseGroupId") defenseGroupId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            // 是管理员或系统
            var reorderIsSuper = false
            // 是组长
            var reorderIsLeader = false
            // 是秘书
            var reorderIsSecretary = false
            // 是组员
            var reorderIsMember = false

            // 是否是管理员或系统
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                reorderIsSuper = true
            } else {
                val users = usersService.getUserFromSession()
                val defenseGroup = defenseGroupService.findById(defenseGroupId)
                if (!ObjectUtils.isEmpty(defenseGroup)) {
                    // 教职工
                    if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                        val staff = staffService.findByUsername(users!!.username)
                        // 是否为组长
                        val data = isLeader(defenseGroup, staff)
                        reorderIsLeader = data.isPresent

                        // 是否为组员
                        if (!reorderIsLeader) {
                            val memberData = isMember(staff, graduationDesignReleaseId, defenseGroupId)
                            reorderIsMember = memberData.isPresent
                        }

                        // 是否秘书
                        reorderIsSecretary = users.username == defenseGroup.secretaryId

                    } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) { // 学生
                        // 是否秘书
                        reorderIsSecretary = users!!.username == defenseGroup.secretaryId
                    }
                }
            }

            modelMap.addAttribute("reorderIsSuper", reorderIsSuper)
            modelMap.addAttribute("reorderIsLeader", reorderIsLeader)
            modelMap.addAttribute("reorderIsSecretary", reorderIsSecretary)
            modelMap.addAttribute("reorderIsMember", reorderIsMember)
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("defenseGroupId", defenseGroupId)
            "web/graduate/design/reorder/design_reorder_order::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 计时页面
     *
     * @param defenseOrderId 序号id
     * @param modelMap       页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/timer"], method = [(RequestMethod.GET)])
    fun timer(@RequestParam("defenseOrderId") defenseOrderId: String, timer: Int, modelMap: ModelMap): String {
        val defenseOrder = defenseOrderService.findById(defenseOrderId)
        return if (!ObjectUtils.isEmpty(defenseOrder)) {
            modelMap.addAttribute("defenseOrder", defenseOrder)
            modelMap.addAttribute("timer", timer)
            "web/graduate/design/reorder/design_reorder_timer"
        } else {
            modelMap["msg"] = "未查询到组信息"
            "msg"
        }
    }

    /**
     * 查询顺序信息
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseOrderId            顺序id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/info"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun info(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("defenseOrderId") defenseOrderId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val defenseOrder = defenseOrderService.findById(defenseOrderId)
            if (!ObjectUtils.isEmpty(defenseOrder)) {
                ajaxUtils.success().msg("获取数据成功").obj(defenseOrder)
            } else {
                ajaxUtils.fail().msg("未查询到相关顺序")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 更新答辩顺序状态
     *
     * @param defenseOrderVo 页面参数
     * @param bindingResult  检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/status"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun status(@Valid defenseOrderVo: DefenseOrderVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(defenseOrderVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val defenseArrangementRecord = defenseArrangementService.findByGraduationDesignReleaseId(defenseOrderVo.graduationDesignReleaseId!!)
                if (defenseArrangementRecord.isPresent) {
                    val defenseArrangement = defenseArrangementRecord.get().into(DefenseArrangement::class.java)
                    // 答辩开始时间之后可用
                    if (DateTimeUtils.timestampAfterDecide(defenseArrangement.defenseStartTime)) {
                        if (groupCondition(defenseOrderVo)) {
                            val defenseOrder = defenseOrderService.findById(defenseOrderVo.defenseOrderId!!)
                            if (!ObjectUtils.isEmpty(defenseOrder)) {
                                defenseOrder.defenseStatus = defenseOrderVo.defenseStatus
                                defenseOrderService.update(defenseOrder)
                                ajaxUtils.success().msg("更新状态成功")
                            } else {
                                ajaxUtils.fail().msg("未查询到相关顺序")
                            }
                        } else {
                            ajaxUtils.fail().msg("您不符合更改条件")
                        }

                    } else {
                        ajaxUtils.fail().msg("请在答辩开始之后操作")
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关毕业设计答辩设置")
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
     * 获取当前教师打分信息
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  校验
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/grade/info"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun gradeInfo(@Valid defenseOrderVo: DefenseOrderVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(defenseOrderVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val defenseOrder = defenseOrderService.findById(defenseOrderVo.defenseOrderId!!)
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    // 答辩状态为 已进行
                    if (!ObjectUtils.isEmpty(defenseOrder.defenseStatus) && defenseOrder.defenseStatus == 1) {
                        // 判断资格
                        val graduationDesignTeacherId = getGraduationDesignTeacherIdByCondition(defenseOrderVo)
                        if (!ObjectUtils.isEmpty(graduationDesignTeacherId)) {
                            var grade = 0.0
                            val defenseRateRecord = defenseRateService.findByDefenseOrderIdAndGraduationDesignTeacherId(defenseOrderVo.defenseOrderId!!, graduationDesignTeacherId!!)
                            if (!ObjectUtils.isEmpty(defenseRateRecord)) {
                                grade = defenseRateRecord.grade!!
                            }
                            ajaxUtils.success().msg("获取分数成功").obj(grade)
                        } else {
                            ajaxUtils.fail().msg("您不符合查询条件")
                        }
                    } else {
                        ajaxUtils.fail().msg("未开始答辩，无法操作")
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关毕业设计答辩设置")
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
     * 更新当前教师打分信息
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  校验
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/grade"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun grade(@Valid defenseOrderVo: DefenseOrderVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(defenseOrderVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val defenseOrder = defenseOrderService.findById(defenseOrderVo.defenseOrderId!!)
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    // 答辩状态为 已进行
                    if (!ObjectUtils.isEmpty(defenseOrder.defenseStatus) && defenseOrder.defenseStatus == 1) {
                        // 判断资格
                        val graduationDesignTeacherId = getGraduationDesignTeacherIdByCondition(defenseOrderVo)
                        if (!ObjectUtils.isEmpty(graduationDesignTeacherId)) {
                            val defenseRate: DefenseRate
                            val defenseRateRecord = defenseRateService.findByDefenseOrderIdAndGraduationDesignTeacherId(defenseOrderVo.defenseOrderId!!, graduationDesignTeacherId!!)
                            if (!ObjectUtils.isEmpty(defenseRateRecord)) {
                                defenseRate = defenseRateRecord.into(DefenseRate::class.java)
                                defenseRate.grade = defenseOrderVo.grade
                            } else {
                                defenseRate = DefenseRate()
                                defenseRate.defenseOrderId = defenseOrderVo.defenseOrderId
                                defenseRate.graduationDesignTeacherId = graduationDesignTeacherId
                                defenseRate.grade = defenseOrderVo.grade
                            }
                            defenseRateService.saveOrUpdate(defenseRate)
                            ajaxUtils.success().msg("保存成功")
                        } else {
                            ajaxUtils.fail().msg("您不符合更改条件")
                        }
                    } else {
                        ajaxUtils.fail().msg("未开始答辩，无法操作")
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关毕业设计答辩设置")
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
     * 获取组及组员信息
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/groups"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun groups(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<DefenseGroupBean> {
        val ajaxUtils = AjaxUtils.of<DefenseGroupBean>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val defenseGroupBeens = defenseGroupService.findByGraduationDesignReleaseIdRelation(graduationDesignReleaseId)
            defenseGroupBeens.forEach { defenseGroupBean ->
                val memberName = ArrayList<String>()
                val defenseGroupMemberBeens = defenseGroupMemberService.findByDefenseGroupIdForStaff(defenseGroupBean.defenseGroupId)
                defenseGroupMemberBeens.forEach { defenseGroupMemberBean -> memberName.add(defenseGroupMemberBean.staffName!!) }
                defenseGroupBean.memberName = memberName
            }
            ajaxUtils.success().msg("获取数据成功！").listData(defenseGroupBeens)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 查询各教师打分及成绩信息
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  检验
     * @return 结果
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/mark/info"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun markInfo(@Valid defenseOrderVo: DefenseOrderVo, bindingResult: BindingResult): AjaxUtils<DefenseRateBean> {
        val ajaxUtils = AjaxUtils.of<DefenseRateBean>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.basicCondition(defenseOrderVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val defenseOrder = defenseOrderService.findById(defenseOrderVo.defenseOrderId!!)
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    val defenseRateBeans = defenseRateService.findByDefenseOrderIdAndDefenseGroupId(defenseOrderVo.defenseOrderId!!, defenseOrderVo.defenseGroupId!!)
                    ajaxUtils.success().msg("获取数据成功！").listData(defenseRateBeans).obj(defenseOrder)
                } else {
                    ajaxUtils.fail().msg("未获取到相关顺序")
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
     * 修改成绩
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/mark"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun mark(@Valid defenseOrderVo: DefenseOrderVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.basicCondition(defenseOrderVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val defenseOrder = defenseOrderService.findById(defenseOrderVo.defenseOrderId!!)
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    defenseOrder.scoreTypeId = defenseOrderVo.scoreTypeId
                    defenseOrderService.update(defenseOrder)
                    ajaxUtils.success().msg("修改成绩成功")
                } else {
                    ajaxUtils.fail().msg("未获取到相关顺序")
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
     * 问题条件
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  校验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/question/info"], method = [(RequestMethod.GET)])
    fun questionInfo(@Valid defenseOrderVo: DefenseOrderVo, bindingResult: BindingResult, modelMap: ModelMap): String {
        return if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.basicCondition(defenseOrderVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val defenseOrder = defenseOrderService.findById(defenseOrderVo.defenseOrderId!!)
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    modelMap.addAttribute("defenseOrder", defenseOrder)
                    modelMap.addAttribute("defenseOrderVo", defenseOrderVo)
                    if (groupCondition(defenseOrderVo)) {
                        "web/graduate/design/reorder/design_reorder_question_edit::#page-wrapper"
                    } else {
                        "web/graduate/design/reorder/design_reorder_question::#page-wrapper"
                    }
                } else {
                    methodControllerCommon.showTip(modelMap, "未查询到相关顺序")
                }
            } else {
                methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
            }
        } else {
            methodControllerCommon.showTip(modelMap, "参数异常")
        }
    }

    /**
     * 更新问题
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  校验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/question"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun question(@Valid defenseOrderVo: DefenseOrderVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.basicCondition(defenseOrderVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                // 判断资格
                if (groupCondition(defenseOrderVo)) {
                    val defenseOrder = defenseOrderService.findById(defenseOrderVo.defenseOrderId!!)
                    if (!ObjectUtils.isEmpty(defenseOrder)) {
                        defenseOrder.defenseQuestion = defenseOrderVo.defenseQuestion
                        defenseOrderService.update(defenseOrder)
                        ajaxUtils.success().msg("更新成功")
                    } else {
                        ajaxUtils.fail().msg("未查询到相关顺序")
                    }
                } else {
                    ajaxUtils.fail().msg("不符合编辑条件")
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
     * 安排页面进入条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or falseo
     */
    @RequestMapping(value = ["/web/graduate/design/reorder/arrange/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun arrangeCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            if (record.isPresent) {
                ajaxUtils.success().msg("在条件范围，允许使用")
            } else {
                ajaxUtils.fail().msg("未进行毕业设计答辩设置")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 组条件
     *
     * @param defenseOrderVo 数据
     * @return true or false
     */
    private fun groupCondition(defenseOrderVo: DefenseOrderVo): Boolean {
        // 判断资格
        var canUse = false
        // 是否是管理员或系统
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            canUse = true
        } else {
            val users = usersService.getUserFromSession()
            val defenseGroup = defenseGroupService.findById(defenseOrderVo.defenseGroupId!!)
            if (!ObjectUtils.isEmpty(defenseGroup)) {
                // 教职工
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    val staff = staffService.findByUsername(users!!.username)
                    // 是否为组长
                    val data = isLeader(defenseGroup, staff)
                    canUse = data.isPresent || users.username == defenseGroup.secretaryId// 是否秘书
                } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) { // 学生
                    // 是否秘书
                    canUse = users!!.username == defenseGroup.secretaryId
                }
            }
        }
        return canUse
    }

    /**
     * 根据条件获取指导老师id
     *
     * @param defenseOrderVo 页面数据
     * @return 指导老师id
     */
    private fun getGraduationDesignTeacherIdByCondition(defenseOrderVo: DefenseOrderVo): String? {
        val users = usersService.getUserFromSession()
        val defenseGroup = defenseGroupService.findById(defenseOrderVo.defenseGroupId!!)
        var graduationDesignTeacherId: String? = null
        if (!ObjectUtils.isEmpty(defenseGroup)) {
            // 是组长
            var reorderIsLeader = false
            // 教职工
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                val staff = staffService.findByUsername(users!!.username)
                // 是否为组长
                val data = isLeader(defenseGroup, staff)
                if (data.isPresent) {
                    val graduationDesignTeacher = data.get()
                    reorderIsLeader = true
                    graduationDesignTeacherId = graduationDesignTeacher.graduationDesignTeacherId
                }
                // 是否为组员
                if (!reorderIsLeader) {
                    val memberData = isMember(staff, defenseOrderVo.graduationDesignReleaseId!!, defenseOrderVo.defenseGroupId!!)
                    if (memberData.isPresent) {
                        val graduationDesignTeacher = memberData.get()
                        graduationDesignTeacherId = graduationDesignTeacher.graduationDesignTeacherId
                    }
                }
            }
        }
        return graduationDesignTeacherId
    }

    /**
     * 判断是否为组长并得到指导教师信息
     *
     * @param defenseGroup 组数据
     * @param staff        教师数据
     * @return 指导教师信息
     */
    private fun isLeader(defenseGroup: DefenseGroup, staff: Staff): Optional<GraduationDesignTeacher> {
        var data = Optional.empty<GraduationDesignTeacher>()
        // 是否为组长
        if (StringUtils.hasLength(defenseGroup.leaderId)) {
            val graduationDesignTeacher = graduationDesignTeacherService.findById(defenseGroup.leaderId)
            if (!ObjectUtils.isEmpty(graduationDesignTeacher)) {
                if (!ObjectUtils.isEmpty(staff)) {
                    if (graduationDesignTeacher.staffId == staff.staffId) {
                        data = Optional.of(graduationDesignTeacher)
                    }
                }
            }
        }
        return data
    }

    /**
     * 判断是否为组员并获取指导教师信息
     *
     * @param staff                     教师数据
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseGroupId            组id
     * @return 指导教师信息
     */
    private fun isMember(staff: Staff, graduationDesignReleaseId: String, defenseGroupId: String): Optional<GraduationDesignTeacher> {
        var data = Optional.empty<GraduationDesignTeacher>()
        if (!ObjectUtils.isEmpty(staff)) {
            val record = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.staffId!!)
            if (record.isPresent) {
                val graduationDesignTeacher = record.get().into(GraduationDesignTeacher::class.java)
                val groupMemberRecord = defenseGroupMemberService.findByDefenseGroupIdAndGraduationDesignTeacherId(defenseGroupId, graduationDesignTeacher.graduationDesignTeacherId)
                if (groupMemberRecord.isPresent) {
                    data = Optional.of(graduationDesignTeacher)
                }
            }
        }
        return data
    }
}