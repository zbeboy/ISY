package top.zbeboy.isy.web.internship.regulate

import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.math.NumberUtils
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
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.InternshipRegulate
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease
import top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution
import top.zbeboy.isy.domain.tables.pojos.Staff
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.export.InternshipRegulateExport
import top.zbeboy.isy.service.internship.InternshipRegulateService
import top.zbeboy.isy.service.internship.InternshipReleaseService
import top.zbeboy.isy.service.internship.InternshipTeacherDistributionService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.bean.error.ErrorBean
import top.zbeboy.isy.web.bean.export.ExportBean
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.internship.common.InternshipConditionCommon
import top.zbeboy.isy.web.internship.common.InternshipMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.internship.regulate.InternshipRegulateVo
import java.io.IOException
import java.sql.Timestamp
import java.time.Clock
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-29 .
 **/
@Controller
open class InternshipRegulateController {

    private val log = LoggerFactory.getLogger(InternshipRegulateController::class.java)

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource
    open lateinit var internshipTeacherDistributionService: InternshipTeacherDistributionService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var internshipRegulateService: InternshipRegulateService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon

    @Resource
    open lateinit var internshipMethodControllerCommon: InternshipMethodControllerCommon

    @Resource
    open lateinit var internshipConditionCommon: InternshipConditionCommon


    /**
     * 实习监管
     *
     * @return 实习监管页面
     */
    @RequestMapping(value = ["/web/menu/internship/regulate"], method = [(RequestMethod.GET)])
    fun internshipRegulate(): String {
        return "web/internship/regulate/internship_regulate::#page-wrapper"
    }

    /**
     * 获取实习列表数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/regulate/internship/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipListDatas(paginationUtils: PaginationUtils): AjaxUtils<InternshipReleaseBean> {
        return internshipMethodControllerCommon.internshipListDatas(paginationUtils)
    }

    /**
     * 实习监管列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/regulate/list"], method = [(RequestMethod.GET)])
    fun regulateList(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val staff = staffService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(staff)) {
                modelMap.addAttribute("staffId", staff.staffId)
            } else {
                modelMap.addAttribute("staffId", null)
            }
        }
        pageParamControllerCommon.currentUserRoleNamePageParam(modelMap)
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
        return "web/internship/regulate/internship_regulate_list::#page-wrapper"
    }

    /**
     * 我的监管记录列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/regulate/my/list"], method = [(RequestMethod.GET)])
    fun myRegulateList(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        var canUse = false
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val staff = staffService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(staff)) {
                val errorBean = accessCondition(internshipReleaseId, staff.staffId!!)
                canUse = !errorBean.isHasError()
                modelMap.addAttribute("staffId", staff.staffId)
                modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            }
        }
        return if (canUse) {
            "web/internship/regulate/internship_regulate_my::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
    }

    /**
     * 监管列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/regulate/list/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun listData(request: HttpServletRequest): DataTablesUtils<InternshipRegulateBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("student_name")
        headers.add("student_number")
        headers.add("student_tel")
        headers.add("school_guidance_teacher")
        headers.add("create_date")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<InternshipRegulateBean>(request, headers)
        val otherCondition = InternshipRegulateBean()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            otherCondition.internshipReleaseId = internshipReleaseId
            val staffId = request.getParameter("staffId")
            if (StringUtils.hasLength(staffId)) {
                if (NumberUtils.isDigits(staffId)) {
                    otherCondition.staffId = NumberUtils.toInt(staffId)
                }
            }
            val records = internshipRegulateService.findAllByPage(dataTablesUtils, otherCondition)
            var internshipRegulateBeans: List<InternshipRegulateBean> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                internshipRegulateBeans = records.into(InternshipRegulateBean::class.java)
                internshipRegulateBeans.forEach { i -> i.createDateStr = DateTimeUtils.formatDate(i.createDate) }
            }
            dataTablesUtils.data = internshipRegulateBeans
            dataTablesUtils.setiTotalRecords(internshipRegulateService.countAll(otherCondition).toLong())
            dataTablesUtils.setiTotalDisplayRecords(internshipRegulateService.countByCondition(dataTablesUtils, otherCondition).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 导出 监管列表 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/internship/regulate/list/data/export"], method = [(RequestMethod.GET)])
    fun dataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            var fileName: String? = "实习指导教师监管记录表"
            var ext: String? = Workbook.XLSX_FILE
            val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

            val extraSearchParam = request.getParameter("extra_search")
            val dataTablesUtils = DataTablesUtils.of<InternshipRegulateBean>()
            if (org.apache.commons.lang3.StringUtils.isNotBlank(extraSearchParam)) {
                dataTablesUtils.search = JSON.parseObject(extraSearchParam)
            }
            val otherCondition = InternshipRegulateBean()
            val internshipReleaseId = request.getParameter("internshipReleaseId")
            if (!ObjectUtils.isEmpty(internshipReleaseId)) {
                otherCondition.internshipReleaseId = request.getParameter("internshipReleaseId")
                val staffId = request.getParameter("staffId")
                if (StringUtils.hasLength(staffId)) {
                    if (NumberUtils.isDigits(staffId)) {
                        otherCondition.staffId = NumberUtils.toInt(staffId)
                    }
                }
                val records = internshipRegulateService.exportData(dataTablesUtils, otherCondition)
                var internshipRegulateBeens: List<InternshipRegulateBean> = ArrayList()
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    internshipRegulateBeens = records.into(InternshipRegulateBean::class.java)
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.fileName)) {
                    fileName = exportBean.fileName
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.ext)) {
                    ext = exportBean.ext
                }
                val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    val export = InternshipRegulateExport(internshipRegulateBeens)
                    val schoolInfoPath = cacheManageService.schoolInfoPath(internshipRelease.departmentId!!)
                    val path = Workbook.internshipPath(schoolInfoPath) + fileName + "." + ext
                    export.exportExcel(RequestUtils.getRealPath(request) + Workbook.internshipPath(schoolInfoPath), fileName!!, ext!!)
                    uploadService.download(fileName, path, response, request)
                }
            }
        } catch (e: IOException) {
            log.error("Export file error, error is {}", e)
        }

    }

    /**
     * 实习监管添加
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教职工id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/regulate/list/add"], method = [(RequestMethod.GET)])
    fun regulateListAdd(@RequestParam("id") internshipReleaseId: String, @RequestParam("staffId") staffId: Int, modelMap: ModelMap): String {
        val errorBean = accessCondition(internshipReleaseId, staffId)
        return if (!errorBean.isHasError()) {
            val internshipRegulate = InternshipRegulate()
            internshipRegulate.internshipReleaseId = internshipReleaseId
            internshipRegulate.staffId = staffId
            modelMap.addAttribute("internshipRegulate", internshipRegulate)
            "web/internship/regulate/internship_regulate_add::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 编辑监管记录
     *
     * @param internshipRegulateId 监管id
     * @param modelMap             页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/regulate/list/edit"], method = [(RequestMethod.GET)])
    fun regulateListEdit(@RequestParam("id") internshipRegulateId: String, modelMap: ModelMap): String {
        val internshipRegulate = internshipRegulateService.findById(internshipRegulateId)
        val errorBean = accessCondition(internshipRegulate.internshipReleaseId, internshipRegulate.staffId!!)
        return if (!errorBean.isHasError()) {
            if (!ObjectUtils.isEmpty(internshipRegulate)) {
                modelMap.addAttribute("internshipRegulate", internshipRegulate)
                "web/internship/regulate/internship_regulate_edit::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "未查询到相关监管信息")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 查看实习监管页面
     *
     * @param id       实习监管id
     * @param modelMap 页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/regulate/list/look"], method = [(RequestMethod.GET)])
    fun regulateListLook(@RequestParam("id") id: String, modelMap: ModelMap): String {
        val internshipRegulate = internshipRegulateService.findById(id)
        return if (!ObjectUtils.isEmpty(internshipRegulate)) {
            modelMap.addAttribute("internshipRegulateDate", DateTimeUtils.formatDate(internshipRegulate.reportDate, "yyyy年MM月dd日"))
            modelMap.addAttribute("internshipRegulate", internshipRegulate)
            "web/internship/regulate/internship_regulate_look::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "未查询到相关监管信息")
        }
    }

    /**
     * 批量删除监管记录
     *
     * @param regulateIds ids
     * @return true 删除成功
     */
    @RequestMapping(value = ["/web/internship/regulate/list/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun regulateListDel(regulateIds: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (StringUtils.hasLength(regulateIds)) {
            val ids = SmallPropsUtils.StringIdsToStringList(regulateIds)
            internshipRegulateService.batchDelete(ids)
        }
        return ajaxUtils.success().msg("删除监管记录成功")
    }

    /**
     * 获取该指导教师下的学生
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教师id
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/regulate/students"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun regulateStudents(@RequestParam("internshipReleaseId") internshipReleaseId: String, @RequestParam("staffId") staffId: Int): AjaxUtils<StudentBean> {
        val ajaxUtils = AjaxUtils.of<StudentBean>()
        val errorBean = accessCondition(internshipReleaseId, staffId)
        if (!errorBean.isHasError()) {
            var studentBeens: List<StudentBean> = ArrayList()
            val records = internshipTeacherDistributionService.findByInternshipReleaseIdAndStaffIdForStudent(internshipReleaseId, staffId)
            if (records.isNotEmpty) {
                studentBeens = records.into(StudentBean::class.java)
            }
            ajaxUtils.success().msg("获取学生数据成功").listData(studentBeens)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }

        return ajaxUtils
    }

    /**
     * 获取学生信息
     *
     * @param studentId 学生id
     * @return 学生信息
     */
    @RequestMapping(value = ["/web/internship/regulate/student/info"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun studentInfo(@RequestParam("studentId") studentId: Int): AjaxUtils<StudentBean> {
        val ajaxUtils = AjaxUtils.of<StudentBean>()
        val record = studentService.findByIdRelationForUsers(studentId)
        if (record.isPresent) {
            val studentBean = record.get().into(StudentBean::class.java)
            studentBean.password = ""
            studentBean.mailboxVerifyCode = ""
            studentBean.passwordResetKey = ""
            ajaxUtils.success().msg("获取学生信息成功").obj(studentBean)
        } else {
            ajaxUtils.fail().msg("未查询到该用户信息")
        }
        return ajaxUtils
    }

    /**
     * 保存实习监管
     *
     * @param internshipRegulateVo 数据
     * @param bindingResult        检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/regulate/my/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun regulateSave(@Valid internshipRegulateVo: InternshipRegulateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(internshipRegulateVo.internshipReleaseId!!, internshipRegulateVo.staffId!!)
            if (!errorBean.isHasError()) {
                val studentRecord = studentService.findByIdRelationForUsers(internshipRegulateVo.studentId!!)
                val staffRecord = staffService.findByIdRelationForUsers(internshipRegulateVo.staffId!!)
                if (studentRecord.isPresent && staffRecord.isPresent) {
                    val studentBean = studentRecord.get().into(StudentBean::class.java)
                    val staffBean = staffRecord.get().into(StaffBean::class.java)
                    val internshipRegulate = InternshipRegulate()
                    internshipRegulate.internshipRegulateId = UUIDUtils.getUUID()
                    internshipRegulate.studentName = studentBean.realName
                    internshipRegulate.studentNumber = studentBean.studentNumber
                    internshipRegulate.studentTel = studentBean.mobile
                    internshipRegulate.internshipContent = internshipRegulateVo.internshipContent
                    internshipRegulate.internshipProgress = internshipRegulateVo.internshipProgress
                    internshipRegulate.reportWay = internshipRegulateVo.reportWay
                    internshipRegulate.reportDate = internshipRegulateVo.reportDate
                    internshipRegulate.schoolGuidanceTeacher = staffBean.realName
                    internshipRegulate.tliy = internshipRegulateVo.tliy
                    internshipRegulate.createDate = Timestamp(Clock.systemDefaultZone().millis())
                    internshipRegulate.studentId = internshipRegulateVo.studentId
                    internshipRegulate.internshipReleaseId = internshipRegulateVo.internshipReleaseId
                    internshipRegulate.staffId = internshipRegulateVo.staffId

                    internshipRegulateService.save(internshipRegulate)
                    ajaxUtils.success().msg("保存成功")
                } else {
                    ajaxUtils.fail().msg(errorBean.errorMsg!!)
                }
            } else {
                ajaxUtils.fail().msg("未查询到相关用户信息")
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 更新监管记录
     *
     * @param internshipRegulateVo 数据
     * @param bindingResult        检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/regulate/my/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun regulateUpdate(@Valid internshipRegulateVo: InternshipRegulateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(internshipRegulateVo.internshipRegulateId)) {
            val errorBean = accessCondition(internshipRegulateVo.internshipReleaseId!!, internshipRegulateVo.staffId!!)
            if (!errorBean.isHasError()) {
                val internshipRegulate = internshipRegulateService.findById(internshipRegulateVo.internshipRegulateId!!)
                internshipRegulate.internshipContent = internshipRegulateVo.internshipContent
                internshipRegulate.internshipProgress = internshipRegulateVo.internshipProgress
                internshipRegulate.reportWay = internshipRegulateVo.reportWay
                internshipRegulate.reportDate = internshipRegulateVo.reportDate
                internshipRegulate.tliy = internshipRegulateVo.tliy
                internshipRegulateService.update(internshipRegulate)
                ajaxUtils.success().msg("更新成功")
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 检验教职工
     *
     * @param info                教职工信息
     * @param internshipReleaseId 实习发布id
     * @param type                检验类型
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/regulate/valid/staff"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validStaff(@RequestParam("staff") info: String, @RequestParam("internshipReleaseId") internshipReleaseId: String, type: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        var staff: Staff? = null
        if (type == 0) {
            staff = staffService.findByUsername(info)
        } else if (type == 1) {
            staff = staffService.findByStaffNumber(info)
        }
        if (!ObjectUtils.isEmpty(staff)) {
            val errorBean = accessCondition(internshipReleaseId, staff!!.staffId!!)
            if (!errorBean.isHasError()) {
                ajaxUtils.success().msg("查询教职工数据成功").obj(staff.staffId!!)
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }

        } else {
            ajaxUtils.fail().msg("查询教职工数据失败")
        }
        return ajaxUtils
    }

    /**
     * 我的监管 进入条件
     *
     * @param internshipReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/regulate/my/list/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myRegulateListCondition(@RequestParam("id") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val staff = staffService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(staff)) {
                val errorBean = accessCondition(internshipReleaseId, staff.staffId!!)
                if (!errorBean.isHasError()) {
                    ajaxUtils.success().msg("在条件范围，允许进入")
                } else {
                    ajaxUtils.fail().msg(errorBean.errorMsg!!)
                }
            } else {
                ajaxUtils.fail().msg("未查询到相关教职工信息")
            }
        } else {
            ajaxUtils.fail().msg("您的注册类型不符合进入条件")
        }
        return ajaxUtils
    }

    /**
     * 身份判断
     *
     * @param staffId 教职工id
     * @return true or false
     */
    private fun identityJudge(staffId: Int): Boolean {
        var canUse = true
        // 强制身份判断
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                val users = usersService.getUserFromSession()
                val staff = staffService.findByUsername(users!!.username)
                if (!ObjectUtils.isEmpty(staff) && staff.staffId != staffId) {
                    canUse = false
                }
            }

            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                canUse = false
            }
        }
        return canUse
    }

    /**
     * 进入实习监管入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private fun accessCondition(internshipReleaseId: String, staffId: Int): ErrorBean<InternshipRelease> {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        val mapData = HashMap<String, Any>()
        if (!errorBean.isHasError()) {
            if (identityJudge(staffId)) {
                val internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStaffId(internshipReleaseId, staffId)
                if (internshipTeacherDistributionRecord.isNotEmpty) {
                    errorBean.hasError = false
                    mapData.put("internshipTeacherDistribution", internshipTeacherDistributionRecord.into(InternshipTeacherDistribution::class.java))
                } else {
                    errorBean.hasError = true
                    errorBean.errorMsg = "您不是该实习指导教师"
                }
            } else {
                errorBean.hasError = true
                errorBean.errorMsg = "您的身份检验不通过"
            }
        }
        errorBean.mapData = mapData
        return errorBean
    }
}