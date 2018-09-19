package top.zbeboy.isy.web.internship.distribution

import com.alibaba.fastjson.JSON
import org.jooq.Record
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
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease
import top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution
import top.zbeboy.isy.domain.tables.pojos.Organize
import top.zbeboy.isy.domain.tables.pojos.Student
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.OrganizeService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.export.InternshipTeacherDistributionExport
import top.zbeboy.isy.service.internship.InternshipReleaseScienceService
import top.zbeboy.isy.service.internship.InternshipReleaseService
import top.zbeboy.isy.service.internship.InternshipTeacherDistributionService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.bean.error.ErrorBean
import top.zbeboy.isy.web.bean.export.ExportBean
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.internship.common.InternshipConditionCommon
import top.zbeboy.isy.web.internship.common.InternshipMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import java.io.IOException
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-12-21 .
 **/
@Controller
open class InternshipTeacherDistributionController {

    private val log = LoggerFactory.getLogger(InternshipTeacherDistributionController::class.java)

    @Resource
    open lateinit var internshipTeacherDistributionService: InternshipTeacherDistributionService

    @Resource
    open lateinit var internshipReleaseScienceService: InternshipReleaseScienceService

    @Resource
    open lateinit var organizeService: OrganizeService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var internshipMethodControllerCommon: InternshipMethodControllerCommon

    @Resource
    open lateinit var internshipConditionCommon: InternshipConditionCommon


    /**
     * 实习教师分配
     *
     * @return 实习教师分配页面
     */
    @RequestMapping(value = ["/web/menu/internship/teacher_distribution"], method = [(RequestMethod.GET)])
    fun teacherDistribution(): String {
        return "web/internship/distribution/internship_teacher_distribution::#page-wrapper"
    }

    /**
     * 获取实习列表数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/internship/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipListDatas(paginationUtils: PaginationUtils): AjaxUtils<InternshipReleaseBean> {
        return internshipMethodControllerCommon.internshipListDatas(paginationUtils)
    }

    /**
     * 进入指导教师分配页面判断条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 教师分配页面
     *
     * @param internshipReleaseId 实习发布id
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/condition"], method = [(RequestMethod.GET)])
    fun distributionCondition(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            page = "web/internship/distribution/internship_distribution_condition::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 查看分配页面
     *
     * @param internshipReleaseId 实习发布id
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/look"], method = [(RequestMethod.GET)])
    fun distributionLook(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            page = "web/internship/distribution/internship_distribution_look::#page-wrapper"
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/condition/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun distributionConditionDatas(request: HttpServletRequest): DataTablesUtils<InternshipTeacherDistributionBean> {
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        var dataTablesUtils = DataTablesUtils.of<InternshipTeacherDistributionBean>()
        if (StringUtils.hasLength(internshipReleaseId)) {
            val errorBean = accessCondition(internshipReleaseId)
            if (!errorBean.isHasError()) {
                dataTablesUtils = buildDataTablesData(request, internshipReleaseId)
            }
        }
        return dataTablesUtils
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/look/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun lookConditionDatas(request: HttpServletRequest): DataTablesUtils<InternshipTeacherDistributionBean> {
        val internshipReleaseId = request.getParameter("internshipReleaseId")
        var dataTablesUtils = DataTablesUtils.of<InternshipTeacherDistributionBean>()
        if (StringUtils.hasLength(internshipReleaseId)) {
            val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
            if (!errorBean.isHasError()) {
                dataTablesUtils = buildDataTablesData(request, internshipReleaseId)
            }
        }
        return dataTablesUtils
    }

    /**
     * 获取数据
     *
     * @param request             请求
     * @param internshipReleaseId 实习发布id
     */
    private fun buildDataTablesData(request: HttpServletRequest, internshipReleaseId: String): DataTablesUtils<InternshipTeacherDistributionBean> {
        val dataTablesUtils: DataTablesUtils<InternshipTeacherDistributionBean>
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("student_real_name")
        headers.add("student_username")
        headers.add("student_number")
        headers.add("staff_real_name")
        headers.add("staff_username")
        headers.add("staff_number")
        headers.add("assigner")
        headers.add("username")
        headers.add("operator")
        dataTablesUtils = DataTablesUtils(request, headers)
        val internshipTeacherDistributionBeens = internshipTeacherDistributionService.findAllByPage(dataTablesUtils, internshipReleaseId)
        dataTablesUtils.data = internshipTeacherDistributionBeens
        dataTablesUtils.setiTotalRecords(internshipTeacherDistributionService.countAll(internshipReleaseId).toLong())
        dataTablesUtils.setiTotalDisplayRecords(internshipTeacherDistributionService.countByCondition(dataTablesUtils, internshipReleaseId).toLong())
        return dataTablesUtils
    }

    /**
     * 导出 分配列表 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/list/data/export"], method = [(RequestMethod.GET)])
    fun dataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            var fileName: String? = "实习指导教师分配数据表"
            var ext: String? = Workbook.XLSX_FILE
            val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

            val extraSearchParam = request.getParameter("extra_search")
            val dataTablesUtils = DataTablesUtils.of<InternshipTeacherDistributionBean>()
            if (org.apache.commons.lang3.StringUtils.isNotBlank(extraSearchParam)) {
                dataTablesUtils.search = JSON.parseObject(extraSearchParam)
            }
            val internshipReleaseId = request.getParameter("internshipReleaseId")
            if (!ObjectUtils.isEmpty(internshipReleaseId)) {
                val internshipTeacherDistributionBeans = internshipTeacherDistributionService.exportData(dataTablesUtils, internshipReleaseId)
                if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.fileName)) {
                    fileName = exportBean.fileName
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.ext)) {
                    ext = exportBean.ext
                }
                val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    val export = InternshipTeacherDistributionExport(internshipTeacherDistributionBeans)
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
     * 添加
     *
     * @param internshipReleaseId 实习id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/condition/add"], method = [(RequestMethod.GET)])
    fun addDistribution(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            page = "web/internship/distribution/internship_add_distribution::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
        return page
    }

    /**
     * 编辑
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/condition/edit"], method = [(RequestMethod.GET)])
    fun editDistribution(@RequestParam("id") internshipReleaseId: String, @RequestParam("studentId") studentId: Int, modelMap: ModelMap): String {
        val page: String
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            val internshipTeacherDistribution: InternshipTeacherDistribution
            var studentBean: StudentBean? = null
            val record = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            if (record.isPresent) {
                internshipTeacherDistribution = record.get().into(InternshipTeacherDistribution::class.java)
                modelMap.addAttribute("internshipReleaseId", internshipTeacherDistribution.internshipReleaseId)
                modelMap.addAttribute("staffId", internshipTeacherDistribution.staffId)
                val studentRecord = studentService.findByIdRelation(studentId)
                if (studentRecord.isPresent) {
                    studentBean = studentRecord.get().into(StudentBean::class.java)
                }
            }
            modelMap.addAttribute("student", studentBean)
            page = "web/internship/distribution/internship_edit_distribution::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
        return page
    }

    /**
     * 批量分配
     *
     * @param internshipReleaseId 实习id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/batch/distribution"], method = [(RequestMethod.GET)])
    fun batchDistribution(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId)
            page = "web/internship/distribution/internship_batch_distribution::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
        return page
    }

    /**
     * 保存时检验学生账号信息
     *
     * @param internshipReleaseId 实习发布Id
     * @param info                信息
     * @param type                检验类型
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/save/valid/student"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validStudent(@RequestParam("id") internshipReleaseId: String, @RequestParam("student") info: String, type: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            val internshipRelease = errorBean.data
            val departmentId = internshipRelease!!.departmentId!!
            var record = Optional.empty<Record>()
            if (type == 0) {
                record = studentService.findByUsernameAndDepartmentId(info, departmentId)
            } else if (type == 1) {
                record = studentService.findByStudentNumberAndDepartmentId(info, departmentId)
            }
            if (record.isPresent) {
                val student = record.get().into(Student::class.java)
                val distribution = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, student.studentId!!)
                if (distribution.isPresent) {
                    ajaxUtils.fail().msg("该学生账号已分配指导教师")
                } else {
                    ajaxUtils.success().msg("可分配")
                }
            } else {
                ajaxUtils.fail().msg("参数有误")
            }
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据检验，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 批量分配 所需班级数据
     *
     * @param internshipReleaseId 实习id
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/batch/distribution/organizes"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun batchDistributionOrganizes(@RequestParam("id") internshipReleaseId: String): AjaxUtils<Organize> {
        val ajaxUtils = AjaxUtils.of<Organize>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            var organizes: List<Organize> = ArrayList()
            val hasOrganizes = ArrayList<Int>()
            val internshipRelease = errorBean.data
            val records = internshipReleaseScienceService.findByInternshipReleaseId(internshipReleaseId)
            if (records.isNotEmpty) {
                val scienceIds = ArrayList<Int>()
                records.forEach { id -> scienceIds.add(id.scienceId) }
                val organizeRecords = organizeService.findInScienceIdsAndGradeAndIsDel(scienceIds, internshipRelease!!.allowGrade, 0)
                if (organizeRecords.isNotEmpty) {
                    organizes = organizeRecords.into(Organize::class.java)
                }
                val record1s = internshipTeacherDistributionService.findByInternshipReleaseIdDistinctOrganizeId(internshipReleaseId)
                if (record1s.isNotEmpty) {
                    record1s.forEach { r -> hasOrganizes.add(r.value1()) }
                }
            }
            val result = HashMap<String, Any>()
            result.put("listResult", organizes)
            result.put("hasOrganizes", hasOrganizes)
            ajaxUtils.success().msg("获取班级数据成功").mapData(result)
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据保存，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 批量分配 所需教师数据
     *
     * @param internshipReleaseId 实习id
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/batch/distribution/teachers"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun batchDistributionTeachers(@RequestParam("id") internshipReleaseId: String): AjaxUtils<StaffBean> {
        val ajaxUtils = AjaxUtils.of<StaffBean>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            val internshipRelease = errorBean.data
            var staffs: List<StaffBean> = ArrayList()
            val staffRecords = staffService.findByDepartmentIdAndEnabledAndVerifyMailboxExistsAuthoritiesRelation(internshipRelease!!.departmentId!!, 1, 1)
            if (staffRecords.isNotEmpty) {
                staffs = staffRecords.into(StaffBean::class.java)
            }
            ajaxUtils.success().msg("获取教师数据成功").listData(staffs)
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据获取，请返回首页")
        }
        return ajaxUtils
    }


    /**
     * 批量分配 所需排除的实习数据
     *
     * @param internshipReleaseId 实习id
     * @return 页面
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/batch/distribution/releases"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun batchDistributionInternshipReleases(@RequestParam("id") internshipReleaseId: String): AjaxUtils<InternshipRelease> {
        val ajaxUtils = AjaxUtils.of<InternshipRelease>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            var internshipReleases: List<InternshipRelease> = ArrayList()
            val internshipRelease = errorBean.data
            // 获取当前实习的关联专业
            val records = internshipReleaseScienceService.findByInternshipReleaseId(internshipReleaseId)
            if (records.isNotEmpty) {
                val scienceIds = ArrayList<Int>()
                records.forEach { id -> scienceIds.add(id.scienceId) }
                // 获取相近的实习
                val excludeInternshipRecord = internshipReleaseScienceService.findInScienceIdAndGradeNeInternshipReleaseId(internshipRelease!!.allowGrade, scienceIds, internshipReleaseId)
                if (excludeInternshipRecord.isNotEmpty) {
                    internshipReleases = excludeInternshipRecord.into(InternshipRelease::class.java)
                }
            }
            ajaxUtils.success().msg("获取数据成功").listData(internshipReleases)
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据获取，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 保存教师分配
     *
     * @param internshipReleaseId 实习id
     * @param organizeId          班级ids
     * @param staffId             教职工ids
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/batch/distribution/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun batchDistributionSave(@RequestParam("id") internshipReleaseId: String, organizeId: String, staffId: String, excludeInternshipReleaseId: String?): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(organizeId) && StringUtils.hasLength(staffId)
                    && SmallPropsUtils.StringIdsIsNumber(organizeId) && SmallPropsUtils.StringIdsIsNumber(staffId)) {
                val organizeIds = SmallPropsUtils.StringIdsToList(organizeId)
                val staffIds = SmallPropsUtils.StringIdsToList(staffId)
                var i = 0
                val users = usersService.getUserFromSession()
                var students: List<StudentBean> = ArrayList()
                // 筛选学生数据
                val enabled: Byte = 1
                val verifyMailbox: Byte = 1
                if (StringUtils.hasLength(excludeInternshipReleaseId)) {
                    val excludeInternshipReleaseIds = SmallPropsUtils.StringIdsToStringList(excludeInternshipReleaseId!!)
                    // 查询并排除掉其它实习的学生
                    val studentRecords = internshipTeacherDistributionService.findStudentForBatchDistributionEnabledAndVerifyMailbox(organizeIds, excludeInternshipReleaseIds, enabled, verifyMailbox)
                    if (studentRecords.isNotEmpty) {
                        students = studentRecords.into(StudentBean::class.java)
                    }
                } else {
                    val studentRecords = studentService.findInOrganizeIdsAndEnabledAndVerifyMailboxExistsAuthoritiesRelation(organizeIds, enabled, verifyMailbox)
                    if (studentRecords.isNotEmpty) {
                        students = studentRecords.into(StudentBean::class.java)
                    }
                }
                // 删除以前的分配记录
                internshipTeacherDistributionService.deleteByInternshipReleaseId(internshipReleaseId)
                for (s in students) {
                    if (i >= staffIds.size) {
                        i = 0
                    }
                    val tempStaffId = staffIds[i]
                    val internshipTeacherDistribution = InternshipTeacherDistribution(tempStaffId, s.studentId, internshipReleaseId, users!!.username, s.realName, users.realName)
                    internshipTeacherDistributionService.save(internshipTeacherDistribution)
                    i++
                }

                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("保存失败，参数异常")
            }
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据保存，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 单个添加保存
     *
     * @param info                学生信息
     * @param staffId             教职工id
     * @param internshipReleaseId 实习发布id
     * @param type                检验类型
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun save(@RequestParam("student") info: String, @RequestParam("staffId") staffId: Int,
             @RequestParam("id") internshipReleaseId: String, type: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            val internshipRelease = errorBean.data
            val departmentId = internshipRelease!!.departmentId!!
            var record = Optional.empty<Record>()
            val users = usersService.getUserFromSession()
            if (type == 0) {
                record = studentService.findByUsernameAndDepartmentId(info, departmentId)
            } else if (type == 1) {
                record = studentService.findByStudentNumberAndDepartmentId(info, departmentId)
            }
            if (record.isPresent) {
                val student = record.get().into(StudentBean::class.java)
                val internshipTeacherDistribution = InternshipTeacherDistribution(staffId, student.studentId, internshipReleaseId, users!!.username, student.realName, users.realName)
                internshipTeacherDistributionService.save(internshipTeacherDistribution)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("保存失败，参数有误")
            }
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据保存，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 更新
     *
     * @param studentId           学生id
     * @param staffId             教职工id
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun update(@RequestParam("studentId") studentId: Int, @RequestParam("staffId") staffId: Int,
               @RequestParam("id") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            val record = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            if (record.isPresent) {
                val internshipTeacherDistribution = record.get().into(InternshipTeacherDistribution::class.java)
                internshipTeacherDistribution.staffId = staffId
                internshipTeacherDistributionService.updateStaffId(internshipTeacherDistribution)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("保存失败，参数有误")
            }
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据保存，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 批量删除
     *
     * @param studentIds          学生ids
     * @param internshipReleaseId 实习id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/condition/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun del(studentIds: String, @RequestParam("id") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(studentIds) && SmallPropsUtils.StringIdsIsNumber(studentIds)) {
                val ids = SmallPropsUtils.StringIdsToList(studentIds)
                ids.forEach { id -> internshipTeacherDistributionService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, id) }
                ajaxUtils.success().msg("删除成功")
            }
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据操作，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 比对其它实习id的学生删除
     *
     * @param internshipReleaseId 实习发布id
     * @param excludeInternships  其它实习id
     * @return true or fals
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/condition/comparison_del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun comparisonDel(@RequestParam("id") internshipReleaseId: String, excludeInternships: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(excludeInternships)) {
                val ids = SmallPropsUtils.StringIdsToStringList(excludeInternships)
                internshipTeacherDistributionService.comparisonDel(internshipReleaseId, ids)
                ajaxUtils.success().msg("删除成功")
            }
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据操作，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 拷贝其它实习id的学生数据
     *
     * @param internshipReleaseId 实习发布id
     * @param copyInternships     其它实习id
     * @return true or fals
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/condition/copy"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun copyData(@RequestParam("id") internshipReleaseId: String, copyInternships: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(copyInternships)) {
                // 删除以前的分配记录 避免主键冲突
                internshipTeacherDistributionService.deleteByInternshipReleaseId(internshipReleaseId)
                val ids = SmallPropsUtils.StringIdsToStringList(copyInternships)
                val records = internshipTeacherDistributionService.findInInternshipReleaseIdsDistinctStudentId(ids)
                val users = usersService.getUserFromSession()
                if (records.isNotEmpty) {
                    val internshipTeacherDistributions = records.into(InternshipTeacherDistribution::class.java)
                    internshipTeacherDistributions.forEach { r ->
                        val internshipTeacherDistribution = InternshipTeacherDistribution(r.staffId, r.studentId, internshipReleaseId, users!!.username, r.studentRealName, users.realName)
                        internshipTeacherDistributionService.save(internshipTeacherDistribution)
                    }
                }
                ajaxUtils.success().msg("数据拷贝成功")
            }
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据操作，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 删除未申请学生的分配
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/teacher_distribution/distribution/delete_not_apply"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun deleteNotApply(@RequestParam("id") internshipReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            internshipTeacherDistributionService.deleteNotApply(internshipReleaseId)
            ajaxUtils.success().msg("删除成功")
        } else {
            ajaxUtils.success().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 教师分配时间范围条件
     *
     * @param internshipReleaseId 实习发布id
     * @return 错误消息
     */
    fun accessCondition(internshipReleaseId: String): ErrorBean<InternshipRelease> {
        val errorBean = internshipConditionCommon.basicCondition(internshipReleaseId)
        if (!errorBean.isHasError()) {
            val internshipRelease = errorBean.data
            if (DateTimeUtils.timestampRangeDecide(internshipRelease!!.teacherDistributionStartTime, internshipRelease.teacherDistributionEndTime)) {
                errorBean.hasError = false
            } else {
                errorBean.hasError = true
                errorBean.errorMsg = "不在时间范围，无法进入"
            }
        }
        return errorBean
    }
}