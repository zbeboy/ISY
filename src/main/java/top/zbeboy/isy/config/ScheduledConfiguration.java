package top.zbeboy.isy.config;

import org.joda.time.DateTime;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseRecord;
import top.zbeboy.isy.service.*;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * 定时任务配置
 *
 * @author zbeboy
 * @version 1.0
 *          例子:
 *          0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
 *          0 0/30 9-17 * * ?   朝九晚五工作时间内每半小时
 *          "0 0 12 * * ?" 每天中午12点触发
 *          "0 15 10 ? * *" 每天上午10:15触发
 *          "0 15 10 * * ?" 每天上午10:15触发
 *          "0 15 10 * * ? *" 每天上午10:15触发
 *          "0 15 10 * * ? 2005" 2005年的每天上午10:15触发
 *          "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
 *          "0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发
 *          "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
 *          "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
 *          "0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发
 *          "0 15 10 15 * ?" 每月15日上午10:15触发
 *          "0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
 *          "0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发
 *          "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发
 *          <p>
 *          注：不支持字符L等等字符
 */
@Configuration
@EnableScheduling
public class ScheduledConfiguration {

    private final Logger log = LoggerFactory.getLogger(ScheduledConfiguration.class);

    @Resource
    private SystemLogService systemLogService;

    @Resource
    private SystemMailboxService systemMailboxService;

    @Resource
    private SystemSmsService systemSmsService;

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipApplyService internshipApplyService;

    @Resource
    private SystemAlertService systemAlertService;

    @Resource
    private SystemMessageService systemMessageService;

    /**
     * 清理信息
     */
    @Scheduled(cron = "0 15 01 01 * ?")// 每月1号 晚间1点15分
    public void clean() {
        // 清理日志,邮件，短信
        DateTime dateTime = DateTime.now();
        DateTime oldTime = dateTime.minusDays(120);
        Timestamp ts = new Timestamp(oldTime.getMillis());
        systemLogService.deleteByOperatingTime(ts);
        systemMailboxService.deleteBySendTime(ts);
        systemSmsService.deleteBySendTime(ts);
        log.info(">>>>>>>>>>>>> scheduled ... log , mailbox , sms ");
    }

    /**
     * 更改实习状态为申请中
     */
    @Scheduled(cron = "0 15 02 * * ?")// 每天 晚间2点15分
    public void internshipApply() {
        // 更改实习提交状态
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Result<InternshipReleaseRecord> internshipReleaseRecords = internshipReleaseService.findByEndTime(now);
        for (InternshipReleaseRecord r : internshipReleaseRecords) {
            internshipApplyService.updateStateWithInternshipReleaseIdAndState(r.getInternshipReleaseId(), 0, 1);
        }
        internshipApplyService.updateStateByChangeFillEndTime(now, 5, 1);
        internshipApplyService.updateStateByChangeFillEndTime(now, 7, 1);
        log.info(">>>>>>>>>>>>> scheduled ... internshipApply state update. ");
    }

    /**
     * 每年清理消息
     */
    @Scheduled(cron = "0 0 0 1 1 ?")// 每年 1月1号
    public void cleanSystem() {
        // 清理系统消息，提醒
        DateTime dateTime = DateTime.now();
        DateTime oldTime = dateTime.minusYears(1);
        Timestamp ts = new Timestamp(oldTime.getMillis());
        systemAlertService.deleteByAlertDate(ts);
        systemMessageService.deleteByMessageDate(ts);
        log.info(">>>>>>>>>>>>> scheduled ... alert , message ");
    }
}
