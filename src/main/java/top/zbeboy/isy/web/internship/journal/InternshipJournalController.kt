package top.zbeboy.isy.web.internship.journal

import org.apache.commons.lang3.BooleanUtils
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
import top.zbeboy.isy.domain.tables.pojos.*
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
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.internship.common.InternshipConditionCommon
import top.zbeboy.isy.web.internship.common.InternshipMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.internship.journal.InternshipJournalVo
import java.io.File
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
open class InternshipJournalController {

    private val log = LoggerFactory.getLogger(InternshipJournalController::class.java)

    @Resource
    open lateinit var internshipApplyService: InternshipApplyService

    @Resource
    open lateinit var internshipJournalService: InternshipJournalService

    @Resource
    open lateinit var internshipTeacherDistributionService: InternshipTeacherDistributionService

    @Resource
    open lateinit var internshipTypeService: InternshipTypeService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var staffService: StaffService

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
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon

    @Resource
    open lateinit var internshipMethodControllerCommon: InternshipMethodControllerCommon

    @Resource
    open lateinit var internshipConditionCommon: InternshipConditionCommon


    /**
     * 实习日志
     *
     * @return 实习日志页面
     */
    @RequestMapping(value = ["/web/menu/internship/journal"], method = [(RequestMethod.GET)])
    fun internshipJournal(): String {
        return "web/internship/journal/internship_journal::#page-wrapper"
    }

    /**
     * 获取实习列表数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/journal/internship/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipListDatas(paginationUtils: PaginationUtils): AjaxUtils<InternshipReleaseBean> {
        return internshipMethodControllerCommon.internshipListDatas(paginationUtils)
    }

    /**
     * 实习日志列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/journal/list"], method = [(RequestMethod.GET)])
    fun journalList(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(student)) {
                modelMap.addAttribute("studentId", student.studentId)
            } else {
                modelMap.addAttribute("studentId", null)
            }
        }
        pageParamControllerCommon.currentUserRoleNameAndTypeNamePageParam(modelMap)
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
        return "web/internship/journal/internship_journal_list::#page-wrapper"
    }

    /**
     * 我的日志列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/journal/my/list"], method = [(RequestMethod.GET)])
    fun myJournalList(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        var canUse = false
        val users = usersService.getUserFromSession()
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val student = studentService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(student)) {
                val errorBean = accessCondition(internshipReleaseId, student.studentId!!)
                canUse = !errorBean.isHasError()
                modelMap.addAttribute("studentId", student.studentId)
                modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            }
        }
        return if (canUse) {
            "web/internship/journal/internship_journal_my::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
    }

    /**
     * 小组日志页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/journal/team"], method = [(RequestMethod.GET)])
    fun teamJournal(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
        return "web/internship/journal/internship_journal_team::#page-wrapper"
    }

    /**
     * 小组日志列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/journal/team/list"], method = [(RequestMethod.GET)])
    fun teamJournalList(@RequestParam("id") internshipReleaseId: String, @RequestParam("staffId") staffId: Int, modelMap: ModelMap): String {
        val staffRecord = staffService.findByIdRelationForUsers(staffId)
        return if (staffRecord.isPresent) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val users = usersService.getUserFromSession()
                val student = studentService.findByUsername(users!!.username)
                if (!ObjectUtils.isEmpty(student)) {
                    modelMap.addAttribute("studentId", student.studentId)
                }
            }
            pageParamControllerCommon.currentUserRoleNameAndTypeNamePageParam(modelMap)
            val staffUser = staffRecord.get().into(Users::class.java)
            modelMap.addAttribute("staffId", staffId)
            modelMap.addAttribute("staffRealName", staffUser.realName)
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            "web/internship/journal/internship_journal_team_list::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "未查询到教师信息")
        }
    }

    /**
     * 日志列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/journal/list/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun listData(request: HttpServletRequest): DataTablesUtils<InternshipJournalBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("student_name")
        headers.add("student_number")
        headers.add("organize")
        headers.add("school_guidance_teacher")
        headers.add("create_date")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<InternshipJournalBean>(request, headers)
        val otherCondition = InternshipJournalBean()
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            otherCondition.internshipReleaseId = internshipReleaseId
            val studentId = request.getParameter("studentId")
            val staffId = request.getParameter("staffId")
            if (StringUtils.hasLength(studentId)) {
                if (NumberUtils.isDigits(studentId)) {
                    otherCondition.studentId = NumberUtils.toInt(studentId)
                }
            }
            if (StringUtils.hasLength(staffId)) {
                if (NumberUtils.isDigits(staffId)) {
                    otherCondition.staffId = NumberUtils.toInt(staffId)
                }
            }
            val records = internshipJournalService.findAllByPage(dataTablesUtils, otherCondition)
            var internshipJournalBeans: List<InternshipJournalBean> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                internshipJournalBeans = records.into(InternshipJournalBean::class.java)
                internshipJournalBeans.forEach { i ->
                    i.createDateStr = DateTimeUtils.formatDate(i.createDate)
                    i.internshipJournalContent = ""
                    i.internshipJournalHtml = ""
                    i.internshipJournalWord = ""
                    i.internshipJournalDate = null
                }
            }
            dataTablesUtils.data = internshipJournalBeans
            dataTablesUtils.setiTotalRecords(internshipJournalService.countAll(otherCondition).toLong())
            dataTablesUtils.setiTotalDisplayRecords(internshipJournalService.countByCondition(dataTablesUtils, otherCondition).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 小组教师数据
     *
     * @param internshipReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/journal/team/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun teamData(@RequestParam("id") internshipReleaseId: String): AjaxUtils<StaffBean> {
        val ajaxUtils = AjaxUtils.of<StaffBean>()
        val record3s = internshipTeacherDistributionService.findByInternshipReleaseIdDistinctStaffId(internshipReleaseId)
        var staffBeen: List<StaffBean> = ArrayList()
        if (record3s.isNotEmpty) {
            staffBeen = record3s.into(StaffBean::class.java)
        }
        return ajaxUtils.success().msg("获取数据成功").listData(staffBeen)
    }

    /**
     * 实习日志添加
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/journal/list/add"], method = [(RequestMethod.GET)])
    fun journalListAdd(@RequestParam("id") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, modelMap: ModelMap): String {
        val errorBean = accessCondition(internshipReleaseId, studentId)
        return if (!errorBean.isHasError()) {
            val internshipJournal = InternshipJournal()
            val studentRecord = studentService.findByIdRelation(studentId)
            if (studentRecord.isPresent) {
                val studentBean = studentRecord.get().into(StudentBean::class.java)
                internshipJournal.studentId = studentId
                internshipJournal.internshipReleaseId = internshipReleaseId
                internshipJournal.studentName = studentBean.realName
                internshipJournal.organize = studentBean.organizeName
                internshipJournal.studentNumber = studentBean.studentNumber
            }
            val internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentIdForStaff(internshipReleaseId, studentId)
            if (internshipTeacherDistributionRecord.isPresent) {
                val staffBean = internshipTeacherDistributionRecord.get().into(StaffBean::class.java)
                internshipJournal.schoolGuidanceTeacher = staffBean.realName
                internshipJournal.staffId = staffBean.staffId
            }
            val internshipRelease = errorBean.data
            val internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease!!.internshipTypeId!!)
            when (internshipType.internshipTypeName) {
                Workbook.INTERNSHIP_COLLEGE_TYPE -> {
                    val internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (internshipCollegeRecord.isPresent) {
                        val internshipCollege = internshipCollegeRecord.get().into(InternshipCollege::class.java)
                        internshipJournal.graduationPracticeCompanyName = internshipCollege.internshipCollegeName
                    }
                }
                Workbook.INTERNSHIP_COMPANY_TYPE -> {
                    val internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (internshipCompanyRecord.isPresent) {
                        val internshipCompany = internshipCompanyRecord.get().into(InternshipCompany::class.java)
                        internshipJournal.graduationPracticeCompanyName = internshipCompany.internshipCompanyName
                    }
                }
                Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> {
                    val graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeCollegeRecord.isPresent) {
                        val graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege::class.java)
                        internshipJournal.graduationPracticeCompanyName = graduationPracticeCollege.graduationPracticeCollegeName
                    }
                }
                Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> {
                    val graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeUnifyRecord.isPresent) {
                        val graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify::class.java)
                        internshipJournal.graduationPracticeCompanyName = graduationPracticeUnify.graduationPracticeUnifyName
                    }
                }
                Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> {
                    val graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                    if (graduationPracticeCompanyRecord.isPresent) {
                        val graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany::class.java)
                        internshipJournal.graduationPracticeCompanyName = graduationPracticeCompany.graduationPracticeCompanyName
                    }
                }
            }
            modelMap.addAttribute("internshipJournal", internshipJournal)
            "web/internship/journal/internship_journal_add::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
    }

    /**
     * 编辑实习日志页面
     *
     * @param id       实习日志id
     * @param modelMap 页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/journal/list/edit"], method = [(RequestMethod.GET)])
    fun journalListEdit(@RequestParam("id") id: String, modelMap: ModelMap): String {
        val internshipJournal = internshipJournalService.findById(id)
        val errorBean = accessCondition(internshipJournal.internshipReleaseId, internshipJournal.studentId!!)
        return if (!errorBean.isHasError()) {
            if (!ObjectUtils.isEmpty(internshipJournal)) {
                modelMap.addAttribute("internshipJournal", internshipJournal)
                "web/internship/journal/internship_journal_edit::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
            }
        } else {
            methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
    }

    /**
     * 查看实习日志页面
     *
     * @param id       实习日志id
     * @param modelMap 页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/journal/list/look"], method = [(RequestMethod.GET)])
    fun journalListLook(@RequestParam("id") id: String, modelMap: ModelMap): String {
        val internshipJournal = internshipJournalService.findById(id)
        return if (!ObjectUtils.isEmpty(internshipJournal)) {
            if (canSeeNndDownload(internshipJournal)) {
                modelMap.addAttribute("internshipJournalDate", DateTimeUtils.formatDate(internshipJournal.internshipJournalDate, "yyyy年MM月dd日"))
                modelMap.addAttribute("internshipJournal", internshipJournal)
                "web/internship/journal/internship_journal_look::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "该日志已限制查阅")
            }
        } else {
            methodControllerCommon.showTip(modelMap, "未查询到相关实习信息")
        }
    }

    /**
     * 下载实习日志
     *
     * @param id       实习日志id
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping(value = ["/web/internship/journal/list/download"], method = [(RequestMethod.GET)])
    fun journalListDownload(@RequestParam("id") id: String, request: HttpServletRequest, response: HttpServletResponse) {
        val internshipJournal = internshipJournalService.findById(id)
        if (!ObjectUtils.isEmpty(internshipJournal)) {
            if (canSeeNndDownload(internshipJournal)) {
                uploadService.download(internshipJournal.studentName + " " + internshipJournal.studentNumber, "/" + internshipJournal.internshipJournalWord, response, request)
            }
        }
    }

    /**
     * 判断是否允许查看和下载
     *
     * @param internshipJournal 实习日志
     * @return true or false
     */
    private fun canSeeNndDownload(internshipJournal: InternshipJournal): Boolean {
        val student = studentService.findById(internshipJournal.studentId!!)
        val users = usersService.getUserFromSession()
        return users!!.username == student.username ||
                roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) ||
                roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) ||
                internshipJournal.isSeeStaff != 1.toByte() || usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)
    }

    /**
     * 下载全部实习日志
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param request             请求
     * @param response            响应
     */
    @RequestMapping(value = ["/web/internship/journal/list/my/downloads"], method = [(RequestMethod.GET)])
    fun journalListMyDownloads(@RequestParam("id") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val errorBean = accessCondition(internshipReleaseId, studentId)
            if (!errorBean.isHasError()) {
                val records = internshipJournalService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    val fileName = ArrayList<String>()
                    val filePath = ArrayList<String>()
                    records.forEach { r ->
                        filePath.add(RequestUtils.getRealPath(request) + r.internshipJournalWord)
                        fileName.add(r.internshipJournalWord.substring(r.internshipJournalWord.lastIndexOf('/') + 1))
                    }
                    val studentRecord = studentService.findByIdRelationForUsers(studentId)
                    if (studentRecord.isPresent) {
                        val users = studentRecord.get().into(Users::class.java)
                        val downloadFileName = users.realName + "全部实习日志"
                        val zipName = downloadFileName + ".zip"
                        val downloadFilePath = Workbook.internshipJournalPath(users) + zipName
                        val zipPath = RequestUtils.getRealPath(request) + downloadFilePath
                        FilesUtils.compressZipMulti(fileName, zipPath, filePath)
                        uploadService.download(downloadFileName, "/" + downloadFilePath, response, request)
                    }
                }
            }
        } catch (e: Exception) {
            log.error("Compress zip error , error is {}", e)
        }
    }

    /**
     * 下载小组全部实习日志
     *
     * @param internshipReleaseId 实习发布id
     * @param request             请求
     * @param response            响应
     */
    @RequestMapping(value = ["/web/internship/journal/list/team/downloads"], method = [(RequestMethod.GET)])
    fun journalListTeamDownloads(@RequestParam("id") internshipReleaseId: String, @RequestParam("staffId") staffId: Int, request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val canDown = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) ||
                    roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) ||
                    usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)
            if (canDown) {
                val records = internshipJournalService.findByInternshipReleaseIdAndStaffId(internshipReleaseId, staffId)
                if (records.isNotEmpty) {
                    val fileName = ArrayList<String>()
                    val filePath = ArrayList<String>()
                    var staffName = ""
                    var isSetStaffName = false
                    for (r in records) {
                        filePath.add(RequestUtils.getRealPath(request) + r.internshipJournalWord)
                        fileName.add(r.internshipJournalWord.substring(r.internshipJournalWord.lastIndexOf('/') + 1))
                        if (BooleanUtils.isFalse(isSetStaffName)) {
                            staffName = r.schoolGuidanceTeacher
                            isSetStaffName = true
                        }
                    }

                    val downloadFileName = staffName + "小组实习日志"
                    val zipName = downloadFileName + ".zip"
                    val downloadFilePath = Workbook.TEMP_FILES_PORTFOLIOS + File.separator + zipName
                    val zipPath = RequestUtils.getRealPath(request) + downloadFilePath
                    FilesUtils.compressZipMulti(fileName, zipPath, filePath)
                    uploadService.download(downloadFileName, "/" + downloadFilePath, response, request)
                }
            }
        } catch (e: Exception) {
            log.error("Compress zip error , error is {}", e)
        }

    }

    /**
     * 批量删除日志
     *
     * @param journalIds ids
     * @param request    请求
     * @return true 删除成功
     */
    @RequestMapping(value = ["/web/internship/journal/list/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun journalListDel(journalIds: String, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            val canDel = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)
            var student: Student? = null
            if (!canDel && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val users = usersService.getUserFromSession()
                student = studentService.findByUsername(users!!.username)
            }
            if (StringUtils.hasLength(journalIds)) {
                val ids = SmallPropsUtils.StringIdsToStringList(journalIds)
                for (id in ids) {
                    val internshipJournal = internshipJournalService.findById(id)
                    if (!ObjectUtils.isEmpty(internshipJournal)) {
                        if (!canDel) {
                            // 学生本人操作
                            if (!ObjectUtils.isEmpty(student) && student!!.studentId == internshipJournal.studentId) {
                                FilesUtils.deleteFile(RequestUtils.getRealPath(request) + internshipJournal.internshipJournalWord)
                                internshipJournalService.deleteById(id)
                            }
                        } else { // 系统或管理员操作
                            FilesUtils.deleteFile(RequestUtils.getRealPath(request) + internshipJournal.internshipJournalWord)
                            internshipJournalService.deleteById(id)
                        }
                    }
                }
                ajaxUtils.success().msg("删除日志成功")
            } else {
                ajaxUtils.fail().msg("未查询到相关信息")
            }
        } catch (e: IOException) {
            log.error("Not found journal file , error is {}", e)
            ajaxUtils.fail().msg("删除日志失败")
        }

        return ajaxUtils
    }

    /**
     * 保存实习日志
     *
     * @param internshipJournalVo 实习日志
     * @param bindingResult       检验
     * @param request             请求
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/journal/my/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun journalSave(@Valid internshipJournalVo: InternshipJournalVo, bindingResult: BindingResult, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = accessCondition(internshipJournalVo.internshipReleaseId!!, internshipJournalVo.studentId!!)
            if (!errorBean.isHasError()) {
                val internshipJournal = InternshipJournal()
                internshipJournal.internshipJournalId = UUIDUtils.getUUID()
                internshipJournal.studentName = internshipJournalVo.studentName
                internshipJournal.studentNumber = internshipJournalVo.studentNumber
                internshipJournal.organize = internshipJournalVo.organize
                internshipJournal.schoolGuidanceTeacher = internshipJournalVo.schoolGuidanceTeacher
                internshipJournal.graduationPracticeCompanyName = internshipJournalVo.graduationPracticeCompanyName
                internshipJournal.internshipJournalContent = internshipJournalVo.internshipJournalContent
                internshipJournal.internshipJournalHtml = internshipJournalVo.internshipJournalHtml
                internshipJournal.internshipJournalDate = internshipJournalVo.internshipJournalDate
                internshipJournal.createDate = Timestamp(Clock.systemDefaultZone().millis())
                internshipJournal.studentId = internshipJournalVo.studentId
                internshipJournal.staffId = internshipJournalVo.staffId
                internshipJournal.internshipReleaseId = internshipJournalVo.internshipReleaseId
                internshipJournal.isSeeStaff = internshipJournalVo.isSeeStaff

                val studentRecord = studentService.findByIdRelationForUsers(internshipJournalVo.studentId!!)
                if (studentRecord.isPresent) {
                    val users = studentRecord.get().into(Users::class.java)
                    val outputPath = filesService.saveInternshipJournal(internshipJournal, users, request)
                    if (StringUtils.hasLength(outputPath)) {
                        internshipJournal.internshipJournalWord = outputPath
                        internshipJournalService.save(internshipJournal)
                        ajaxUtils.success().msg("保存成功")
                    } else {
                        ajaxUtils.fail().msg("保存文件失败")
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关学生信息")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }

        } else {
            ajaxUtils.fail().msg("保存失败，参数错误")
        }
        return ajaxUtils
    }

    /**
     * 实习日志编辑
     *
     * @param internshipJournalVo 实习日志
     * @param bindingResult       检验
     * @param request             请求
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/journal/my/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun journalUpdate(@Valid internshipJournalVo: InternshipJournalVo, bindingResult: BindingResult, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (!bindingResult.hasErrors() && StringUtils.hasLength(internshipJournalVo.internshipJournalId)) {
                val errorBean = accessCondition(internshipJournalVo.internshipReleaseId!!, internshipJournalVo.studentId!!)
                if (!errorBean.isHasError()) {
                    val internshipJournal = internshipJournalService.findById(internshipJournalVo.internshipJournalId!!)
                    internshipJournal.studentName = internshipJournalVo.studentName
                    internshipJournal.internshipJournalContent = internshipJournalVo.internshipJournalContent
                    internshipJournal.internshipJournalHtml = internshipJournalVo.internshipJournalHtml
                    internshipJournal.internshipJournalDate = internshipJournalVo.internshipJournalDate
                    internshipJournal.isSeeStaff = if (internshipJournalVo.isSeeStaff == null) 0 else internshipJournalVo.isSeeStaff
                    FilesUtils.deleteFile(RequestUtils.getRealPath(request) + internshipJournal.internshipJournalWord)
                    val studentRecord = studentService.findByIdRelationForUsers(internshipJournalVo.studentId!!)
                    if (studentRecord.isPresent) {
                        val users = studentRecord.get().into(Users::class.java)
                        val outputPath = filesService.saveInternshipJournal(internshipJournal, users, request)
                        if (StringUtils.hasLength(outputPath)) {
                            internshipJournal.internshipJournalWord = outputPath
                            internshipJournalService.update(internshipJournal)
                            ajaxUtils.success().msg("更新成功")
                        } else {
                            ajaxUtils.fail().msg("保存文件失败")
                        }
                    } else {
                        ajaxUtils.fail().msg("未查询到相关学生信息")
                    }

                } else {
                    ajaxUtils.fail().msg(errorBean.errorMsg!!)
                }
            } else {
                ajaxUtils.fail().msg("更新失败，参数错误")
            }
        } catch (e: IOException) {
            log.error("Delete dist journal error , error is {} ", e)
            ajaxUtils.fail().msg("删除文件失败")
        }

        return ajaxUtils
    }

    /**
     * 检验学生
     *
     * @param info                学生信息
     * @param internshipReleaseId 实习发布id
     * @param type                检验类型
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/journal/valid/student"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validStudent(@RequestParam("student") info: String, @RequestParam("internshipReleaseId") internshipReleaseId: String, type: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        var student: Student? = null
        if (type == 0) {
            student = studentService.findByUsername(info)
        } else if (type == 1) {
            student = studentService.findByStudentNumber(info)
        }
        if (!ObjectUtils.isEmpty(student)) {
            // 当前用户角色
            var canGo = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)
            // step 1.
            if (!canGo) {
                // 是否为该生指导教师
                val staffInfo = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentIdForStaff(internshipReleaseId, student!!.studentId!!)
                canGo = if (staffInfo.isPresent) {
                    val staff = staffInfo.get().into(Staff::class.java)
                    val users = usersService.getUserFromSession()
                    staff.username == users!!.username
                } else {
                    false
                }
            }
            // step 2.
            if (canGo) {
                val errorBean = accessCondition(internshipReleaseId, student!!.studentId!!)
                if (!errorBean.isHasError()) {
                    ajaxUtils.success().msg("查询学生数据成功").obj(student.studentId!!)
                } else {
                    ajaxUtils.fail().msg(errorBean.errorMsg!!)
                }
            } else {
                ajaxUtils.fail().msg("您权限不足或未参与该实习")
            }
        } else {
            ajaxUtils.fail().msg("查询学生数据失败")
        }
        return ajaxUtils
    }

    /**
     * 我的日志列表条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/journal/my/list/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myJournalListCondition(@RequestParam("id") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(student)) {
                val errorBean = accessCondition(internshipReleaseId, student.studentId!!)
                if (!errorBean.isHasError()) {
                    ajaxUtils.success().msg("在条件范围，允许使用")
                } else {
                    ajaxUtils.fail().msg(errorBean.errorMsg!!)
                }
            } else {
                ajaxUtils.fail().msg("未查询到您的账号信息")
            }
        } else {
            ajaxUtils.fail().msg("您的注册类型不符合进入条件")
        }
        return ajaxUtils
    }

    /**
     * 进入实习日志入口条件
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
            val internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            if (internshipApplyRecord.isPresent) {
                val internshipApply = internshipApplyRecord.get().into(InternshipApply::class.java)
                mapData.put("internshipApply", internshipApply)
                // 状态为 2：已通过；4：基本信息变更申请中；5：基本信息变更填写中；6：单位信息变更申请中；7：单位信息变更填写中 允许进行填写
                if (internshipApply.internshipApplyState == 2 || internshipApply.internshipApplyState == 4 ||
                        internshipApply.internshipApplyState == 5 || internshipApply.internshipApplyState == 6 || internshipApply.internshipApplyState == 7) {
                    errorBean.hasError = false
                    errorBean.errorMsg = "允许使用"
                } else {
                    errorBean.hasError = true
                    errorBean.errorMsg = "检测到您未通过申请，不允许使用"
                }
            } else {
                errorBean.hasError = true
                errorBean.errorMsg = "检测到您未申请该实习"
            }
            errorBean.mapData = mapData
        }
        return errorBean
    }
}