package top.zbeboy.isy.web.graduate.design.common

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher
import top.zbeboy.isy.domain.tables.pojos.Student
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseFileService
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.file.FileBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2018-01-14 .
 **/
@Component
open class GraduationDesignMethodControllerCommon {

    private val log = LoggerFactory.getLogger(GraduationDesignMethodControllerCommon::class.java)

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var graduationDesignReleaseService: GraduationDesignReleaseService

    @Resource
    open lateinit var graduationDesignReleaseFileService: GraduationDesignReleaseFileService

    @Resource
    open lateinit var filesService: FilesService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var graduationDesignTutorService: GraduationDesignTutorService

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    /**
     * 获取毕业设计发布数据 用于通用列表数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    fun graduationDesignListDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignReleaseBean>()
        val graduationDesignReleaseBean = GraduationDesignReleaseBean()
        graduationDesignReleaseBean.graduationDesignIsDel = 0
        val commonData = methodControllerCommon.adminOrNormalData()
        graduationDesignReleaseBean.departmentId = if (StringUtils.isEmpty(commonData["departmentId"])) -1 else commonData["departmentId"]
        graduationDesignReleaseBean.collegeId = if (StringUtils.isEmpty(commonData["collegeId"])) -1 else commonData["collegeId"]
        graduationDesignReleaseBean.scienceId = if (StringUtils.isEmpty(commonData["scienceId"])) -1 else commonData["scienceId"]
        graduationDesignReleaseBean.allowGrade = if (StringUtils.isEmpty(commonData["grade"])) null else commonData["grade"].toString()
        val records = graduationDesignReleaseService.findAllByPage(paginationUtils, graduationDesignReleaseBean)
        val graduationDesignReleaseBeens = graduationDesignReleaseService.dealData(paginationUtils, records, graduationDesignReleaseBean)
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignReleaseBeens).paginationUtils(paginationUtils)
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
    fun deleteFileGraduateDesign(filePath: String, fileId: String, graduationDesignReleaseId: String, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (FilesUtils.deleteFile(RequestUtils.getRealPath(request) + filePath)) {
                graduationDesignReleaseFileService.deleteByFileIdAndGraduationDesignReleaseId(fileId, graduationDesignReleaseId)
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
     * 设置教职工id和学生id
     *
     * @param modelMap 页面对象
     */
    fun setStaffIdAndStudentId(modelMap: ModelMap, graduationDesignRelease: GraduationDesignRelease) {
        val users = usersService.getUserFromSession()
        var hasValue = false
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.getUsername(), graduationDesignRelease.scienceId!!, graduationDesignRelease.allowGrade)
            if (studentRecord.isPresent()) {
                val student = studentRecord.get().into(Student::class.java)
                if (!ObjectUtils.isEmpty(student)) {
                    val staffRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.getStudentId()!!, graduationDesignRelease.graduationDesignReleaseId)
                    if (staffRecord.isPresent()) {
                        val graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher::class.java)
                        modelMap.addAttribute("studentId", student.getStudentId())
                        modelMap.addAttribute("staffId", graduationDesignTeacher.getStaffId())
                        hasValue = true
                    }
                }
            }
        } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            val staff = staffService.findByUsername(users!!.getUsername())
            if (!ObjectUtils.isEmpty(staff)) {
                val staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignRelease.graduationDesignReleaseId, staff.getStaffId()!!)
                if (staffRecord.isPresent()) {
                    modelMap.addAttribute("studentId", 0)
                    modelMap.addAttribute("staffId", staff.getStaffId())
                    hasValue = true
                }
            }
        }

        if (!hasValue) {
            modelMap.addAttribute("studentId", 0)
            modelMap.addAttribute("staffId", 0)
        }
    }
}