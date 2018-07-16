package top.zbeboy.isy.config

import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.internship.InternshipApplyService
import top.zbeboy.isy.service.internship.InternshipReleaseService
import top.zbeboy.isy.service.platform.UsersKeyService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersUniqueInfoService
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.service.system.SystemAlertService
import top.zbeboy.isy.service.system.SystemMessageService
import java.sql.Timestamp
import java.time.Clock
import javax.annotation.Resource

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
open class ScheduledConfiguration {
    private val log = LoggerFactory.getLogger(ScheduledConfiguration::class.java)

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource
    open lateinit var internshipApplyService: InternshipApplyService

    @Resource
    open lateinit var systemAlertService: SystemAlertService

    @Resource
    open lateinit var systemMessageService: SystemMessageService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var authoritiesService: AuthoritiesService

    @Resource
    open lateinit var usersKeyService: UsersKeyService

    @Resource
    open lateinit var usersUniqueInfoService: UsersUniqueInfoService

    /**
     * 清理未验证用户信息
     */
    @Scheduled(cron = "0 15 01 02 * ?") // 每月2号 晚间1点15分
    fun cleanUsers() {
        // 清理
        val dateTime = DateTime.now()
        val oldTime = dateTime.minusDays(30)
        // 查询未验证用户
        val records = this.usersService.findByJoinDateAndVerifyMailbox(oldTime.toDate(), 0)
        records.forEach { r ->
            val usersType = this.cacheManageService.findByUsersTypeId(r.usersTypeId!!)
            this.authoritiesService.deleteByUsername(r.username)
            if (usersType.usersTypeName == Workbook.STAFF_USERS_TYPE) {
                this.staffService.deleteByUsername(r.username)
            } else if (usersType.usersTypeName == Workbook.STUDENT_USERS_TYPE) {
                this.studentService.deleteByUsername(r.username)
            }
            this.usersService.deleteById(r.username)
            this.usersKeyService.deleteByUsername(r.username)
            this.cacheManageService.deleteUsersKey(r.username)
            this.usersUniqueInfoService.deleteByUsername(r.username)
        }
        log.info(">>>>>>>>>>>>> scheduled ... clean users ")
    }

    /**
     * 更改实习状态为申请中
     */
    @Scheduled(cron = "0 15 02 * * ?") // 每天 晚间2点15分
    fun internshipApply() {
        // 更改实习提交状态
        val now = Timestamp(Clock.systemDefaultZone().millis())
        val internshipReleaseRecords = this.internshipReleaseService.findByEndTime(now)
        for (r in internshipReleaseRecords) {
            this.internshipApplyService.updateStateWithInternshipReleaseIdAndState(r.internshipReleaseId, 0, 1)
        }
        this.internshipApplyService.updateStateByChangeFillEndTime(now, 5, 1)
        this.internshipApplyService.updateStateByChangeFillEndTime(now, 7, 1)
        log.info(">>>>>>>>>>>>> scheduled ... internshipApply state update. ")
    }

    /**
     * 每年清理消息
     */
    @Scheduled(cron = "0 0 0 1 1 ?") // 每年 1月1号
    fun cleanSystem() {
        // 清理系统消息，提醒
        val dateTime = DateTime.now()
        val oldTime = dateTime.minusYears(1)
        val ts = Timestamp(oldTime.millis)
        this.systemAlertService.deleteByAlertDate(ts)
        this.systemMessageService.deleteByMessageDate(ts)
        log.info(">>>>>>>>>>>>> scheduled ... clean alert , message ")
    }
}