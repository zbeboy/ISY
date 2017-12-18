package top.zbeboy.isy.web.internship.common

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.internship.InternshipFileService
import top.zbeboy.isy.service.internship.InternshipReleaseService
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.file.FileBean
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-12-18 .
 **/
@Component
open class InternshipMethodControllerCommon {

    private val log = LoggerFactory.getLogger(InternshipMethodControllerCommon::class.java)

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource
    open lateinit var uploadService: UploadService

    @Resource
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var internshipFileService: InternshipFileService

    /**
     * 获取实习列表数据 用于实习教师分配等通用列表数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
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
     * 上传实习附件
     *
     * @param schoolId                    学校id
     * @param collegeId                   院id
     * @param departmentId                系id
     * @param multipartHttpServletRequest 文件请求
     * @return 文件信息
     */
    fun uploadFileInternship(schoolId: Int, collegeId: Int, departmentId: Int,
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
    fun deleteFileInternship(filePath: String, fileId: String, internshipReleaseId: String, request: HttpServletRequest): AjaxUtils<*> {
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