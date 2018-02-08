package top.zbeboy.isy.web.graduate.design.archives

import com.alibaba.fastjson.JSON
import org.joda.time.DateTime
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
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignArchives
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.export.GraduationDesignArchivesExport
import top.zbeboy.isy.service.graduate.design.GraduationDesignArchivesService
import top.zbeboy.isy.service.graduate.design.GraduationDesignDeclareDataService
import top.zbeboy.isy.service.graduate.design.GraduationDesignPresubjectService
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.export.ExportBean
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
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
open class GraduationDesignArchivesController {

    private val log = LoggerFactory.getLogger(GraduationDesignArchivesController::class.java)

    @Resource
    open lateinit var graduationDesignArchivesService: GraduationDesignArchivesService

    @Resource
    open lateinit var graduationDesignReleaseService: GraduationDesignReleaseService

    @Resource
    open lateinit var graduationDesignDeclareDataService: GraduationDesignDeclareDataService

    @Resource
    open lateinit var graduationDesignPresubjectService: GraduationDesignPresubjectService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon


    /**
     * 毕业设计归档
     *
     * @return 毕业设计归档页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/archives"], method = [(RequestMethod.GET)])
    fun manifest(): String {
        return "web/graduate/design/archives/design_archives::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/archives/design/data"], method = [(RequestMethod.GET)])
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
    @RequestMapping(value = ["/web/graduate/design/archives/list"], method = [(RequestMethod.GET)])
    fun list(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId)
        return if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
            modelMap.addAttribute("grade", graduationDesignRelease.allowGrade)
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            modelMap.addAttribute("curYear", DateTime.now().year)
            modelMap.addAttribute("upYear", DateTime.now().year - 1)
            "web/graduate/design/archives/design_archives_list::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "未查询到毕业设计相关信息")
        }
    }

    /**
     * 归档数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/archives/list/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun listData(request: HttpServletRequest): DataTablesUtils<GraduationDesignArchivesBean> {
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        var dataTablesUtils = DataTablesUtils.of<GraduationDesignArchivesBean>()
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            // 前台数据标题 注：要和前台标题顺序一致，获取order用
            val headers = ArrayList<String>()
            headers.add("college_name")
            headers.add("college_code")
            headers.add("science_name")
            headers.add("science_code")
            headers.add("graduation_date")
            headers.add("staff_name")
            headers.add("staff_number")
            headers.add("academic_title_name")
            headers.add("assistant_teacher")
            headers.add("assistant_teacher_number")
            headers.add("assistant_teacher_academic")
            headers.add("presubject_title")
            headers.add("subject_type_name")
            headers.add("origin_type_name")
            headers.add("student_number")
            headers.add("student_name")
            headers.add("score_type_name")
            headers.add("is_excellent")
            headers.add("archive_number")
            headers.add("note")
            headers.add("operator")
            dataTablesUtils = DataTablesUtils(request, headers)
            val otherCondition = GraduationDesignArchivesBean()
            otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
            val graduationDesignArchivesBeans = graduationDesignArchivesService.findAllByPage(dataTablesUtils, otherCondition)
            dataTablesUtils.data = graduationDesignArchivesBeans
            dataTablesUtils.setiTotalRecords(graduationDesignArchivesService.countAll(otherCondition).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignArchivesService.countByCondition(dataTablesUtils, otherCondition).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 导出 归档 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/web/graduate/design/archives/list/data/export"], method = [(RequestMethod.GET)])
    fun dataExport(request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
            if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
                val graduationDesignDeclareData = graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
                if (!ObjectUtils.isEmpty(graduationDesignDeclareData)) {
                    val year = graduationDesignDeclareData!!.graduationDate.substring(0, graduationDesignDeclareData.graduationDate.indexOf("年"))
                    var fileName: String? = graduationDesignDeclareData.scienceName + " " + year + "届毕业设计（论文）汇总清单"
                    var ext: String? = Workbook.XLSX_FILE
                    val exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean::class.java)

                    val extraSearchParam = request.getParameter("extra_search")
                    val dataTablesUtils = DataTablesUtils.of<GraduationDesignArchivesBean>()
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(extraSearchParam)) {
                        dataTablesUtils.search = JSON.parseObject(extraSearchParam)
                    }
                    val otherCondition = GraduationDesignArchivesBean()
                    otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
                    val graduationDesignArchivesBeans = graduationDesignArchivesService.exportData(dataTablesUtils, otherCondition)
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.fileName)) {
                        fileName = exportBean.fileName
                    }
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.ext)) {
                        ext = exportBean.ext
                    }
                    val graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId)
                    if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
                        val export = GraduationDesignArchivesExport(graduationDesignArchivesBeans)
                        val schoolInfoPath = cacheManageService.schoolInfoPath(graduationDesignRelease.departmentId!!)
                        val path = Workbook.graduateDesignPath(schoolInfoPath) + fileName + "." + ext
                        export.exportExcel(RequestUtils.getRealPath(request) + Workbook.graduateDesignPath(schoolInfoPath), fileName!!, ext!!)
                        uploadService.download(fileName, path, response, request)
                    }
                }
            }
        } catch (e: IOException) {
            log.error("Export file error, error is {}", e)
        }

    }

    /**
     * 生成档案号
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param archivesAffix             前缀
     * @param archivesStart             开始序号
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/archives/generate"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun archivesGenerate(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String,
                         @RequestParam("archivesAffix") archivesAffix: String,
                         @RequestParam("archivesStart") archivesStart: Int): AjaxUtils<*> {
        var tempArchivesStart = archivesStart
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val records = graduationDesignPresubjectService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            for (r in records) {
                val graduationDesignArchives = GraduationDesignArchives()
                graduationDesignArchives.graduationDesignPresubjectId = r.graduationDesignPresubjectId
                graduationDesignArchives.archiveNumber = archivesAffix + polishArchiveNumber(tempArchivesStart)
                graduationDesignArchives.isExcellent = 0
                graduationDesignArchivesService.saveAndIgnore(graduationDesignArchives)
                tempArchivesStart++
            }
            ajaxUtils.success().msg("生成档案号成功")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 补0操作
     *
     * @param sort 序号
     * @return 档案编号
     */
    private fun polishArchiveNumber(sort: Int): String {
        return when {
            sort < 10 -> "00" + sort
            sort < 100 -> "0" + sort
            else -> sort.toString() + ""
        }
    }

    /**
     * 设置百优
     *
     * @param graduationDesignReleaseId    毕业设计发布id
     * @param graduationDesignPresubjectId 毕业设计题目id
     * @param excellent                    百优
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/archives/excellent"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun archivesExcellent(@RequestParam("id") graduationDesignReleaseId: String,
                          @RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String,
                          @RequestParam("excellent") excellent: Byte?): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val record = graduationDesignArchivesService.findByGraduationDesignPresubjectId(graduationDesignPresubjectId)
            if (!ObjectUtils.isEmpty(record)) {
                val graduationDesignArchives = record.into(GraduationDesignArchives::class.java)
                graduationDesignArchives.isExcellent = excellent
                graduationDesignArchivesService.update(graduationDesignArchives)
                ajaxUtils.success().msg("更新成功")
            } else {
                ajaxUtils.fail().msg("未查询到相关档案信息")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 获取档案信息
     *
     * @param graduationDesignPresubjectId 毕业设计题目id
     * @return 信息
     */
    @RequestMapping(value = ["/web/graduate/design/archives/info"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun archivesInfo(@RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val record = graduationDesignArchivesService.findByGraduationDesignPresubjectId(graduationDesignPresubjectId)
        if (!ObjectUtils.isEmpty(record)) {
            val graduationDesignArchives = record.into(GraduationDesignArchives::class.java)
            ajaxUtils.success().msg("获取档案信息成功").obj(graduationDesignArchives)
        } else {
            ajaxUtils.fail().msg("未查询到相关档案信息")
        }
        return ajaxUtils
    }

    /**
     * 检验档案号
     *
     * @param archiveNumber 档案号
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/archives/valid/number"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun archivesValidNumber(@RequestParam("archiveNumber") archiveNumber: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val record = graduationDesignArchivesService.findByArchiveNumber(StringUtils.trimWhitespace(archiveNumber))
        if (!ObjectUtils.isEmpty(record)) {
            ajaxUtils.fail().msg("该档案号已被使用")
        } else {
            ajaxUtils.success().msg("允许使用")
        }
        return ajaxUtils
    }

    /**
     * 更改档案号
     *
     * @param archiveNumber                档案号
     * @param graduationDesignReleaseId    毕业设计发布id
     * @param graduationDesignPresubjectId 毕业设计题目id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/archives/number"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun archivesNumber(@RequestParam("archiveNumber") archiveNumber: String,
                       @RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String,
                       @RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val tempArchiveNumber = StringUtils.trimWhitespace(archiveNumber)
            val record = graduationDesignArchivesService.findByArchiveNumber(tempArchiveNumber)
            if (ObjectUtils.isEmpty(record)) {
                val realRecord = graduationDesignArchivesService.findByGraduationDesignPresubjectId(graduationDesignPresubjectId)
                val graduationDesignArchives: GraduationDesignArchives
                if (!ObjectUtils.isEmpty(realRecord)) {
                    graduationDesignArchives = realRecord.into(GraduationDesignArchives::class.java)
                    graduationDesignArchives.archiveNumber = archiveNumber
                    graduationDesignArchivesService.update(graduationDesignArchives)
                } else {
                    graduationDesignArchives = GraduationDesignArchives()
                    graduationDesignArchives.graduationDesignPresubjectId = graduationDesignPresubjectId
                    graduationDesignArchives.isExcellent = 0
                    graduationDesignArchives.archiveNumber = archiveNumber
                    graduationDesignArchivesService.save(graduationDesignArchives)
                }

                ajaxUtils.success().msg("更改档案号成功")
            } else {
                ajaxUtils.fail().msg("该档案号已被使用")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 更新备注
     *
     * @param graduationDesignReleaseId    毕业设计发布id
     * @param graduationDesignPresubjectId 毕业题目id
     * @param note                         备注
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/archives/note"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun archivesNote(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String,
                     @RequestParam("graduationDesignPresubjectId") graduationDesignPresubjectId: String,
                     @RequestParam("note") note: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val record = graduationDesignArchivesService.findByGraduationDesignPresubjectId(graduationDesignPresubjectId)
            if (!ObjectUtils.isEmpty(record)) {
                val graduationDesignArchives = record.into(GraduationDesignArchives::class.java)
                graduationDesignArchives.note = note
                graduationDesignArchivesService.update(graduationDesignArchives)
                ajaxUtils.success().msg("更新成功")
            } else {
                ajaxUtils.fail().msg("未查询到相关档案信息")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }
}