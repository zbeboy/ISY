package top.zbeboy.isy.web.common

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.SystemAlert
import top.zbeboy.isy.domain.tables.pojos.SystemMessage
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.DesService
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.MailService
import top.zbeboy.isy.service.system.SystemAlertService
import top.zbeboy.isy.service.system.SystemMessageService
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.file.FileBean
import top.zbeboy.isy.web.util.AjaxUtils
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

    @Resource
    open lateinit var desService: DesService

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
     * 如果是管理员则获取院id，如果是教职工角色则获取系id,如果是普通学生就获取系id,专业id,年级
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
            val userTypeId = users!!.usersTypeId!!
            val usersType = cacheManageService.findByUsersTypeId(userTypeId)
            if (usersType.usersTypeName == Workbook.STUDENT_USERS_TYPE) {// 学生
                val organizeBean = cacheManageService.getRoleOrganizeInfo(users)
                map.put("departmentId", organizeBean!!.departmentId)
                map.put("scienceId", organizeBean.scienceId)
                map.put("grade", organizeBean.grade.toInt())
            } else if (usersType.usersTypeName == Workbook.STAFF_USERS_TYPE) {// 教职工
                val departmentId = cacheManageService.getRoleDepartmentId(users)
                map.put("departmentId", departmentId)
            }
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
     * 上传附件
     *
     * @param schoolId                    学校id
     * @param collegeId                   院id
     * @param departmentId                系id
     * @param multipartHttpServletRequest 文件请求
     * @return 文件信息
     */
    fun uploadFile(schoolId: Int?, collegeId: Int?, @RequestParam("departmentId") departmentId: Int,
                   multipartHttpServletRequest: MultipartHttpServletRequest): AjaxUtils<FileBean> {
        val data = AjaxUtils.of<FileBean>()
        try {
            val path = Workbook.graduateDesignPath(cacheManageService.schoolInfoPath(schoolId, collegeId, departmentId))
            val fileBeen = uploadService.upload(multipartHttpServletRequest,
                    RequestUtils.getRealPath(multipartHttpServletRequest) + path, multipartHttpServletRequest.remoteAddr)
            data.success().listData(fileBeen).obj(path)
        } catch (e: Exception) {
            log.error("Upload file exception,is {}", e)
        }

        return data
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
            uploadService.download(files.originalFileName, files.relativePath, response, request)
        }
    }

    /**
     * 加密个人数据
     *
     * @param param 待加密数据
     * @param usersKey 用户KEY
     */
    fun encryptPersonalData(param: String?, usersKey: String): String? {
        var data = param
        if (StringUtils.hasLength(data)) {
            data = desService.encrypt(data!!, usersKey)
        }
        return data
    }

    /**
     * 解密个人数据
     *
     * @param param 待加密数据
     * @param usersKey 用户KEY
     */
    fun decryptPersonalData(param: String?, usersKey: String): String? {
        var data = param
        if (StringUtils.hasLength(data)) {
            data = desService.decrypt(data, usersKey)
        }
        return data
    }
}