package top.zbeboy.isy.web.graduate.design.manifest

import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.math.NumberUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.export.GraduationDesignManifestExport
import top.zbeboy.isy.service.graduate.design.DefenseOrderService
import top.zbeboy.isy.service.graduate.design.GraduationDesignDeclareDataService
import top.zbeboy.isy.service.graduate.design.GraduationDesignManifestService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.export.ExportBean
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.common.PageParamControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import java.io.IOException
import java.util.ArrayList
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2018-02-08 .
 **/
@Controller
open class GraduationDesignManifestController {

    private val log = LoggerFactory.getLogger(GraduationDesignManifestController::class.java)

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var graduationDesignManifestService: GraduationDesignManifestService

    @Resource
    open lateinit var graduationDesignDeclareDataService: GraduationDesignDeclareDataService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var defenseOrderService: DefenseOrderService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon

    @Resource
    open lateinit var pageParamControllerCommon: PageParamControllerCommon

    /**
     * 毕业设计清单
     *
     * @return 毕业设计清单页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/manifest"], method = [(RequestMethod.GET)])
    fun manifest(): String {
        return "web/graduate/design/manifest/design_manifest::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/manifest/design/data"], method = [(RequestMethod.GET)])
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
    @RequestMapping(value = ["/web/graduate/design/manifest/list"], method = [(RequestMethod.GET)])
    fun list(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val users = usersService.getUserFromSession()
        var hasValue = false
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            val staff = staffService.findByUsername(users!!.username)
            if (!ObjectUtils.isEmpty(staff)) {
                val staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.staffId!!)
                if (staffRecord.isPresent) {
                    modelMap.addAttribute("staffId", staff.staffId)
                    hasValue = true
                }
            }
        }
        if (!hasValue) {
            modelMap.addAttribute("staffId", 0)
        }
        pageParamControllerCommon.currentUserRoleNameAndTypeNamePageParam(modelMap)
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
        return "web/graduate/design/manifest/design_manifest_list::#page-wrapper"
    }

    /**
     * 清单数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/manifest/list/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun listData(request: HttpServletRequest): DataTablesUtils<GraduationDesignDeclareBean> {
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        var dataTablesUtils = DataTablesUtils.of<GraduationDesignDeclareBean>()
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            // 前台数据标题 注：要和前台标题顺序一致，获取order用
            val headers = ArrayList<String>()
            headers.add("presubject_title")
            headers.add("subject_type_name")
            headers.add("origin_type_name")
            headers.add("guide_teacher")
            headers.add("academic_title_name")
            headers.add("guide_peoples")
            headers.add("student_number")
            headers.add("student_name")
            headers.add("score_type_name")
            headers.add("operator")
            dataTablesUtils = DataTablesUtils(request, headers)
            val otherCondition = GraduationDesignDeclareBean()
            val staffId = NumberUtils.toInt(request.getParameter("staffId"))
            otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
            otherCondition.staffId = staffId
            val graduationDesignDeclareBeens = graduationDesignManifestService.findAllManifestByPage(dataTablesUtils, otherCondition)
            dataTablesUtils.data = graduationDesignDeclareBeens
            dataTablesUtils.setiTotalRecords(graduationDesignManifestService.countAllManifest(otherCondition).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignManifestService.countManifestByCondition(dataTablesUtils, otherCondition).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 导出 清单 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/graduate/design/manifest/list/data/export"], method = [(RequestMethod.GET)])
    fun listDataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
            val staffId = NumberUtils.toInt(request.getParameter("staffId"))
            if (!ObjectUtils.isEmpty(graduationDesignReleaseId) && staffId > 0) {
                val staffRecord = staffService.findByIdRelation(staffId)
                if (staffRecord.isPresent) {
                    val users = staffRecord.get().into(Users::class.java)
                    val graduationDesignDeclareData = graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
                    if (!ObjectUtils.isEmpty(graduationDesignDeclareData)) {
                        val year = graduationDesignDeclareData!!.graduationDate.substring(0, graduationDesignDeclareData.graduationDate.indexOf("年"))
                        var fileName: String? = users.realName + "_ " + year + "届" + graduationDesignDeclareData.departmentName + "毕业设计归档清单"
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
                        val graduationDesignDeclareBeens = graduationDesignManifestService.exportManifestData(dataTablesUtils, otherCondition)
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.fileName)) {
                            fileName = exportBean.fileName
                        }
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.ext)) {
                            ext = exportBean.ext
                        }
                        val export = GraduationDesignManifestExport(graduationDesignDeclareBeens)
                        val path = Workbook.graduationDesignManifestPath(users) + fileName + "." + ext
                        export.exportExcel(RequestUtils.getRealPath(request) + Workbook.graduationDesignManifestPath(users), fileName!!, ext!!)
                        uploadService.download(fileName, path, response, request)
                    }
                }
            }
        } catch (e: IOException) {
            log.error("Export file error, error is {}", e)
        }

    }

    /**
     * 成绩信息
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseOrderId            毕业设计答辩顺序id
     * @return 成绩
     */
    @RequestMapping(value = ["/web/graduate/design/manifest/mark/info"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun markInfo(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String,
                 @RequestParam("defenseOrderId") defenseOrderId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val defenseOrder = defenseOrderService.findById(defenseOrderId)
            if (!ObjectUtils.isEmpty(defenseOrder)) {
                ajaxUtils.success().msg("获取数据成功！").obj(defenseOrder)
            } else {
                ajaxUtils.fail().msg("未获取到相关顺序")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 修改成绩
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseOrderId            毕业设计答辩顺序id
     * @param scoreTypeId               成绩类型id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/manifest/mark"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun mark(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String,
             @RequestParam("defenseOrderId") defenseOrderId: String,
             @RequestParam("scoreTypeId") scoreTypeId: Int, @RequestParam("staffId") staffId: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            var canUse = false
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                canUse = true
            } else {
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    val users = usersService.getUserFromSession()
                    val staff = staffService.findByUsername(users!!.username)
                    canUse = !ObjectUtils.isEmpty(staff) && staff.staffId == staffId
                }
            }
            if (canUse) {
                val defenseOrder = defenseOrderService.findById(defenseOrderId)
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    defenseOrder.scoreTypeId = scoreTypeId
                    defenseOrderService.update(defenseOrder)
                    ajaxUtils.success().msg("修改成绩成功")
                } else {
                    ajaxUtils.fail().msg("未获取到相关顺序")
                }
            } else {
                ajaxUtils.fail().msg("您不符合编辑条件")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }
}