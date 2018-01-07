package top.zbeboy.isy.web.internship.review

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.internship.*
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.internship.common.InternshipConditionCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.vo.internship.review.InternshipReviewVo
import java.sql.Timestamp
import java.text.ParseException
import java.time.Clock
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-12-27 .
 **/
@Controller
open class InternshipReviewController {

    private val log = LoggerFactory.getLogger(InternshipReviewController::class.java)

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource
    open lateinit var internshipApplyService: InternshipApplyService

    @Resource
    open lateinit var internshipReviewService: InternshipReviewService

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
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var internshipChangeHistoryService: InternshipChangeHistoryService

    @Resource
    open lateinit var internshipChangeCompanyHistoryService: InternshipChangeCompanyHistoryService

    @Resource
    open lateinit var internshipTeacherDistributionService: InternshipTeacherDistributionService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var internshipConditionCommon: InternshipConditionCommon

    @Resource
    open lateinit var cacheManageService: CacheManageService


    /**
     * 实习审核
     *
     * @return 实习审核页面
     */
    @RequestMapping(value = ["/web/menu/internship/review"], method = [(RequestMethod.GET)])
    fun internshipReview(): String {
        return "web/internship/review/internship_review::#page-wrapper"
    }

    /**
     * 获取实习审核数据
     *
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/review/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipListDatas(paginationUtils: PaginationUtils): AjaxUtils<InternshipReleaseBean> {
        val ajaxUtils = AjaxUtils.of<InternshipReleaseBean>()
        val internshipReleaseBean = InternshipReleaseBean()
        internshipReleaseBean.internshipReleaseIsDel = 0
        val commonData = methodControllerCommon.adminOrNormalData()
        internshipReleaseBean.departmentId = if (StringUtils.isEmpty(commonData["departmentId"])) -1 else commonData["departmentId"]
        internshipReleaseBean.collegeId = if (StringUtils.isEmpty(commonData["collegeId"])) -1 else commonData["collegeId"]
        val records = internshipReleaseService.findAllByPage(paginationUtils, internshipReleaseBean)
        val internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipReleaseBean)
        internshipReleaseBeens.forEach { r ->
            r.waitTotalData = internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.internshipReleaseId, 1)
            r.passTotalData = internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.internshipReleaseId, 2)
            r.failTotalData = internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.internshipReleaseId, 3)
            r.basicApplyTotalData = internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.internshipReleaseId, 4)
            r.companyApplyTotalData = internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.internshipReleaseId, 6)
            r.basicFillTotalData = internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.internshipReleaseId, 5)
            r.companyFillTotalData = internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.internshipReleaseId, 7)
        }
        return ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils)
    }

    /**
     * 进入实习审核页面判断条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/review/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 实习审核页面
     *
     * @param internshipReleaseId 实习发布id
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/review/audit"], method = [(RequestMethod.GET)])
    fun reviewAudit(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            "web/internship/review/internship_audit::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 实习已通过学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/review/pass"], method = [(RequestMethod.GET)])
    fun reviewPass(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            "web/internship/review/internship_pass::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 实习未通过学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/review/fail"], method = [(RequestMethod.GET)])
    fun reviewFail(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            "web/internship/review/internship_fail::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 实习申请基本信息修改学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/review/base_info_apply"], method = [(RequestMethod.GET)])
    fun reviewBaseInfoApply(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            "web/internship/review/internship_base_info_apply::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 实习基本信息修改填写中学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/review/base_info_fill"], method = [(RequestMethod.GET)])
    fun reviewBaseInfoFill(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            "web/internship/review/internship_base_info_fill::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 实习单位信息修改申请学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/review/company_apply"], method = [(RequestMethod.GET)])
    fun reviewCompanyApply(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            "web/internship/review/internship_company_apply::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 实习单位信息修改填写中学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/review/company_fill"], method = [(RequestMethod.GET)])
    fun reviewCompanyFill(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            "web/internship/review/internship_company_fill::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 查看详情页
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/review/audit/detail"], method = [(RequestMethod.GET)])
    fun auditDetail(@RequestParam("internshipReleaseId") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, modelMap: ModelMap): String {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        return if (!errorBean.isHasError()) {
            val internshipRelease = errorBean.data
            val internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease!!.internshipTypeId!!)
            when (internshipType.internshipTypeName) {
                Workbook.INTERNSHIP_COLLEGE_TYPE -> {
                    val internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (internshipCollegeRecord.isPresent) {
                        val internshipCollege = internshipCollegeRecord.get().into(InternshipCollege::class.java)
                        val student = cacheManageService.getStudentByStudentId(internshipCollege.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        internshipCollege.studentSex = methodControllerCommon.decryptPersonalData(internshipCollege.studentSex, usersKey)
                        internshipCollege.parentalContact = methodControllerCommon.decryptPersonalData(internshipCollege.parentalContact, usersKey)
                        modelMap.addAttribute("internshipData", internshipCollege)
                        "web/internship/review/internship_college_detail::#page-wrapper"
                    } else {
                        methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                    }
                }
                Workbook.INTERNSHIP_COMPANY_TYPE -> {
                    val internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (internshipCompanyRecord.isPresent) {
                        val internshipCompany = internshipCompanyRecord.get().into(InternshipCompany::class.java)
                        val student = cacheManageService.getStudentByStudentId(internshipCompany.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        internshipCompany.studentSex = methodControllerCommon.decryptPersonalData(internshipCompany.studentSex, usersKey)
                        internshipCompany.parentalContact = methodControllerCommon.decryptPersonalData(internshipCompany.parentalContact, usersKey)
                        modelMap.addAttribute("internshipData", internshipCompany)
                        "web/internship/review/internship_company_detail::#page-wrapper"
                    } else {
                        methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                    }
                }
                Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> {
                    val graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeCollegeRecord.isPresent) {
                        val graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege::class.java)
                        val student = cacheManageService.getStudentByStudentId(graduationPracticeCollege.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        graduationPracticeCollege.studentSex = methodControllerCommon.decryptPersonalData(graduationPracticeCollege.studentSex, usersKey)
                        graduationPracticeCollege.parentalContact = methodControllerCommon.decryptPersonalData(graduationPracticeCollege.parentalContact, usersKey)
                        modelMap.addAttribute("internshipData", graduationPracticeCollege)
                        "web/internship/review/graduation_practice_college_detail::#page-wrapper"
                    } else {
                        methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                    }
                }
                Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> {
                    val graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeUnifyRecord.isPresent) {
                        val graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify::class.java)
                        val student = cacheManageService.getStudentByStudentId(graduationPracticeUnify.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        graduationPracticeUnify.studentSex = methodControllerCommon.decryptPersonalData(graduationPracticeUnify.studentSex, usersKey)
                        graduationPracticeUnify.parentalContact = methodControllerCommon.decryptPersonalData(graduationPracticeUnify.parentalContact, usersKey)
                        modelMap.addAttribute("internshipData", graduationPracticeUnify)
                        "web/internship/review/graduation_practice_unify_detail::#page-wrapper"
                    } else {
                        methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                    }
                }
                Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> {
                    val graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeCompanyRecord.isPresent) {
                        val graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany::class.java)
                        val student = cacheManageService.getStudentByStudentId(graduationPracticeCompany.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        graduationPracticeCompany.studentSex = methodControllerCommon.decryptPersonalData(graduationPracticeCompany.studentSex, usersKey)
                        graduationPracticeCompany.parentalContact = methodControllerCommon.decryptPersonalData(graduationPracticeCompany.parentalContact, usersKey)
                        modelMap.addAttribute("internshipData", graduationPracticeCompany)
                        "web/internship/review/graduation_practice_company_detail::#page-wrapper"
                    } else {
                        methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
                    }
                }
                else -> methodControllerCommon.showTip(modelMap, "未找到相关实习类型页面")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 查询申请中的学生数据
     *
     * @param internshipReviewVo 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/review/audit/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun auditDatas(internshipReviewVo: InternshipReviewVo): AjaxUtils<*> {
        val internshipApplyBean = InternshipApplyBean()
        internshipApplyBean.internshipApplyState = 1
        internshipApplyBean.internshipReleaseId = internshipReviewVo.internshipReleaseId
        return internshipReviewData(internshipReviewVo, internshipApplyBean)
    }

    /**
     * 查询已通过的学生数据
     *
     * @param internshipReviewVo 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/review/pass/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun passDatas(internshipReviewVo: InternshipReviewVo): AjaxUtils<*> {
        val internshipApplyBean = InternshipApplyBean()
        internshipApplyBean.internshipApplyState = 2
        internshipApplyBean.internshipReleaseId = internshipReviewVo.internshipReleaseId
        return internshipReviewData(internshipReviewVo, internshipApplyBean)
    }

    /**
     * 查询未通过的学生数据
     *
     * @param internshipReviewVo 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/review/fail/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun failDatas(internshipReviewVo: InternshipReviewVo): AjaxUtils<*> {
        val internshipApplyBean = InternshipApplyBean()
        internshipApplyBean.internshipApplyState = 3
        internshipApplyBean.internshipReleaseId = internshipReviewVo.internshipReleaseId
        return internshipReviewData(internshipReviewVo, internshipApplyBean)
    }

    /**
     * 查询基本信息变更申请的学生数据
     *
     * @param internshipReviewVo 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/review/base_info_apply/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun baseInfoApplyDatas(internshipReviewVo: InternshipReviewVo): AjaxUtils<*> {
        val internshipApplyBean = InternshipApplyBean()
        internshipApplyBean.internshipApplyState = 4
        internshipApplyBean.internshipReleaseId = internshipReviewVo.internshipReleaseId
        return internshipReviewData(internshipReviewVo, internshipApplyBean)
    }

    /**
     * 查询基本信息变更填写中的学生数据
     *
     * @param internshipReviewVo 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/review/base_info_fill/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun baseInfoFillDatas(internshipReviewVo: InternshipReviewVo): AjaxUtils<*> {
        val internshipApplyBean = InternshipApplyBean()
        internshipApplyBean.internshipApplyState = 5
        internshipApplyBean.internshipReleaseId = internshipReviewVo.internshipReleaseId
        return internshipReviewData(internshipReviewVo, internshipApplyBean)
    }

    /**
     * 查询单位信息变更申请的学生数据
     *
     * @param internshipReviewVo 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/review/company_apply/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun companyApplyDatas(internshipReviewVo: InternshipReviewVo): AjaxUtils<*> {
        val internshipApplyBean = InternshipApplyBean()
        internshipApplyBean.internshipApplyState = 6
        internshipApplyBean.internshipReleaseId = internshipReviewVo.internshipReleaseId
        return internshipReviewData(internshipReviewVo, internshipApplyBean)
    }

    /**
     * 查询单位信息变更填写中的学生数据
     *
     * @param internshipReviewVo 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/review/company_fill/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun companyFillDatas(internshipReviewVo: InternshipReviewVo): AjaxUtils<*> {
        val internshipApplyBean = InternshipApplyBean()
        internshipApplyBean.internshipApplyState = 7
        internshipApplyBean.internshipReleaseId = internshipReviewVo.internshipReleaseId
        return internshipReviewData(internshipReviewVo, internshipApplyBean)
    }

    /**
     * 数据
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 申请条件
     * @return 数据
     */
    private fun internshipReviewData(paginationUtils: PaginationUtils, internshipApplyBean: InternshipApplyBean): AjaxUtils<InternshipReviewBean> {
        val ajaxUtils = AjaxUtils.of<InternshipReviewBean>()
        val errorBean = internshipConditionCommon.basicCondition(internshipApplyBean.internshipReleaseId!!)
        if (!errorBean.isHasError()) {
            val internshipReviewBeens = internshipReviewService.findAllByPage(paginationUtils, internshipApplyBean)
            if (!ObjectUtils.isEmpty(internshipReviewBeens)) {
                internshipReviewBeens.forEach { i -> fillInternshipReviewBean(i) }
            }
            ajaxUtils.success().msg("获取数据成功").listData(internshipReviewBeens).paginationUtils(paginationUtils)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 实习审核 保存
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/review/audit/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun auditSave(internshipReviewBean: InternshipReviewBean): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!ObjectUtils.isEmpty(internshipReviewBean.internshipReleaseId) && !ObjectUtils.isEmpty(internshipReviewBean.studentId)) {
            val errorBean = internshipConditionCommon.basicCondition(internshipReviewBean.internshipReleaseId!!)
            if (!errorBean.isHasError()) {
                val internshipRelease = errorBean.data
                val internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease!!.internshipTypeId!!)
                updateInternshipMaterialState(internshipType, internshipReviewBean)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("未查询相关实习信息")
            }
        } else {
            ajaxUtils.fail().msg("缺失必要参数")
        }
        return ajaxUtils
    }

    /**
     * 实习审核 通过
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/review/audit/pass"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun auditPass(internshipReviewBean: InternshipReviewBean, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!ObjectUtils.isEmpty(internshipReviewBean.internshipReleaseId) && !ObjectUtils.isEmpty(internshipReviewBean.studentId)) {
            val errorBean = internshipConditionCommon.basicCondition(internshipReviewBean.internshipReleaseId!!)
            if (!errorBean.isHasError()) {
                val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                if (internshipApplyRecord.isPresent) {
                    val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
                    internshipApply.reason = internshipReviewBean.reason
                    internshipApply.internshipApplyState = internshipReviewBean.internshipApplyState
                    internshipApplyService.update(internshipApply)
                    val internshipRelease = internshipReleaseService.findById(internshipReviewBean.internshipReleaseId!!)
                    if (!ObjectUtils.isEmpty(internshipRelease)) {
                        val internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.internshipTypeId!!)
                        updateInternshipMaterialState(internshipType, internshipReviewBean)
                        val internshipChangeHistory = InternshipChangeHistory()
                        internshipChangeHistory.internshipChangeHistoryId = UUIDUtils.getUUID()
                        internshipChangeHistory.internshipReleaseId = internshipReviewBean.internshipReleaseId
                        internshipChangeHistory.studentId = internshipReviewBean.studentId
                        internshipChangeHistory.state = internshipReviewBean.internshipApplyState
                        internshipChangeHistory.applyTime = Timestamp(Clock.systemDefaultZone().millis())
                        internshipChangeHistoryService.save(internshipChangeHistory)

                        val userRecord = studentService.findByIdRelation(internshipReviewBean.studentId)
                        if (userRecord.isPresent) {
                            val users = userRecord.get().into(Users::class.java)
                            val curUsers = usersService.getUserFromSession()
                            val notify = "您的自主实习 " + internshipRelease.internshipTitle + " 申请已通过。"
                            methodControllerCommon.sendNotify(users, curUsers!!, internshipRelease.internshipTitle, notify, request)
                        }
                        ajaxUtils.success().msg("保存成功")
                    } else {
                        ajaxUtils.fail().msg("未查询到相关实习信息")
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关实习申请信息")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("缺失必要参数")
        }
        return ajaxUtils
    }

    /**
     * 更新实习材料状态
     */
    private fun updateInternshipMaterialState(internshipType: InternshipType, internshipReviewBean: InternshipReviewBean) {
        when (internshipType.internshipTypeName) {
            Workbook.INTERNSHIP_COLLEGE_TYPE -> {
                val internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                if (internshipCollegeRecord.isPresent) {
                    val internshipCollege = internshipCollegeRecord.get().into(InternshipCollege::class.java)
                    internshipCollege.commitmentBook = internshipReviewBean.commitmentBook
                    internshipCollege.safetyResponsibilityBook = internshipReviewBean.safetyResponsibilityBook
                    internshipCollege.practiceAgreement = internshipReviewBean.practiceAgreement
                    internshipCollege.internshipApplication = internshipReviewBean.internshipApplication
                    internshipCollege.practiceReceiving = internshipReviewBean.practiceReceiving
                    internshipCollege.securityEducationAgreement = internshipReviewBean.securityEducationAgreement
                    internshipCollege.parentalConsent = internshipReviewBean.parentalConsent
                    internshipCollegeService.update(internshipCollege)
                }
            }
            Workbook.INTERNSHIP_COMPANY_TYPE -> {
                val internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                if (internshipCompanyRecord.isPresent) {
                    val internshipCompany = internshipCompanyRecord.get().into(InternshipCompany::class.java)
                    internshipCompany.commitmentBook = internshipReviewBean.commitmentBook
                    internshipCompany.safetyResponsibilityBook = internshipReviewBean.safetyResponsibilityBook
                    internshipCompany.practiceAgreement = internshipReviewBean.practiceAgreement
                    internshipCompany.internshipApplication = internshipReviewBean.internshipApplication
                    internshipCompany.practiceReceiving = internshipReviewBean.practiceReceiving
                    internshipCompany.securityEducationAgreement = internshipReviewBean.securityEducationAgreement
                    internshipCompany.parentalConsent = internshipReviewBean.parentalConsent
                    internshipCompanyService.update(internshipCompany)
                }
            }
            Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> {
                val graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                if (graduationPracticeCollegeRecord.isPresent) {
                    val graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege::class.java)
                    graduationPracticeCollege.commitmentBook = internshipReviewBean.commitmentBook
                    graduationPracticeCollege.safetyResponsibilityBook = internshipReviewBean.safetyResponsibilityBook
                    graduationPracticeCollege.practiceAgreement = internshipReviewBean.practiceAgreement
                    graduationPracticeCollege.internshipApplication = internshipReviewBean.internshipApplication
                    graduationPracticeCollege.practiceReceiving = internshipReviewBean.practiceReceiving
                    graduationPracticeCollege.securityEducationAgreement = internshipReviewBean.securityEducationAgreement
                    graduationPracticeCollege.parentalConsent = internshipReviewBean.parentalConsent
                    graduationPracticeCollegeService.update(graduationPracticeCollege)
                }
            }
            Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> {
                val graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                if (graduationPracticeUnifyRecord.isPresent) {
                    val graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify::class.java)
                    graduationPracticeUnify.commitmentBook = internshipReviewBean.commitmentBook
                    graduationPracticeUnify.safetyResponsibilityBook = internshipReviewBean.safetyResponsibilityBook
                    graduationPracticeUnify.practiceAgreement = internshipReviewBean.practiceAgreement
                    graduationPracticeUnify.internshipApplication = internshipReviewBean.internshipApplication
                    graduationPracticeUnify.practiceReceiving = internshipReviewBean.practiceReceiving
                    graduationPracticeUnify.securityEducationAgreement = internshipReviewBean.securityEducationAgreement
                    graduationPracticeUnify.parentalConsent = internshipReviewBean.parentalConsent
                    graduationPracticeUnifyService.update(graduationPracticeUnify)
                }
            }
            Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> {
                val graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                if (graduationPracticeCompanyRecord.isPresent) {
                    val graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany::class.java)
                    graduationPracticeCompany.commitmentBook = internshipReviewBean.commitmentBook
                    graduationPracticeCompany.safetyResponsibilityBook = internshipReviewBean.safetyResponsibilityBook
                    graduationPracticeCompany.practiceAgreement = internshipReviewBean.practiceAgreement
                    graduationPracticeCompany.internshipApplication = internshipReviewBean.internshipApplication
                    graduationPracticeCompany.practiceReceiving = internshipReviewBean.practiceReceiving
                    graduationPracticeCompany.securityEducationAgreement = internshipReviewBean.securityEducationAgreement
                    graduationPracticeCompany.parentalConsent = internshipReviewBean.parentalConsent
                    graduationPracticeCompanyService.update(graduationPracticeCompany)
                }
            }
        }
    }

    /**
     * 更新实习材料状态
     *
     * @param internshipType 实习类型
     * @param b              状态
     */
    private fun updateInternshipMaterialState(internshipType: InternshipType, b: Byte?, InternshipReleaseId: String, studentId: Int) {
        when (internshipType.internshipTypeName) {
            Workbook.INTERNSHIP_COLLEGE_TYPE -> {
                val internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId)
                if (internshipCollegeRecord.isPresent) {
                    val internshipCollege = internshipCollegeRecord.get().into(InternshipCollege::class.java)
                    internshipCollege.commitmentBook = b
                    internshipCollege.safetyResponsibilityBook = b
                    internshipCollege.practiceAgreement = b
                    internshipCollege.internshipApplication = b
                    internshipCollege.practiceReceiving = b
                    internshipCollege.securityEducationAgreement = b
                    internshipCollege.parentalConsent = b
                    internshipCollegeService.update(internshipCollege)
                }
            }
            Workbook.INTERNSHIP_COMPANY_TYPE -> {
                val internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId)
                if (internshipCompanyRecord.isPresent) {
                    val internshipCompany = internshipCompanyRecord.get().into(InternshipCompany::class.java)
                    internshipCompany.commitmentBook = b
                    internshipCompany.safetyResponsibilityBook = b
                    internshipCompany.practiceAgreement = b
                    internshipCompany.internshipApplication = b
                    internshipCompany.practiceReceiving = b
                    internshipCompany.securityEducationAgreement = b
                    internshipCompany.parentalConsent = b
                    internshipCompanyService.update(internshipCompany)
                }
            }
            Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> {
                val graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId)
                if (graduationPracticeCollegeRecord.isPresent) {
                    val graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege::class.java)
                    graduationPracticeCollege.commitmentBook = b
                    graduationPracticeCollege.safetyResponsibilityBook = b
                    graduationPracticeCollege.practiceAgreement = b
                    graduationPracticeCollege.internshipApplication = b
                    graduationPracticeCollege.practiceReceiving = b
                    graduationPracticeCollege.securityEducationAgreement = b
                    graduationPracticeCollege.parentalConsent = b
                    graduationPracticeCollegeService.update(graduationPracticeCollege)
                }
            }
            Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> {
                val graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId)
                if (graduationPracticeUnifyRecord.isPresent) {
                    val graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify::class.java)
                    graduationPracticeUnify.commitmentBook = b
                    graduationPracticeUnify.safetyResponsibilityBook = b
                    graduationPracticeUnify.practiceAgreement = b
                    graduationPracticeUnify.internshipApplication = b
                    graduationPracticeUnify.practiceReceiving = b
                    graduationPracticeUnify.securityEducationAgreement = b
                    graduationPracticeUnify.parentalConsent = b
                    graduationPracticeUnifyService.update(graduationPracticeUnify)
                }
            }
            Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> {
                val graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId)
                if (graduationPracticeCompanyRecord.isPresent) {
                    val graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany::class.java)
                    graduationPracticeCompany.commitmentBook = b
                    graduationPracticeCompany.safetyResponsibilityBook = b
                    graduationPracticeCompany.practiceAgreement = b
                    graduationPracticeCompany.internshipApplication = b
                    graduationPracticeCompany.practiceReceiving = b
                    graduationPracticeCompany.securityEducationAgreement = b
                    graduationPracticeCompany.parentalConsent = b
                    graduationPracticeCompanyService.update(graduationPracticeCompany)
                }
            }
        }
    }

    /**
     * 实习审核 同意  基本信息修改申请 单位信息修改申请
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/review/audit/agree"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun auditAgree(internshipReviewBean: InternshipReviewBean, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (!ObjectUtils.isEmpty(internshipReviewBean.internshipReleaseId) && !ObjectUtils.isEmpty(internshipReviewBean.studentId)) {
                val errorBean = internshipConditionCommon.basicCondition(internshipReviewBean.internshipReleaseId!!)
                if (!errorBean.isHasError()) {
                    val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                    if (internshipApplyRecord.isPresent) {
                        val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
                        internshipApply.reason = internshipReviewBean.reason
                        internshipApply.internshipApplyState = internshipReviewBean.internshipApplyState
                        val format = "yyyy-MM-dd HH:mm:ss"
                        if (StringUtils.hasLength(internshipReviewBean.fillTime)) {
                            val timeArr = DateTimeUtils.splitDateTime("至", internshipReviewBean.fillTime!!)
                            internshipApply.changeFillStartTime = DateTimeUtils.formatDateToTimestamp(timeArr[0], format)
                            internshipApply.changeFillEndTime = DateTimeUtils.formatDateToTimestamp(timeArr[1], format)
                        }
                        internshipApplyService.update(internshipApply)

                        // 若同意进入 7：单位信息变更填写中 需要删除提交材料的状态
                        if (internshipReviewBean.internshipApplyState == 7) {
                            val internshipRelease = internshipReleaseService.findById(internshipReviewBean.internshipReleaseId!!)
                            val internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.internshipTypeId!!)
                            updateInternshipMaterialState(internshipType, 0, internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                        }

                        ajaxUtils.success().msg("更新状态成功")
                        val internshipChangeHistory = InternshipChangeHistory()
                        internshipChangeHistory.internshipChangeHistoryId = UUIDUtils.getUUID()
                        internshipChangeHistory.internshipReleaseId = internshipReviewBean.internshipReleaseId
                        internshipChangeHistory.studentId = internshipReviewBean.studentId
                        internshipChangeHistory.state = internshipReviewBean.internshipApplyState
                        internshipChangeHistory.applyTime = Timestamp(Clock.systemDefaultZone().millis())
                        internshipChangeHistory.reason = internshipApply.reason
                        internshipChangeHistory.changeFillStartTime = internshipApply.changeFillStartTime
                        internshipChangeHistory.changeFillEndTime = internshipApply.changeFillEndTime
                        internshipChangeHistoryService.save(internshipChangeHistory)

                        val userRecord = studentService.findByIdRelation(internshipReviewBean.studentId)
                        if (userRecord.isPresent) {
                            val users = userRecord.get().into(Users::class.java)
                            val curUsers = usersService.getUserFromSession()
                            val notify = "已同意您的实习变更申请，请尽快登录系统在填写时间范围变更您的内容。"
                            methodControllerCommon.sendNotify(users, curUsers!!, "同意实习变更申请", notify, request)
                        }
                    } else {
                        ajaxUtils.fail().msg("未查询到相关实习申请信息")
                    }
                } else {
                    ajaxUtils.fail().msg(errorBean.errorMsg!!)
                }
            } else {
                ajaxUtils.fail().msg("缺失必要参数")
            }
        } catch (e: ParseException) {
            log.error(" format time is exception.", e)
            ajaxUtils.fail().msg("时间参数异常")
        }

        return ajaxUtils
    }

    /**
     * 实习审核 拒绝  基本信息修改申请 单位信息修改申请
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/review/audit/disagree"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun auditDisagree(internshipReviewBean: InternshipReviewBean, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!ObjectUtils.isEmpty(internshipReviewBean.internshipReleaseId) && !ObjectUtils.isEmpty(internshipReviewBean.studentId)) {
            val errorBean = internshipConditionCommon.basicCondition(internshipReviewBean.internshipReleaseId!!)
            if (!errorBean.isHasError()) {
                val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.internshipReleaseId!!, internshipReviewBean.studentId)
                if (internshipApplyRecord.isPresent) {
                    val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
                    internshipApply.reason = internshipReviewBean.reason
                    internshipApply.internshipApplyState = internshipReviewBean.internshipApplyState
                    internshipApplyService.update(internshipApply)
                    ajaxUtils.success().msg("更新状态成功")
                    val internshipChangeHistory = InternshipChangeHistory()
                    internshipChangeHistory.internshipChangeHistoryId = UUIDUtils.getUUID()
                    internshipChangeHistory.internshipReleaseId = internshipReviewBean.internshipReleaseId
                    internshipChangeHistory.studentId = internshipReviewBean.studentId
                    internshipChangeHistory.state = internshipReviewBean.internshipApplyState
                    internshipChangeHistory.applyTime = Timestamp(Clock.systemDefaultZone().millis())
                    internshipChangeHistory.reason = internshipApply.reason
                    internshipChangeHistoryService.save(internshipChangeHistory)

                    val userRecord = studentService.findByIdRelation(internshipReviewBean.studentId)
                    if (userRecord.isPresent) {
                        val users = userRecord.get().into(Users::class.java)
                        val curUsers = usersService.getUserFromSession()
                        val notify = "已拒绝您的实习变更申请。"
                        methodControllerCommon.sendNotify(users, curUsers!!, "拒绝实习变更申请", notify, request)
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关实习申请信息")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("缺失必要参数")
        }
        return ajaxUtils
    }

    /**
     * 实习审核 不通过
     *
     * @param reason               原因
     * @param internshipApplyState 实习审核状态
     * @param internshipReleaseId  实习发布id
     * @param studentId            学生id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/review/audit/fail"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun auditFail(@RequestParam("reason") reason: String, @RequestParam("internshipApplyState") internshipApplyState: Int,
                  @RequestParam("internshipReleaseId") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            if (internshipApplyRecord.isPresent) {
                val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
                internshipApply.internshipApplyState = internshipApplyState
                internshipApply.reason = reason
                internshipApplyService.update(internshipApply)
                ajaxUtils.success().msg("更改成功")
                val internshipChangeHistory = InternshipChangeHistory()
                internshipChangeHistory.internshipChangeHistoryId = UUIDUtils.getUUID()
                internshipChangeHistory.internshipReleaseId = internshipReleaseId
                internshipChangeHistory.studentId = studentId
                internshipChangeHistory.state = internshipApplyState
                internshipChangeHistory.applyTime = Timestamp(Clock.systemDefaultZone().millis())
                internshipChangeHistory.reason = internshipApply.reason
                internshipChangeHistoryService.save(internshipChangeHistory)

                val userRecord = studentService.findByIdRelation(studentId)
                if (userRecord.isPresent) {
                    val users = userRecord.get().into(Users::class.java)
                    val curUsers = usersService.getUserFromSession()
                    val notify = "您的自主实习申请未通过，具体原因：" + reason
                    methodControllerCommon.sendNotify(users, curUsers!!, "实习申请未通过", notify, request)
                }
            } else {
                ajaxUtils.fail().msg("未查询到相关申请信息")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 删除申请记录
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/review/audit/delete"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun auditDelete(@RequestParam("internshipReleaseId") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
        if (!ObjectUtils.isEmpty(internshipRelease)) {
            internshipApplyService.deleteInternshipApplyRecord(internshipRelease.internshipTypeId!!, internshipReleaseId, studentId)
            internshipApplyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            internshipChangeHistoryService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            internshipChangeCompanyHistoryService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            internshipTeacherDistributionService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)

            val userRecord = studentService.findByIdRelation(studentId)
            if (userRecord.isPresent) {
                val users = userRecord.get().into(Users::class.java)
                val curUsers = usersService.getUserFromSession()
                val notify = "您的自主实习可能存在问题，已被管理员删除此次申请，若您有任何疑问，请联系管理员"
                methodControllerCommon.sendNotify(users, curUsers!!, "实习申请被删除", notify, request)
            }
            ajaxUtils.success().msg("删除申请成功")
        } else {
            ajaxUtils.fail().msg("未查询到相关实习信息")
        }
        return ajaxUtils
    }

    /**
     * 填充实习数据
     *
     * @param internshipReviewBean 学生申请数据
     * @return 学生申请数据
     */
    private fun fillInternshipReviewBean(internshipReviewBean: InternshipReviewBean) {
        val internshipTypeId = internshipReviewBean.internshipTypeId
        val studentId = internshipReviewBean.studentId
        val internshipReleaseId = internshipReviewBean.internshipReleaseId
        val internshipType = internshipTypeService.findByInternshipTypeId(internshipTypeId)
        when (internshipType.internshipTypeName) {
            Workbook.INTERNSHIP_COLLEGE_TYPE -> {
                val internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId!!, studentId)
                if (internshipCollegeRecord.isPresent) {
                    val internshipCollege = internshipCollegeRecord.get().into(InternshipCollege::class.java)
                    internshipReviewBean.hasController = true
                    internshipReviewBean.commitmentBook = internshipCollege.commitmentBook
                    internshipReviewBean.safetyResponsibilityBook = internshipCollege.safetyResponsibilityBook
                    internshipReviewBean.practiceAgreement = internshipCollege.practiceAgreement
                    internshipReviewBean.internshipApplication = internshipCollege.internshipApplication
                    internshipReviewBean.practiceReceiving = internshipCollege.practiceReceiving
                    internshipReviewBean.securityEducationAgreement = internshipCollege.securityEducationAgreement
                    internshipReviewBean.parentalConsent = internshipCollege.parentalConsent
                }
            }
            Workbook.INTERNSHIP_COMPANY_TYPE -> {
                val internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId!!, studentId)
                if (internshipCompanyRecord.isPresent) {
                    val internshipCompany = internshipCompanyRecord.get().into(InternshipCompany::class.java)
                    internshipReviewBean.hasController = true
                    internshipReviewBean.commitmentBook = internshipCompany.commitmentBook
                    internshipReviewBean.safetyResponsibilityBook = internshipCompany.safetyResponsibilityBook
                    internshipReviewBean.practiceAgreement = internshipCompany.practiceAgreement
                    internshipReviewBean.internshipApplication = internshipCompany.internshipApplication
                    internshipReviewBean.practiceReceiving = internshipCompany.practiceReceiving
                    internshipReviewBean.securityEducationAgreement = internshipCompany.securityEducationAgreement
                    internshipReviewBean.parentalConsent = internshipCompany.parentalConsent
                }
            }
            Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> {
                val graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId!!, studentId)
                if (graduationPracticeCollegeRecord.isPresent) {
                    val graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege::class.java)
                    internshipReviewBean.hasController = true
                    internshipReviewBean.commitmentBook = graduationPracticeCollege.commitmentBook
                    internshipReviewBean.safetyResponsibilityBook = graduationPracticeCollege.safetyResponsibilityBook
                    internshipReviewBean.practiceAgreement = graduationPracticeCollege.practiceAgreement
                    internshipReviewBean.internshipApplication = graduationPracticeCollege.internshipApplication
                    internshipReviewBean.practiceReceiving = graduationPracticeCollege.practiceReceiving
                    internshipReviewBean.securityEducationAgreement = graduationPracticeCollege.securityEducationAgreement
                    internshipReviewBean.parentalConsent = graduationPracticeCollege.parentalConsent
                }
            }
            Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> {
                val graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId!!, studentId)
                if (graduationPracticeUnifyRecord.isPresent) {
                    val graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify::class.java)
                    internshipReviewBean.hasController = true
                    internshipReviewBean.commitmentBook = graduationPracticeUnify.commitmentBook
                    internshipReviewBean.safetyResponsibilityBook = graduationPracticeUnify.safetyResponsibilityBook
                    internshipReviewBean.practiceAgreement = graduationPracticeUnify.practiceAgreement
                    internshipReviewBean.internshipApplication = graduationPracticeUnify.internshipApplication
                    internshipReviewBean.practiceReceiving = graduationPracticeUnify.practiceReceiving
                    internshipReviewBean.securityEducationAgreement = graduationPracticeUnify.securityEducationAgreement
                    internshipReviewBean.parentalConsent = graduationPracticeUnify.parentalConsent
                }
            }
            Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> {
                val graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId!!, studentId)
                if (graduationPracticeCompanyRecord.isPresent) {
                    val graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany::class.java)
                    internshipReviewBean.hasController = true
                    internshipReviewBean.commitmentBook = graduationPracticeCompany.commitmentBook
                    internshipReviewBean.safetyResponsibilityBook = graduationPracticeCompany.safetyResponsibilityBook
                    internshipReviewBean.practiceAgreement = graduationPracticeCompany.practiceAgreement
                    internshipReviewBean.internshipApplication = graduationPracticeCompany.internshipApplication
                    internshipReviewBean.practiceReceiving = graduationPracticeCompany.practiceReceiving
                    internshipReviewBean.securityEducationAgreement = graduationPracticeCompany.securityEducationAgreement
                    internshipReviewBean.parentalConsent = graduationPracticeCompany.parentalConsent
                }
            }
        }
    }

    /**
     * 文件下载
     *
     * @param fileId   文件id
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping("/web/internship/review/download/file")
    fun downloadFile(@RequestParam("fileId") fileId: String, request: HttpServletRequest, response: HttpServletResponse) {
        methodControllerCommon.downloadFile(fileId, request, response)
    }
}