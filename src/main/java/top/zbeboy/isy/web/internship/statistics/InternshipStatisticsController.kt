package top.zbeboy.isy.web.internship.statistics

import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.export.*
import top.zbeboy.isy.service.internship.*
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.export.ExportBean
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.bean.internship.statistics.InternshipChangeCompanyHistoryBean
import top.zbeboy.isy.web.bean.internship.statistics.InternshipChangeHistoryBean
import top.zbeboy.isy.web.bean.internship.statistics.InternshipStatisticsBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import java.io.IOException
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-12-28 .
 **/
@Controller
open class InternshipStatisticsController {

    private val log = LoggerFactory.getLogger(InternshipStatisticsController::class.java)

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource
    open lateinit var internshipTypeService: InternshipTypeService

    @Resource
    open lateinit var internshipStatisticsService: InternshipStatisticsService

    @Resource
    open lateinit var internshipCollegeService: InternshipCollegeService

    @Resource
    open lateinit var internshipCompanyService: InternshipCompanyService

    @Resource
    open lateinit var graduationPracticeCompanyService: GraduationPracticeCompanyService

    @Resource
    open lateinit var graduationPracticeCollegeService: GraduationPracticeCollegeService

    @Resource
    open lateinit var graduationPracticeUnifyService: GraduationPracticeUnifyService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var internshipChangeHistoryService: InternshipChangeHistoryService

    @Resource
    open lateinit var internshipChangeCompanyHistoryService: InternshipChangeCompanyHistoryService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var uploadService: UploadService


    /**
     * 实习统计
     *
     * @return 实习统计页面
     */
    @RequestMapping(value = ["/web/menu/internship/statistical"], method = [(RequestMethod.GET)])
    fun internshipStatistical(): String {
        return "web/internship/statistics/internship_statistics::#page-wrapper"
    }

    /**
     * 已提交列表
     *
     * @return 已提交列表 统计页面
     */
    @RequestMapping(value = ["/web/internship/statistical/submitted"], method = [(RequestMethod.GET)])
    fun statisticalSubmitted(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
        return "web/internship/statistics/internship_submitted::#page-wrapper"
    }

    /**
     * 申请记录列表
     *
     * @return 申请记录列表页面
     */
    @RequestMapping(value = ["/web/internship/statistical/record/apply"], method = [(RequestMethod.GET)])
    fun changeHistory(@RequestParam("id") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, modelMap: ModelMap): String {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
        modelMap.addAttribute("studentId", studentId)
        return "web/internship/statistics/internship_change_history::#page-wrapper"
    }

    /**
     * 单位变更记录列表
     *
     * @return 单位变更记录列表页面
     */
    @RequestMapping(value = ["/web/internship/statistical/record/company"], method = [(RequestMethod.GET)])
    fun changeCompanyHistory(@RequestParam("id") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, modelMap: ModelMap): String {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
        modelMap.addAttribute("studentId", studentId)
        return "web/internship/statistics/internship_change_company_history::#page-wrapper"
    }

    /**
     * 未提交列表
     *
     * @return 未提交列表 统计页面
     */
    @RequestMapping(value = ["/web/internship/statistical/unsubmitted"], method = [(RequestMethod.GET)])
    fun statisticalUnSubmitted(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
        return "web/internship/statistics/internship_unsubmitted::#page-wrapper"
    }

    /**
     * 数据列表
     *
     * @return 数据列表 统计页面
     */
    @RequestMapping(value = ["/web/internship/statistical/data_list"], method = [(RequestMethod.GET)])
    fun statisticalDataList(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
        return if (!ObjectUtils.isEmpty(internshipRelease)) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            val internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.internshipTypeId!!)
            when (internshipType.internshipTypeName) {
                Workbook.INTERNSHIP_COLLEGE_TYPE -> "web/internship/statistics/internship_college_data::#page-wrapper"
                Workbook.INTERNSHIP_COMPANY_TYPE -> "web/internship/statistics/internship_company_data::#page-wrapper"
                Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> "web/internship/statistics/graduation_practice_college_data::#page-wrapper"
                Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> "web/internship/statistics/graduation_practice_unify_data::#page-wrapper"
                Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> "web/internship/statistics/graduation_practice_company_data::#page-wrapper"
                else -> methodControllerCommon.showTip(modelMap, "未找到相关实习类型页面")
            }
        } else {
            methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
    }

    /**
     * 获取实习统计数据
     *
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipListDatas(paginationUtils: PaginationUtils): AjaxUtils<InternshipReleaseBean> {
        val ajaxUtils = AjaxUtils.of<InternshipReleaseBean>()
        val internshipReleaseBean = InternshipReleaseBean()
        internshipReleaseBean.internshipReleaseIsDel = 0
        val commonData = methodControllerCommon.adminOrNormalData()
        internshipReleaseBean.departmentId = if (org.springframework.util.StringUtils.isEmpty(commonData["departmentId"])) -1 else commonData["departmentId"]
        internshipReleaseBean.collegeId = if (org.springframework.util.StringUtils.isEmpty(commonData["collegeId"])) -1 else commonData["collegeId"]
        val records = internshipReleaseService.findAllByPage(paginationUtils, internshipReleaseBean)
        val internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipReleaseBean)
        internshipReleaseBeens.forEach { r ->
            val internshipStatisticsBean = InternshipStatisticsBean()
            internshipStatisticsBean.internshipReleaseId = r.internshipReleaseId
            r.submittedTotalData = internshipStatisticsService.submittedCountAll(internshipStatisticsBean)
            r.unsubmittedTotalData = internshipStatisticsService.unsubmittedCountAll(internshipStatisticsBean)
        }
        return ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils)
    }

    /**
     * 已提交列表 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/submitted/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun submittedDatas(request: HttpServletRequest): DataTablesUtils<InternshipStatisticsBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("student_name")
        headers.add("student_number")
        headers.add("science_name")
        headers.add("organize_name")
        headers.add("internship_apply_state")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<InternshipStatisticsBean>(request, headers)
        val internshipStatisticsBean = InternshipStatisticsBean()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            internshipStatisticsBean.internshipReleaseId = request.getParameter("internshipReleaseId")
            val records = internshipStatisticsService.submittedFindAllByPage(dataTablesUtils, internshipStatisticsBean)
            var internshipStatisticsBeens: List<InternshipStatisticsBean> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                internshipStatisticsBeens = records.into(InternshipStatisticsBean::class.java)
            }
            dataTablesUtils.data = internshipStatisticsBeens
            dataTablesUtils.setiTotalRecords(internshipStatisticsService.submittedCountAll(internshipStatisticsBean).toLong())
            dataTablesUtils.setiTotalDisplayRecords(internshipStatisticsService.submittedCountByCondition(dataTablesUtils, internshipStatisticsBean).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 申请变更记录数据
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/record/apply/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun changeHistoryDatas(@RequestParam("internshipReleaseId") internshipReleaseId: String, @RequestParam("studentId") studentId: Int): AjaxUtils<InternshipChangeHistoryBean> {
        val ajaxUtils = AjaxUtils.of<InternshipChangeHistoryBean>()
        var internshipChangeHistoryBeans: List<InternshipChangeHistoryBean> = ArrayList()
        val records = internshipChangeHistoryService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
        if (records.isNotEmpty) {
            internshipChangeHistoryBeans = records.into(InternshipChangeHistoryBean::class.java)
            internshipChangeHistoryBeans.forEach { i ->
                i.applyTimeStr = DateTimeUtils.formatDate(i.applyTime)
                if (!ObjectUtils.isEmpty(i.changeFillStartTime)) {
                    i.changeFillStartTimeStr = DateTimeUtils.formatDate(i.changeFillStartTime)
                }
                if (!ObjectUtils.isEmpty(i.changeFillEndTime)) {
                    i.changeFillEndTimeStr = DateTimeUtils.formatDate(i.changeFillEndTime)
                }
            }
        }
        return ajaxUtils.success().msg("获取数据成功").listData(internshipChangeHistoryBeans)
    }

    /**
     * 单位变更记录数据
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/record/company/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun changeCompanyDatas(@RequestParam("internshipReleaseId") internshipReleaseId: String, @RequestParam("studentId") studentId: Int): AjaxUtils<InternshipChangeCompanyHistoryBean> {
        val ajaxUtils = AjaxUtils.of<InternshipChangeCompanyHistoryBean>()
        var internshipChangeCompanyHistoryBeans: List<InternshipChangeCompanyHistoryBean> = ArrayList()
        val records = internshipChangeCompanyHistoryService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
        if (records.isNotEmpty) {
            internshipChangeCompanyHistoryBeans = records.into(InternshipChangeCompanyHistoryBean::class.java)
            internshipChangeCompanyHistoryBeans.forEach { i -> i.changeTimeStr = DateTimeUtils.formatDate(i.changeTime) }
        }
        return ajaxUtils.success().msg("获取数据成功").listData(internshipChangeCompanyHistoryBeans)
    }

    /**
     * 未提交列表 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/unsubmitted/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun unsubmittedDatas(request: HttpServletRequest): DataTablesUtils<InternshipStatisticsBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("student_name")
        headers.add("student_number")
        headers.add("science_name")
        headers.add("organize_name")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<InternshipStatisticsBean>(request, headers)
        val internshipStatisticsBean = InternshipStatisticsBean()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            internshipStatisticsBean.internshipReleaseId = request.getParameter("internshipReleaseId")
            val records = internshipStatisticsService.unsubmittedFindAllByPage(dataTablesUtils, internshipStatisticsBean)
            var internshipStatisticsBeens: List<InternshipStatisticsBean> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                internshipStatisticsBeens = records.into(InternshipStatisticsBean::class.java)
            }
            dataTablesUtils.data = internshipStatisticsBeens
            dataTablesUtils.setiTotalRecords(internshipStatisticsService.unsubmittedCountAll(internshipStatisticsBean).toLong())
            dataTablesUtils.setiTotalDisplayRecords(internshipStatisticsService.unsubmittedCountByCondition(dataTablesUtils, internshipStatisticsBean).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 数据列表 顶岗实习(留学院) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/college/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun collegeData(request: HttpServletRequest): DataTablesUtils<InternshipCollege> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("student_name")
        headers.add("college_class")
        headers.add("student_sex")
        headers.add("student_number")
        headers.add("phone_number")
        headers.add("qq_mailbox")
        headers.add("parental_contact")
        headers.add("headmaster")
        headers.add("headmaster_contact")
        headers.add("internship_college_name")
        headers.add("internship_college_address")
        headers.add("internship_college_contacts")
        headers.add("internship_college_tel")
        headers.add("school_guidance_teacher")
        headers.add("school_guidance_teacher_tel")
        headers.add("start_time")
        headers.add("end_time")
        headers.add("commitment_book")
        headers.add("safety_responsibility_book")
        headers.add("practice_agreement")
        headers.add("internship_application")
        headers.add("practice_receiving")
        headers.add("security_education_agreement")
        headers.add("parental_consent")
        val dataTablesUtils = DataTablesUtils<InternshipCollege>(request, headers)
        val internshipCollege = InternshipCollege()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            internshipCollege.internshipReleaseId = request.getParameter("internshipReleaseId")
            val records = internshipCollegeService.findAllByPage(dataTablesUtils, internshipCollege)
            var internshipColleges: List<InternshipCollege> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                internshipColleges = records.into(InternshipCollege::class.java)
                internshipColleges.forEach { data ->
                    val student = cacheManageService.getStudentByStudentId(data.studentId)
                    val usersKey = cacheManageService.getUsersKey(student.username!!)
                    data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                    data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                }
            }
            dataTablesUtils.data = internshipColleges
            dataTablesUtils.setiTotalRecords(internshipCollegeService.countAll(internshipCollege).toLong())
            dataTablesUtils.setiTotalDisplayRecords(internshipCollegeService.countByCondition(dataTablesUtils, internshipCollege).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 导出 顶岗实习(留学院) 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/internship/statistical/college/data/export"], method = [(RequestMethod.GET)])
    fun collegeDataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            var fileName: String? = "顶岗实习(留学院)"
            var ext: String? = Workbook.XLSX_FILE
            val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

            val extraSearchParam = request.getParameter("extra_search")
            val dataTablesUtils = DataTablesUtils.of<InternshipCollege>()
            if (StringUtils.isNotBlank(extraSearchParam)) {
                dataTablesUtils.search = JSON.parseObject(extraSearchParam)
            }
            val internshipCollege = InternshipCollege()
            val internshipReleaseId = request.getParameter("internshipReleaseId")
            if (!ObjectUtils.isEmpty(internshipReleaseId)) {
                internshipCollege.internshipReleaseId = request.getParameter("internshipReleaseId")
                val records = internshipCollegeService.exportData(dataTablesUtils, internshipCollege)
                var internshipColleges: List<InternshipCollege> = ArrayList()
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    internshipColleges = records.into(InternshipCollege::class.java)
                    internshipColleges.forEach { data ->
                        val student = cacheManageService.getStudentByStudentId(data.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                        data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                    }
                }
                if (StringUtils.isNotBlank(exportBean.fileName)) {
                    fileName = exportBean.fileName
                }
                if (StringUtils.isNotBlank(exportBean.ext)) {
                    ext = exportBean.ext
                }
                val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    val export = InternshipCollegeExport(internshipColleges)
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
     * 数据列表 校外自主实习(去单位) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/company/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun companyData(request: HttpServletRequest): DataTablesUtils<InternshipCompany> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("student_name")
        headers.add("college_class")
        headers.add("student_sex")
        headers.add("student_number")
        headers.add("phone_number")
        headers.add("qq_mailbox")
        headers.add("parental_contact")
        headers.add("headmaster")
        headers.add("headmaster_contact")
        headers.add("internship_company_name")
        headers.add("internship_company_address")
        headers.add("internship_company_contacts")
        headers.add("internship_company_tel")
        headers.add("school_guidance_teacher")
        headers.add("school_guidance_teacher_tel")
        headers.add("start_time")
        headers.add("end_time")
        headers.add("commitment_book")
        headers.add("safety_responsibility_book")
        headers.add("practice_agreement")
        headers.add("internship_application")
        headers.add("practice_receiving")
        headers.add("security_education_agreement")
        headers.add("parental_consent")
        val dataTablesUtils = DataTablesUtils<InternshipCompany>(request, headers)
        val internshipCompany = InternshipCompany()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            internshipCompany.internshipReleaseId = request.getParameter("internshipReleaseId")
            val records = internshipCompanyService.findAllByPage(dataTablesUtils, internshipCompany)
            var internshipCompanies: List<InternshipCompany> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                internshipCompanies = records.into(InternshipCompany::class.java)
                internshipCompanies.forEach { data ->
                    val student = cacheManageService.getStudentByStudentId(data.studentId)
                    val usersKey = cacheManageService.getUsersKey(student.username!!)
                    data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                    data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                }
            }
            dataTablesUtils.data = internshipCompanies
            dataTablesUtils.setiTotalRecords(internshipCompanyService.countAll(internshipCompany).toLong())
            dataTablesUtils.setiTotalDisplayRecords(internshipCompanyService.countByCondition(dataTablesUtils, internshipCompany).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 导出 校外自主实习(去单位) 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/internship/statistical/company/data/export"], method = [(RequestMethod.GET)])
    fun companyDataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            var fileName: String? = "校外自主实习(去单位)"
            var ext: String? = Workbook.XLSX_FILE
            val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

            val extraSearchParam = request.getParameter("extra_search")
            val dataTablesUtils = DataTablesUtils.of<InternshipCompany>()
            if (StringUtils.isNotBlank(extraSearchParam)) {
                dataTablesUtils.search = JSON.parseObject(extraSearchParam)
            }
            val internshipCompany = InternshipCompany()
            val internshipReleaseId = request.getParameter("internshipReleaseId")
            if (!ObjectUtils.isEmpty(internshipReleaseId)) {
                internshipCompany.internshipReleaseId = request.getParameter("internshipReleaseId")
                val records = internshipCompanyService.exportData(dataTablesUtils, internshipCompany)
                var internshipCompanies: List<InternshipCompany> = ArrayList()
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    internshipCompanies = records.into(InternshipCompany::class.java)
                    internshipCompanies.forEach { data ->
                        val student = cacheManageService.getStudentByStudentId(data.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                        data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                    }
                }
                if (StringUtils.isNotBlank(exportBean.fileName)) {
                    fileName = exportBean.fileName
                }
                if (StringUtils.isNotBlank(exportBean.ext)) {
                    ext = exportBean.ext
                }
                val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    val export = InternshipCompanyExport(internshipCompanies)
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
     * 数据列表 毕业实习(校外) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/graduation_practice_company/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun graduationPracticeCompanyData(request: HttpServletRequest): DataTablesUtils<GraduationPracticeCompany> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("student_name")
        headers.add("college_class")
        headers.add("student_sex")
        headers.add("student_number")
        headers.add("phone_number")
        headers.add("qq_mailbox")
        headers.add("parental_contact")
        headers.add("headmaster")
        headers.add("headmaster_contact")
        headers.add("graduation_practice_company_name")
        headers.add("graduation_practice_company_address")
        headers.add("graduation_practice_company_contacts")
        headers.add("graduation_practice_company_tel")
        headers.add("school_guidance_teacher")
        headers.add("school_guidance_teacher_tel")
        headers.add("start_time")
        headers.add("end_time")
        headers.add("commitment_book")
        headers.add("safety_responsibility_book")
        headers.add("practice_agreement")
        headers.add("internship_application")
        headers.add("practice_receiving")
        headers.add("security_education_agreement")
        headers.add("parental_consent")
        val dataTablesUtils = DataTablesUtils<GraduationPracticeCompany>(request, headers)
        val graduationPracticeCompany = GraduationPracticeCompany()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            graduationPracticeCompany.internshipReleaseId = request.getParameter("internshipReleaseId")
            val records = graduationPracticeCompanyService.findAllByPage(dataTablesUtils, graduationPracticeCompany)
            var graduationPracticeCompanies: List<GraduationPracticeCompany> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                graduationPracticeCompanies = records.into(GraduationPracticeCompany::class.java)
                graduationPracticeCompanies.forEach { data ->
                    val student = cacheManageService.getStudentByStudentId(data.studentId)
                    val usersKey = cacheManageService.getUsersKey(student.username!!)
                    data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                    data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                }
            }
            dataTablesUtils.data = graduationPracticeCompanies
            dataTablesUtils.setiTotalRecords(graduationPracticeCompanyService.countAll(graduationPracticeCompany).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationPracticeCompanyService.countByCondition(dataTablesUtils, graduationPracticeCompany).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 导出 毕业实习(校外) 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/internship/statistical/graduation_practice_company/data/export"], method = [(RequestMethod.GET)])
    fun graduationPracticeCompanyDataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            var fileName: String? = "毕业实习(校外)"
            var ext: String? = Workbook.XLSX_FILE
            val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

            val extraSearchParam = request.getParameter("extra_search")
            val dataTablesUtils = DataTablesUtils.of<GraduationPracticeCompany>()
            if (StringUtils.isNotBlank(extraSearchParam)) {
                dataTablesUtils.search = JSON.parseObject(extraSearchParam)
            }
            val graduationPracticeCompany = GraduationPracticeCompany()
            val internshipReleaseId = request.getParameter("internshipReleaseId")
            if (!ObjectUtils.isEmpty(internshipReleaseId)) {
                graduationPracticeCompany.internshipReleaseId = request.getParameter("internshipReleaseId")
                val records = graduationPracticeCompanyService.exportData(dataTablesUtils, graduationPracticeCompany)
                var graduationPracticeCompanies: List<GraduationPracticeCompany> = ArrayList()
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    graduationPracticeCompanies = records.into(GraduationPracticeCompany::class.java)
                    graduationPracticeCompanies.forEach { data ->
                        val student = cacheManageService.getStudentByStudentId(data.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                        data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                    }
                }
                if (StringUtils.isNotBlank(exportBean.fileName)) {
                    fileName = exportBean.fileName
                }
                if (StringUtils.isNotBlank(exportBean.ext)) {
                    ext = exportBean.ext
                }
                val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    val export = GraduationPracticeCompanyExport(graduationPracticeCompanies)
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
     * 数据列表 毕业实习(校内) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/graduation_practice_college/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun graduationPracticeCollegeData(request: HttpServletRequest): DataTablesUtils<GraduationPracticeCollege> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("student_name")
        headers.add("college_class")
        headers.add("student_sex")
        headers.add("student_number")
        headers.add("phone_number")
        headers.add("qq_mailbox")
        headers.add("parental_contact")
        headers.add("headmaster")
        headers.add("headmaster_contact")
        headers.add("graduation_practice_college_name")
        headers.add("graduation_practice_college_address")
        headers.add("graduation_practice_college_contacts")
        headers.add("graduation_practice_college_tel")
        headers.add("school_guidance_teacher")
        headers.add("school_guidance_teacher_tel")
        headers.add("start_time")
        headers.add("end_time")
        headers.add("commitment_book")
        headers.add("safety_responsibility_book")
        headers.add("practice_agreement")
        headers.add("internship_application")
        headers.add("practice_receiving")
        headers.add("security_education_agreement")
        headers.add("parental_consent")
        val dataTablesUtils = DataTablesUtils<GraduationPracticeCollege>(request, headers)
        val graduationPracticeCollege = GraduationPracticeCollege()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            graduationPracticeCollege.internshipReleaseId = request.getParameter("internshipReleaseId")
            val records = graduationPracticeCollegeService.findAllByPage(dataTablesUtils, graduationPracticeCollege)
            var graduationPracticeColleges: List<GraduationPracticeCollege> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                graduationPracticeColleges = records.into(GraduationPracticeCollege::class.java)
                graduationPracticeColleges.forEach { data ->
                    val student = cacheManageService.getStudentByStudentId(data.studentId)
                    val usersKey = cacheManageService.getUsersKey(student.username!!)
                    data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                    data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                }
            }
            dataTablesUtils.data = graduationPracticeColleges
            dataTablesUtils.setiTotalRecords(graduationPracticeCollegeService.countAll(graduationPracticeCollege).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationPracticeCollegeService.countByCondition(dataTablesUtils, graduationPracticeCollege).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 导出 毕业实习(校内) 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/internship/statistical/graduation_practice_college/data/export"], method = [(RequestMethod.GET)])
    fun graduationPracticeCollegeDataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            var fileName: String? = "毕业实习(校内)"
            var ext: String? = Workbook.XLSX_FILE
            val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

            val extraSearchParam = request.getParameter("extra_search")
            val dataTablesUtils = DataTablesUtils.of<GraduationPracticeCollege>()
            if (StringUtils.isNotBlank(extraSearchParam)) {
                dataTablesUtils.search = JSON.parseObject(extraSearchParam)
            }
            val graduationPracticeCollege = GraduationPracticeCollege()
            val internshipReleaseId = request.getParameter("internshipReleaseId")
            if (!ObjectUtils.isEmpty(internshipReleaseId)) {
                graduationPracticeCollege.internshipReleaseId = request.getParameter("internshipReleaseId")
                val records = graduationPracticeCollegeService.exportData(dataTablesUtils, graduationPracticeCollege)
                var graduationPracticeColleges: List<GraduationPracticeCollege> = ArrayList()
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    graduationPracticeColleges = records.into(GraduationPracticeCollege::class.java)
                    graduationPracticeColleges.forEach { data ->
                        val student = cacheManageService.getStudentByStudentId(data.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                        data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                    }
                }
                if (StringUtils.isNotBlank(exportBean.fileName)) {
                    fileName = exportBean.fileName
                }
                if (StringUtils.isNotBlank(exportBean.ext)) {
                    ext = exportBean.ext
                }
                val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    val export = GraduationPracticeCollegeExport(graduationPracticeColleges)
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
     * 数据列表 毕业实习(学校统一组织校外实习) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/statistical/graduation_practice_unify/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun graduationPracticeUnifyData(request: HttpServletRequest): DataTablesUtils<GraduationPracticeUnify> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("student_name")
        headers.add("college_class")
        headers.add("student_sex")
        headers.add("student_number")
        headers.add("phone_number")
        headers.add("qq_mailbox")
        headers.add("parental_contact")
        headers.add("headmaster")
        headers.add("headmaster_contact")
        headers.add("graduation_practice_unify_name")
        headers.add("graduation_practice_unify_address")
        headers.add("graduation_practice_unify_contacts")
        headers.add("graduation_practice_unify_tel")
        headers.add("school_guidance_teacher")
        headers.add("school_guidance_teacher_tel")
        headers.add("start_time")
        headers.add("end_time")
        headers.add("commitment_book")
        headers.add("safety_responsibility_book")
        headers.add("practice_agreement")
        headers.add("internship_application")
        headers.add("practice_receiving")
        headers.add("security_education_agreement")
        headers.add("parental_consent")
        val dataTablesUtils = DataTablesUtils<GraduationPracticeUnify>(request, headers)
        val graduationPracticeUnify = GraduationPracticeUnify()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            graduationPracticeUnify.internshipReleaseId = request.getParameter("internshipReleaseId")
            val records = graduationPracticeUnifyService.findAllByPage(dataTablesUtils, graduationPracticeUnify)
            var graduationPracticeUnifies: List<GraduationPracticeUnify> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                graduationPracticeUnifies = records.into(GraduationPracticeUnify::class.java)
                graduationPracticeUnifies.forEach { data ->
                    val student = cacheManageService.getStudentByStudentId(data.studentId)
                    val usersKey = cacheManageService.getUsersKey(student.username!!)
                    data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                    data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                }
            }
            dataTablesUtils.data = graduationPracticeUnifies
            dataTablesUtils.setiTotalRecords(graduationPracticeUnifyService.countAll(graduationPracticeUnify).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationPracticeUnifyService.countByCondition(dataTablesUtils, graduationPracticeUnify).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 导出 毕业实习(学校统一组织校外实习) 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/internship/statistical/graduation_practice_unify/data/export"], method = [(RequestMethod.GET)])
    fun graduationPracticeUnifyDataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            var fileName: String? = "毕业实习(学校统一组织校外实习)"
            var ext: String? = Workbook.XLSX_FILE
            val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

            val extraSearchParam = request.getParameter("extra_search")
            val dataTablesUtils = DataTablesUtils.of<GraduationPracticeUnify>()
            if (StringUtils.isNotBlank(extraSearchParam)) {
                dataTablesUtils.search = JSON.parseObject(extraSearchParam)
            }
            val graduationPracticeUnify = GraduationPracticeUnify()
            val internshipReleaseId = request.getParameter("internshipReleaseId")
            if (!ObjectUtils.isEmpty(internshipReleaseId)) {
                graduationPracticeUnify.internshipReleaseId = request.getParameter("internshipReleaseId")
                val records = graduationPracticeUnifyService.exportData(dataTablesUtils, graduationPracticeUnify)
                var graduationPracticeUnifies: List<GraduationPracticeUnify> = ArrayList()
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    graduationPracticeUnifies = records.into(GraduationPracticeUnify::class.java)
                    graduationPracticeUnifies.forEach { data ->
                        val student = cacheManageService.getStudentByStudentId(data.studentId)
                        val usersKey = cacheManageService.getUsersKey(student.username!!)
                        data.studentSex = methodControllerCommon.decryptPersonalData(data.studentSex, usersKey)
                        data.parentalContact = methodControllerCommon.decryptPersonalData(data.parentalContact, usersKey)
                    }
                }
                if (StringUtils.isNotBlank(exportBean.fileName)) {
                    fileName = exportBean.fileName
                }
                if (StringUtils.isNotBlank(exportBean.ext)) {
                    ext = exportBean.ext
                }
                val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    val export = GraduationPracticeUnifyExport(graduationPracticeUnifies)
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
}