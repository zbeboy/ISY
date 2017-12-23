package top.zbeboy.isy.web.internship.apply

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
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.internship.*
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.bean.error.ErrorBean
import top.zbeboy.isy.web.bean.file.FileBean
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.internship.common.InternshipConditionCommon
import top.zbeboy.isy.web.internship.common.InternshipMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.vo.internship.apply.*
import java.io.IOException
import java.sql.Timestamp
import java.text.ParseException
import java.time.Clock
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-23 .
 **/
@Controller
open class InternshipApplyController {

    private val log = LoggerFactory.getLogger(InternshipApplyController::class.java)

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource
    open lateinit var internshipApplyService: InternshipApplyService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var internshipReleaseScienceService: InternshipReleaseScienceService

    @Resource
    open lateinit var internshipTeacherDistributionService: InternshipTeacherDistributionService

    @Resource
    open lateinit var internshipTypeService: InternshipTypeService

    @Resource
    open lateinit var internshipCollegeService: InternshipCollegeService

    @Resource
    open lateinit var internshipCompanyService: InternshipCompanyService

    @Resource
    open lateinit var graduationPracticeCollegeService: GraduationPracticeCollegeService

    @Resource
    open lateinit var graduationPracticeCompanyService: GraduationPracticeCompanyService

    @Resource
    open lateinit var graduationPracticeUnifyService: GraduationPracticeUnifyService

    @Resource
    open lateinit var internshipChangeCompanyHistoryService: InternshipChangeCompanyHistoryService

    @Resource
    open lateinit var internshipChangeHistoryService: InternshipChangeHistoryService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var internshipConditionCommon: InternshipConditionCommon

    @Resource
    open lateinit var internshipMethodControllerCommon: InternshipMethodControllerCommon

    @Resource
    open lateinit var cacheManageService: CacheManageService


    /**
     * 实习申请
     *
     * @return 实习申请页面
     */
    @RequestMapping(value = ["/web/menu/internship/apply"], method = [(RequestMethod.GET)])
    fun internshipApply(): String {
        return "web/internship/apply/internship_apply::#page-wrapper"
    }

    /**
     * 获取实习申请数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/apply/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun applyDatas(paginationUtils: PaginationUtils): AjaxUtils<InternshipReleaseBean> {
        val ajaxUtils = AjaxUtils.of<InternshipReleaseBean>()
        val internshipReleaseBean = InternshipReleaseBean()
        internshipReleaseBean.internshipReleaseIsDel = 0
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val departmentId = cacheManageService.getRoleDepartmentId(users)
            internshipReleaseBean.departmentId = departmentId
            if (record.isPresent && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val organize = record.get().into(Organize::class.java)
                internshipReleaseBean.allowGrade = organize.grade
            }
        }
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val collegeId = cacheManageService.getRoleCollegeId(users)
            internshipReleaseBean.collegeId = collegeId
            if (record.isPresent && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val organize = record.get().into(Organize::class.java)
                internshipReleaseBean.allowGrade = organize.grade
            }
        }
        val records = internshipReleaseService.findAllByPage(paginationUtils, internshipReleaseBean)
        val internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipReleaseBean)
        return ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils)
    }

    /**
     * 获取我的实习申请数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/apply/my/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun myApplyDatas(paginationUtils: PaginationUtils): AjaxUtils<InternshipApplyBean> {
        val ajaxUtils = AjaxUtils.of<InternshipApplyBean>()
        var internshipApplyBeens: List<InternshipApplyBean> = ArrayList()
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val internshipApplyBean = InternshipApplyBean()
            internshipApplyBean.internshipReleaseIsDel = 0
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            internshipApplyBean.studentId = student.studentId
            val records = internshipApplyService.findAllByPage(paginationUtils, internshipApplyBean)
            internshipApplyBeens = internshipApplyService.dealData(paginationUtils, records, internshipApplyBean)
            internshipApplyBeens.forEach { i ->
                val staffRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentIdForStaff(i.internshipReleaseId, student.studentId!!)
                if (staffRecord.isPresent) {
                    val staff = staffRecord.get().into(Users::class.java)
                    i.schoolGuidanceTeacher = staff.realName
                    i.schoolGuidanceTeacherTel = staff.mobile
                }
            }
        }
        return ajaxUtils.success().msg("获取数据成功").listData(internshipApplyBeens).paginationUtils(paginationUtils)
    }

    /**
     * 进入申请页
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 申请页
     */
    @RequestMapping(value = ["/web/internship/apply/access"], method = [(RequestMethod.GET)])
    fun applyAccess(@RequestParam("id") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, modelMap: ModelMap): String {
        val page: String
        val errorBean = accessCondition(internshipReleaseId, studentId)
        if (!errorBean.isHasError()) {
            val internshipRelease = errorBean.data
            val internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease!!.internshipTypeId!!)
            when (internshipType.internshipTypeName) {
                Workbook.INTERNSHIP_COLLEGE_TYPE -> {
                    val internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (internshipCollegeRecord.isPresent) {
                        val internshipCollege = internshipCollegeRecord.get().into(InternshipCollege::class.java)
                        modelMap.addAttribute("internshipData", internshipCollege)
                        modelMap.addAttribute("internshipApply", errorBean.mapData!!["internshipApply"])
                        page = "web/internship/apply/internship_college_edit::#page-wrapper"
                    } else {
                        buildCommonModelMap(modelMap, errorBean)
                        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
                        page = "web/internship/apply/internship_college_add::#page-wrapper"
                    }
                }
                Workbook.INTERNSHIP_COMPANY_TYPE -> {
                    val internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (internshipCompanyRecord.isPresent) {
                        val internshipCompany = internshipCompanyRecord.get().into(InternshipCompany::class.java)
                        modelMap.addAttribute("internshipData", internshipCompany)
                        modelMap.addAttribute("internshipApply", errorBean.mapData!!["internshipApply"])
                        page = "web/internship/apply/internship_company_edit::#page-wrapper"
                    } else {
                        buildCommonModelMap(modelMap, errorBean)
                        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
                        page = "web/internship/apply/internship_company_add::#page-wrapper"
                    }
                }
                Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> {
                    val graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeCollegeRecord.isPresent) {
                        val graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege::class.java)
                        modelMap.addAttribute("internshipData", graduationPracticeCollege)
                        modelMap.addAttribute("internshipApply", errorBean.mapData!!["internshipApply"])
                        page = "web/internship/apply/graduation_practice_college_edit::#page-wrapper"
                    } else {
                        buildCommonModelMap(modelMap, errorBean)
                        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
                        page = "web/internship/apply/graduation_practice_college_add::#page-wrapper"
                    }
                }
                Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> {
                    val graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeUnifyRecord.isPresent) {
                        val graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify::class.java)
                        modelMap.addAttribute("internshipData", graduationPracticeUnify)
                        modelMap.addAttribute("internshipApply", errorBean.mapData!!["internshipApply"])
                        page = "web/internship/apply/graduation_practice_unify_edit::#page-wrapper"
                    } else {
                        buildCommonModelMap(modelMap, errorBean)
                        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
                        page = "web/internship/apply/graduation_practice_unify_add::#page-wrapper"
                    }
                }
                Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> {
                    val graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeCompanyRecord.isPresent) {
                        val graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany::class.java)
                        modelMap.addAttribute("internshipData", graduationPracticeCompany)
                        modelMap.addAttribute("internshipApply", errorBean.mapData!!["internshipApply"])
                        page = "web/internship/apply/graduation_practice_company_edit::#page-wrapper"
                    } else {
                        buildCommonModelMap(modelMap, errorBean)
                        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
                        page = "web/internship/apply/graduation_practice_company_add::#page-wrapper"
                    }
                }
                else -> page = methodControllerCommon.showTip(modelMap, "未找到相关实习类型页面")
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
        return page
    }

    /**
     * 查看详情页
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/apply/audit/detail"], method = [(RequestMethod.GET)])
    fun auditDetail(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val users = usersService.getUserFromSession()
        val student = studentService.findByUsername(users!!.username)
        if (Objects.isNull(student)) {
            page = methodControllerCommon.showTip(modelMap, "查询学生信息为空")
            return page
        }
        val studentId = student.studentId!!
        val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
        val internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.internshipTypeId!!)
        when (internshipType.internshipTypeName) {
            Workbook.INTERNSHIP_COLLEGE_TYPE -> {
                val internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                if (internshipCollegeRecord.isPresent) {
                    val internshipCollege = internshipCollegeRecord.get().into(InternshipCollege::class.java)
                    modelMap.addAttribute("internshipData", internshipCollege)
                    page = "web/internship/apply/internship_college_detail::#page-wrapper"
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                }
            }
            Workbook.INTERNSHIP_COMPANY_TYPE -> {
                val internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                if (internshipCompanyRecord.isPresent) {
                    val internshipCompany = internshipCompanyRecord.get().into(InternshipCompany::class.java)
                    modelMap.addAttribute("internshipData", internshipCompany)
                    page = "web/internship/apply/internship_company_detail::#page-wrapper"
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                }
            }
            Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> {
                val graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                if (graduationPracticeCollegeRecord.isPresent) {
                    val graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege::class.java)
                    modelMap.addAttribute("internshipData", graduationPracticeCollege)
                    page = "web/internship/apply/graduation_practice_college_detail::#page-wrapper"
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                }
            }
            Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> {
                val graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                if (graduationPracticeUnifyRecord.isPresent) {
                    val graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify::class.java)
                    modelMap.addAttribute("internshipData", graduationPracticeUnify)
                    page = "web/internship/apply/graduation_practice_unify_detail::#page-wrapper"
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                }
            }
            Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> {
                val graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                if (graduationPracticeCompanyRecord.isPresent) {
                    val graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany::class.java)
                    modelMap.addAttribute("internshipData", graduationPracticeCompany)
                    page = "web/internship/apply/graduation_practice_company_detail::#page-wrapper"
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                }
            }
            else -> page = methodControllerCommon.showTip(modelMap, "未找到相关实习类型页面")
        }
        return page
    }

    /**
     * 保存顶岗实习(留学院)
     *
     * @param internshipCollegeVo 顶岗实习(留学院)
     * @param bindingResult       检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/college/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun applyCollegeSave(@Valid internshipCollegeVo: InternshipCollegeVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(internshipCollegeVo.internshipReleaseId!!, internshipCollegeVo.studentId!!)
            if (!errorBean.isHasError()) {
                internshipCollegeService.saveWithTransaction(internshipCollegeVo)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
            }
        } else {
            ajaxUtils.fail().msg("参数异常，保存失败")
        }
        return ajaxUtils
    }

    /**
     * 更新顶岗实习(留学院)
     *
     * @param internshipCollegeVo 顶岗实习(留学院)
     * @param bindingResult       检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/college/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun applyCollegeUpdate(@Valid internshipCollegeVo: InternshipCollegeVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(internshipCollegeVo.internshipCollegeId)) {
                val errorBean = accessCondition(internshipCollegeVo.internshipReleaseId!!, internshipCollegeVo.studentId!!)
                if (!errorBean.isHasError()) {
                    val headmasterArr = internshipCollegeVo.headmaster!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (headmasterArr.size >= 2) {
                        internshipCollegeVo.headmaster = headmasterArr[0]
                        internshipCollegeVo.headmasterContact = headmasterArr[1]
                    }
                    val schoolGuidanceTeacherArr = internshipCollegeVo.schoolGuidanceTeacher!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (schoolGuidanceTeacherArr.size >= 2) {
                        internshipCollegeVo.schoolGuidanceTeacher = schoolGuidanceTeacherArr[0]
                        internshipCollegeVo.schoolGuidanceTeacherTel = schoolGuidanceTeacherArr[1]
                    }
                    val internshipCollege = internshipCollegeService.findById(internshipCollegeVo.internshipCollegeId)
                    val internshipApply = errorBean.mapData!!["internshipApply"] as InternshipApply
                    if (internshipApply.internshipApplyState == 5) {// 5：基本信息变更填写中
                        internshipCollege.studentName = internshipCollegeVo.studentName
                        internshipCollege.collegeClass = internshipCollegeVo.collegeClass
                        internshipCollege.studentSex = internshipCollegeVo.studentSex
                        internshipCollege.studentNumber = internshipCollegeVo.studentNumber
                        internshipCollege.phoneNumber = internshipCollegeVo.phoneNumber
                        internshipCollege.qqMailbox = internshipCollegeVo.qqMailbox
                        internshipCollege.parentalContact = internshipCollegeVo.parentalContact
                        internshipCollege.headmaster = internshipCollegeVo.headmaster
                        internshipCollege.headmasterContact = internshipCollegeVo.headmasterContact
                        internshipCollege.schoolGuidanceTeacher = internshipCollegeVo.schoolGuidanceTeacher
                        internshipCollege.schoolGuidanceTeacherTel = internshipCollegeVo.schoolGuidanceTeacherTel
                        internshipCollege.startTime = DateTimeUtils.formatDate(internshipCollegeVo.startTime!!)
                        internshipCollege.endTime = DateTimeUtils.formatDate(internshipCollegeVo.endTime!!)
                        internshipCollegeService.update(internshipCollege)
                        ajaxUtils.success().msg("更新成功")
                    } else if (internshipApply.internshipApplyState == 7) {// 7：单位信息变更填写中

                        val internshipChangeCompanyHistoryRecord = internshipChangeCompanyHistoryService.findByInternshipReleaseIdAndStudentId(internshipCollegeVo.internshipReleaseId, internshipCollegeVo.studentId!!)
                        if (internshipChangeCompanyHistoryRecord.isEmpty()) {
                            val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                            internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                            internshipChangeCompanyHistory.internshipReleaseId = internshipCollege.internshipReleaseId
                            internshipChangeCompanyHistory.studentId = internshipCollege.studentId
                            internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                            internshipChangeCompanyHistory.companyName = internshipCollege.internshipCollegeName
                            internshipChangeCompanyHistory.companyAddress = internshipCollege.internshipCollegeAddress
                            internshipChangeCompanyHistory.companyContacts = internshipCollege.internshipCollegeContacts
                            internshipChangeCompanyHistory.companyTel = internshipCollege.internshipCollegeTel
                            internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                        }

                        internshipCollege.internshipCollegeName = internshipCollegeVo.internshipCollegeName
                        internshipCollege.internshipCollegeAddress = internshipCollegeVo.internshipCollegeAddress
                        internshipCollege.internshipCollegeContacts = internshipCollegeVo.internshipCollegeContacts
                        internshipCollege.internshipCollegeTel = internshipCollegeVo.internshipCollegeTel
                        internshipCollegeService.update(internshipCollege)
                        ajaxUtils.success().msg("更新成功")

                        val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                        internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                        internshipChangeCompanyHistory.internshipReleaseId = internshipCollegeVo.internshipReleaseId
                        internshipChangeCompanyHistory.studentId = internshipCollegeVo.studentId
                        internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                        internshipChangeCompanyHistory.companyName = internshipCollegeVo.internshipCollegeName
                        internshipChangeCompanyHistory.companyAddress = internshipCollegeVo.internshipCollegeAddress
                        internshipChangeCompanyHistory.companyContacts = internshipCollegeVo.internshipCollegeContacts
                        internshipChangeCompanyHistory.companyTel = internshipCollegeVo.internshipCollegeTel
                        internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                    } else {
                        if (internshipApply.internshipApplyState == 3) { // 未通过
                            internshipApply.internshipApplyState = 1 // 更新为申请中
                            internshipApplyService.update(internshipApply)
                        }
                        internshipCollege.studentName = internshipCollegeVo.studentName
                        internshipCollege.collegeClass = internshipCollegeVo.collegeClass
                        internshipCollege.studentSex = internshipCollegeVo.studentSex
                        internshipCollege.studentNumber = internshipCollegeVo.studentNumber
                        internshipCollege.phoneNumber = internshipCollegeVo.phoneNumber
                        internshipCollege.qqMailbox = internshipCollegeVo.qqMailbox
                        internshipCollege.parentalContact = internshipCollegeVo.parentalContact
                        internshipCollege.headmaster = internshipCollegeVo.headmaster
                        internshipCollege.headmasterContact = internshipCollegeVo.headmasterContact
                        internshipCollege.internshipCollegeName = internshipCollegeVo.internshipCollegeName
                        internshipCollege.internshipCollegeAddress = internshipCollegeVo.internshipCollegeAddress
                        internshipCollege.internshipCollegeContacts = internshipCollegeVo.internshipCollegeContacts
                        internshipCollege.internshipCollegeTel = internshipCollegeVo.internshipCollegeTel
                        internshipCollege.schoolGuidanceTeacher = internshipCollegeVo.schoolGuidanceTeacher
                        internshipCollege.schoolGuidanceTeacherTel = internshipCollegeVo.schoolGuidanceTeacherTel
                        internshipCollege.startTime = DateTimeUtils.formatDate(internshipCollegeVo.startTime!!)
                        internshipCollege.endTime = DateTimeUtils.formatDate(internshipCollegeVo.endTime!!)
                        internshipCollegeService.update(internshipCollege)
                        ajaxUtils.success().msg("更新成功")
                    }
                } else {
                    ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
                }
            } else {
                ajaxUtils.fail().msg("参数异常，更新失败")
            }
        } catch (e: ParseException) {
            log.error("Parse time is exception {}", e)
            ajaxUtils.fail().msg("转换时间异常，更新失败")
        }

        return ajaxUtils
    }

    /**
     * 保存校外自主实习(去单位)
     *
     * @param internshipCompanyVo 校外自主实习(去单位)
     * @param bindingResult       检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/company/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun applyCompanySave(@Valid internshipCompanyVo: InternshipCompanyVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(internshipCompanyVo.internshipReleaseId!!, internshipCompanyVo.studentId!!)
            if (!errorBean.isHasError()) {
                internshipCompanyService.saveWithTransaction(internshipCompanyVo)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
            }
        } else {
            ajaxUtils.fail().msg("参数异常，保存失败")
        }
        return ajaxUtils
    }

    /**
     * 更新校外自主实习(去单位)
     *
     * @param internshipCompanyVo 校外自主实习(去单位)
     * @param bindingResult       检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/company/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun applyCompanyUpdate(@Valid internshipCompanyVo: InternshipCompanyVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(internshipCompanyVo.internshipCompanyId)) {
                val errorBean = accessCondition(internshipCompanyVo.internshipReleaseId!!, internshipCompanyVo.studentId!!)
                if (!errorBean.isHasError()) {
                    val headmasterArr = internshipCompanyVo.headmaster!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (headmasterArr.size >= 2) {
                        internshipCompanyVo.headmaster = headmasterArr[0]
                        internshipCompanyVo.headmasterContact = headmasterArr[1]
                    }
                    val schoolGuidanceTeacherArr = internshipCompanyVo.schoolGuidanceTeacher!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (schoolGuidanceTeacherArr.size >= 2) {
                        internshipCompanyVo.schoolGuidanceTeacher = schoolGuidanceTeacherArr[0]
                        internshipCompanyVo.schoolGuidanceTeacherTel = schoolGuidanceTeacherArr[1]
                    }
                    val internshipCompany = internshipCompanyService.findById(internshipCompanyVo.internshipCompanyId)
                    val internshipApply = errorBean.mapData!!["internshipApply"] as InternshipApply
                    if (internshipApply.internshipApplyState == 5) {
                        internshipCompany.studentName = internshipCompanyVo.studentName
                        internshipCompany.collegeClass = internshipCompanyVo.collegeClass
                        internshipCompany.studentSex = internshipCompanyVo.studentSex
                        internshipCompany.studentNumber = internshipCompanyVo.studentNumber
                        internshipCompany.phoneNumber = internshipCompanyVo.phoneNumber
                        internshipCompany.qqMailbox = internshipCompanyVo.qqMailbox
                        internshipCompany.parentalContact = internshipCompanyVo.parentalContact
                        internshipCompany.headmaster = internshipCompanyVo.headmaster
                        internshipCompany.headmasterContact = internshipCompanyVo.headmasterContact
                        internshipCompany.schoolGuidanceTeacher = internshipCompanyVo.schoolGuidanceTeacher
                        internshipCompany.schoolGuidanceTeacherTel = internshipCompanyVo.schoolGuidanceTeacherTel
                        internshipCompany.startTime = DateTimeUtils.formatDate(internshipCompanyVo.startTime!!)
                        internshipCompany.endTime = DateTimeUtils.formatDate(internshipCompanyVo.endTime!!)
                        internshipCompanyService.update(internshipCompany)
                        ajaxUtils.success().msg("更新成功")
                    } else if (internshipApply.internshipApplyState == 7) {
                        val internshipChangeCompanyHistoryRecord = internshipChangeCompanyHistoryService.findByInternshipReleaseIdAndStudentId(internshipCompanyVo.internshipReleaseId, internshipCompanyVo.studentId!!)
                        if (internshipChangeCompanyHistoryRecord.isEmpty()) {
                            val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                            internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                            internshipChangeCompanyHistory.internshipReleaseId = internshipCompany.internshipReleaseId
                            internshipChangeCompanyHistory.studentId = internshipCompany.studentId
                            internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                            internshipChangeCompanyHistory.companyName = internshipCompany.internshipCompanyName
                            internshipChangeCompanyHistory.companyAddress = internshipCompany.internshipCompanyAddress
                            internshipChangeCompanyHistory.companyContacts = internshipCompany.internshipCompanyContacts
                            internshipChangeCompanyHistory.companyTel = internshipCompany.internshipCompanyTel
                            internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                        }

                        internshipCompany.internshipCompanyName = internshipCompanyVo.internshipCompanyName
                        internshipCompany.internshipCompanyAddress = internshipCompanyVo.internshipCompanyAddress
                        internshipCompany.internshipCompanyContacts = internshipCompanyVo.internshipCompanyContacts
                        internshipCompany.internshipCompanyTel = internshipCompanyVo.internshipCompanyTel
                        internshipCompanyService.update(internshipCompany)
                        ajaxUtils.success().msg("更新成功")

                        val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                        internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                        internshipChangeCompanyHistory.internshipReleaseId = internshipCompanyVo.internshipReleaseId
                        internshipChangeCompanyHistory.studentId = internshipCompanyVo.studentId
                        internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                        internshipChangeCompanyHistory.companyName = internshipCompanyVo.internshipCompanyName
                        internshipChangeCompanyHistory.companyAddress = internshipCompanyVo.internshipCompanyAddress
                        internshipChangeCompanyHistory.companyContacts = internshipCompanyVo.internshipCompanyContacts
                        internshipChangeCompanyHistory.companyTel = internshipCompanyVo.internshipCompanyTel
                        internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                    } else {
                        if (internshipApply.internshipApplyState == 3) { // 未通过
                            internshipApply.internshipApplyState = 1 // 更新为申请中
                            internshipApplyService.update(internshipApply)
                        }
                        internshipCompany.studentName = internshipCompanyVo.studentName
                        internshipCompany.collegeClass = internshipCompanyVo.collegeClass
                        internshipCompany.studentSex = internshipCompanyVo.studentSex
                        internshipCompany.studentNumber = internshipCompanyVo.studentNumber
                        internshipCompany.phoneNumber = internshipCompanyVo.phoneNumber
                        internshipCompany.qqMailbox = internshipCompanyVo.qqMailbox
                        internshipCompany.parentalContact = internshipCompanyVo.parentalContact
                        internshipCompany.headmaster = internshipCompanyVo.headmaster
                        internshipCompany.headmasterContact = internshipCompanyVo.headmasterContact
                        internshipCompany.internshipCompanyName = internshipCompanyVo.internshipCompanyName
                        internshipCompany.internshipCompanyAddress = internshipCompanyVo.internshipCompanyAddress
                        internshipCompany.internshipCompanyContacts = internshipCompanyVo.internshipCompanyContacts
                        internshipCompany.internshipCompanyTel = internshipCompanyVo.internshipCompanyTel
                        internshipCompany.schoolGuidanceTeacher = internshipCompanyVo.schoolGuidanceTeacher
                        internshipCompany.schoolGuidanceTeacherTel = internshipCompanyVo.schoolGuidanceTeacherTel
                        internshipCompany.startTime = DateTimeUtils.formatDate(internshipCompanyVo.startTime!!)
                        internshipCompany.endTime = DateTimeUtils.formatDate(internshipCompanyVo.endTime!!)
                        internshipCompanyService.update(internshipCompany)
                        ajaxUtils.success().msg("更新成功")
                    }
                } else {
                    ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
                }
            } else {
                ajaxUtils.fail().msg("参数异常，更新失败")
            }
        } catch (e: ParseException) {
            log.error("Parse time is exception {}", e)
            ajaxUtils.fail().msg("转换时间异常，更新失败")
        }

        return ajaxUtils
    }

    /**
     * 保存毕业实习(校内)
     *
     * @param graduationPracticeCollegeVo 毕业实习(校内)
     * @param bindingResult               检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/graduation/college/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun graduationCollegeSave(@Valid graduationPracticeCollegeVo: GraduationPracticeCollegeVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(graduationPracticeCollegeVo.internshipReleaseId!!, graduationPracticeCollegeVo.studentId!!)
            if (!errorBean.isHasError()) {
                graduationPracticeCollegeService.saveWithTransaction(graduationPracticeCollegeVo)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
            }
        } else {
            ajaxUtils.fail().msg("参数异常，保存失败")
        }
        return ajaxUtils
    }

    /**
     * 更新毕业实习(校内)
     *
     * @param graduationPracticeCollegeVo 毕业实习(校内)
     * @param bindingResult               检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/graduation/college/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun graduationCollegeUpdate(@Valid graduationPracticeCollegeVo: GraduationPracticeCollegeVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(graduationPracticeCollegeVo.graduationPracticeCollegeId)) {
                val errorBean = accessCondition(graduationPracticeCollegeVo.internshipReleaseId!!, graduationPracticeCollegeVo.studentId!!)
                if (!errorBean.isHasError()) {
                    val headmasterArr = graduationPracticeCollegeVo.headmaster!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (headmasterArr.size >= 2) {
                        graduationPracticeCollegeVo.headmaster = headmasterArr[0]
                        graduationPracticeCollegeVo.headmasterContact = headmasterArr[1]
                    }
                    val schoolGuidanceTeacherArr = graduationPracticeCollegeVo.schoolGuidanceTeacher!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (schoolGuidanceTeacherArr.size >= 2) {
                        graduationPracticeCollegeVo.schoolGuidanceTeacher = schoolGuidanceTeacherArr[0]
                        graduationPracticeCollegeVo.schoolGuidanceTeacherTel = schoolGuidanceTeacherArr[1]
                    }
                    val graduationPracticeCollege = graduationPracticeCollegeService.findById(graduationPracticeCollegeVo.graduationPracticeCollegeId)
                    val internshipApply = errorBean.mapData!!["internshipApply"] as InternshipApply
                    if (internshipApply.internshipApplyState == 5) {
                        graduationPracticeCollege.studentName = graduationPracticeCollegeVo.studentName
                        graduationPracticeCollege.collegeClass = graduationPracticeCollegeVo.collegeClass
                        graduationPracticeCollege.studentSex = graduationPracticeCollegeVo.studentSex
                        graduationPracticeCollege.studentNumber = graduationPracticeCollegeVo.studentNumber
                        graduationPracticeCollege.phoneNumber = graduationPracticeCollegeVo.phoneNumber
                        graduationPracticeCollege.qqMailbox = graduationPracticeCollegeVo.qqMailbox
                        graduationPracticeCollege.parentalContact = graduationPracticeCollegeVo.parentalContact
                        graduationPracticeCollege.headmaster = graduationPracticeCollegeVo.headmaster
                        graduationPracticeCollege.headmasterContact = graduationPracticeCollegeVo.headmasterContact
                        graduationPracticeCollege.schoolGuidanceTeacher = graduationPracticeCollegeVo.schoolGuidanceTeacher
                        graduationPracticeCollege.schoolGuidanceTeacherTel = graduationPracticeCollegeVo.schoolGuidanceTeacherTel
                        graduationPracticeCollege.startTime = DateTimeUtils.formatDate(graduationPracticeCollegeVo.startTime!!)
                        graduationPracticeCollege.endTime = DateTimeUtils.formatDate(graduationPracticeCollegeVo.endTime!!)
                        graduationPracticeCollegeService.update(graduationPracticeCollege)
                        ajaxUtils.success().msg("更新成功")
                    } else if (internshipApply.internshipApplyState == 7) {
                        val internshipChangeCompanyHistoryRecord = internshipChangeCompanyHistoryService.findByInternshipReleaseIdAndStudentId(graduationPracticeCollegeVo.internshipReleaseId, graduationPracticeCollegeVo.studentId!!)
                        if (internshipChangeCompanyHistoryRecord.isEmpty()) {
                            val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                            internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                            internshipChangeCompanyHistory.internshipReleaseId = graduationPracticeCollege.internshipReleaseId
                            internshipChangeCompanyHistory.studentId = graduationPracticeCollege.studentId
                            internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                            internshipChangeCompanyHistory.companyName = graduationPracticeCollege.graduationPracticeCollegeName
                            internshipChangeCompanyHistory.companyAddress = graduationPracticeCollege.graduationPracticeCollegeAddress
                            internshipChangeCompanyHistory.companyContacts = graduationPracticeCollege.graduationPracticeCollegeContacts
                            internshipChangeCompanyHistory.companyTel = graduationPracticeCollege.graduationPracticeCollegeTel
                            internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                        }

                        graduationPracticeCollege.graduationPracticeCollegeName = graduationPracticeCollegeVo.graduationPracticeCollegeName
                        graduationPracticeCollege.graduationPracticeCollegeAddress = graduationPracticeCollegeVo.graduationPracticeCollegeAddress
                        graduationPracticeCollege.graduationPracticeCollegeContacts = graduationPracticeCollegeVo.graduationPracticeCollegeContacts
                        graduationPracticeCollege.graduationPracticeCollegeTel = graduationPracticeCollegeVo.graduationPracticeCollegeTel
                        graduationPracticeCollegeService.update(graduationPracticeCollege)
                        ajaxUtils.success().msg("更新成功")

                        val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                        internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                        internshipChangeCompanyHistory.internshipReleaseId = graduationPracticeCollegeVo.internshipReleaseId
                        internshipChangeCompanyHistory.studentId = graduationPracticeCollegeVo.studentId
                        internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                        internshipChangeCompanyHistory.companyName = graduationPracticeCollegeVo.graduationPracticeCollegeName
                        internshipChangeCompanyHistory.companyAddress = graduationPracticeCollegeVo.graduationPracticeCollegeAddress
                        internshipChangeCompanyHistory.companyContacts = graduationPracticeCollegeVo.graduationPracticeCollegeContacts
                        internshipChangeCompanyHistory.companyTel = graduationPracticeCollegeVo.graduationPracticeCollegeTel
                        internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                    } else {
                        if (internshipApply.internshipApplyState == 3) { // 未通过
                            internshipApply.internshipApplyState = 1 // 更新为申请中
                            internshipApplyService.update(internshipApply)
                        }
                        graduationPracticeCollege.studentName = graduationPracticeCollegeVo.studentName
                        graduationPracticeCollege.collegeClass = graduationPracticeCollegeVo.collegeClass
                        graduationPracticeCollege.studentSex = graduationPracticeCollegeVo.studentSex
                        graduationPracticeCollege.studentNumber = graduationPracticeCollegeVo.studentNumber
                        graduationPracticeCollege.phoneNumber = graduationPracticeCollegeVo.phoneNumber
                        graduationPracticeCollege.qqMailbox = graduationPracticeCollegeVo.qqMailbox
                        graduationPracticeCollege.parentalContact = graduationPracticeCollegeVo.parentalContact
                        graduationPracticeCollege.headmaster = graduationPracticeCollegeVo.headmaster
                        graduationPracticeCollege.headmasterContact = graduationPracticeCollegeVo.headmasterContact
                        graduationPracticeCollege.graduationPracticeCollegeName = graduationPracticeCollegeVo.graduationPracticeCollegeName
                        graduationPracticeCollege.graduationPracticeCollegeAddress = graduationPracticeCollegeVo.graduationPracticeCollegeAddress
                        graduationPracticeCollege.graduationPracticeCollegeContacts = graduationPracticeCollegeVo.graduationPracticeCollegeContacts
                        graduationPracticeCollege.graduationPracticeCollegeTel = graduationPracticeCollegeVo.graduationPracticeCollegeTel
                        graduationPracticeCollege.schoolGuidanceTeacher = graduationPracticeCollegeVo.schoolGuidanceTeacher
                        graduationPracticeCollege.schoolGuidanceTeacherTel = graduationPracticeCollegeVo.schoolGuidanceTeacherTel
                        graduationPracticeCollege.startTime = DateTimeUtils.formatDate(graduationPracticeCollegeVo.startTime!!)
                        graduationPracticeCollege.endTime = DateTimeUtils.formatDate(graduationPracticeCollegeVo.endTime!!)
                        graduationPracticeCollegeService.update(graduationPracticeCollege)
                        ajaxUtils.success().msg("更新成功")
                    }
                } else {
                    ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
                }
            } else {
                ajaxUtils.fail().msg("参数异常，更新失败")
            }
        } catch (e: ParseException) {
            log.error("Parse time is exception {}", e)
            ajaxUtils.fail().msg("转换时间异常，更新失败")
        }

        return ajaxUtils
    }

    /**
     * 保存毕业实习(学校统一组织校外实习)
     *
     * @param graduationPracticeUnifyVo 毕业实习(学校统一组织校外实习)
     * @param bindingResult             检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/graduation/unify/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun graduationUnifySave(@Valid graduationPracticeUnifyVo: GraduationPracticeUnifyVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(graduationPracticeUnifyVo.internshipReleaseId!!, graduationPracticeUnifyVo.studentId!!)
            if (!errorBean.isHasError()) {
                graduationPracticeUnifyService.saveWithTransaction(graduationPracticeUnifyVo)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
            }
        } else {
            ajaxUtils.fail().msg("参数异常，保存失败")
        }
        return ajaxUtils
    }

    /**
     * 更新毕业实习(学校统一组织校外实习)
     *
     * @param graduationPracticeUnifyVo 毕业实习(学校统一组织校外实习)
     * @param bindingResult             检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/graduation/unify/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun graduationUnifyUpdate(@Valid graduationPracticeUnifyVo: GraduationPracticeUnifyVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(graduationPracticeUnifyVo.graduationPracticeUnifyId)) {
                val errorBean = accessCondition(graduationPracticeUnifyVo.internshipReleaseId!!, graduationPracticeUnifyVo.studentId!!)
                if (!errorBean.isHasError()) {
                    val headmasterArr = graduationPracticeUnifyVo.headmaster!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (headmasterArr.size >= 2) {
                        graduationPracticeUnifyVo.headmaster = headmasterArr[0]
                        graduationPracticeUnifyVo.headmasterContact = headmasterArr[1]
                    }
                    val schoolGuidanceTeacherArr = graduationPracticeUnifyVo.schoolGuidanceTeacher!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (schoolGuidanceTeacherArr.size >= 2) {
                        graduationPracticeUnifyVo.schoolGuidanceTeacher = schoolGuidanceTeacherArr[0]
                        graduationPracticeUnifyVo.schoolGuidanceTeacherTel = schoolGuidanceTeacherArr[1]
                    }
                    val graduationPracticeUnify = graduationPracticeUnifyService.findById(graduationPracticeUnifyVo.graduationPracticeUnifyId)
                    val internshipApply = errorBean.mapData!!["internshipApply"] as InternshipApply
                    if (internshipApply.internshipApplyState == 5) {
                        graduationPracticeUnify.studentName = graduationPracticeUnifyVo.studentName
                        graduationPracticeUnify.collegeClass = graduationPracticeUnifyVo.collegeClass
                        graduationPracticeUnify.studentSex = graduationPracticeUnifyVo.studentSex
                        graduationPracticeUnify.studentNumber = graduationPracticeUnifyVo.studentNumber
                        graduationPracticeUnify.phoneNumber = graduationPracticeUnifyVo.phoneNumber
                        graduationPracticeUnify.qqMailbox = graduationPracticeUnifyVo.qqMailbox
                        graduationPracticeUnify.parentalContact = graduationPracticeUnifyVo.parentalContact
                        graduationPracticeUnify.headmaster = graduationPracticeUnifyVo.headmaster
                        graduationPracticeUnify.headmasterContact = graduationPracticeUnifyVo.headmasterContact
                        graduationPracticeUnify.schoolGuidanceTeacher = graduationPracticeUnifyVo.schoolGuidanceTeacher
                        graduationPracticeUnify.schoolGuidanceTeacherTel = graduationPracticeUnifyVo.schoolGuidanceTeacherTel
                        graduationPracticeUnify.startTime = DateTimeUtils.formatDate(graduationPracticeUnifyVo.startTime!!)
                        graduationPracticeUnify.endTime = DateTimeUtils.formatDate(graduationPracticeUnifyVo.endTime!!)
                        graduationPracticeUnifyService.update(graduationPracticeUnify)
                        ajaxUtils.success().msg("更新成功")
                    } else if (internshipApply.internshipApplyState == 7) {
                        val internshipChangeCompanyHistoryRecord = internshipChangeCompanyHistoryService.findByInternshipReleaseIdAndStudentId(graduationPracticeUnifyVo.internshipReleaseId, graduationPracticeUnifyVo.studentId!!)
                        if (internshipChangeCompanyHistoryRecord.isEmpty()) {
                            val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                            internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                            internshipChangeCompanyHistory.internshipReleaseId = graduationPracticeUnify.internshipReleaseId
                            internshipChangeCompanyHistory.studentId = graduationPracticeUnify.studentId
                            internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                            internshipChangeCompanyHistory.companyName = graduationPracticeUnify.graduationPracticeUnifyName
                            internshipChangeCompanyHistory.companyAddress = graduationPracticeUnify.graduationPracticeUnifyAddress
                            internshipChangeCompanyHistory.companyContacts = graduationPracticeUnify.graduationPracticeUnifyContacts
                            internshipChangeCompanyHistory.companyTel = graduationPracticeUnify.graduationPracticeUnifyTel
                            internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                        }

                        graduationPracticeUnify.graduationPracticeUnifyName = graduationPracticeUnifyVo.graduationPracticeUnifyName
                        graduationPracticeUnify.graduationPracticeUnifyAddress = graduationPracticeUnifyVo.graduationPracticeUnifyAddress
                        graduationPracticeUnify.graduationPracticeUnifyContacts = graduationPracticeUnifyVo.graduationPracticeUnifyContacts
                        graduationPracticeUnify.graduationPracticeUnifyTel = graduationPracticeUnifyVo.graduationPracticeUnifyTel
                        graduationPracticeUnifyService.update(graduationPracticeUnify)
                        ajaxUtils.success().msg("更新成功")

                        val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                        internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                        internshipChangeCompanyHistory.internshipReleaseId = graduationPracticeUnifyVo.internshipReleaseId
                        internshipChangeCompanyHistory.studentId = graduationPracticeUnifyVo.studentId
                        internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                        internshipChangeCompanyHistory.companyName = graduationPracticeUnifyVo.graduationPracticeUnifyName
                        internshipChangeCompanyHistory.companyAddress = graduationPracticeUnifyVo.graduationPracticeUnifyAddress
                        internshipChangeCompanyHistory.companyContacts = graduationPracticeUnifyVo.graduationPracticeUnifyContacts
                        internshipChangeCompanyHistory.companyTel = graduationPracticeUnifyVo.graduationPracticeUnifyTel
                        internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                    } else {
                        if (internshipApply.internshipApplyState == 3) { // 未通过
                            internshipApply.internshipApplyState = 1 // 更新为申请中
                            internshipApplyService.update(internshipApply)
                        }
                        graduationPracticeUnify.studentName = graduationPracticeUnifyVo.studentName
                        graduationPracticeUnify.collegeClass = graduationPracticeUnifyVo.collegeClass
                        graduationPracticeUnify.studentSex = graduationPracticeUnifyVo.studentSex
                        graduationPracticeUnify.studentNumber = graduationPracticeUnifyVo.studentNumber
                        graduationPracticeUnify.phoneNumber = graduationPracticeUnifyVo.phoneNumber
                        graduationPracticeUnify.qqMailbox = graduationPracticeUnifyVo.qqMailbox
                        graduationPracticeUnify.parentalContact = graduationPracticeUnifyVo.parentalContact
                        graduationPracticeUnify.headmaster = graduationPracticeUnifyVo.headmaster
                        graduationPracticeUnify.headmasterContact = graduationPracticeUnifyVo.headmasterContact
                        graduationPracticeUnify.graduationPracticeUnifyName = graduationPracticeUnifyVo.graduationPracticeUnifyName
                        graduationPracticeUnify.graduationPracticeUnifyAddress = graduationPracticeUnifyVo.graduationPracticeUnifyAddress
                        graduationPracticeUnify.graduationPracticeUnifyContacts = graduationPracticeUnifyVo.graduationPracticeUnifyContacts
                        graduationPracticeUnify.graduationPracticeUnifyTel = graduationPracticeUnifyVo.graduationPracticeUnifyTel
                        graduationPracticeUnify.schoolGuidanceTeacher = graduationPracticeUnifyVo.schoolGuidanceTeacher
                        graduationPracticeUnify.schoolGuidanceTeacherTel = graduationPracticeUnifyVo.schoolGuidanceTeacherTel
                        graduationPracticeUnify.startTime = DateTimeUtils.formatDate(graduationPracticeUnifyVo.startTime!!)
                        graduationPracticeUnify.endTime = DateTimeUtils.formatDate(graduationPracticeUnifyVo.endTime!!)
                        graduationPracticeUnifyService.update(graduationPracticeUnify)
                        ajaxUtils.success().msg("更新成功")
                    }
                } else {
                    ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
                }
            } else {
                ajaxUtils.fail().msg("参数异常，更新失败")
            }
        } catch (e: ParseException) {
            log.error("Parse time is exception {}", e)
            ajaxUtils.fail().msg("转换时间异常，更新失败")
        }

        return ajaxUtils
    }

    /**
     * 保存毕业实习(校外)
     *
     * @param graduationPracticeCompanyVo 毕业实习(校外)
     * @param bindingResult               检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/graduation/company/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun graduationCompanySave(@Valid graduationPracticeCompanyVo: GraduationPracticeCompanyVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(graduationPracticeCompanyVo.internshipReleaseId!!, graduationPracticeCompanyVo.studentId!!)
            if (!errorBean.isHasError()) {
                graduationPracticeCompanyService.saveWithTransaction(graduationPracticeCompanyVo)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
            }
        } else {
            ajaxUtils.fail().msg("参数异常，保存失败")
        }
        return ajaxUtils
    }

    /**
     * 更新毕业实习(校外)
     *
     * @param graduationPracticeCompanyVo 毕业实习(校外)
     * @param bindingResult               检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/graduation/company/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun graduationCompanyUpdate(@Valid graduationPracticeCompanyVo: GraduationPracticeCompanyVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(graduationPracticeCompanyVo.graduationPracticeCompanyId)) {
                val errorBean = accessCondition(graduationPracticeCompanyVo.internshipReleaseId!!, graduationPracticeCompanyVo.studentId!!)
                if (!errorBean.isHasError()) {
                    val headmasterArr = graduationPracticeCompanyVo.headmaster!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (headmasterArr.size >= 2) {
                        graduationPracticeCompanyVo.headmaster = headmasterArr[0]
                        graduationPracticeCompanyVo.headmasterContact = headmasterArr[1]
                    }
                    val schoolGuidanceTeacherArr = graduationPracticeCompanyVo.schoolGuidanceTeacher!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (schoolGuidanceTeacherArr.size >= 2) {
                        graduationPracticeCompanyVo.schoolGuidanceTeacher = schoolGuidanceTeacherArr[0]
                        graduationPracticeCompanyVo.schoolGuidanceTeacherTel = schoolGuidanceTeacherArr[1]
                    }
                    val graduationPracticeCompany = graduationPracticeCompanyService.findById(graduationPracticeCompanyVo.graduationPracticeCompanyId)
                    val internshipApply = errorBean.mapData!!["internshipApply"] as InternshipApply
                    if (internshipApply.internshipApplyState == 5) {
                        graduationPracticeCompany.studentName = graduationPracticeCompanyVo.studentName
                        graduationPracticeCompany.collegeClass = graduationPracticeCompanyVo.collegeClass
                        graduationPracticeCompany.studentSex = graduationPracticeCompanyVo.studentSex
                        graduationPracticeCompany.studentNumber = graduationPracticeCompanyVo.studentNumber
                        graduationPracticeCompany.phoneNumber = graduationPracticeCompanyVo.phoneNumber
                        graduationPracticeCompany.qqMailbox = graduationPracticeCompanyVo.qqMailbox
                        graduationPracticeCompany.parentalContact = graduationPracticeCompanyVo.parentalContact
                        graduationPracticeCompany.headmaster = graduationPracticeCompanyVo.headmaster
                        graduationPracticeCompany.headmasterContact = graduationPracticeCompanyVo.headmasterContact
                        graduationPracticeCompany.schoolGuidanceTeacher = graduationPracticeCompanyVo.schoolGuidanceTeacher
                        graduationPracticeCompany.schoolGuidanceTeacherTel = graduationPracticeCompanyVo.schoolGuidanceTeacherTel
                        graduationPracticeCompany.startTime = DateTimeUtils.formatDate(graduationPracticeCompanyVo.startTime!!)
                        graduationPracticeCompany.endTime = DateTimeUtils.formatDate(graduationPracticeCompanyVo.endTime!!)
                        graduationPracticeCompanyService.update(graduationPracticeCompany)
                        ajaxUtils.success().msg("更新成功")
                    } else if (internshipApply.internshipApplyState == 7) {
                        val internshipChangeCompanyHistoryRecord = internshipChangeCompanyHistoryService.findByInternshipReleaseIdAndStudentId(graduationPracticeCompanyVo.internshipReleaseId, graduationPracticeCompanyVo.studentId!!)
                        if (internshipChangeCompanyHistoryRecord.isEmpty()) {
                            val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                            internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                            internshipChangeCompanyHistory.internshipReleaseId = graduationPracticeCompany.internshipReleaseId
                            internshipChangeCompanyHistory.studentId = graduationPracticeCompany.studentId
                            internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                            internshipChangeCompanyHistory.companyName = graduationPracticeCompany.graduationPracticeCompanyName
                            internshipChangeCompanyHistory.companyAddress = graduationPracticeCompany.graduationPracticeCompanyAddress
                            internshipChangeCompanyHistory.companyContacts = graduationPracticeCompany.graduationPracticeCompanyContacts
                            internshipChangeCompanyHistory.companyTel = graduationPracticeCompany.graduationPracticeCompanyTel
                            internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                        }
                        graduationPracticeCompany.graduationPracticeCompanyName = graduationPracticeCompanyVo.graduationPracticeCompanyName
                        graduationPracticeCompany.graduationPracticeCompanyAddress = graduationPracticeCompanyVo.graduationPracticeCompanyAddress
                        graduationPracticeCompany.graduationPracticeCompanyContacts = graduationPracticeCompanyVo.graduationPracticeCompanyContacts
                        graduationPracticeCompany.graduationPracticeCompanyTel = graduationPracticeCompanyVo.graduationPracticeCompanyTel
                        graduationPracticeCompanyService.update(graduationPracticeCompany)
                        ajaxUtils.success().msg("更新成功")

                        val internshipChangeCompanyHistory = InternshipChangeCompanyHistory()
                        internshipChangeCompanyHistory.internshipChangeCompanyHistoryId = UUIDUtils.getUUID()
                        internshipChangeCompanyHistory.internshipReleaseId = graduationPracticeCompanyVo.internshipReleaseId
                        internshipChangeCompanyHistory.studentId = graduationPracticeCompanyVo.studentId
                        internshipChangeCompanyHistory.changeTime = Timestamp(Clock.systemDefaultZone().millis())
                        internshipChangeCompanyHistory.companyName = graduationPracticeCompanyVo.graduationPracticeCompanyName
                        internshipChangeCompanyHistory.companyAddress = graduationPracticeCompanyVo.graduationPracticeCompanyAddress
                        internshipChangeCompanyHistory.companyContacts = graduationPracticeCompanyVo.graduationPracticeCompanyContacts
                        internshipChangeCompanyHistory.companyTel = graduationPracticeCompanyVo.graduationPracticeCompanyTel
                        internshipChangeCompanyHistoryService.save(internshipChangeCompanyHistory)
                    } else {
                        if (internshipApply.internshipApplyState == 3) { // 未通过
                            internshipApply.internshipApplyState = 1 // 更新为申请中
                            internshipApplyService.update(internshipApply)
                        }
                        graduationPracticeCompany.studentName = graduationPracticeCompanyVo.studentName
                        graduationPracticeCompany.collegeClass = graduationPracticeCompanyVo.collegeClass
                        graduationPracticeCompany.studentSex = graduationPracticeCompanyVo.studentSex
                        graduationPracticeCompany.studentNumber = graduationPracticeCompanyVo.studentNumber
                        graduationPracticeCompany.phoneNumber = graduationPracticeCompanyVo.phoneNumber
                        graduationPracticeCompany.qqMailbox = graduationPracticeCompanyVo.qqMailbox
                        graduationPracticeCompany.parentalContact = graduationPracticeCompanyVo.parentalContact
                        graduationPracticeCompany.headmaster = graduationPracticeCompanyVo.headmaster
                        graduationPracticeCompany.headmasterContact = graduationPracticeCompanyVo.headmasterContact
                        graduationPracticeCompany.graduationPracticeCompanyName = graduationPracticeCompanyVo.graduationPracticeCompanyName
                        graduationPracticeCompany.graduationPracticeCompanyAddress = graduationPracticeCompanyVo.graduationPracticeCompanyAddress
                        graduationPracticeCompany.graduationPracticeCompanyContacts = graduationPracticeCompanyVo.graduationPracticeCompanyContacts
                        graduationPracticeCompany.graduationPracticeCompanyTel = graduationPracticeCompanyVo.graduationPracticeCompanyTel
                        graduationPracticeCompany.schoolGuidanceTeacher = graduationPracticeCompanyVo.schoolGuidanceTeacher
                        graduationPracticeCompany.schoolGuidanceTeacherTel = graduationPracticeCompanyVo.schoolGuidanceTeacherTel
                        graduationPracticeCompany.startTime = DateTimeUtils.formatDate(graduationPracticeCompanyVo.startTime!!)
                        graduationPracticeCompany.endTime = DateTimeUtils.formatDate(graduationPracticeCompanyVo.endTime!!)
                        graduationPracticeCompanyService.update(graduationPracticeCompany)
                        ajaxUtils.success().msg("更新成功")
                    }
                } else {
                    ajaxUtils.fail().msg("不符合实习条件，无法进行数据操作")
                }
            } else {
                ajaxUtils.fail().msg("参数异常，更新失败")
            }
        } catch (e: ParseException) {
            log.error("Parse time is exception {}", e)
            ajaxUtils.fail().msg("转换时间异常，更新失败")
        }

        return ajaxUtils
    }

    /**
     * 组装页面相同参数
     *
     * @param modelMap  页面对象
     * @param errorBean 判断条件
     */
    private fun buildCommonModelMap(modelMap: ModelMap, errorBean: ErrorBean<InternshipRelease>) {
        val studentBean = errorBean.mapData!!["student"] as StudentBean
        var qqMail = ""
        if (studentBean.username.toLowerCase().contains("@qq.com")) {
            qqMail = studentBean.username
        }
        modelMap.addAttribute("qqMail", qqMail)
        modelMap.addAttribute("student", studentBean)
        val internshipTeacherParams = getInternshipTeacherFromErrorBean(errorBean)
        modelMap.addAttribute("internshipTeacher", internshipTeacherParams["internshipTeacher"])
        modelMap.addAttribute("internshipTeacherName", internshipTeacherParams["internshipTeacherName"])
        modelMap.addAttribute("internshipTeacherMobile", internshipTeacherParams["internshipTeacherMobile"])
        modelMap.addAttribute("companyInfo", studentBean.schoolName!! + studentBean.collegeName!!)
        modelMap.addAttribute("companyAddr", studentBean.collegeAddress)
    }

    /**
     * 从error bean中获取指导教师
     *
     * @param errorBean 判断条件
     * @return 指导教师
     */
    private fun getInternshipTeacherFromErrorBean(errorBean: ErrorBean<InternshipRelease>): Map<String, String> {
        val params = HashMap<String, String>()
        val internshipTeacherDistribution = errorBean.mapData!!["internshipTeacherDistribution"] as InternshipTeacherDistribution
        val staffId = internshipTeacherDistribution.staffId!!
        val staffRecord = staffService.findByIdRelation(staffId)
        if (staffRecord.isPresent) {
            val staffBean = staffRecord.get().into(StaffBean::class.java)
            params.put("internshipTeacherName", staffBean.realName!!)
            params.put("internshipTeacherMobile", staffBean.mobile!!)
            params.put("internshipTeacher", staffBean.realName + " " + staffBean.mobile)
        }
        return params
    }

    /**
     * 进入实习申请页面判断条件
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") internshipReleaseId: String, studentId: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId, studentId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 检验学生
     *
     * @param info 学生信息
     * @param type 检验类型
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/valid/student"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validStudent(@RequestParam("student") info: String, type: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        var student: Student? = null
        if (type == 0) {
            student = studentService.findByUsername(info)
        } else if (type == 1) {
            student = studentService.findByStudentNumber(info)
        }
        if (!ObjectUtils.isEmpty(student)) {
            ajaxUtils.success().msg("查询学生数据成功").obj(student!!.studentId!!)
        } else {
            ajaxUtils.fail().msg("查询学生数据失败")
        }
        return ajaxUtils
    }

    /**
     * 获取班主任数据
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/apply/teachers"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun teachers(@RequestParam("id") internshipReleaseId: String, @RequestParam("studentId") studentId: Int): AjaxUtils<StaffBean> {
        val ajaxUtils = AjaxUtils.of<StaffBean>()
        val errorBean = accessCondition(internshipReleaseId, studentId)
        if (!errorBean.isHasError()) {
            val internshipRelease = errorBean.data
            var staffs: List<StaffBean> = ArrayList()
            val staffRecord = staffService.findByDepartmentIdAndEnabledAndVerifyMailboxExistsAuthoritiesRelation(internshipRelease!!.departmentId!!, 1, 1)
            if (staffRecord.isNotEmpty) {
                staffs = staffRecord.into(StaffBean::class.java)
            }
            ajaxUtils.success().msg("获取班主任数据成功").listData(staffs)
        } else {
            ajaxUtils.fail().msg("您不符合申请条件，无法获取数据")
        }
        return ajaxUtils
    }

    /**
     * 撤消状态
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/recall"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun applyRecall(@RequestParam("id") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val users = usersService.getUserFromSession()
        val student = studentService.findByUsername(users!!.username)
        if (Objects.isNull(student)) {
            return ajaxUtils.fail().msg("未查询到相关学生信息")
        }
        val studentId = student.studentId!!
        val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
        if (internshipApplyRecord.isPresent) {
            val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
            // 处于以下状态不允许撤消 2：已通过；5：基本信息变更填写中；7：单位信息变更填写中
            if (internshipApply.internshipApplyState == 2 || internshipApply.internshipApplyState == 5 ||
                    internshipApply.internshipApplyState == 7) {
                ajaxUtils.fail().msg("您当前状态下，不允许进行撤消操作")
            }
            var isCancel = false
            // 处于 0：未提交申请 1：申请中 允许撤消 该状态下的撤消将会删除所有相关实习信息
            if (internshipApply.internshipApplyState == 1 || internshipApply.internshipApplyState == 0) {
                val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
                internshipApplyService.deleteInternshipApplyRecord(internshipRelease.internshipTypeId!!, internshipReleaseId, studentId)
                internshipApplyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                internshipChangeHistoryService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                internshipChangeCompanyHistoryService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                ajaxUtils.success().msg("撤消申请成功")
            }
            // 处于4：基本信息变更申请中 6：单位信息变更申请中 在这两个状态下将返回已通过状态
            if (internshipApply.internshipApplyState == 4 || internshipApply.internshipApplyState == 6) {
                internshipApply.internshipApplyState = 2
                internshipApplyService.update(internshipApply)
                ajaxUtils.success().msg("撤消申请成功")
                isCancel = true
            }

            if (isCancel) {
                val internshipChangeHistory = InternshipChangeHistory()
                internshipChangeHistory.state = -1
                if (internshipApply.internshipApplyState == 4) {
                    internshipChangeHistory.reason = "撤消基本信息变更申请"
                }
                if (internshipApply.internshipApplyState == 6) {
                    internshipChangeHistory.reason = "撤消单位信息变更申请"
                }
                internshipChangeHistory.internshipChangeHistoryId = UUIDUtils.getUUID()
                internshipChangeHistory.internshipReleaseId = internshipReleaseId
                internshipChangeHistory.studentId = studentId
                internshipChangeHistory.applyTime = Timestamp(Clock.systemDefaultZone().millis())
                internshipChangeHistoryService.save(internshipChangeHistory)
            }
        } else {
            ajaxUtils.fail().msg("未查询到相关申请信息")
        }
        return ajaxUtils
    }

    /**
     * 基础信息变更 单位信息变更申请
     *
     * @param reason               原因
     * @param internshipApplyState 状态
     * @param internshipReleaseId  实习发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/state"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun applyState(@RequestParam("reason") reason: String, @RequestParam("internshipApplyState") internshipApplyState: Int,
                   @RequestParam("internshipReleaseId") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val users = usersService.getUserFromSession()
        val student = studentService.findByUsername(users!!.username)
        if (Objects.isNull(student)) {
            return ajaxUtils.fail().msg("未查询到相关学生信息")
        }
        val studentId = student.studentId!!
        val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
        if (internshipApplyRecord.isPresent) {
            val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
            // 处于 2：已通过 才可变更申请
            if (internshipApply.internshipApplyState == 2) {
                val now = Timestamp(Clock.systemDefaultZone().millis())
                internshipApply.internshipApplyState = internshipApplyState
                internshipApply.reason = reason
                internshipApply.applyTime = now
                internshipApplyService.update(internshipApply)
                ajaxUtils.success().msg("申请成功")

                val internshipChangeHistory = InternshipChangeHistory()
                internshipChangeHistory.internshipChangeHistoryId = UUIDUtils.getUUID()
                internshipChangeHistory.internshipReleaseId = internshipReleaseId
                internshipChangeHistory.studentId = studentId
                internshipChangeHistory.state = internshipApplyState
                internshipChangeHistory.applyTime = now
                internshipChangeHistory.reason = internshipApply.reason
                internshipChangeHistoryService.save(internshipChangeHistory)

            } else {
                ajaxUtils.fail().msg("您当前状态，无法变更申请")
            }
        } else {
            ajaxUtils.fail().msg("未查询到相关申请信息")
        }
        return ajaxUtils
    }

    /**
     * 保存实习申请资料
     *
     * @param internshipReleaseId         实习发布id
     * @param multipartHttpServletRequest 文件
     * @param request                     请求
     * @return true or false
     */
    @RequestMapping("/web/internship/apply/upload")
    @ResponseBody
    fun applyUpload(@RequestParam("internshipReleaseId") internshipReleaseId: String, multipartHttpServletRequest: MultipartHttpServletRequest, request: HttpServletRequest): AjaxUtils<FileBean> {
        val data = AjaxUtils.of<FileBean>()
        try {
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            if (Objects.isNull(student)) {
                return data.fail().msg("未查询到相关学生信息")
            }
            val studentId = student.studentId!!
            if (!ObjectUtils.isEmpty(student)) {
                val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                if (internshipApplyRecord.isPresent) {
                    val path = Workbook.internshipApplyPath(users)
                    val fileBeen = uploadService.upload(multipartHttpServletRequest,
                            RequestUtils.getRealPath(request) + path, request.remoteAddr)
                    fileBeen.forEach { fileBean ->
                        val fileId = UUIDUtils.getUUID()
                        val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
                        if (StringUtils.hasLength(internshipApply.internshipFileId)) {
                            val oldFile = filesService.findById(internshipApply.internshipFileId)
                            try {
                                FilesUtils.deleteFile(RequestUtils.getRealPath(request) + oldFile.relativePath)
                                val files = Files()
                                files.fileId = fileId
                                files.ext = fileBean.ext
                                files.newName = fileBean.newName
                                files.originalFileName = fileBean.originalFileName
                                files.size = fileBean.size.toString()
                                files.relativePath = path + fileBean.newName!!
                                filesService.save(files)
                                internshipApply.internshipFileId = fileId
                                internshipApplyService.update(internshipApply)
                                filesService.deleteById(oldFile.fileId)
                            } catch (e: IOException) {
                                log.error("Delete file error, error is {}", e)
                            }

                        }
                    }
                }
                data.success().msg("保存成功")
            } else {
                data.fail().msg("保存失败")
            }
        } catch (e: Exception) {
            log.error("Upload apply error, error is {}", e)
            data.fail().msg("保存文件异常")
        }

        return data
    }

    /**
     * 删除电子材料
     *
     * @param internshipReleaseId 实习发布id
     * @param request             请求
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/apply/delete/file"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun deleteFile(@RequestParam("id") internshipReleaseId: String, request: HttpServletRequest): AjaxUtils<*> {
        val data = AjaxUtils.of<Any>()
        try {
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            if (Objects.isNull(student)) {
                return data.fail().msg("未查询到相关学生信息")
            }
            val studentId = student.studentId!!
            val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            if (internshipApplyRecord.isPresent) {
                val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
                val files = filesService.findById(internshipApply.internshipFileId)
                internshipApply.internshipFileId = ""
                internshipApplyService.update(internshipApply)
                FilesUtils.deleteFile(RequestUtils.getRealPath(request) + files.relativePath)
                data.success().msg("删除文件成功")
            } else {
                data.fail().msg("未发现申请信息")
            }
        } catch (e: IOException) {
            log.error(" delete file is exception.", e)
            data.fail().msg("删除文件异常")
        }

        return data
    }

    /**
     * 文件下载
     *
     * @param fileId   文件id
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping("/web/internship/apply/download/file")
    fun downloadFile(@RequestParam("fileId") fileId: String, request: HttpServletRequest, response: HttpServletResponse) {
        methodControllerCommon.downloadFile(fileId, request, response)
    }

    /**
     * 进入实习申请入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private fun accessCondition(internshipReleaseId: String, studentId: Int): ErrorBean<InternshipRelease> {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            if (!internshipMethodControllerCommon.onlySelfStudentOperate(studentId)) {
                errorBean.hasError = true
                errorBean.errorMsg = "您的个人信息有误"
                return errorBean
            }

            val mapData = HashMap<String, Any>()
            val internshipRelease = errorBean.data
            val inTimeRange: Boolean// 在实习申请时间范围
            if (DateTimeUtils.timestampRangeDecide(internshipRelease!!.startTime, internshipRelease.endTime)) {
                errorBean.hasError = false
                errorBean.errorMsg = "允许填写"
                inTimeRange = true
            } else {
                errorBean.hasError = true
                errorBean.errorMsg = "不在时间范围，无法进入"
                inTimeRange = false
            }
            if (inTimeRange) {
                val studentRecord = studentService.findByIdRelation(studentId)
                if (studentRecord.isPresent) {
                    val studentBean = studentRecord.get().into(StudentBean::class.java)
                    mapData.put("student", studentBean)
                    val internshipReleaseScienceRecord = internshipReleaseScienceService.findByInternshipReleaseIdAndScienceId(internshipReleaseId, studentBean.scienceId!!)
                    if (internshipReleaseScienceRecord.isPresent) { // 判断专业
                        val internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                        if (internshipTeacherDistributionRecord.isPresent) { // 判断指导教师
                            val internshipTeacherDistribution = internshipTeacherDistributionRecord.get().into(InternshipTeacherDistribution::class.java)
                            mapData.put("internshipTeacherDistribution", internshipTeacherDistribution)
                            errorBean.hasError = false
                        } else {
                            errorBean.hasError = true
                            errorBean.errorMsg = "该学生账号未分配实习指导教师"
                            return errorBean
                        }
                    } else {
                        errorBean.hasError = true
                        errorBean.errorMsg = "该学生账号所在专业不在实习范围"
                        return errorBean
                    }
                } else {
                    errorBean.hasError = true
                    errorBean.errorMsg = "未查询到学生信息"
                    return errorBean
                }
            }
            val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            if (internshipApplyRecord.isPresent) {
                val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
                mapData.put("internshipApply", internshipApply)
                // 状态为 5：基本信息变更填写中 或 7：单位信息变更填写中 位于这两个状态，一定是通过审核后的 无视实习时间条件 但需要判断更改时间条件
                if (internshipApply.internshipApplyState == 5 || internshipApply.internshipApplyState == 7) {
                    // 判断更改时间条件
                    if (DateTimeUtils.timestampRangeDecide(internshipApply.changeFillStartTime, internshipApply.changeFillEndTime)) {
                        errorBean.hasError = false
                        errorBean.errorMsg = "允许填写"
                    } else {
                        errorBean.hasError = true
                        errorBean.errorMsg = "不在时间范围，无法进入"
                    }
                }
                // 状态为 3：未通过 该状态下 无视时间条件
                if (internshipApply.internshipApplyState == 3) {
                    // 可直接填写
                    errorBean.hasError = false
                    errorBean.errorMsg = "允许填写"
                }
                // 状态为 1：申请中；2：已通过；4：基本信息变更申请中；6：单位信息变更申请中； 则不允许进行填写 无视时间条件
                if (internshipApply.internshipApplyState == 1 || internshipApply.internshipApplyState == 2 ||
                        internshipApply.internshipApplyState == 4 || internshipApply.internshipApplyState == 6) {
                    // 不允许直接填写
                    errorBean.hasError = true
                    errorBean.errorMsg = "您当前状态，不允许填写"
                }
                // 状态为 0：未提交申请
                if (internshipApply.internshipApplyState == 0) {
                    if (inTimeRange) {
                        errorBean.hasError = false
                        errorBean.errorMsg = "允许填写"
                    } else {
                        errorBean.hasError = true
                        errorBean.errorMsg = "不在时间范围，无法进入"
                    }
                }
            }
            if (!inTimeRange && !errorBean.isHasError()) {
                val studentRecord = studentService.findByIdRelation(studentId)
                if (studentRecord.isPresent) {
                    val studentBean = studentRecord.get().into(StudentBean::class.java)
                    mapData.put("student", studentBean)
                }
                val internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                if (internshipTeacherDistributionRecord.isPresent) {
                    val internshipTeacherDistribution = internshipTeacherDistributionRecord.get().into(InternshipTeacherDistribution::class.java)
                    mapData.put("internshipTeacherDistribution", internshipTeacherDistribution)
                }
            }
            errorBean.mapData = mapData
        }

        return errorBean
    }
}