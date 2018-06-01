package top.zbeboy.isy.service.weixin

import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import top.zbeboy.isy.service.util.MD5Utils
import top.zbeboy.isy.web.util.weixin.AesException
import top.zbeboy.isy.web.util.weixin.WXBizMsgCrypt
import top.zbeboy.isy.web.vo.weixin.WeixinVo
import java.io.IOException
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * Created by zbeboy 2017-11-20 .
 **/
@Service("weixinService")
open class WeixinServiceImpl : WeixinService{

    private val log = LoggerFactory.getLogger(WeixinServiceImpl::class.java)

    override fun checkSignature(weixinVo: WeixinVo): Boolean {
        val arr = arrayOf(weixinVo.token!!, weixinVo.timestamp!!, weixinVo.nonce!!)
        Arrays.sort(arr)
        val newArr = MD5Utils.sha_1(arr[0] + arr[1] + arr[2])
        return StringUtils.equals(weixinVo.signature, newArr)
    }

    override fun encryptMsg(msg: String, weixinVo: WeixinVo): String {
        var afterMsg = ""
        try {
            val encodingAesKey = weixinVo.encodingAESKey
            val token = weixinVo.token
            val timestamp = weixinVo.timestamp
            val nonce = weixinVo.nonce
            val appId = weixinVo.appId
            val pc = WXBizMsgCrypt(token!!, encodingAesKey!!, appId!!)
            afterMsg = pc.encryptMsg(msg, timestamp!!, nonce!!)
        } catch (e: AesException) {
            log.error("Encrypt weixin msg is exception {}", e)
        }

        return afterMsg
    }

    override fun decryptMsg(msg: String, weixinVo: WeixinVo): String {
        var afterMsg = ""
        try {
            val encodingAesKey = weixinVo.encodingAESKey
            val token = weixinVo.token
            val timestamp = weixinVo.timestamp
            val nonce = weixinVo.nonce
            val appId = weixinVo.appId
            val pc = WXBizMsgCrypt(token!!, encodingAesKey!!, appId!!)
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val sr = StringReader(msg)
            val `is` = InputSource(sr)
            val document = db.parse(`is`)
            val root = document.documentElement
            val nodelist1 = root.getElementsByTagName("Encrypt")
            val nodelist2 = root.getElementsByTagName("MsgSignature")
            val encrypt = nodelist1.item(0).textContent
            val msgSignature = nodelist2.item(0).textContent
            val format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1\$s]]></Encrypt></xml>"
            val fromXML = String.format(format, encrypt)
            afterMsg = pc.decryptMsg(msgSignature, timestamp!!, nonce!!, fromXML)
        } catch (e: SAXException) {
            log.error("Decrypt weixin msg is exception {}", e)
        } catch (e: ParserConfigurationException) {
            log.error("Decrypt weixin msg is exception {}", e)
        } catch (e: AesException) {
            log.error("Decrypt weixin msg is exception {}", e)
        } catch (e: IOException) {
            log.error("Decrypt weixin msg is exception {}", e)
        }

        return afterMsg
    }

}