package top.zbeboy.isy.web.platform.message

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import top.zbeboy.isy.service.system.SystemAlertService
import top.zbeboy.isy.service.system.SystemMessageService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean
import top.zbeboy.isy.web.bean.system.message.SystemMessageBean
import top.zbeboy.isy.web.util.AjaxUtils
import java.security.Principal
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-03 .
 **/
@Controller
open class MessageController {

    @Resource
    lateinit open var systemAlertService: SystemAlertService

    @Resource
    lateinit open var systemMessageService: SystemMessageService

    /**
     * 推送到一个用户 的系统信息
     *
     * @param principal 用户账号
     * @return 消息
     * @throws InterruptedException 异常
     */
    @MessageMapping("/remind")
    @SendToUser(destinations = ["/topic/reminds"], broadcast = false)
    @Throws(InterruptedException::class)
    fun reminds(principal: Principal): AjaxUtils<*> {
        Thread.sleep(3000)
        val username = principal.name
        val data = HashMap<String, Any>()
        val pageNum = 1
        val pageSize = 5

        // 提醒
        var systemAlertBeens: List<SystemAlertBean> = ArrayList()
        val systemAlertRecord = systemAlertService.findAllByPageForShow(pageNum, pageSize, username, false)
        if (systemAlertRecord.isNotEmpty) {
            systemAlertBeens = systemAlertRecord.into(SystemAlertBean::class.java)
            systemAlertBeens.forEach { i -> i.alertDateStr = DateTimeUtils.formatDate(i.alertDate, "yyyyMMddHHmmss") }
        }
        data.put("alerts", systemAlertBeens)
        data.put("alertsCount", systemAlertService.countAllForShow(username, false))

        // 消息
        var systemMessageBeens: List<SystemMessageBean> = ArrayList()
        val systemMessageRecord = systemMessageService.findAllByPageForShow(pageNum, pageSize, username, false)
        if (systemMessageRecord.isNotEmpty) {
            systemMessageBeens = systemMessageRecord.into(SystemMessageBean::class.java)
            systemMessageBeens.forEach { i -> i.messageDateStr = DateTimeUtils.formatDate(i.messageDate, "yyyyMMddHHmmss") }
        }
        data.put("messages", systemMessageBeens)
        data.put("messagesCount", systemMessageService.countAllForShow(username, false))
        return AjaxUtils.of<Any>().success().msg("获取数据成功").mapData(data)
    }
}