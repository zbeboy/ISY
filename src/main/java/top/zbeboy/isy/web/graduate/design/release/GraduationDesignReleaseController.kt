package top.zbeboy.isy.web.graduate.design.release

import com.alibaba.fastjson.JSON
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
import top.zbeboy.isy.domain.tables.pojos.Files
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignReleaseFile
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseFileService
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.file.FileBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.vo.graduate.design.release.GraduationDesignReleaseAddVo
import top.zbeboy.isy.web.vo.graduate.design.release.GraduationDesignReleaseUpdateVo
import java.sql.Timestamp
import java.text.ParseException
import java.time.Clock
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2018-01-14 .
 **/
@Controller
open class GraduationDesignReleaseController {

    private val log = LoggerFactory.getLogger(GraduationDesignReleaseController::class.java)

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var graduationDesignReleaseService: GraduationDesignReleaseService

    @Resource
    open lateinit var graduationDesignReleaseFileService: GraduationDesignReleaseFileService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon


    /**
     * 毕业设计发布
     *
     * @return 毕业设计发布页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/release"], method = [(RequestMethod.GET)])
    fun releaseData(): String {
        return "web/graduate/design/release/design_release::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/release/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun releaseDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignReleaseBean>()
        val graduationDesignReleaseBean = GraduationDesignReleaseBean()
        val commonData = methodControllerCommon.adminOrNormalData()
        graduationDesignReleaseBean.departmentId = if (StringUtils.isEmpty(commonData["departmentId"])) -1 else commonData["departmentId"]
        graduationDesignReleaseBean.collegeId = if (StringUtils.isEmpty(commonData["collegeId"])) -1 else commonData["collegeId"]
        val records = graduationDesignReleaseService.findAllByPage(paginationUtils, graduationDesignReleaseBean)
        val graduationDesignReleaseBeans = graduationDesignReleaseService.dealData(paginationUtils, records, graduationDesignReleaseBean)
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignReleaseBeans).paginationUtils(paginationUtils)
    }

    /**
     * 毕业设计发布添加页面
     *
     * @param modelMap 页面对象
     * @return 毕业设计发布添加页面
     */
    @RequestMapping(value = ["/web/graduate/design/release/add"], method = [(RequestMethod.GET)])
    fun releaseAdd(modelMap: ModelMap): String {
        val commonData = methodControllerCommon.adminOrNormalData()
        modelMap.addAttribute("departmentId", if (StringUtils.isEmpty(commonData["departmentId"])) -1 else commonData["departmentId"])
        modelMap.addAttribute("collegeId", if (StringUtils.isEmpty(commonData["collegeId"])) -1 else commonData["collegeId"])
        return "web/graduate/design/release/design_release_add::#page-wrapper"
    }

    /**
     * 毕业设计发布编辑页面
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 毕业设计发布编辑页面
     */
    @RequestMapping(value = ["/web/graduate/design/release/edit"], method = [(RequestMethod.GET)])
    fun releaseEdit(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val records = graduationDesignReleaseService.findByIdRelation(graduationDesignReleaseId)
        var graduationDesignRelease = GraduationDesignReleaseBean()
        if (records.isPresent) {
            graduationDesignRelease = records.get().into(GraduationDesignReleaseBean::class.java)
            graduationDesignRelease.startTimeStr = DateTimeUtils.formatDate(graduationDesignRelease.startTime)
            graduationDesignRelease.endTimeStr = DateTimeUtils.formatDate(graduationDesignRelease.endTime)
            graduationDesignRelease.fillTeacherStartTimeStr = DateTimeUtils.formatDate(graduationDesignRelease.fillTeacherStartTime)
            graduationDesignRelease.fillTeacherEndTimeStr = DateTimeUtils.formatDate(graduationDesignRelease.fillTeacherEndTime)
        }
        modelMap.addAttribute("graduationDesignRelease", graduationDesignRelease)
        return "web/graduate/design/release/design_release_edit::#page-wrapper"
    }

    /**
     * 保存时检验标题
     *
     * @param title 标题
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/release/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("graduationDesignTitle") title: String): AjaxUtils<*> {
        val graduationDesignTitle = StringUtils.trimWhitespace(title)
        if (StringUtils.hasLength(graduationDesignTitle)) {
            val graduationDesignReleases = graduationDesignReleaseService.findByGraduationDesignTitle(graduationDesignTitle)
            if (ObjectUtils.isEmpty(graduationDesignReleases) && graduationDesignReleases.isEmpty()) {
                return AjaxUtils.of<Any>().success().msg("标题不重复")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("标题重复")
    }

    /**
     * 更新时检验标题
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param title                     标题
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/release/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String, @RequestParam("graduationDesignTitle") title: String): AjaxUtils<*> {
        val releaseTitle = StringUtils.trimWhitespace(title)
        if (StringUtils.hasLength(releaseTitle)) {
            val graduationDesignReleaseRecords = graduationDesignReleaseService.findByGraduationDesignTitleNeGraduationDesignReleaseId(releaseTitle, graduationDesignReleaseId)
            if (ObjectUtils.isEmpty(graduationDesignReleaseRecords) && graduationDesignReleaseRecords.isEmpty()) {
                return AjaxUtils.of<Any>().success().msg("标题不重复")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("标题重复")
    }

    /**
     * 保存
     *
     * @param graduationDesignReleaseAddVo 毕业设计
     * @param bindingResult                检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/release/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun save(@Valid graduationDesignReleaseAddVo: GraduationDesignReleaseAddVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val graduationDesignReleaseId = UUIDUtils.getUUID()
            val fillTeacherTime = graduationDesignReleaseAddVo.fillTeacherTime
            val files = graduationDesignReleaseAddVo.files
            val graduationDesignRelease = GraduationDesignRelease()
            graduationDesignRelease.graduationDesignReleaseId = graduationDesignReleaseId
            graduationDesignRelease.graduationDesignTitle = graduationDesignReleaseAddVo.graduationDesignTitle
            graduationDesignRelease.releaseTime = Timestamp(Clock.systemDefaultZone().millis())
            val users = usersService.getUserFromSession()
            graduationDesignRelease.username = users!!.username
            graduationDesignRelease.publisher = users.realName
            saveOrUpdateTime(graduationDesignRelease, graduationDesignReleaseAddVo.startTime!!, graduationDesignReleaseAddVo.endTime!!, fillTeacherTime!!)
            graduationDesignRelease.allowGrade = graduationDesignReleaseAddVo.grade
            graduationDesignRelease.departmentId = graduationDesignReleaseAddVo.departmentId
            graduationDesignRelease.scienceId = graduationDesignReleaseAddVo.scienceId
            graduationDesignRelease.graduationDesignIsDel = graduationDesignReleaseAddVo.graduationDesignIsDel
            graduationDesignReleaseService.save(graduationDesignRelease)
            saveOrUpdateFiles(files, graduationDesignReleaseId)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("保存失败")
    }

    /**
     * 更新
     *
     * @param graduationDesignReleaseUpdateVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/release/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun update(@Valid graduationDesignReleaseUpdateVo: GraduationDesignReleaseUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val graduationDesignReleaseId = graduationDesignReleaseUpdateVo.graduationDesignReleaseId
            val fillTeacherTime = graduationDesignReleaseUpdateVo.fillTeacherTime
            val files = graduationDesignReleaseUpdateVo.files
            val graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId!!)
            graduationDesignRelease.graduationDesignTitle = graduationDesignReleaseUpdateVo.graduationDesignTitle
            saveOrUpdateTime(graduationDesignRelease, graduationDesignReleaseUpdateVo.startTime!!, graduationDesignReleaseUpdateVo.endTime!!, fillTeacherTime!!)
            graduationDesignRelease.graduationDesignIsDel = graduationDesignReleaseUpdateVo.graduationDesignIsDel
            graduationDesignReleaseService.update(graduationDesignRelease)
            val records = graduationDesignReleaseFileService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
            if (records.isNotEmpty) {
                graduationDesignReleaseFileService.deleteByGraduationDesignReleaseId(graduationDesignReleaseId)
                val graduationDesignReleaseFiles = records.into(GraduationDesignReleaseFile::class.java)
                graduationDesignReleaseFiles.forEach { f -> filesService.deleteById(f.fileId) }
            }
            saveOrUpdateFiles(files, graduationDesignReleaseId)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("保存失败")
    }

    /**
     * 更新毕业设计发布状态
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param isDel                     注销参数
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/release/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateDel(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String, @RequestParam("isDel") isDel: Byte?): AjaxUtils<*> {
        val graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId)
        graduationDesignRelease.graduationDesignIsDel = isDel
        graduationDesignReleaseService.update(graduationDesignRelease)
        return AjaxUtils.of<Any>().success().msg("更新状态成功")
    }

    /**
     * 更新或保存时间
     *
     * @param graduationDesignRelease 实习
     * @param startTime               开始时间
     * @param endTime                 结束时间
     * @param fillTeacherTime         学生申报教师时间
     */
    private fun saveOrUpdateTime(graduationDesignRelease: GraduationDesignRelease, startTime: String, endTime: String, fillTeacherTime: String) {
        try {
            val format = "yyyy-MM-dd HH:mm:ss"
            // 确认后不允许编辑
            if (ObjectUtils.isEmpty(graduationDesignRelease.isOkTeacher) || graduationDesignRelease.isOkTeacher != 1.toByte()) {
                val teacherDistributionArr = DateTimeUtils.splitDateTime("至", fillTeacherTime)
                graduationDesignRelease.fillTeacherStartTime = DateTimeUtils.formatDateToTimestamp(teacherDistributionArr[0], format)
                graduationDesignRelease.fillTeacherEndTime = DateTimeUtils.formatDateToTimestamp(teacherDistributionArr[1], format)
            }
            graduationDesignRelease.startTime = DateTimeUtils.formatDateToTimestamp(startTime, format)
            graduationDesignRelease.endTime = DateTimeUtils.formatDateToTimestamp(endTime, format)
        } catch (e: ParseException) {
            log.error(" format time is exception.", e)
        }

    }

    /**
     * 更新或保存文件
     *
     * @param files                     文件json
     * @param graduationDesignReleaseId 毕业设计id
     */
    private fun saveOrUpdateFiles(files: String?, graduationDesignReleaseId: String) {
        if (StringUtils.hasLength(files)) {
            val filesList = JSON.parseArray(files, Files::class.java)
            for (f in filesList) {
                val fileId = UUIDUtils.getUUID()
                f.fileId = fileId
                filesService.save(f)
                val graduationDesignReleaseFile = GraduationDesignReleaseFile(graduationDesignReleaseId, fileId)
                graduationDesignReleaseFileService.save(graduationDesignReleaseFile)
            }
        }
    }

    /**
     * 上传毕业设计附件
     *
     * @param schoolId                    学校id
     * @param collegeId                   院id
     * @param departmentId                系id
     * @param multipartHttpServletRequest 文件请求
     * @return 文件信息
     */
    @RequestMapping("/web/graduate/design/release/upload/file/design")
    @ResponseBody
    fun uploadFileGraduateDesign(schoolId: Int?, collegeId: Int?, @RequestParam("departmentId") departmentId: Int,
                                 multipartHttpServletRequest: MultipartHttpServletRequest): AjaxUtils<FileBean> {
        return methodControllerCommon.uploadFile(schoolId, collegeId, departmentId, multipartHttpServletRequest)
    }

    /**
     * 删除附件
     *
     * @param filePath 文件路径
     * @param request  请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/release/delete/file")
    @ResponseBody
    fun deleteFile(@RequestParam("filePath") filePath: String, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (methodControllerCommon.deleteFile(filePath, request)) {
            ajaxUtils.success().msg("删除文件成功")
        } else {
            ajaxUtils.fail().msg("删除文件失败")
        }
        return ajaxUtils
    }

    /**
     * 删除毕业设计附件
     *
     * @param filePath                  文件路径
     * @param fileId                    文件id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param request                   请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/release/delete/file/design")
    @ResponseBody
    fun deleteFileGraduateDesign(@RequestParam("filePath") filePath: String, @RequestParam("fileId") fileId: String,
                                 @RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String, request: HttpServletRequest): AjaxUtils<*> {
        return graduationDesignMethodControllerCommon.deleteFileGraduateDesign(filePath, fileId, graduationDesignReleaseId, request)
    }
}