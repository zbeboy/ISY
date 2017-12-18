package top.zbeboy.isy.web.internship.release

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
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.common.CommonControllerMethodService
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.internship.InternshipFileService
import top.zbeboy.isy.service.internship.InternshipReleaseScienceService
import top.zbeboy.isy.service.internship.InternshipReleaseService
import top.zbeboy.isy.service.internship.InternshipTypeService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.file.FileBean
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.vo.internship.release.InternshipReleaseAddVo
import top.zbeboy.isy.web.vo.internship.release.InternshipReleaseUpdateVo
import java.sql.Timestamp
import java.text.ParseException
import java.time.Clock
import java.util.ArrayList
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-18 .
 **/
@Controller
open class InternshipReleaseController {

    private val log = LoggerFactory.getLogger(InternshipReleaseController::class.java)

    @Resource
    open lateinit var internshipTypeService: InternshipTypeService

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource
    open lateinit var internshipReleaseScienceService: InternshipReleaseScienceService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var internshipFileService: InternshipFileService


    /**
     * 实习发布数据
     *
     * @return 实习发布数据页面
     */
    @RequestMapping(value = ["/web/menu/internship/release"], method = [(RequestMethod.GET)])
    fun releaseData(): String {
        return "web/internship/release/internship_release::#page-wrapper"
    }

    /**
     * 获取实习发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/internship/release/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun releaseDatas(paginationUtils: PaginationUtils): AjaxUtils<InternshipReleaseBean> {
        val ajaxUtils = AjaxUtils.of<InternshipReleaseBean>()
        val internshipReleaseBean = InternshipReleaseBean()
        val commonData = methodControllerCommon.adminOrNormalData()
        internshipReleaseBean.departmentId = if (StringUtils.isEmpty(commonData["departmentId"])) -1 else commonData["departmentId"]
        internshipReleaseBean.collegeId = if (StringUtils.isEmpty(commonData["collegeId"])) -1 else commonData["collegeId"]
        val records = internshipReleaseService.findAllByPage(paginationUtils, internshipReleaseBean)
        val internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipReleaseBean)
        return ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils)
    }

    /**
     * 获取实习列表数据 用于实习教师分配等通用列表数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/anyone/internship/data"], method = [(RequestMethod.GET)])
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
        return ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils)
    }

    /**
     * 实习发布添加页面
     *
     * @param modelMap 页面对象
     * @return 实习发布添加页面
     */
    @RequestMapping(value = ["/web/internship/release/add"], method = [(RequestMethod.GET)])
    fun releaseAdd(modelMap: ModelMap): String {
        val commonData = methodControllerCommon.adminOrNormalData()
        modelMap.addAttribute("departmentId", if (StringUtils.isEmpty(commonData["departmentId"])) -1 else commonData["departmentId"])
        modelMap.addAttribute("collegeId", if (StringUtils.isEmpty(commonData["collegeId"])) -1 else commonData["collegeId"])
        return "web/internship/release/internship_release_add::#page-wrapper"
    }

    /**
     * 实习发布编辑页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 实习发布编辑页面
     */
    @RequestMapping(value = ["/web/internship/release/edit"], method = [(RequestMethod.GET)])
    fun releaseEdit(@RequestParam("id") internshipReleaseId: String, modelMap: ModelMap): String {
        val records = internshipReleaseService.findByIdRelation(internshipReleaseId)
        var internshipRelease = InternshipReleaseBean()
        var sciences: List<Science> = ArrayList()
        if (records.isPresent) {
            internshipRelease = records.get().into(InternshipReleaseBean::class.java)
            val recordResult = internshipReleaseScienceService.findByInternshipReleaseIdRelation(internshipRelease.internshipReleaseId)
            if (recordResult.isNotEmpty) {
                sciences = recordResult.into(Science::class.java)
            }
        }
        modelMap.addAttribute("internshipRelease", internshipRelease)
        modelMap.addAttribute("sciences", sciences)
        return "web/internship/release/internship_release_edit::#page-wrapper"
    }

    /**
     * 获取实习类型数据
     *
     * @return 实习类型数据
     */
    @RequestMapping(value = ["/user/internship/types"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipTypes(): AjaxUtils<InternshipType> {
        val ajaxUtils = AjaxUtils.of<InternshipType>()
        val internshipTypes = ArrayList<InternshipType>()
        val internshipType = InternshipType(0, "请选择实习类型")
        internshipTypes.add(internshipType)
        internshipTypes.addAll(internshipTypeService.findAll())
        return ajaxUtils.success().msg("获取实习类型数据成功").listData(internshipTypes)
    }

    /**
     * 获取实习附件数据
     *
     * @param internshipReleaseId 实习发布id
     * @return 实习附件数据
     */
    @RequestMapping(value = ["/user/internship/files"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipFiles(@RequestParam("internshipReleaseId") internshipReleaseId: String): AjaxUtils<Files> {
        val ajaxUtils = AjaxUtils.of<Files>()
        var files: List<Files> = ArrayList()
        val records = internshipFileService.findByInternshipReleaseId(internshipReleaseId)
        if (records.isNotEmpty) {
            files = records.into(Files::class.java)
        }
        return ajaxUtils.success().msg("获取实习附件数据成功").listData(files)
    }

    /**
     * 保存时检验标题
     *
     * @param title 标题
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/release/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("releaseTitle") title: String): AjaxUtils<*> {
        val releaseTitle = StringUtils.trimWhitespace(title)
        if (StringUtils.hasLength(releaseTitle)) {
            val internshipReleases = internshipReleaseService.findByReleaseTitle(releaseTitle)
            if (ObjectUtils.isEmpty(internshipReleases) && internshipReleases.isEmpty()) {
                return AjaxUtils.of<Any>().success().msg("标题不重复")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("标题重复")
    }

    /**
     * 更新时检验标题
     *
     * @param internshipReleaseId 实习发布id
     * @param title               标题
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/release/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("internshipReleaseId") internshipReleaseId: String, @RequestParam("releaseTitle") title: String): AjaxUtils<*> {
        val releaseTitle = StringUtils.trimWhitespace(title)
        if (StringUtils.hasLength(releaseTitle)) {
            val internshipReleases = internshipReleaseService.findByReleaseTitleNeInternshipReleaseId(releaseTitle, internshipReleaseId)
            if (ObjectUtils.isEmpty(internshipReleases) && internshipReleases.isEmpty()) {
                return AjaxUtils.of<Any>().success().msg("标题不重复")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("标题重复")
    }

    /**
     * 保存
     *
     * @param internshipReleaseAddVo 实习
     * @param bindingResult          检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/release/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun save(@Valid internshipReleaseAddVo: InternshipReleaseAddVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val internshipReleaseId = UUIDUtils.getUUID()
            val teacherDistributionTime = internshipReleaseAddVo.teacherDistributionTime
            val time = internshipReleaseAddVo.time
            val files = internshipReleaseAddVo.files
            val internshipRelease = InternshipRelease()
            internshipRelease.internshipReleaseId = internshipReleaseId
            internshipRelease.internshipTitle = internshipReleaseAddVo.releaseTitle
            internshipRelease.releaseTime = Timestamp(Clock.systemDefaultZone().millis())
            val users = usersService.getUserFromSession()
            internshipRelease.username = users!!.username
            saveOrUpdateTime(internshipRelease, teacherDistributionTime!!, time!!)
            internshipRelease.allowGrade = internshipReleaseAddVo.grade

            internshipRelease.departmentId = internshipReleaseAddVo.departmentId
            internshipRelease.internshipReleaseIsDel = internshipReleaseAddVo.internshipReleaseIsDel
            internshipRelease.internshipTypeId = internshipReleaseAddVo.internshipTypeId
            internshipReleaseService.save(internshipRelease)
            if (StringUtils.hasLength(internshipReleaseAddVo.scienceId)) {
                val scienceArr = internshipReleaseAddVo.scienceId!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (scienceId in scienceArr) {
                    internshipReleaseScienceService.save(internshipReleaseId, NumberUtils.toInt(scienceId))
                }
            }
            saveOrUpdateFiles(files, internshipReleaseId)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("保存失败")
    }

    /**
     * 更新
     *
     * @param internshipReleaseUpdateVo 实习
     * @param bindingResult             检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/release/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun update(@Valid internshipReleaseUpdateVo: InternshipReleaseUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val internshipReleaseId = internshipReleaseUpdateVo.internshipReleaseId
            val teacherDistributionTime = internshipReleaseUpdateVo.teacherDistributionTime
            val time = internshipReleaseUpdateVo.time
            val files = internshipReleaseUpdateVo.files
            val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
            internshipRelease.internshipTitle = internshipReleaseUpdateVo.releaseTitle
            saveOrUpdateTime(internshipRelease, teacherDistributionTime!!, time!!)
            internshipRelease.internshipReleaseIsDel = internshipReleaseUpdateVo.internshipReleaseIsDel
            internshipReleaseService.update(internshipRelease)
            val records = internshipFileService.findByInternshipReleaseId(internshipReleaseId)
            if (records.isNotEmpty) {
                internshipFileService.deleteByInternshipReleaseId(internshipReleaseId)
                val internshipFiles = records.into(InternshipFile::class.java)
                internshipFiles.forEach { f -> filesService.deleteById(f.fileId) }
            }
            saveOrUpdateFiles(files, internshipReleaseId!!)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("保存失败")
    }

    /**
     * 更新实习发布状态
     *
     * @param internshipReleaseId 实习发布id
     * @param isDel               注销参数
     * @return true or false
     */
    @RequestMapping(value = ["/web/internship/release/update/del"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateDel(@RequestParam("internshipReleaseId") internshipReleaseId: String, @RequestParam("isDel") isDel: Byte?): AjaxUtils<*> {
        val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
        internshipRelease.internshipReleaseIsDel = isDel
        internshipReleaseService.update(internshipRelease)
        return AjaxUtils.of<Any>().success().msg("更新状态成功")
    }

    /**
     * 更新或保存时间
     *
     * @param internshipRelease       实习
     * @param teacherDistributionTime 教师分配时间
     * @param time                    申请时间
     */
    private fun saveOrUpdateTime(internshipRelease: InternshipRelease, teacherDistributionTime: String, time: String) {
        try {
            val format = "yyyy-MM-dd HH:mm:ss"
            val teacherDistributionArr = DateTimeUtils.splitDateTime("至", teacherDistributionTime)
            internshipRelease.teacherDistributionStartTime = DateTimeUtils.formatDateToTimestamp(teacherDistributionArr[0], format)
            internshipRelease.teacherDistributionEndTime = DateTimeUtils.formatDateToTimestamp(teacherDistributionArr[1], format)
            val timeArr = DateTimeUtils.splitDateTime("至", time)
            internshipRelease.startTime = DateTimeUtils.formatDateToTimestamp(timeArr[0], format)
            internshipRelease.endTime = DateTimeUtils.formatDateToTimestamp(timeArr[1], format)
        } catch (e: ParseException) {
            log.error(" format time is exception.", e)
        }

    }

    /**
     * 更新或保存文件
     *
     * @param files               文件json
     * @param internshipReleaseId 实习id
     */
    private fun saveOrUpdateFiles(files: String?, internshipReleaseId: String) {
        if (StringUtils.hasLength(files)) {
            val filesList = JSON.parseArray(files, Files::class.java)
            for (f in filesList) {
                val fileId = UUIDUtils.getUUID()
                f.fileId = fileId
                filesService.save(f)
                val internshipFile = InternshipFile(internshipReleaseId, fileId)
                internshipFileService.save(internshipFile)
            }
        }
    }

    /**
     * 上传实习附件
     *
     * @param schoolId                    学校id
     * @param collegeId                   院id
     * @param departmentId                系id
     * @param multipartHttpServletRequest 文件请求
     * @return 文件信息
     */
    @RequestMapping("/anyone/users/upload/internship")
    @ResponseBody
    fun usersUploadInternship(schoolId: Int, collegeId: Int, @RequestParam("departmentId") departmentId: Int,
                              multipartHttpServletRequest: MultipartHttpServletRequest): AjaxUtils<FileBean> {
        val data = AjaxUtils.of<FileBean>()
        try {
            val path = Workbook.internshipPath(uploadService.schoolInfoPath(schoolId, collegeId, departmentId))
            val fileBeen = uploadService.upload(multipartHttpServletRequest,
                    RequestUtils.getRealPath(multipartHttpServletRequest) + path, multipartHttpServletRequest.remoteAddr)
            data.success().listData(fileBeen).obj(path)
        } catch (e: Exception) {
            log.error("Upload file exception,is {}", e)
        }

        return data
    }

    /**
     * 删除实习附件
     *
     * @param filePath            文件路径
     * @param fileId              文件id
     * @param internshipReleaseId 实习id
     * @param request             请求
     * @return true or false
     */
    @RequestMapping("/anyone/users/delete/file/internship")
    @ResponseBody
    fun deleteFileInternship(@RequestParam("filePath") filePath: String, @RequestParam("fileId") fileId: String,
                             @RequestParam("internshipReleaseId") internshipReleaseId: String, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (FilesUtils.deleteFile(RequestUtils.getRealPath(request) + filePath)) {
                internshipFileService.deleteByFileIdAndInternshipReleaseId(fileId, internshipReleaseId)
                filesService.deleteById(fileId)
                ajaxUtils.success().msg("删除文件成功")
            } else {
                ajaxUtils.fail().msg("删除文件失败")
            }
        } catch (e: Exception) {
            log.error("Delete file exception, is {}", e)
        }

        return ajaxUtils
    }
}