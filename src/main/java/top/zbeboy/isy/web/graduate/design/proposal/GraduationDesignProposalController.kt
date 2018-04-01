package top.zbeboy.isy.web.graduate.design.proposal

import org.apache.commons.lang.math.NumberUtils
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
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.graduate.design.GraduationDesignDatumGroupService
import top.zbeboy.isy.service.graduate.design.GraduationDesignDatumService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.error.ErrorBean
import top.zbeboy.isy.web.bean.file.FileBean
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumGroupBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.vo.graduate.design.proposal.GraduationDesignProposalAddVo
import java.io.IOException
import java.sql.Timestamp
import java.time.Clock
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

/**
 * Created by zbeboy 2018-01-29 .
 **/
@Controller
open class GraduationDesignProposalController {

    private val log = LoggerFactory.getLogger(GraduationDesignProposalController::class.java)

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var graduationDesignDatumService: GraduationDesignDatumService

    @Resource
    open lateinit var graduationDesignDatumGroupService: GraduationDesignDatumGroupService

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var graduationDesignTutorService: GraduationDesignTutorService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon


    /**
     * 毕业设计资料
     *
     * @return 毕业设计资料页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/proposal"], method = [(RequestMethod.GET)])
    fun proposal(): String {
        return "web/graduate/design/proposal/design_proposal::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/design/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun designDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils)
    }

    /**
     * 附件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/affix"], method = [(RequestMethod.GET)])
    fun affix(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            "web/graduate/design/proposal/design_proposal_affix::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 文件下载
     *
     * @param fileId   文件id
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping("/web/graduate/design/proposal/download/file")
    fun downloadFile(@RequestParam("fileId") fileId: String, request: HttpServletRequest, response: HttpServletResponse) {
        methodControllerCommon.downloadFile(fileId, request, response)
    }

    /**
     * 我的资料页面
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/my"], method = [(RequestMethod.GET)])
    fun my(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        return if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            val errorBean = studentAccessCondition(graduationDesignReleaseId, student.studentId!!)
            if (!errorBean.isHasError()) {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                "web/graduate/design/proposal/design_proposal_my::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
            }
        } else {
            methodControllerCommon.showTip(modelMap, "目前仅提供学生使用")
        }
    }

    /**
     * 小组资料
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/team"], method = [(RequestMethod.GET)])
    fun team(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            graduationDesignMethodControllerCommon.setStaffIdAndStudentId(modelMap, graduationDesignRelease!!)
            pageParamControllerCommon.currentUserRoleNameAndTypeNamePageParam(modelMap)
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            "web/graduate/design/proposal/design_proposal_team::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 组内资料
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/group"], method = [(RequestMethod.GET)])
    fun group(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            var canUse = false
            val users = usersService.getUserFromSession()
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
                if (studentRecord.isPresent) {
                    val student = studentRecord.get().into(Student::class.java)
                    val staffRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.studentId!!, graduationDesignReleaseId)
                    if (staffRecord.isPresent) {
                        val graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher::class.java)
                        modelMap.addAttribute("graduationDesignTeacherId", graduationDesignTeacher.graduationDesignTeacherId)
                        modelMap.addAttribute("currUseIsStaff", 0)
                        canUse = true
                    }
                }
            } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                val staff = staffService.findByUsername(users!!.username)
                if (!ObjectUtils.isEmpty(staff)) {
                    val staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.staffId!!)
                    if (staffRecord.isPresent) {
                        val graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher::class.java)
                        modelMap.addAttribute("graduationDesignTeacherId", graduationDesignTeacher.graduationDesignTeacherId)
                        modelMap.addAttribute("currUseIsStaff", 1)
                        canUse = true
                    }
                }
            }

            page = if (canUse) {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                "web/graduate/design/proposal/design_proposal_group::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "您不符合进入条件")
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 我的资料数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/my/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun myData(request: HttpServletRequest): DataTablesUtils<GraduationDesignDatumBean> {
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        var dataTablesUtils = DataTablesUtils.of<GraduationDesignDatumBean>()
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val users = usersService.getUserFromSession()
                val student = studentService.findByUsername(users!!.username)
                val errorBean = studentAccessCondition(graduationDesignReleaseId, student.studentId!!)
                if (!errorBean.isHasError()) {
                    val graduationDesignTutor = errorBean.mapData!!["graduationDesignTutor"] as GraduationDesignTutor
                    // 前台数据标题 注：要和前台标题顺序一致，获取order用
                    val headers = ArrayList<String>()
                    headers.add("original_file_name")
                    headers.add("graduation_design_datum_type_name")
                    headers.add("version")
                    headers.add("update_time")
                    headers.add("operator")
                    val otherCondition = GraduationDesignDatumBean()
                    dataTablesUtils = DataTablesUtils(request, headers)
                    var graduationDesignDatumBeens: List<GraduationDesignDatumBean> = ArrayList()
                    otherCondition.graduationDesignTutorId = graduationDesignTutor.graduationDesignTutorId
                    val records = graduationDesignDatumService.findAllByPage(dataTablesUtils, otherCondition)
                    if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                        graduationDesignDatumBeens = records.into(GraduationDesignDatumBean::class.java)
                        graduationDesignDatumBeenFilter(graduationDesignDatumBeens)
                    }
                    dataTablesUtils.data = graduationDesignDatumBeens
                    dataTablesUtils.setiTotalRecords(graduationDesignDatumService.countAll(otherCondition).toLong())
                    dataTablesUtils.setiTotalDisplayRecords(graduationDesignDatumService.countByCondition(dataTablesUtils, otherCondition).toLong())
                }
            }
        }
        return dataTablesUtils
    }

    /**
     * 小组数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/team/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun teamData(request: HttpServletRequest): DataTablesUtils<GraduationDesignDatumBean> {
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        var dataTablesUtils = DataTablesUtils.of<GraduationDesignDatumBean>()
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
            if (!errorBean.isHasError()) {
                // 前台数据标题 注：要和前台标题顺序一致，获取order用
                val headers = ArrayList<String>()
                headers.add("real_name")
                headers.add("student_number")
                headers.add("organize_name")
                headers.add("original_file_name")
                headers.add("version")
                headers.add("graduation_design_datum_type_name")
                headers.add("update_time")
                headers.add("operator")
                dataTablesUtils = DataTablesUtils(request, headers)
                val otherCondition = GraduationDesignDatumBean()
                val staffId = NumberUtils.toInt(request.getParameter("staffId"))
                otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
                otherCondition.staffId = staffId
                var graduationDesignDatumBeens: List<GraduationDesignDatumBean> = ArrayList()
                val records = graduationDesignDatumService.findTeamAllByPage(dataTablesUtils, otherCondition)
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    graduationDesignDatumBeens = records.into(GraduationDesignDatumBean::class.java)
                    graduationDesignDatumBeenFilter(graduationDesignDatumBeens)
                }
                dataTablesUtils.data = graduationDesignDatumBeens
                dataTablesUtils.setiTotalRecords(graduationDesignDatumService.countTeamAll(otherCondition).toLong())
                dataTablesUtils.setiTotalDisplayRecords(graduationDesignDatumService.countTeamByCondition(dataTablesUtils, otherCondition).toLong())
            }
        }
        return dataTablesUtils
    }

    /**
     * 过滤数据，保护隐私
     *
     * @param graduationDesignDatumBeens 数据
     */
    private fun graduationDesignDatumBeenFilter(graduationDesignDatumBeens: List<GraduationDesignDatumBean>) {
        graduationDesignDatumBeens.forEach { i ->
            i.updateTimeStr = DateTimeUtils.formatDate(i.updateTime)
            i.relativePath = ""
        }
    }

    /**
     * 组内资料数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/group/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun groupData(request: HttpServletRequest): DataTablesUtils<GraduationDesignDatumGroupBean> {
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        val graduationDesignTeacherId = request.getParameter("graduationDesignTeacherId")
        var dataTablesUtils = DataTablesUtils.of<GraduationDesignDatumGroupBean>()
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
            if (!errorBean.isHasError()) {
                // 前台数据标题 注：要和前台标题顺序一致，获取order用
                val headers = ArrayList<String>()
                headers.add("original_file_name")
                headers.add("upload_time")
                headers.add("operator")
                dataTablesUtils = DataTablesUtils(request, headers)
                val otherCondition = GraduationDesignDatumGroupBean()
                otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
                otherCondition.graduationDesignTeacherId = graduationDesignTeacherId
                var graduationDesignDatumGroupBeens: List<GraduationDesignDatumGroupBean> = ArrayList()
                val records = graduationDesignDatumGroupService.findAllByPage(dataTablesUtils, otherCondition)
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
                    graduationDesignDatumGroupBeens = records.into(GraduationDesignDatumGroupBean::class.java)
                    graduationDesignDatumGroupBeens.forEach { i ->
                        i.uploadTimeStr = DateTimeUtils.formatDate(i.uploadTime)
                        i.relativePath = ""
                    }
                }
                dataTablesUtils.data = graduationDesignDatumGroupBeens
                dataTablesUtils.setiTotalRecords(graduationDesignDatumGroupService.countAll(otherCondition).toLong())
                dataTablesUtils.setiTotalDisplayRecords(graduationDesignDatumGroupService.countByCondition(dataTablesUtils, otherCondition).toLong())
            }
        }
        return dataTablesUtils
    }

    /**
     * 保存或更新毕业设计资料
     *
     * @param graduationDesignProposalAddVo 数据
     * @param multipartHttpServletRequest   文件
     * @param request                       请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/proposal/my/save")
    @ResponseBody
    fun mySave(@Valid graduationDesignProposalAddVo: GraduationDesignProposalAddVo, bindingResult: BindingResult,
               multipartHttpServletRequest: MultipartHttpServletRequest, request: HttpServletRequest): AjaxUtils<FileBean> {
        val ajaxUtils = AjaxUtils.of<FileBean>()
        try {
            if (!bindingResult.hasErrors()) {
                // 学生使用
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    val users = usersService.getUserFromSession()
                    val student = studentService.findByUsername(users!!.username)
                    val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignProposalAddVo.graduationDesignReleaseId!!)
                    graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
                    if (!errorBean.isHasError()) {
                        val graduationDesignRelease = errorBean.data
                        // 是否符合该毕业设计条件
                        val record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.studentId!!, graduationDesignRelease!!.graduationDesignReleaseId)
                        if (record.isPresent) {
                            var canUse = true
                            val graduationDesignTutor = record.get().into(GraduationDesignTutor::class.java)
                            // 是否更新
                            val recordDatum = graduationDesignDatumService.findByGraduationDesignTutorIdAndGraduationDesignDatumTypeId(graduationDesignTutor.graduationDesignTutorId, graduationDesignProposalAddVo.graduationDesignDatumTypeId!!)
                            var graduationDesignDatum: GraduationDesignDatum? = null
                            var isUpdate = false
                            if (recordDatum.isPresent) {
                                val graduationDesignDatumBean = recordDatum.get().into(GraduationDesignDatumBean::class.java)
                                if (student.studentId == graduationDesignDatumBean.studentId) {
                                    graduationDesignDatum = recordDatum.get().into(GraduationDesignDatum::class.java)
                                    isUpdate = true
                                } else {
                                    canUse = false
                                }
                            } else {
                                graduationDesignDatum = GraduationDesignDatum()
                            }
                            if (canUse) {
                                val path = Workbook.graduationDesignProposalPath(users)
                                val fileBeen = uploadService.upload(multipartHttpServletRequest,
                                        RequestUtils.getRealPath(request) + path, request.remoteAddr)
                                if (fileBeen.isNotEmpty()) {
                                    val fileId = UUIDUtils.getUUID()
                                    val tempFile = fileBeen[0]
                                    val files = Files()
                                    files.fileId = fileId
                                    files.ext = tempFile.ext
                                    files.newName = tempFile.newName
                                    files.originalFileName = tempFile.originalFileName
                                    files.size = tempFile.size.toString()
                                    files.relativePath = path + tempFile.newName!!
                                    filesService.save(files)
                                    if (isUpdate) {
                                        // 删除旧文件
                                        val oldFile = filesService.findById(graduationDesignDatum!!.fileId)
                                        FilesUtils.deleteFile(RequestUtils.getRealPath(request) + oldFile.relativePath)
                                        graduationDesignDatum.fileId = fileId
                                        graduationDesignDatum.updateTime = DateTimeUtils.getNow()
                                        graduationDesignDatum.version = graduationDesignProposalAddVo.version
                                        graduationDesignDatumService.update(graduationDesignDatum)
                                        filesService.deleteById(oldFile.fileId)
                                    } else {
                                        graduationDesignDatum!!.graduationDesignDatumId = UUIDUtils.getUUID()
                                        graduationDesignDatum.fileId = fileId
                                        graduationDesignDatum.updateTime = DateTimeUtils.getNow()
                                        graduationDesignDatum.graduationDesignTutorId = graduationDesignTutor.graduationDesignTutorId
                                        graduationDesignDatum.version = graduationDesignProposalAddVo.version
                                        graduationDesignDatum.graduationDesignDatumTypeId = graduationDesignProposalAddVo.graduationDesignDatumTypeId
                                        graduationDesignDatumService.save(graduationDesignDatum)
                                    }
                                    ajaxUtils.success().msg("保存成功")
                                } else {
                                    ajaxUtils.fail().msg("未查询到文件信息")
                                }
                            } else {
                                ajaxUtils.fail().msg("无法核实该文件属于您")
                            }
                        } else {
                            ajaxUtils.fail().msg("您的账号不符合该毕业设计条件")
                        }
                    } else {
                        ajaxUtils.fail().msg(errorBean.errorMsg!!)
                    }
                } else {
                    ajaxUtils.fail().msg("目前仅提供学生使用")
                }
            } else {
                ajaxUtils.fail().msg("参数异常")
            }
        } catch (e: Exception) {
            log.error("Upload graduation design proposal error, error is {}", e)
            ajaxUtils.fail().msg("保存文件异常")
        }

        return ajaxUtils
    }

    /**
     * 删除文件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param graduationDesignDatumId   毕业资料id
     * @param request                   请求
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun proposalDelete(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("graduationDesignDatumId") graduationDesignDatumId: String, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<FileBean>()
        try {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
            graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
            if (!errorBean.isHasError()) {
                val users = usersService.getUserFromSession()
                val graduationDesignDatum = canUseCondition(graduationDesignDatumId, users)
                if (!ObjectUtils.isEmpty(graduationDesignDatum)) {
                    val files = filesService.findById(graduationDesignDatum!!.fileId)
                    if (!ObjectUtils.isEmpty(files)) {
                        FilesUtils.deleteFile(RequestUtils.getRealPath(request) + files.relativePath)
                        graduationDesignDatumService.deleteById(graduationDesignDatumId)
                        filesService.deleteById(files.fileId)
                        ajaxUtils.success().msg("删除成功")
                    } else {
                        ajaxUtils.fail().msg("未查询到该文件信息")
                    }
                } else {
                    ajaxUtils.fail().msg("不符合条件，删除失败")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } catch (e: IOException) {
            log.error("Delete graduation design proposal error, error is {}", e)
            ajaxUtils.fail().msg("保存文件异常")
        }

        return ajaxUtils
    }

    /**
     * 下载文件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param graduationDesignDatumId   毕业资料id
     * @param request                   请求
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/download"], method = [(RequestMethod.GET)])
    fun download(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("graduationDesignDatumId") graduationDesignDatumId: String, response: HttpServletResponse, request: HttpServletRequest) {
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            var canUse = false
            val users = usersService.getUserFromSession()
            var graduationDesignDatum: GraduationDesignDatum? = null
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                graduationDesignDatum = graduationDesignDatumService.findById(graduationDesignDatumId)
                canUse = true
            } else {
                // 学生
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    val record = graduationDesignDatumService.findByIdRelation(graduationDesignDatumId)
                    if (record.isPresent) {
                        val student = studentService.findByUsername(users!!.username)
                        val graduationDesignDatumBean = record.get().into(GraduationDesignDatumBean::class.java)
                        if (student.studentId == graduationDesignDatumBean.studentId) {
                            graduationDesignDatum = record.get().into(GraduationDesignDatum::class.java)
                            canUse = true
                        }
                    }
                } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    graduationDesignDatum = graduationDesignDatumService.findById(graduationDesignDatumId)
                    canUse = true
                }
            }

            if (canUse) {
                val files = filesService.findById(graduationDesignDatum!!.fileId)
                if (!ObjectUtils.isEmpty(files)) {
                    uploadService.download(files.originalFileName + "V" + graduationDesignDatum.version, files.relativePath, response, request)
                }
            }
        }
    }

    /**
     * 获取毕业设计资料信息
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param graduationDesignDatumId   毕业设计资料id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/team/datum"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun teamDatum(@RequestParam("id") graduationDesignReleaseId: String, @RequestParam("graduationDesignDatumId") graduationDesignDatumId: String): AjaxUtils<GraduationDesignDatumBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignDatumBean>()
        val errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val record = graduationDesignDatumService.findByIdRelation(graduationDesignDatumId)
            if (record.isPresent) {
                val graduationDesignDatum = record.get().into(GraduationDesignDatumBean::class.java)
                ajaxUtils.success().msg("获取数据成功").obj(graduationDesignDatum)
            } else {
                ajaxUtils.fail().msg("未查询到相关毕业设计资料信息")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 更新毕业设计资料
     *
     * @param graduationDesignProposalAddVo 数据
     * @param multipartHttpServletRequest   文件
     * @param request                       请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/proposal/team/update")
    @ResponseBody
    fun teamUpdate(@Valid graduationDesignProposalAddVo: GraduationDesignProposalAddVo, bindingResult: BindingResult,
                   multipartHttpServletRequest: MultipartHttpServletRequest, request: HttpServletRequest): AjaxUtils<FileBean> {
        val ajaxUtils = AjaxUtils.of<FileBean>()
        try {
            if (!bindingResult.hasErrors() && StringUtils.hasLength(graduationDesignProposalAddVo.graduationDesignDatumId)) {
                val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignProposalAddVo.graduationDesignReleaseId!!)
                graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
                if (!errorBean.isHasError()) {
                    val users = usersService.getUserFromSession()
                    val graduationDesignDatum = canUseCondition(graduationDesignProposalAddVo.graduationDesignDatumId!!, users)
                    if (!ObjectUtils.isEmpty(graduationDesignDatum)) {
                        val path = Workbook.graduationDesignProposalPath(users!!)
                        val fileBeen = uploadService.upload(multipartHttpServletRequest,
                                RequestUtils.getRealPath(request) + path, request.remoteAddr)
                        if (fileBeen.isNotEmpty()) {
                            val fileId = UUIDUtils.getUUID()
                            val tempFile = fileBeen[0]
                            val files = Files()
                            files.fileId = fileId
                            files.ext = tempFile.ext
                            files.newName = tempFile.newName
                            files.originalFileName = tempFile.originalFileName
                            files.size = tempFile.size.toString()
                            files.relativePath = path + tempFile.newName!!
                            filesService.save(files)
                            val oldFile = filesService.findById(graduationDesignDatum!!.fileId)
                            FilesUtils.deleteFile(RequestUtils.getRealPath(request) + oldFile.relativePath)
                            graduationDesignDatum.fileId = fileId
                            graduationDesignDatum.updateTime = DateTimeUtils.getNow()
                            graduationDesignDatum.version = graduationDesignProposalAddVo.version
                            graduationDesignDatumService.update(graduationDesignDatum)
                            filesService.deleteById(oldFile.fileId)
                            ajaxUtils.success().msg("保存成功")
                        } else {
                            ajaxUtils.fail().msg("未查询到文件信息")
                        }
                    } else {
                        ajaxUtils.fail().msg("不符合保存条件，保存失败")
                    }
                } else {
                    ajaxUtils.fail().msg(errorBean.errorMsg!!)
                }
            } else {
                ajaxUtils.fail().msg("参数异常")
            }
        } catch (e: Exception) {
            log.error("Upload graduation design proposal error, error is {}", e)
            ajaxUtils.fail().msg("保存文件异常")
        }

        return ajaxUtils
    }

    /**
     * 保存组内资料
     *
     * @param graduationDesignReleaseId   毕业设计发布oid
     * @param multipartHttpServletRequest 文件
     * @param request                     请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/proposal/group/save")
    @ResponseBody
    fun groupSave(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String,
                  multipartHttpServletRequest: MultipartHttpServletRequest, request: HttpServletRequest): AjaxUtils<FileBean> {
        val ajaxUtils = AjaxUtils.of<FileBean>()
        try {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
            graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
            if (!errorBean.isHasError()) {
                val users = usersService.getUserFromSession()
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    val staff = staffService.findByUsername(users!!.username)
                    if (!ObjectUtils.isEmpty(staff)) {
                        val staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.staffId!!)
                        if (staffRecord.isPresent) {
                            val graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher::class.java)
                            val path = Workbook.graduationDesignProposalPath(users)
                            val fileBeen = uploadService.upload(multipartHttpServletRequest,
                                    RequestUtils.getRealPath(request) + path, request.remoteAddr)
                            if (fileBeen.isNotEmpty()) {
                                val fileId = UUIDUtils.getUUID()
                                val tempFile = fileBeen[0]
                                val files = Files()
                                files.fileId = fileId
                                files.ext = tempFile.ext
                                files.newName = tempFile.newName
                                files.originalFileName = tempFile.originalFileName
                                files.size = tempFile.size.toString()
                                files.relativePath = path + tempFile.newName!!
                                filesService.save(files)

                                val graduationDesignDatumGroup = GraduationDesignDatumGroup()
                                graduationDesignDatumGroup.graduationDesignDatumGroupId = UUIDUtils.getUUID()
                                graduationDesignDatumGroup.fileId = fileId
                                graduationDesignDatumGroup.graduationDesignTeacherId = graduationDesignTeacher.graduationDesignTeacherId
                                graduationDesignDatumGroup.uploadTime = Timestamp(Clock.systemDefaultZone().millis())
                                graduationDesignDatumGroupService.save(graduationDesignDatumGroup)
                                ajaxUtils.success().msg("保存成功")
                            } else {
                                ajaxUtils.fail().msg("保存文件失败")
                            }
                        } else {
                            ajaxUtils.fail().msg("您不是该毕业设计指导教师")
                        }
                    } else {
                        ajaxUtils.fail().msg("未查询到教职工信息")
                    }
                } else {
                    ajaxUtils.fail().msg("您的注册类型不符合进入条件")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } catch (e: Exception) {
            log.error("Upload graduation design proposal error, error is {}", e)
            ajaxUtils.fail().msg("保存文件异常")
        }

        return ajaxUtils
    }

    /**
     * 删除组内资料
     *
     * @param graduationDesignReleaseId    毕业设计发布oid
     * @param graduationDesignDatumGroupId 组内资料id
     * @param request                      请求
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/group/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun groupDel(@RequestParam("id") graduationDesignReleaseId: String,
                 @RequestParam("graduationDesignDatumGroupId") graduationDesignDatumGroupId: String, request: HttpServletRequest): AjaxUtils<FileBean> {
        val ajaxUtils = AjaxUtils.of<FileBean>()
        try {
            val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
            graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean)
            if (!errorBean.isHasError()) {
                val users = usersService.getUserFromSession()
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    val staff = staffService.findByUsername(users!!.username)
                    if (!ObjectUtils.isEmpty(staff)) {
                        val staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.staffId!!)
                        if (staffRecord.isPresent) {
                            val graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher::class.java)
                            val graduationDesignDatumGroup = graduationDesignDatumGroupService.findById(graduationDesignDatumGroupId)
                            if (!ObjectUtils.isEmpty(graduationDesignDatumGroup)) {
                                if (graduationDesignTeacher.graduationDesignTeacherId == graduationDesignDatumGroup.graduationDesignTeacherId) {
                                    val files = filesService.findById(graduationDesignDatumGroup.fileId)
                                    if (!ObjectUtils.isEmpty(files)) {
                                        FilesUtils.deleteFile(RequestUtils.getRealPath(request) + files.relativePath)
                                        graduationDesignDatumGroupService.delete(graduationDesignDatumGroup)
                                        filesService.deleteById(files.fileId)
                                        ajaxUtils.success().msg("删除文件成功")
                                    } else {
                                        ajaxUtils.fail().msg("未查询到文件信息")
                                    }
                                } else {
                                    ajaxUtils.fail().msg("该文件不属于您")
                                }
                            } else {
                                ajaxUtils.fail().msg("未查询到组内文件信息")
                            }
                        } else {
                            ajaxUtils.fail().msg("您不是该毕业设计指导教师")
                        }
                    } else {
                        ajaxUtils.fail().msg("未查询到教职工信息")
                    }
                } else {
                    ajaxUtils.fail().msg("您的注册类型不符合进入条件")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } catch (e: Exception) {
            log.error("Delete graduation design proposal file error, error is {}", e)
            ajaxUtils.fail().msg("删除文件异常")
        }

        return ajaxUtils
    }

    /**
     * 下载组内资料
     *
     * @param graduationDesignReleaseId    毕业设计发布oid
     * @param graduationDesignDatumGroupId 组内资料id
     * @param request                      请求
     * @param response                     响应
     */
    @RequestMapping("/web/graduate/design/proposal/group/download")
    @ResponseBody
    fun groupDownload(@RequestParam("id") graduationDesignReleaseId: String,
                      @RequestParam("graduationDesignDatumGroupId") graduationDesignDatumGroupId: String,
                      request: HttpServletRequest, response: HttpServletResponse) {
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            val users = usersService.getUserFromSession()
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
                if (studentRecord.isPresent) {
                    val student = studentRecord.get().into(Student::class.java)
                    if (!ObjectUtils.isEmpty(student)) {
                        val staffRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.studentId!!, graduationDesignReleaseId)
                        if (staffRecord.isPresent) {
                            val graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher::class.java)
                            val graduationDesignDatumGroup = graduationDesignDatumGroupService.findById(graduationDesignDatumGroupId)
                            groupDownloadBuild(graduationDesignTeacher, graduationDesignDatumGroup, request, response)
                        }
                    }
                }
            } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                val staff = staffService.findByUsername(users!!.username)
                if (!ObjectUtils.isEmpty(staff)) {
                    val staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.staffId!!)
                    if (staffRecord.isPresent) {
                        val graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher::class.java)
                        val graduationDesignDatumGroup = graduationDesignDatumGroupService.findById(graduationDesignDatumGroupId)
                        groupDownloadBuild(graduationDesignTeacher, graduationDesignDatumGroup, request, response)
                    }
                }
            }

        }
    }

    /**
     * 组内资料下载构建
     *
     * @param graduationDesignTeacher    指导教师信息
     * @param graduationDesignDatumGroup 组内资料信息
     * @param request                    请求
     * @param response                   响应
     */
    private fun groupDownloadBuild(graduationDesignTeacher: GraduationDesignTeacher, graduationDesignDatumGroup: GraduationDesignDatumGroup,
                                   request: HttpServletRequest, response: HttpServletResponse) {
        if (!ObjectUtils.isEmpty(graduationDesignDatumGroup)) {
            if (graduationDesignTeacher.graduationDesignTeacherId == graduationDesignDatumGroup.graduationDesignTeacherId) {
                val files = filesService.findById(graduationDesignDatumGroup.fileId)
                if (!ObjectUtils.isEmpty(files)) {
                    uploadService.download(files.originalFileName, files.relativePath, response, request)
                }
            }
        }
    }

    /**
     * 团队进入条件入口
     *
     * @param graduationDesignDatumId 毕业资料id
     * @param users                   用户信息
     * @return 毕业资料数据
     */
    private fun canUseCondition(graduationDesignDatumId: String, users: Users?): GraduationDesignDatum? {
        var graduationDesignDatum: GraduationDesignDatum? = null
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            graduationDesignDatum = graduationDesignDatumService.findById(graduationDesignDatumId)
        } else {
            // 学生
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val record = graduationDesignDatumService.findByIdRelation(graduationDesignDatumId)
                if (record.isPresent) {
                    val student = studentService.findByUsername(users!!.username)
                    val graduationDesignDatumBean = record.get().into(GraduationDesignDatumBean::class.java)

                    if (student.studentId == graduationDesignDatumBean.studentId) {
                        graduationDesignDatum = record.get().into(GraduationDesignDatum::class.java)
                    }
                }
            } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                val staffRecord = graduationDesignDatumService.findByIdRelation(graduationDesignDatumId)
                if (staffRecord.isPresent) {
                    val staff = staffService.findByUsername(users!!.username)
                    val graduationDesignDatumBean = staffRecord.get().into(GraduationDesignDatumBean::class.java)
                    if (staff.staffId == graduationDesignDatumBean.staffId) {
                        graduationDesignDatum = staffRecord.get().into(GraduationDesignDatum::class.java)
                    }
                }
            }
        }
        return graduationDesignDatum
    }

    /**
     * 我的资料页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/my/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            val errorBean = studentAccessCondition(graduationDesignReleaseId, student.studentId!!)
            if (!errorBean.isHasError()) {
                ajaxUtils.success().msg("在条件范围，允许使用")
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("目前仅提供学生使用")
        }
        return ajaxUtils
    }

    /**
     * 小组页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/team/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun teamCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
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
     * 组内资料页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/proposal/group/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun groupCondition(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = groupAccessCondition(graduationDesignReleaseId)
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
    @RequestMapping(value = ["/web/graduate/design/proposal/condition"], method = [(RequestMethod.POST)])
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
     * 进入学生资料条件
     *
     * @param graduationDesignReleaseId 发布
     * @param studentId                 学生id
     * @return true or false
     */
    private fun studentAccessCondition(graduationDesignReleaseId: String, studentId: Int): ErrorBean<GraduationDesignRelease> {
        val dataMap = HashMap<String, Any>()
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(studentId, graduationDesignReleaseId)
            if (record.isPresent) {
                val graduationDesignTutor = record.get().into(GraduationDesignTutor::class.java)
                dataMap["graduationDesignTutor"] = graduationDesignTutor
                errorBean.hasError = false
            } else {
                errorBean.hasError = true
                errorBean.errorMsg = "您不符合该毕业设计条件"
            }
            errorBean.mapData = dataMap
        }

        return errorBean
    }

    /**
     * 组内资料页面判断条件Id
     *
     * @param graduationDesignReleaseId 毕业设计发布
     * @return 条件
     */
    private fun groupAccessCondition(graduationDesignReleaseId: String): ErrorBean<GraduationDesignRelease> {
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            var canUse = false
            val users = usersService.getUserFromSession()
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
                if (studentRecord.isPresent) {
                    val student = studentRecord.get().into(Student::class.java)
                    if (!ObjectUtils.isEmpty(student)) {
                        val staffRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.studentId!!, graduationDesignReleaseId)
                        if (staffRecord.isPresent) {
                            canUse = true
                        }
                    }
                }
            } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                val staff = staffService.findByUsername(users!!.username)
                if (!ObjectUtils.isEmpty(staff)) {
                    val staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.staffId!!)
                    if (staffRecord.isPresent) {
                        canUse = true
                    }
                }
            }

            if (canUse) {
                errorBean.hasError = false
            } else {
                errorBean.hasError = true
                errorBean.errorMsg = "您不符合进入条件"
            }
        }
        return errorBean
    }
}