package top.zbeboy.isy.web.system.message

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.SystemAlert
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.SystemAlertService
import top.zbeboy.isy.service.system.SystemMessageService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.web.bean.system.message.SystemMessageBean
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-07 .
 **/
@Controller
open class SystemMessageController {

    @Resource
    open lateinit var systemMessageService: SystemMessageService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var systemAlertService: SystemAlertService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    /**
     * 系统消息数据
     *
     * @return 系统数据页面
     */
    @RequestMapping(value = ["/anyone/message"], method = [(RequestMethod.GET)])
    fun message(): String {
        return "web/system/message/system_message::#page-wrapper"
    }

    /**
     * 消息详情
     *
     * @param messageId 消息id
     * @param modelMap  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/anyone/message/detail"], method = [(RequestMethod.GET)])
    fun messageDetail(@RequestParam("id") messageId: String, modelMap: ModelMap): String {
        var systemMessageBean = SystemMessageBean()
        val users = usersService.getUserFromSession()
        val record = systemMessageService.findByIdAndAcceptUsersRelation(messageId, users!!.username)
        if (record.isPresent) {
            systemMessageBean = record.get().into(SystemMessageBean::class.java)
            systemMessageBean.messageDateStr = DateTimeUtils.formatDate(systemMessageBean.messageDate, "yyyy年MM月dd日 hh:mm:ss")
            systemMessageBean.isSee = 1
            systemMessageService.update(systemMessageBean)
            // 若单独点击消息则需要更新提醒状态
            val systemAlertType = cacheManageService.findBySystemAlertTypeName(Workbook.ALERT_MESSAGE_TYPE)
            if (!ObjectUtils.isEmpty(systemAlertType)) {
                val systemAlertRecord = systemAlertService.findByUsernameAndLinkIdAndSystemAlertTypeId(users.username, systemMessageBean.systemMessageId, systemAlertType.systemAlertTypeId!!)
                if (systemAlertRecord.isPresent) {
                    val systemAlert = systemAlertRecord.get().into(SystemAlert::class.java)
                    systemAlert.isSee = 1
                    systemAlertService.update(systemAlert)
                }
            }
        }
        modelMap.addAttribute("message", systemMessageBean)

        return "web/system/message/system_message_look::#page-wrapper"
    }

    /**
     * 获取系统消息数据
     *
     * @return 数据
     */
    @RequestMapping(value = ["/anyone/message/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun messageDatas(paginationUtils: PaginationUtils): AjaxUtils<SystemMessageBean> {
        val ajaxUtils = AjaxUtils.of<SystemMessageBean>()
        val systemMessageBean = SystemMessageBean()
        val users = usersService.getUserFromSession()
        systemMessageBean.acceptUsers = users!!.username
        val records = systemMessageService.findAllByPage(paginationUtils, systemMessageBean)
        val systemMessageBeens = systemMessageService.dealData(paginationUtils, records, systemMessageBean)
        return ajaxUtils.success().msg("获取数据成功").listData(systemMessageBeens).paginationUtils(paginationUtils)
    }

}