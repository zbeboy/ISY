package top.zbeboy.isy.web.internship.common

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.internship.InternshipFileService
import top.zbeboy.isy.service.internship.InternshipReleaseService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
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
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var internshipFileService: InternshipFileService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var roleService: RoleService

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

    /**
     * 仅允许学生本人操作
     *
     * @param studentId 学生id
     * @return 是否可操作
     */
    fun onlySelfStudentOperate(studentId: Int): Boolean {
        // 强制身份判断
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                val users = usersService.getUserFromSession()
                val student = studentService.findByUsername(users!!.getUsername())
                return ObjectUtils.isEmpty(student) || student.getStudentId() == studentId
            }
        }
        return true
    }
}