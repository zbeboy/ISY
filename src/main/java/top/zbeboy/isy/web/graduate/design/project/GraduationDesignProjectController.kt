package top.zbeboy.isy.web.graduate.design.project

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
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.graduate.design.GraduationDesignPlanService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.error.ErrorBean
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean
import top.zbeboy.isy.web.bean.graduate.design.project.GraduationDesignPlanBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.graduate.design.project.GraduationDesignProjectAddVo
import top.zbeboy.isy.web.vo.graduate.design.project.GraduationDesignProjectUpdateVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * Created by zbeboy 2018-01-22 .
 **/
@Controller
open class GraduationDesignProjectController {

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var graduationDesignPlanService: GraduationDesignPlanService

    @Resource
    open lateinit var graduationDesignTutorService: GraduationDesignTutorService

    @Resource
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon


    /**
     * 毕业设计规划
     *
     * @return 毕业设计规划页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/project"], method = [(RequestMethod.GET)])
    fun project(): String {
        return "web/graduate/design/project/design_project::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/project/design/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun designDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils)
    }

    /**
     * 列表
     *
     * @return 列表页面
     */
    @RequestMapping(value = ["/web/graduate/design/project/list"], method = [(RequestMethod.GET)])
    fun projectList(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = simpleCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                val users = usersService.getUserFromSession()
                val staff = staffService.findByUsername(users!!.username)
                if (!ObjectUtils.isEmpty(staff)) {
                    modelMap.addAttribute("staffId", staff.staffId)
                }
            }
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            page = "web/graduate/design/project/design_project_list::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * Ta的规划
     *
     * @return Ta的规划页面
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/detail"], method = [(RequestMethod.GET)])
    fun projectDetail(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("staffId") staffId: Int, modelMap: ModelMap): String {
        val page: String
        val errorBean = simpleCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            // 当前毕业设计指导教师查看自己的规划
            if (isOwner(staffId)) {
                page = "web/graduate/design/project/design_project_my::#page-wrapper"
            } else {
                val staffRecord = staffService.findByIdRelationForUsers(staffId)
                if (staffRecord.isPresent) {
                    val staffUser = staffRecord.get().into(Users::class.java)
                    modelMap.addAttribute("staffRealName", staffUser.realName)
                    page = "web/graduate/design/project/design_project_detail::#page-wrapper"
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到教师信息")
                }
            }
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("staffId", staffId)
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * Ta的学生
     *
     * @return Ta的学生页面
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/students"], method = [(RequestMethod.GET)])
    fun projectStudents(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("staffId") staffId: Int, modelMap: ModelMap): String {
        val page: String
        val errorBean = simpleCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            // 当前毕业设计指导教师查看自己的规划
            if (isOwner(staffId)) {
                page = "web/graduate/design/project/design_project_my_students::#page-wrapper"
            } else {
                val staffRecord = staffService.findByIdRelationForUsers(staffId)
                if (staffRecord.isPresent) {
                    val staffUser = staffRecord.get().into(Users::class.java)
                    modelMap.addAttribute("staffRealName", staffUser.realName)
                    page = "web/graduate/design/project/design_project_students::#page-wrapper"
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到教师信息")
                }
            }
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("staffId", staffId)
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 检测当前用户
     *
     * @param staffId 教职工id
     * @return true or false
     */
    private fun isOwner(staffId: Int): Boolean {
        var isOwner = false
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val staff = staffService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(staff)) {
                if (staff.staffId == staffId) {
                    isOwner = true
                }
            }
        }
        return isOwner
    }

    /**
     * 我的规划
     *
     * @return 我的规划页面
     */
    @RequestMapping(value = ["/web/graduate/design/project/my/list"], method = [(RequestMethod.GET)])
    fun myProjects(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = myCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val staff = errorBean.mapData!!["staff"] as Staff
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("staffId", staff.staffId)
            page = "web/graduate/design/project/design_project_my::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 我的学生
     *
     * @return 我的学生页面
     */
    @RequestMapping(value = ["/web/graduate/design/project/my/students"], method = [(RequestMethod.GET)])
    fun myStudents(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = myCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
        if (!errorBean.isHasError()) {
            val staff = errorBean.mapData!!["staff"] as Staff
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("staffId", staff.staffId)
            page = "web/graduate/design/project/design_project_my_students::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 教师数据
     *
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/teachers"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun teachers(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<GraduationDesignTeacherBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTeacherBean>()
        val errorBean = simpleCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignTeacherBeen = graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId)
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeen)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 规划添加
     *
     * @return 规划添加页面
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/add"], method = [(RequestMethod.GET)])
    fun projectListAdd(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            // 查询最近的一条件记录，时间为当前
            val graduationDesignTeacher = errorBean.mapData!!["graduationDesignTeacher"] as GraduationDesignTeacher
            val record = graduationDesignPlanService.findByGraduationDesignTeacherIdAndLessThanAddTime(graduationDesignTeacher.graduationDesignTeacherId, DateTimeUtils.getNow())
            val graduationDesignPlan: GraduationDesignPlanBean
            if (!ObjectUtils.isEmpty(record)) {
                graduationDesignPlan = record!!.into(GraduationDesignPlanBean::class.java)
            } else {
                graduationDesignPlan = GraduationDesignPlanBean()
            }
            page = "web/graduate/design/project/design_project_add::#page-wrapper"
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("graduationDesignPlanRecently", graduationDesignPlan)
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 规划编辑
     *
     * @return 规划编辑页面
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/edit"], method = [(RequestMethod.GET)])
    fun projectListEdit(@RequestParam("id") graduationDesignReleaseId: String,
                        @RequestParam("graduationDesignPlanId") graduationDesignPlanId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignPlanRecord = graduationDesignPlanService.findByIdRelation(graduationDesignPlanId)
            if (graduationDesignPlanRecord.isPresent) {
                val graduationDesignPlan = graduationDesignPlanRecord.get().into(GraduationDesignPlanBean::class.java)
                // 查询上一条件记录，时间为当前计划时间
                val graduationDesignTeacher = errorBean.mapData!!["graduationDesignTeacher"] as GraduationDesignTeacher
                val record = graduationDesignPlanService.findByGraduationDesignTeacherIdAndLessThanAddTime(graduationDesignTeacher.graduationDesignTeacherId, graduationDesignPlan.addTime)
                val graduationDesignPlanRecently: GraduationDesignPlanBean
                if (!ObjectUtils.isEmpty(record)) {
                    graduationDesignPlanRecently = record!!.into(GraduationDesignPlanBean::class.java)
                } else {
                    graduationDesignPlanRecently = GraduationDesignPlanBean()
                }
                page = "web/graduate/design/project/design_project_edit::#page-wrapper"
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                modelMap.addAttribute("graduationDesignPlanRecently", graduationDesignPlanRecently)
                modelMap.addAttribute("graduationDesignPlan", graduationDesignPlan)
            } else {
                page = methodControllerCommon.showTip(modelMap, "未查询到相关信息")
            }

        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 规划下载
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/download"], method = [(RequestMethod.GET)])
    fun projectListDownload(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("staffId") staffId: Int, request: HttpServletRequest, response: HttpServletResponse) {
        val errorBean = simpleCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignTutorRecord = graduationDesignTutorService.findByStaffIdAndGraduationDesignReleaseIdRelationForStudent(staffId, graduationDesignReleaseId)
            var graduationDesignTutorBeanList: List<GraduationDesignTutorBean> = ArrayList()
            if (graduationDesignTutorRecord.isNotEmpty) {
                graduationDesignTutorBeanList = graduationDesignTutorRecord.into(GraduationDesignTutorBean::class.java)
            }
            var graduationDesignPlanBeanList: List<GraduationDesignPlanBean> = ArrayList()
            val graduationDesignPlanRecord = graduationDesignPlanService.findByGraduationDesignReleaseIdAndStaffIdOrderByAddTime(graduationDesignReleaseId, staffId)
            if (graduationDesignPlanRecord.isNotEmpty) {
                graduationDesignPlanBeanList = graduationDesignPlanRecord.into(GraduationDesignPlanBean::class.java)
            }
            val staffRecord = staffService.findByIdRelation(staffId)
            if (staffRecord.isPresent) {
                val users = staffRecord.get().into(Users::class.java)
                val path = filesService.saveGraduationDesignPlan(users, request, graduationDesignTutorBeanList, graduationDesignPlanBeanList)
                uploadService.download("毕业设计指导计划（" + users.realName + "）", path, response, request)
            }
        }
    }

    /**
     * 获取列表数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 列表数据
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun listData(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("staffId") staffId: Int): AjaxUtils<GraduationDesignPlanBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignPlanBean>()
        val errorBean = simpleCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            var graduationDesignPlans: List<GraduationDesignPlanBean> = ArrayList()
            val records = graduationDesignPlanService.findByGraduationDesignReleaseIdAndStaffIdOrderByAddTime(graduationDesignReleaseId, staffId)
            if (records.isNotEmpty) {
                graduationDesignPlans = records.into(GraduationDesignPlanBean::class.java)
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignPlans)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 获取学生列表数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 学生列表数据
     */
    @RequestMapping(value = ["/web/graduate/design/project/students/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun studentListData(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("staffId") staffId: Int): AjaxUtils<GraduationDesignTutorBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTutorBean>()
        val errorBean = simpleCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
        if (!errorBean.isHasError()) {
            val records = graduationDesignTutorService.findByStaffIdAndGraduationDesignReleaseIdRelationForStudent(staffId, graduationDesignReleaseId)
            var graduationDesignTutorBeans: List<GraduationDesignTutorBean> = ArrayList()
            if (records.isNotEmpty) {
                graduationDesignTutorBeans = records.into(GraduationDesignTutorBean::class.java)
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTutorBeans)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 保存
     *
     * @param graduationDesignProjectAddVo 数据
     * @param bindingResult                检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/project/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun save(@Valid graduationDesignProjectAddVo: GraduationDesignProjectAddVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(graduationDesignProjectAddVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val graduationDesignPlan = GraduationDesignPlan()
                val graduationDesignTeacher = errorBean.mapData!!["graduationDesignTeacher"] as GraduationDesignTeacher
                graduationDesignPlan.graduationDesignPlanId = UUIDUtils.getUUID()
                graduationDesignPlan.graduationDesignTeacherId = graduationDesignTeacher.graduationDesignTeacherId
                graduationDesignPlan.scheduling = graduationDesignProjectAddVo.scheduling
                graduationDesignPlan.supervisionTime = graduationDesignProjectAddVo.supervisionTime
                graduationDesignPlan.guideContent = graduationDesignProjectAddVo.guideContent
                graduationDesignPlan.note = graduationDesignProjectAddVo.note
                graduationDesignPlan.schoolroomId = graduationDesignProjectAddVo.schoolroomId
                graduationDesignPlan.addTime = DateTimeUtils.getNow()
                graduationDesignPlanService.save(graduationDesignPlan)
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
     * 更新
     *
     * @param graduationDesignProjectUpdateVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/project/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun save(@Valid graduationDesignProjectUpdateVo: GraduationDesignProjectUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(graduationDesignProjectUpdateVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val graduationDesignPlan = graduationDesignPlanService.findById(graduationDesignProjectUpdateVo.graduationDesignPlanId!!)
                graduationDesignPlan.scheduling = graduationDesignProjectUpdateVo.scheduling
                graduationDesignPlan.supervisionTime = graduationDesignProjectUpdateVo.supervisionTime
                graduationDesignPlan.guideContent = graduationDesignProjectUpdateVo.guideContent
                graduationDesignPlan.note = graduationDesignProjectUpdateVo.note
                graduationDesignPlan.schoolroomId = graduationDesignProjectUpdateVo.schoolroomId
                graduationDesignPlanService.update(graduationDesignPlan)
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
     * 删除
     *
     * @param graduationDesignPlanIds   规划id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/delete"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun collegeUpdateDel(graduationDesignPlanIds: String, @RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(graduationDesignPlanIds)) {
                graduationDesignPlanService.deleteById(SmallPropsUtils.StringIdsToStringList(graduationDesignPlanIds))
                ajaxUtils.success().msg("删除成功")
            } else {
                ajaxUtils.fail().msg("未发现选中信息")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 列表判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/project/list/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun listCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = simpleCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入Ta的学生页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/project/students/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun studentsCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = simpleCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入我的规划页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/project/my/list/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myListCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = myCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入我的学生页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/project/my/students/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myStudentsCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = myCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
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
    @RequestMapping(value = ["/web/graduate/design/project/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 简单条件
     *
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    private fun simpleCondition(graduationDesignReleaseId: String): ErrorBean<GraduationDesignRelease> {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherCondition(errorBean)
        return errorBean
    }

    /**
     * 我的数据条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private fun myCondition(graduationDesignReleaseId: String): ErrorBean<GraduationDesignRelease> {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherCondition(errorBean)
        if (!errorBean.isHasError()) {
            val mapData = HashMap<String, Any>()
            okCurrentTeacher(errorBean, mapData)
            errorBean.mapData = mapData
        }
        return errorBean
    }

    /**
     * 进入入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private fun accessCondition(graduationDesignReleaseId: String): ErrorBean<GraduationDesignRelease> {
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val mapData = HashMap<String, Any>()
            okCurrentTeacher(errorBean, mapData)
            errorBean.mapData = mapData
        }
        return errorBean
    }

    /**
     * 毕业设计指导教师判断
     *
     * @param errorBean 条件
     * @param mapData   data
     */
    private fun okCurrentTeacher(errorBean: ErrorBean<GraduationDesignRelease>, mapData: MutableMap<String, Any>) {
        val graduationDesignRelease = errorBean.data
        // 是否为该次毕业设计指导教师
        val users = usersService.getUserFromSession()
        val staff = staffService.findByUsername(users!!.username)
        if (!ObjectUtils.isEmpty(staff)) {
            mapData.put("staff", staff)
            val record = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignRelease!!.graduationDesignReleaseId, staff.staffId!!)
            if (record.isPresent) {
                val graduationDesignTeacher = record.get().into(GraduationDesignTeacher::class.java)
                mapData.put("graduationDesignTeacher", graduationDesignTeacher)
                errorBean.hasError = false
            } else {
                errorBean.hasError = true
                errorBean.errorMsg = "您不是该毕业设计指导教师"
            }
        } else {
            errorBean.hasError = true
            errorBean.errorMsg = "未查询到相关教职工信息"
        }
    }
}