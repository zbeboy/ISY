package top.zbeboy.isy.service.system

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.elastic.pojo.SystemSmsElastic
import top.zbeboy.isy.glue.system.SystemSmsGlue
import top.zbeboy.isy.service.util.UUIDUtils
import java.io.*
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
    override fun sendShortMessage(mobile: String, content: String, action: String?,
                                  sendType: String?, codingType: String?, backEncodeType: String?) {
        var result: String?
        try {
            var ct = codingType
            var bet = backEncodeType
            if (ct == null || ct == "") {
                ct = Charsets.UTF_8.displayName()
            }
            if (bet == null || bet == "") {
                bet = Charsets.UTF_8.displayName()
            }
            val send = StringBuffer()
            if (action != null && action != "") {
                send.append("action=").append(action)
            } else {
                send.append("action=send")
            }

            send.append("&userid=").append(isyProperties.getMobile().userId)
            send.append("&account=").append(
                    URLEncoder.encode(isyProperties.getMobile().account, ct))
            send.append("&password=").append(
                    URLEncoder.encode(isyProperties.getMobile().password, ct))
            send.append("&mobile=").append(mobile)
            send.append("&content=").append(
                    URLEncoder.encode(content, ct))
            if (sendType != null && sendType.toLowerCase() == "get") {
                result = SmsClientAccessTool.getInstance().doAccessHTTPGet(
                        isyProperties.getMobile().url + "?" + send.toString(), bet)
            } else {
                result = SmsClientAccessTool.getInstance().doAccessHTTPPost(isyProperties.getMobile().url!!,
                        send.toString(), bet)
            }
        } catch (e: Exception) {
            log.error("发送短信异常:{}", e)
            result = e.message
        }

        val systemSms = SystemSmsElastic(UUIDUtils.getUUID(), Timestamp(Clock.systemDefaultZone().millis()), mobile, result)
        systemSmsGlue.save(systemSms)
    }

    @Async
    override fun sendValidMobileShortMessage(mobile: String, verificationCode: String) {
        log.debug(" mobile valid : {} : {}", mobile, verificationCode)
        if (isyProperties.getMobile().isOpen) {
            val content = "【" + isyProperties.getMobile().sign + "】 您的验证码是:" + verificationCode + "，感谢您的使用！"
            sendShortMessage(mobile, content, "", "", "", "")
        } else {
            log.debug(" 管理员已关闭短信发送 ")
        }
    }


    class SmsClientAccessTool {

        private val log = LoggerFactory.getLogger(SmsClientAccessTool::class.java)

        companion object {
            @JvmField
            var smsClientToolInstance: SmsClientAccessTool? = null

            /**
             * 采用单列方式来访问操作
             *
             * @return
             */
            @JvmStatic
            @Synchronized
            fun getInstance(): SmsClientAccessTool {

                if (smsClientToolInstance == null) {
                    smsClientToolInstance = SmsClientAccessTool()
                }
                return smsClientToolInstance as SmsClientAccessTool
            }
        }

        /**
         * POST方法
         *
         * @param sendUrl
         * ：访问URL
         * @param sendParam
         * ：参数串
         * @param backEncodeType
         * ：返回的编码
         * @return
         */
        fun doAccessHTTPPost(sendUrl: String, sendParam: String,
                             backEncodeType: String?): String {
            var encodeType = backEncodeType

            val receive = StringBuffer()
            val wr: BufferedWriter? = null
            try {
                if (encodeType == null || encodeType == "") {
                    encodeType = "UTF-8"
                }

                val url = URL(sendUrl)
                val URLConn = url
                        .openConnection() as HttpURLConnection

                URLConn.doOutput = true
                URLConn.doInput = true
                URLConn.requestMethod = "POST"
                URLConn.useCaches = false
                URLConn.allowUserInteraction = true
                HttpURLConnection.setFollowRedirects(true)
                URLConn.instanceFollowRedirects = true

                URLConn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8")
                URLConn.setRequestProperty("Content-Length", sendParam.toByteArray().size.toString())

                val dos = DataOutputStream(URLConn
                        .outputStream)
                dos.writeBytes(sendParam)

                val rd = BufferedReader(InputStreamReader(
                        URLConn.inputStream, encodeType))
                var line: String? = rd.readLine()
                while (line != null) {
                    receive.append(line).append("\r\n")
                    line = rd.readLine()
                }
                rd.close()
            } catch (e: java.io.IOException) {
                receive.append("访问产生了异常-->").append(e.message)
                log.error("Send sms error . {}", e)
            } finally {
                if (wr != null) {
                    try {
                        wr.close()
                    } catch (ex: IOException) {
                        log.error("Send sms error . {}", ex)
                    }
                }
            }

            return receive.toString()
        }

        fun doAccessHTTPGet(sendUrl: String, backEncodeType: String?): String {
            var encodeType = backEncodeType

            val receive = StringBuffer()
            var `in`: BufferedReader? = null
            try {
                if (encodeType == null || encodeType == "") {
                    encodeType = "UTF-8"
                }

                val url = URL(sendUrl)
                val URLConn = url
                        .openConnection() as HttpURLConnection

                URLConn.doInput = true
                URLConn.doOutput = true
                URLConn.connect()
                URLConn.outputStream.flush()
                `in` = BufferedReader(InputStreamReader(URLConn
                        .inputStream, encodeType))

                var line: String? = `in`.readLine()
                while (line != null) {
                    receive.append(line).append("\r\n")
                    line = `in`.readLine()
                }

            } catch (e: IOException) {
                receive.append("访问产生了异常-->").append(e.message)
                log.error("Send sms error . {}", e)
            } finally {
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (ex: java.io.IOException) {
                        log.error("Send sms error . {}", ex)
                    }
                }
            }

            return receive.toString()
        }
    }


}