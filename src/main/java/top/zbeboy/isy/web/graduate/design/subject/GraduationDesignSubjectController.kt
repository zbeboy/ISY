package top.zbeboy.isy.web.graduate.design.subject

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
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.export.GraduationDesignDeclareExport
import top.zbeboy.isy.service.graduate.design.*
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.error.ErrorBean
import top.zbeboy.isy.web.bean.export.ExportBean
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.bean.graduate.design.subject.GraduationDesignPresubjectBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.graduate.design.subject.GraduationDesignDeclareUpdateVo
import top.zbeboy.isy.web.vo.graduate.design.subject.GraduationDesignPresubjectAddVo
import top.zbeboy.isy.web.vo.graduate.design.subject.GraduationDesignPresubjectUpdateVo
import java.io.IOException
import java.util.ArrayList
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * Created by zbeboy 2018-01-26 .
 **/
@Controller
open class GraduationDesignSubjectController {

    private val log = LoggerFactory.getLogger(GraduationDesignSubjectController::class.java)

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var graduationDesignPresubjectService: GraduationDesignPresubjectService

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var graduationDesignTutorService: GraduationDesignTutorService

    @Resource
    open lateinit var graduationDesignDeclareDataService: GraduationDesignDeclareDataService

    @Resource
    open lateinit var graduationDesignDeclareService: GraduationDesignDeclareService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon


    /**
     * 毕业设计题目
     *
     * @return 毕业设计题目页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/subject"], method = [(RequestMethod.GET)])
    fun subject(): String {
        return "web/graduate/design/subject/design_subject::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/subject/design/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun designDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils)
    }

    /**
     * 列表
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/subject/list"], method = [(RequestMethod.GET)])
    fun list(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            val users = usersService.getUserFromSession()
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val student = studentService.findByUsername(users!!.username)
                if (!ObjectUtils.isEmpty(student)) {
                    modelMap.addAttribute("studentId", student.studentId)
                }
            }
            pageParamControllerCommon.currentUserRoleNameAndTypeNamePageParam(modelMap)
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("endTime", DateTimeUtils.formatDate(graduationDesignRelease!!.endTime))
            page = "web/graduate/design/subject/design_subject_list::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/subject/list/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun listData(request: HttpServletRequest): DataTablesUtils<GraduationDesignPresubjectBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("presubject_title")
        headers.add("student_name")
        headers.add("student_number")
        headers.add("organize_name")
        headers.add("update_time")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<GraduationDesignPresubjectBean>(request, headers)
        val otherCondition = GraduationDesignPresubjectBean()
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
            val records = graduationDesignPresubjectService.findAllByPage(dataTablesUtils, otherCondition)
            var graduationDesignPresubjectBeens: List<GraduationDesignPresubjectBean> = ArrayList()
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                graduationDesignPresubjectBeens = records.into(GraduationDesignPresubjectBean::class.java)
                graduationDesignPresubjectBeens.forEach { i ->
                    i.updateTimeStr = DateTimeUtils.formatDate(i.updateTime)
                    i.presubjectPlan = ""
                }
            }
            dataTablesUtils.data = graduationDesignPresubjectBeens
            dataTablesUtils.setiTotalRecords(graduationDesignPresubjectService.countAll(otherCondition).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignPresubjectService.countByCondition(dataTablesUtils, otherCondition).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 申报数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun declareData(request: HttpServletRequest): DataTablesUtils<GraduationDesignDeclareBean> {
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        var dataTablesUtils = DataTablesUtils.of<GraduationDesignDeclareBean>()
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
            if (!errorBean.isHasError()) {
                // 前台数据标题 注：要和前台标题顺序一致，获取order用
                val headers = ArrayList<String>()
                headers.add("select")
                headers.add("presubject_title")
                headers.add("subject_type_name")
                headers.add("origin_type_name")
                headers.add("is_new_subject")
                headers.add("is_new_teacher_make")
                headers.add("is_new_subject_make")
                headers.add("is_old_subject_change")
                headers.add("old_subject_uses_times")
                headers.add("plan_period")
                headers.add("guide_teacher")
                headers.add("academic_title_name")
                headers.add("assistant_teacher")
                headers.add("assistant_teacher_academic")
                headers.add("guide_times")
                headers.add("guide_peoples")
                headers.add("student_number")
                headers.add("student_name")
                headers.add("organize_name")
                headers.add("is_ok_apply")
                headers.add("operator")
                dataTablesUtils = DataTablesUtils(request, headers)
                val otherCondition = GraduationDesignDeclareBean()
                val staffId = NumberUtils.toInt(request.getParameter("staffId"))
                otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
                otherCondition.staffId = staffId
                val graduationDesignDeclareBeens = graduationDesignDeclareService.findAllByPage(dataTablesUtils, otherCondition)
                dataTablesUtils.data = graduationDesignDeclareBeens
                dataTablesUtils.setiTotalRecords(graduationDesignDeclareService.countAll(otherCondition).toLong())
                dataTablesUtils.setiTotalDisplayRecords(graduationDesignDeclareService.countByCondition(dataTablesUtils, otherCondition).toLong())
            }
        }
        return dataTablesUtils
    }

    /**
     * 导出 申报 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/data/export"], method = [(RequestMethod.GET)])
    fun declareDataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
            val staffId = NumberUtils.toInt(request.getParameter("staffId"))
            if (!ObjectUtils.isEmpty(graduationDesignReleaseId) && staffId > 0) {
                val staffRecord = staffService.findByIdRelation(staffId)
                val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
                if (!errorBean.isHasError() && staffRecord.isPresent) {
                    val users = staffRecord.get().into(Users::class.java)
                    val graduationDesignRelease = errorBean.data
                    val graduationDesignDeclareData = graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
                    if (!ObjectUtils.isEmpty(graduationDesignDeclareData)) {
                        val year = graduationDesignDeclareData!!.graduationDate.substring(0, graduationDesignDeclareData.graduationDate.indexOf("年"))
                        var fileName: String? = users.realName + "_ " + year + "届毕业设计（论文）题目申报表"
                        var ext: String? = Workbook.XLSX_FILE
                        val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

                        val extraSearchParam = request.getParameter("extra_search")
                        val dataTablesUtils = DataTablesUtils.of<GraduationDesignDeclareBean>()
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(extraSearchParam)) {
                            dataTablesUtils.search = JSON.parseObject(extraSearchParam)
                        }
                        val otherCondition = GraduationDesignDeclareBean()
                        otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
                        otherCondition.staffId = staffId
                        val graduationDesignDeclareBeens = graduationDesignDeclareService.exportData(dataTablesUtils, otherCondition)
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.fileName)) {
                            fileName = exportBean.fileName
                        }
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.ext)) {
                            ext = exportBean.ext
                        }
                        if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
                            val peoples = graduationDesignTutorService.countByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staffId)
                            val export = GraduationDesignDeclareExport(graduationDesignDeclareBeens, graduationDesignDeclareData, peoples, year)
                            val schoolInfoPath = cacheManageService.schoolInfoPath(graduationDesignRelease!!.departmentId!!)
                            val path = Workbook.graduateDesignPath(schoolInfoPath) + fileName + "." + ext
                            export.exportExcel(RequestUtils.getRealPath(request) + Workbook.graduateDesignPath(schoolInfoPath), fileName!!, ext!!)
                            uploadService.download(fileName, path, response, request)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            log.error("Export file error, error is {}", e)
        }

    }

    /**
     * 查看
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/subject/list/look"], method = [(RequestMethod.GET)])
    fun look(@RequestParam("id") graduationDesignReleaseId: String,
             @RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            var canLook = false
            val graduationDesignPresubjectRecord = graduationDesignPresubjectService.findByIdRelation(graduationDesignPresubjectId)
            if (graduationDesignPresubjectRecord.isPresent) {
                val graduationDesignPresubject = graduationDesignPresubjectRecord.get().into(GraduationDesignPresubjectBean::class.java)
                val graduationDesignRelease = errorBean.data
                val users = usersService.getUserFromSession()
                if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                    canLook = true
                } else {
                    if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                        val student = studentService.findByUsername(users!!.username)
                        if (graduationDesignPresubject.studentId == student.studentId) {
                            canLook = true
                        }
                    }
                    if (!canLook) {
                        // 仅允许教职工查看
                        if (graduationDesignPresubject.publicLevel == 1) {
                            val usersType = cacheManageService.findByUsersTypeId(users!!.usersTypeId!!)
                            if (usersType.usersTypeName == Workbook.STAFF_USERS_TYPE) {
                                canLook = true
                            }
                        } else if (graduationDesignPresubject.publicLevel == 2) {
                            // 毕业设计结束后允许任何人查看，结束前仅允许教职工及管理员查看(半公开)
                            if (DateTimeUtils.timestampAfterDecide(graduationDesignRelease!!.endTime)) {
                                canLook = true
                            }
                        } else if (graduationDesignPresubject.publicLevel == 3) {
                            canLook = true
                        }
                    }
                }

                if (canLook) {
                    graduationDesignPresubject.updateTimeStr = DateTimeUtils.formatDate(graduationDesignPresubject.updateTime)
                    modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject)
                    modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                    page = "web/graduate/design/subject/design_subject_look::#page-wrapper"
                } else {
                    page = methodControllerCommon.showTip(modelMap, "您不符合查看条件")
                }
            } else {
                page = methodControllerCommon.showTip(modelMap, "未查询到相关毕业设计题目信息")
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 编辑
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/subject/list/edit"], method = [(RequestMethod.GET)])
    fun edit(@RequestParam("id") graduationDesignReleaseId: String,
             @RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectId)
            if (listAndMyCanEditCondition(graduationDesignPresubject)) {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject)
                "web/graduate/design/subject/design_subject_my_edit::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "您不符合编辑条件")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 我的题目
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/subject/my"], method = [(RequestMethod.GET)])
    fun my(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            val users = usersService.getUserFromSession()
            val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
            if (studentRecord.isPresent) {
                val student = studentRecord.get().into(Student::class.java)
                val record = graduationDesignPresubjectService.findByStudentIdAndGraduationDesignReleaseId(student.studentId!!, graduationDesignReleaseId)
                var graduationDesignPresubject = GraduationDesignPresubjectBean()
                if (!ObjectUtils.isEmpty(record)) {
                    graduationDesignPresubject = record.into(GraduationDesignPresubjectBean::class.java)
                    graduationDesignPresubject.updateTimeStr = DateTimeUtils.formatDate(graduationDesignPresubject.updateTime)
                }
                modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject)
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                "web/graduate/design/subject/design_subject_my::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "您可能不是学生用户或不符合进入条件")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 题目申报
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare"], method = [(RequestMethod.GET)])
    fun declare(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            graduationDesignMethodControllerCommon.setStaffIdAndStudentId(modelMap, graduationDesignRelease!!)
            pageParamControllerCommon.currentUserRoleNameAndTypeNamePageParam(modelMap)
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("endTime", DateTimeUtils.formatDate(graduationDesignRelease.endTime))
            "web/graduate/design/subject/design_subject_declare::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 修改题目
     *
     * @param graduationDesignReleaseId    发布id
     * @param graduationDesignPresubjectId 题目id
     * @param staffId                      教职工id
     * @param modelMap                     页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/edit/title"], method = [(RequestMethod.GET)])
    fun declareEditTtile(@RequestParam("id") graduationDesignReleaseId: String,
                         @RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String,
                         @RequestParam("staffId") staffId: Int, modelMap: ModelMap): String {
        val graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectId)
        return if (declareUpdateTitleCondition(graduationDesignReleaseId, graduationDesignPresubject, staffId)) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject)
            modelMap.addAttribute("staffId", staffId)
            "web/graduate/design/subject/design_subject_declare_title::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "您不符合进入条件")
        }
    }

    /**
     * 编辑申报项
     *
     * @param graduationDesignReleaseId    发布id
     * @param graduationDesignPresubjectId 题目id
     * @param modelMap                     页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/edit/apply"], method = [(RequestMethod.GET)])
    fun declareEditApply(@RequestParam("id") graduationDesignReleaseId: String,
                         @RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String, modelMap: ModelMap): String {
        val graduationDesignPresubjectRecord = graduationDesignPresubjectService.findByIdRelation(graduationDesignPresubjectId)
        return if (graduationDesignPresubjectRecord.isPresent) {
            val graduationDesignPresubject = graduationDesignPresubjectRecord.get().into(GraduationDesignPresubjectBean::class.java)
            val graduationDesignTutorRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(graduationDesignPresubject.studentId!!, graduationDesignReleaseId)
            if (graduationDesignTutorRecord.isPresent) {
                val graduationDesignTutorBean = graduationDesignTutorRecord.get().into(GraduationDesignTutorBean::class.java)
                val staffId = graduationDesignTutorBean.staffId
                graduationDesignPresubject.graduationDesignPresubjectId = graduationDesignPresubjectId
                if (updateApplyCondition(graduationDesignReleaseId, graduationDesignPresubject, staffId)) {
                    modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                    modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject)
                    modelMap.addAttribute("staffId", staffId)
                    "web/graduate/design/subject/design_subject_declare_apply::#page-wrapper"
                } else {
                    methodControllerCommon.showTip(modelMap, "您不符合进入条件")
                }
            } else {
                methodControllerCommon.showTip(modelMap, "未查询到指导教师信息")
            }
        } else {
            methodControllerCommon.showTip(modelMap, "未查询到相关题目信息")
        }
    }

    /**
     * 申报基础信息
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/basic"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun declareBasic(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignDeclareData = graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            ajaxUtils.success().msg("获取数据成功").obj(graduationDesignDeclareData!!)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 获取指导人数
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/basic/peoples"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun declareBasicPeoples(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("staffId") staffId: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            var peoples = 0
            if (staffId > 0) {
                peoples = graduationDesignTutorService.countByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staffId)
            }
            ajaxUtils.success().msg("获取数据成功").obj(peoples)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 编辑我的题目
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/subject/my/edit"], method = [(RequestMethod.GET)])
    fun myEdit(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            val users = usersService.getUserFromSession()
            val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
            if (studentRecord.isPresent) {
                val student = studentRecord.get().into(Student::class.java)
                val record = graduationDesignPresubjectService.findByStudentIdAndGraduationDesignReleaseId(student.studentId!!, graduationDesignReleaseId)
                val graduationDesignPresubject: GraduationDesignPresubject
                if (!ObjectUtils.isEmpty(record)) {
                    graduationDesignPresubject = record.into(GraduationDesignPresubject::class.java)
                    val graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignPresubject.graduationDesignPresubjectId)
                    page = if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.isOkApply) || graduationDesignDeclare.isOkApply != 1.toByte()) {
                        "web/graduate/design/subject/design_subject_my_edit::#page-wrapper"
                    } else {
                        methodControllerCommon.showTip(modelMap, "您的题目已确认申报，无法更改")
                    }
                } else {
                    graduationDesignPresubject = GraduationDesignPresubject()
                    page = "web/graduate/design/subject/design_subject_my_add::#page-wrapper"
                }
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject)
            } else {
                page = methodControllerCommon.showTip(modelMap, "您可能不是学生用户或不符合进入条件")
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 保存时检验毕业设计题目
     *
     * @param presubjectTitle 题目
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/save/valid/title"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValidTitle(@RequestParam("presubjectTitle") presubjectTitle: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val graduationDesignPresubjects = graduationDesignPresubjectService.findByPresubjectTitle(StringUtils.trimAllWhitespace(presubjectTitle))
        if (graduationDesignPresubjects.isNotEmpty()) {
            ajaxUtils.fail().msg("该题目已被使用")
        } else {
            ajaxUtils.success().msg("该题目未被使用")
        }
        return ajaxUtils
    }

    /**
     * 编辑时检验毕业设计题目
     *
     * @param presubjectTitle 题目
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/update/valid/title"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValidTitle(@RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String,
                         @RequestParam("presubjectTitle") presubjectTitle: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val graduationDesignPresubjects = graduationDesignPresubjectService.findByPresubjectTitleNeId(StringUtils.trimAllWhitespace(presubjectTitle), graduationDesignPresubjectId)
        if (!ObjectUtils.isEmpty(graduationDesignPresubjects) && graduationDesignPresubjects.size > 0) {
            ajaxUtils.fail().msg("该题目已被使用")
        } else {
            ajaxUtils.success().msg("该题目未被使用")
        }
        return ajaxUtils
    }

    /**
     * 保存
     *
     * @param graduationDesignPresubjectAddVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/my/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun mySave(@Valid graduationDesignPresubjectAddVo: GraduationDesignPresubjectAddVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignPresubjectAddVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val graduationDesignRelease = errorBean.data
                val users = usersService.getUserFromSession()
                val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
                if (studentRecord.isPresent) {
                    val student = studentRecord.get().into(Student::class.java)
                    val graduationDesignPresubject = GraduationDesignPresubject()
                    graduationDesignPresubject.graduationDesignPresubjectId = UUIDUtils.getUUID()
                    graduationDesignPresubject.studentId = student.studentId
                    graduationDesignPresubject.graduationDesignReleaseId = graduationDesignPresubjectAddVo.graduationDesignReleaseId
                    graduationDesignPresubject.presubjectTitle = graduationDesignPresubjectAddVo.presubjectTitle
                    graduationDesignPresubject.presubjectPlan = graduationDesignPresubjectAddVo.presubjectPlan
                    graduationDesignPresubject.publicLevel = graduationDesignPresubjectAddVo.publicLevel
                    graduationDesignPresubject.updateTime = DateTimeUtils.getNow()
                    graduationDesignPresubjectService.save(graduationDesignPresubject)
                    ajaxUtils.success().msg("保存成功")
                } else {
                    ajaxUtils.fail().msg("您可能不是学生用户或不符合进入条件")
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
     * 更新
     *
     * @param graduationDesignPresubjectUpdateVo 数据
     * @param bindingResult                      检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/my/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myUpdate(@Valid graduationDesignPresubjectUpdateVo: GraduationDesignPresubjectUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignPresubjectUpdateVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectUpdateVo.graduationDesignPresubjectId!!)
                if (!ObjectUtils.isEmpty(graduationDesignPresubject) && listAndMyCanEditCondition(graduationDesignPresubject)) {
                    updatePresubject(graduationDesignPresubjectUpdateVo, graduationDesignPresubject)
                    ajaxUtils.success().msg("保存成功")
                } else {
                    ajaxUtils.fail().msg("您不允许编辑该题目")
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
     * 修改题目
     *
     * @param graduationDesignPresubjectUpdateVo 数据
     * @param bindingResult                      检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/edit/title/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun declareUpdateTitle(@Valid graduationDesignPresubjectUpdateVo: GraduationDesignPresubjectUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectUpdateVo.graduationDesignPresubjectId!!)
            if (!ObjectUtils.isEmpty(graduationDesignPresubject) && declareUpdateTitleCondition(graduationDesignPresubjectUpdateVo.graduationDesignReleaseId!!, graduationDesignPresubject, graduationDesignPresubjectUpdateVo.staffId)) {
                updatePresubject(graduationDesignPresubjectUpdateVo, graduationDesignPresubject)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("不符合编辑条件")
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 修改申报项
     *
     * @param graduationDesignDeclareUpdateVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/edit/apply/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateApply(@Valid graduationDesignDeclareUpdateVo: GraduationDesignDeclareUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignDeclareUpdateVo.graduationDesignReleaseId!!)
            graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
            if (!errorBean.isHasError()) {
                val users = usersService.getUserFromSession()
                val graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignDeclareUpdateVo.graduationDesignPresubjectId!!)
                // 未确认申报
                if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.isOkApply) || graduationDesignDeclare.isOkApply != 1.toByte()) {
                    // 如果是管理员或系统
                    val canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isOkStaff(users, graduationDesignDeclareUpdateVo.graduationDesignReleaseId!!, graduationDesignDeclareUpdateVo.staffId!!)
                    if (canEdit) {
                        val tempGraduationDesignDeclare = GraduationDesignDeclare()
                        tempGraduationDesignDeclare.subjectTypeId = graduationDesignDeclareUpdateVo.subjectTypeId
                        tempGraduationDesignDeclare.originTypeId = graduationDesignDeclareUpdateVo.originTypeId
                        tempGraduationDesignDeclare.isNewSubject = graduationDesignDeclareUpdateVo.isNewSubject
                        tempGraduationDesignDeclare.isNewTeacherMake = graduationDesignDeclareUpdateVo.isNewTeacherMake
                        tempGraduationDesignDeclare.isNewSubjectMake = graduationDesignDeclareUpdateVo.isNewSubjectMake
                        tempGraduationDesignDeclare.isOldSubjectChange = graduationDesignDeclareUpdateVo.isOldSubjectChange
                        tempGraduationDesignDeclare.oldSubjectUsesTimes = graduationDesignDeclareUpdateVo.oldSubjectUsesTimes
                        tempGraduationDesignDeclare.planPeriod = graduationDesignDeclareUpdateVo.planPeriod
                        tempGraduationDesignDeclare.assistantTeacher = graduationDesignDeclareUpdateVo.assistantTeacher
                        tempGraduationDesignDeclare.assistantTeacherAcademic = graduationDesignDeclareUpdateVo.assistantTeacherAcademic
                        tempGraduationDesignDeclare.assistantTeacherNumber = graduationDesignDeclareUpdateVo.assistantTeacherNumber
                        tempGraduationDesignDeclare.guideTimes = graduationDesignDeclareUpdateVo.guideTimes
                        tempGraduationDesignDeclare.guidePeoples = graduationDesignDeclareUpdateVo.guidePeoples
                        tempGraduationDesignDeclare.isOkApply = graduationDesignDeclareUpdateVo.isOkApply
                        tempGraduationDesignDeclare.graduationDesignPresubjectId = graduationDesignDeclareUpdateVo.graduationDesignPresubjectId
                        graduationDesignDeclareService.saveOrUpdate(tempGraduationDesignDeclare)
                        ajaxUtils.success().msg("保存成功")
                    } else {
                        ajaxUtils.fail().msg("不符合条件，无法编辑")
                    }
                } else {
                    ajaxUtils.fail().msg("已确认申报，无法编辑")
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
     * 确认申报
     *
     * @param graduationDesignReleaseId     发布id
     * @param staffId                       教职工id
     * @param graduationDesignPresubjectIds 题目ids
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/apply/ok"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun okApply(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("staffId") staffId: Int,
                graduationDesignPresubjectIds: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(graduationDesignPresubjectIds)) {
                val ids = SmallPropsUtils.StringIdsToStringList(graduationDesignPresubjectIds)
                val tempGraduationDesignDeclare = GraduationDesignDeclare()
                val users = usersService.getUserFromSession()
                // 如果是管理员或系统
                val canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isOkStaff(users, graduationDesignReleaseId, staffId)
                for (id in ids) {
                    val graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(id)
                    // 未确认申报
                    if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.isOkApply) || graduationDesignDeclare.isOkApply != 1.toByte()) {
                        if (canEdit) {
                            tempGraduationDesignDeclare.isOkApply = 1
                            tempGraduationDesignDeclare.graduationDesignPresubjectId = id
                            graduationDesignDeclareService.saveOrUpdateState(tempGraduationDesignDeclare)
                        }
                    }
                }
            }
            ajaxUtils.success().msg("确认成功")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }

        return ajaxUtils
    }

    /**
     * 统一设置
     *
     * @param graduationDesignReleaseId 发布id
     * @param staffId                   教职工id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/edit/all"], method = [(RequestMethod.GET)])
    fun editAll(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("staffId") staffId: Int, modelMap: ModelMap): String {
        val page: String
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
        if (!errorBean.isHasError()) {
            val records = graduationDesignDeclareService.findByStaffIdRelationNeIsOkApply(staffId, graduationDesignReleaseId)
            if (records.isNotEmpty) {
                val users = usersService.getUserFromSession()
                // 如果是管理员或系统
                val canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isOkStaff(users, graduationDesignReleaseId, staffId)
                page = if (canEdit) {
                    val stringBuilder = StringBuilder()
                    for (r in records) {
                        stringBuilder.append(r.get(0).toString()).append(",")
                    }
                    modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                    modelMap.addAttribute("staffId", staffId)
                    modelMap.addAttribute("graduationDesignPresubjectIds", stringBuilder.substring(0, stringBuilder.lastIndexOf(",")))
                    "web/graduate/design/subject/design_subject_declare_all::#page-wrapper"
                } else {
                    methodControllerCommon.showTip(modelMap, "您不满足条件，无法进入")
                }
            } else {
                page = methodControllerCommon.showTip(modelMap, "未发现未申报的数据")
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }

        return page
    }

    /**
     * 统一设置更新
     *
     * @param graduationDesignDeclareUpdateVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/all/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateAll(@Valid graduationDesignDeclareUpdateVo: GraduationDesignDeclareUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignDeclareUpdateVo.graduationDesignReleaseId!!)
            graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
            if (!errorBean.isHasError()) {
                val users = usersService.getUserFromSession()
                // 如果是管理员或系统
                val canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isOkStaff(users, graduationDesignDeclareUpdateVo.graduationDesignReleaseId!!, graduationDesignDeclareUpdateVo.staffId!!)
                if (canEdit) {
                    val ids = SmallPropsUtils.StringIdsToStringList(graduationDesignDeclareUpdateVo.graduationDesignPresubjectId!!)
                    for (id in ids) {
                        val tempGraduationDesignDeclare = GraduationDesignDeclare()
                        tempGraduationDesignDeclare.subjectTypeId = graduationDesignDeclareUpdateVo.subjectTypeId
                        tempGraduationDesignDeclare.originTypeId = graduationDesignDeclareUpdateVo.originTypeId
                        tempGraduationDesignDeclare.isNewSubject = graduationDesignDeclareUpdateVo.isNewSubject
                        tempGraduationDesignDeclare.isNewTeacherMake = graduationDesignDeclareUpdateVo.isNewTeacherMake
                        tempGraduationDesignDeclare.isNewSubjectMake = graduationDesignDeclareUpdateVo.isNewSubjectMake
                        tempGraduationDesignDeclare.isOldSubjectChange = graduationDesignDeclareUpdateVo.isOldSubjectChange
                        tempGraduationDesignDeclare.oldSubjectUsesTimes = graduationDesignDeclareUpdateVo.oldSubjectUsesTimes
                        tempGraduationDesignDeclare.planPeriod = graduationDesignDeclareUpdateVo.planPeriod
                        tempGraduationDesignDeclare.assistantTeacher = graduationDesignDeclareUpdateVo.assistantTeacher
                        tempGraduationDesignDeclare.assistantTeacherAcademic = graduationDesignDeclareUpdateVo.assistantTeacherAcademic
                        tempGraduationDesignDeclare.assistantTeacherNumber = graduationDesignDeclareUpdateVo.assistantTeacherNumber
                        tempGraduationDesignDeclare.guideTimes = graduationDesignDeclareUpdateVo.guideTimes
                        tempGraduationDesignDeclare.guidePeoples = graduationDesignDeclareUpdateVo.guidePeoples
                        tempGraduationDesignDeclare.isOkApply = graduationDesignDeclareUpdateVo.isOkApply
                        tempGraduationDesignDeclare.graduationDesignPresubjectId = id
                        graduationDesignDeclareService.saveOrUpdate(tempGraduationDesignDeclare)
                    }
                    ajaxUtils.success().msg("保存成功")
                } else {
                    ajaxUtils.fail().msg("不符合条件，无法编辑")
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
     * 进入题目申报判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun declareCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 题目申报操作条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/declare/operator/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun declareOperatorCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入我的题目页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/my/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            isOkStudent(errorBean, ajaxUtils)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 我的题目操作条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/subject/my/operator/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myOperatorCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            isOkStudent(errorBean, ajaxUtils)
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
    @RequestMapping(value = ["/web/graduate/design/subject/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 更新题目
     *
     * @param graduationDesignPresubjectUpdateVo 数据
     * @param graduationDesignPresubject         原题
     */
    private fun updatePresubject(graduationDesignPresubjectUpdateVo: GraduationDesignPresubjectUpdateVo, graduationDesignPresubject: GraduationDesignPresubject) {
        graduationDesignPresubject.presubjectTitle = graduationDesignPresubjectUpdateVo.presubjectTitle
        graduationDesignPresubject.presubjectPlan = graduationDesignPresubjectUpdateVo.presubjectPlan
        graduationDesignPresubject.publicLevel = graduationDesignPresubjectUpdateVo.publicLevel
        graduationDesignPresubject.updateTime = DateTimeUtils.getNow()
        graduationDesignPresubjectService.update(graduationDesignPresubject)
    }

    /**
     * 学生判断条件
     *
     * @param errorBean 错误
     * @param ajaxUtils ajax
     */
    private fun isOkStudent(errorBean: ErrorBean<GraduationDesignRelease>, ajaxUtils: AjaxUtils<*>) {
        val users = usersService.getUserFromSession()
        val graduationDesignRelease = errorBean.data
        val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
        if (studentRecord.isPresent) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg("您可能不是学生用户或不符合进入条件")
        }
    }

    /**
     * 判断当前操作用户是否为教职工
     *
     * @param users                     当前用户信息
     * @param graduationDesignReleaseId 发布Id
     * @param staffId                   教职工id
     * @return true or false
     */
    private fun isOkStaff(users: Users?, graduationDesignReleaseId: String, staffId: Int): Boolean {
        var canEdit = false
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            val staff = staffService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(staff)) {
                val staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.staffId!!)
                if (staffRecord.isPresent) {
                    val tempStaff = staffRecord.get().into(Staff::class.java)
                    canEdit = tempStaff.staffId == staffId
                }
            }
        }
        return canEdit
    }

    /**
     * 编辑条件
     *
     * @param graduationDesignPresubject 数据
     * @return true or false
     */
    private fun listAndMyCanEditCondition(graduationDesignPresubject: GraduationDesignPresubject): Boolean {
        var canEdit = false
        val users = usersService.getUserFromSession()
        val graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignPresubject.graduationDesignPresubjectId)
        if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.isOkApply) || graduationDesignDeclare.isOkApply != 1.toByte()) {
            // 教职工可查看，因题目可在分配教师前写，不存在指导教师，本人指导教师则无法编辑，因此仅允许查看，在列表没有编辑
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                canEdit = true
            } else {
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    val student = studentService.findByUsername(users!!.username)
                    if (graduationDesignPresubject.studentId == student.studentId) {
                        canEdit = true
                    }
                }
            }
        }
        return canEdit
    }

    /**
     * 修改题目条件
     *
     * @param graduationDesignReleaseId  发布id
     * @param graduationDesignPresubject 题目
     * @param staffId                    教职工id
     * @return true or false
     */
    private fun declareUpdateTitleCondition(graduationDesignReleaseId: String, graduationDesignPresubject: GraduationDesignPresubject, staffId: Int): Boolean {
        var canEdit = false
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            val users = usersService.getUserFromSession()
            val graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignPresubject.graduationDesignPresubjectId)
            // 未确认申报
            if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.isOkApply) || graduationDesignDeclare.isOkApply != 1.toByte()) {
                // 如果是管理员或系统
                if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                    canEdit = true
                } else {
                    if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                        val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
                        if (studentRecord.isPresent) {
                            val student = studentRecord.get().into(Student::class.java)
                            canEdit = student.studentId == graduationDesignPresubject.studentId
                        }
                    } else {
                        canEdit = isOkStaff(users, graduationDesignReleaseId, staffId)
                    }
                }
            }
        }
        return canEdit
    }

    /**
     * 编辑申报条件
     *
     * @param graduationDesignReleaseId  发布id
     * @param graduationDesignPresubject 题目
     * @param staffId                    教职工id
     * @return true or false
     */
    private fun updateApplyCondition(graduationDesignReleaseId: String, graduationDesignPresubject: GraduationDesignPresubject, staffId: Int): Boolean {
        var canEdit = false
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
        if (!errorBean.isHasError()) {
            val users = usersService.getUserFromSession()
            val graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignPresubject.graduationDesignPresubjectId)
            // 未确认申报
            if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.isOkApply) || graduationDesignDeclare.isOkApply != 1.toByte()) {
                // 如果是管理员或系统或教职工
                canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isOkStaff(users, graduationDesignReleaseId, staffId)
            }
        }
        return canEdit
    }
}