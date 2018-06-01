package top.zbeboy.isy.web.data.elastic

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.service.data.ElasticSyncService
import top.zbeboy.isy.web.util.AjaxUtils
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Controller
open class ElasticSyncController {

    @Resource
    open lateinit var elasticSyncService: ElasticSyncService


    /**
     * Elastic同步数据
     *
     * @return Elastic同步数据页面
     */
    @RequestMapping(value = ["/web/menu/data/elastic"], method = [(RequestMethod.GET)])
    fun elasticData(): String {
        return "web/data/elastic/elastic_data::#page-wrapper"
    }

    /**
     * 同步班级数据
     *
     * @return 消息
     */
    @RequestMapping(value = ["/web/data/elastic/sync/organize"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun syncOrganize(): AjaxUtils<*> {
        elasticSyncService.syncOrganizeData()
        return AjaxUtils.of<Any>().success().msg("异步同步班级数据中...")
    }

    /**
     * 同步用户数据
     *
     * @return 消息
     */
    @RequestMapping(value = ["/web/data/elastic/sync/users"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun syncUsers(): AjaxUtils<*> {
        elasticSyncService.syncUsersData()
        return AjaxUtils.of<Any>().success().msg("异步同步用户数据中...")
    }

    /**
     * 同步学生数据
     *
     * @return 消息
     */
    @RequestMapping(value = ["/web/data/elastic/sync/student"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun syncStudent(): AjaxUtils<*> {
        elasticSyncService.syncStudentData()
        return AjaxUtils.of<Any>().success().msg("异步同步学生数据中...")
    }

    /**
     * 同步教职工数据
     *
     * @return 消息
     */
    @RequestMapping(value = ["/web/data/elastic/sync/staff"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun syncStaff(): AjaxUtils<*> {
        elasticSyncService.syncStaffData()
        return AjaxUtils.of<Any>().success().msg("异步同步教职工数据中...")
    }

    /**
     * 清空系统日志
     *
     * @return 消息
     */
    @RequestMapping(value = ["/web/data/elastic/clean/system_log"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun cleanSystemLog(): AjaxUtils<*> {
        elasticSyncService.cleanSystemLog()
        return AjaxUtils.of<Any>().success().msg("异步清空系统日志中...")
    }

    /**
     * 清空邮件日志
     *
     * @return 消息
     */
    @RequestMapping(value = ["/web/data/elastic/clean/mailbox_log"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun cleanMailboxLog(): AjaxUtils<*> {
        elasticSyncService.cleanSystemMailbox()
        return AjaxUtils.of<Any>().success().msg("异步清空邮件日志中...")
    }

    /**
     * 清空短信日志
     *
     * @return 消息
     */
    @RequestMapping(value = ["/web/data/elastic/clean/sms_log"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun cleanSmsLog(): AjaxUtils<*> {
        elasticSyncService.cleanSystemSms()
        return AjaxUtils.of<Any>().success().msg("异步清空短信日志中...")
    }
}