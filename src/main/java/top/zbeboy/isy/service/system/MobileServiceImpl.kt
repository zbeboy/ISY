package top.zbeboy.isy.service.system

import org.apache.commons.lang.CharEncoding
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.elastic.pojo.SystemSmsElastic
import top.zbeboy.isy.glue.system.SystemSmsGlue
import top.zbeboy.isy.service.util.UUIDUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.sql.Timestamp
import java.time.Clock
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Service("mobileService")
open class MobileServiceImpl : MobileService {

    private val log = LoggerFactory.getLogger(MobileServiceImpl::class.java)

    @Autowired
    open lateinit var isyProperties: ISYProperties

    @Resource
    open lateinit var systemSmsGlue: SystemSmsGlue

    @Async
    override fun sendShortMessage(mobile: String, content: String) {
        var result: String?
        try {
            var httpUrl = "http://apis.baidu.com/kingtto_media/106sms/106sms"
            val sendContent = URLEncoder.encode(content, CharEncoding.UTF_8)
            log.debug(" mobile sendContent : {}", sendContent)
            val httpArg = "mobile=$mobile&content=$sendContent"
            val reader: BufferedReader
            val sbf = StringBuilder()
            httpUrl = httpUrl + "?" + httpArg
            val url = URL(httpUrl)
            val connection = url
                    .openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", isyProperties.getMobile().apiKey)
            connection.connect()
            val `is` = connection.inputStream
            reader = BufferedReader(InputStreamReader(`is`, CharEncoding.UTF_8))
            var strRead: String? = reader.readLine()
            while (strRead != null) {
                sbf.append(strRead)
                sbf.append("\r\n")
                strRead = reader.readLine()
            }
            reader.close()
            result = sbf.toString()
        } catch (e: Exception) {
            log.info("Send sms to mobile {} is exception : {}", mobile, e)
            result = e.message
        }

        val systemSms = SystemSmsElastic(UUIDUtils.getUUID(), Timestamp(Clock.systemDefaultZone().millis()), mobile, result)
        systemSmsGlue.save(systemSms)
    }

    @Async
    override fun sendValidMobileShortMessage(mobile: String, verificationCode: String) {
        log.debug(" mobile valid : {} : {}", mobile, verificationCode)
        if (isyProperties.getMobile().isOpen) {
            val content = "【" + isyProperties.getMobile().sign + "】 您的验证码:" + verificationCode
            sendShortMessage(mobile, content)
        } else {
            log.debug(" 管理员已关闭短信发送 ")
        }
    }
}