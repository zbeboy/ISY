package top.zbeboy.isy.web.common

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.BuildingService
import top.zbeboy.isy.service.data.DepartmentService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.MailService
import top.zbeboy.isy.service.system.SystemAlertService
import top.zbeboy.isy.service.system.SystemMessageService
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import java.io.IOException
import java.sql.Timestamp
import java.time.Clock
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-12-07 .
 **/
@Component
open class MethodControllerCommon {

    private val log = LoggerFactory.getLogger(MethodControllerCommon::class.java)

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var departmentService: DepartmentService

    @Resource
    open lateinit var buildingService: BuildingService

    @Autowired
    open lateinit var isyProperties: ISYProperties

    @Autowired
    open lateinit var requestUtils: RequestUtils

    @Resource
    open lateinit var mailService: MailService

    @Resource
    open lateinit var systemAlertService: SystemAlertService

    @Resource
    open lateinit var systemMessageService: SystemMessageService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    lateinit open var uploadService: UploadService

    @Resource
    lateinit open var filesService: FilesService

    /**
     * 通过毕业设计发布 生成楼数据
     *
     * @param graduationDesignRelease 毕业设计发布
     * @return 楼
     */
    fun generateBuildFromGraduationDesignRelease(graduationDesignRelease: GraduationDesignRelease): List<Building> {
        val buildings = ArrayList<Building>()
        val isDel: Byte = 0
        val building = Building(0, "请选择楼", isDel, 0)
        buildings.add(building)
        val record = departmentService.findByIdRelation(graduationDesignRelease.departmentId!!)
        if (record.isPresent) {
            val college = record.get().into(College::class.java)
            val buildingRecords = buildingService.findByCollegeIdAndIsDel(college.collegeId!!, isDel)
            buildingRecords.mapTo(buildings) { Building(it.buildingId, it.buildingName, it.buildingIsDel, it.collegeId) }
        }
        return buildings
    }

    /**
     * 组装提示信息
     *
     * @param modelMap 页面对象
     * @param tip      提示内容
     */
    fun showTip(modelMap: ModelMap, tip: String): String {
        modelMap.addAttribute("showTip", true)
        modelMap.addAttribute("tip", tip)
        modelMap.addAttribute("showButton", true)
        modelMap.addAttribute("buttonText", "返回上一页")
        return Workbook.TIP_PAGE
    }

    /**
     * 如果是管理员则获取院id，如果是普通学生或教职工角色则获取系id
     *
     * @return 根据角色返回相应数据
     */
    fun adminOrNormalData(): Map<String, Int> {
        val map = HashMap<String, Int>()
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            val users = usersService.getUserFromSession()
            val collegeId = cacheManageService.getRoleCollegeId(users!!)
            map.put("collegeId", collegeId)
        } else if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            val users = usersService.getUserFromSession()
            val departmentId = cacheManageService.getRoleDepartmentId(users!!)
            map.put("departmentId", departmentId)
        }
        return map
    }

    /**
     * 发送邮件通知
     *
     * @param users        接收者
     * @param curUsers     发送者
     * @param messageTitle 消息标题
     * @param notify       通知内容
     */
    fun sendNotify(users: Users, curUsers: Users, messageTitle: String, notify: String, request: HttpServletRequest) {
        //发送验证邮件
        if (isyProperties.getMail().isOpen) {
            mailService.sendNotifyMail(users, requestUtils.getBaseUrl(request), notify)
        }

        // 保存消息
        val systemMessage = SystemMessage()
        val messageId = UUIDUtils.getUUID()
        val isSee: Byte = 0
        val now = Timestamp(Clock.systemDefaultZone().millis())
        systemMessage.systemMessageId = messageId
        systemMessage.acceptUsers = users.username
        systemMessage.isSee = isSee
        systemMessage.sendUsers = curUsers.username
        systemMessage.messageTitle = messageTitle
        systemMessage.messageContent = notify
        systemMessage.messageDate = now
        systemMessageService.save(systemMessage)
        // 保存提醒
        val systemAlert = SystemAlert()
        val systemAlertType = cacheManageService.findBySystemAlertTypeName(Workbook.ALERT_MESSAGE_TYPE)
        systemAlert.systemAlertId = UUIDUtils.getUUID()
        systemAlert.isSee = isSee
        systemAlert.alertContent = "新消息"
        systemAlert.linkId = messageId
        systemAlert.systemAlertTypeId = systemAlertType.systemAlertTypeId
        systemAlert.username = users.username
        systemAlert.alertDate = now
        systemAlertService.save(systemAlert)
    }

    /**
     * 删除文件
     *
     * @param filePath            文件路径
     * @param request             请求
     * @return true or false
     */
    fun deleteFile(filePath: String, request: HttpServletRequest): Boolean {
        return try {
            FilesUtils.deleteFile(RequestUtils.getRealPath(request) + filePath)
        } catch (e: IOException) {
            log.error(" delete file is exception.", e)
            false
        }
    }

    /**
     * 文件下载
     *
     * @param fileId   文件id
     * @param request  请求
     * @param response 响应
     */
    fun downloadFile(fileId: String, request: HttpServletRequest, response: HttpServletResponse) {
        val files = filesService.findById(fileId)
        if (!ObjectUtils.isEmpty(files)) {
            uploadService.download(files.originalFileName, "/" + files.relativePath, response, request)
        }
    }
}