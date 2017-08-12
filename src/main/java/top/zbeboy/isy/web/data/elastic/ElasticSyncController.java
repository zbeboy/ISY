package top.zbeboy.isy.web.data.elastic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.service.data.ElasticSyncService;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;

/**
 * Created by zhaoyin on 17-8-8.
 */
@Slf4j
@Controller
public class ElasticSyncController {

    @Resource
    private ElasticSyncService elasticSyncService;

    /**
     * Elastic同步数据
     *
     * @return Elastic同步数据页面
     */
    @RequestMapping(value = "/web/menu/data/elastic", method = RequestMethod.GET)
    public String elasticData() {
        return "web/data/elastic/elastic_data::#page-wrapper";
    }

    /**
     * 同步班级数据
     *
     * @return 消息
     */
    @RequestMapping(value = "/web/data/elastic/sync/organize", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils syncOrganize() {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        elasticSyncService.syncOrganizeData();
        return ajaxUtils.success().msg("异步同步班级数据中...");
    }

    /**
     * 同步用户数据
     *
     * @return 消息
     */
    @RequestMapping(value = "/web/data/elastic/sync/users", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils syncUsers() {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        elasticSyncService.syncUsersData();
        return ajaxUtils.success().msg("异步同步用户数据中...");
    }

    /**
     * 同步学生数据
     *
     * @return 消息
     */
    @RequestMapping(value = "/web/data/elastic/sync/student", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils syncStudent() {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        elasticSyncService.syncStudentData();
        return ajaxUtils.success().msg("异步同步学生数据中...");
    }

    /**
     * 同步教职工数据
     *
     * @return 消息
     */
    @RequestMapping(value = "/web/data/elastic/sync/staff", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils syncStaff() {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        elasticSyncService.syncStaffData();
        return ajaxUtils.success().msg("异步同步教职工数据中...");
    }

    /**
     * 清空系统日志
     *
     * @return 消息
     */
    @RequestMapping(value = "/web/data/elastic/clean/system_log", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils cleanSystemLog() {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        elasticSyncService.cleanSystemLog();
        return ajaxUtils.success().msg("异步清空系统日志中...");
    }

    /**
     * 清空邮件日志
     *
     * @return 消息
     */
    @RequestMapping(value = "/web/data/elastic/clean/mailbox_log", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils cleanMailboxLog() {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        elasticSyncService.cleanSystemMailbox();
        return ajaxUtils.success().msg("异步清空邮件日志中...");
    }

    /**
     * 清空短信日志
     *
     * @return 消息
     */
    @RequestMapping(value = "/web/data/elastic/clean/sms_log", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils cleanSmsLog() {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        elasticSyncService.cleanSystemSms();
        return ajaxUtils.success().msg("异步清空短信日志中...");
    }
}
