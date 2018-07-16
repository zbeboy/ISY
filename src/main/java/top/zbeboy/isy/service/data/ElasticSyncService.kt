package top.zbeboy.isy.service.data

/**
 * Created by zbeboy 2017-12-12 .
 **/
interface ElasticSyncService {
    /**
     * 清理日志
     */
    fun cleanSystemLog()

    /**
     * 清理邮件日志
     */
    fun cleanSystemMailbox()

    /**
     * 清理短信日志
     */
    fun cleanSystemSms()
}