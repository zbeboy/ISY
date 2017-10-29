package top.zbeboy.isy.test

import org.apache.commons.lang3.RandomStringUtils
import org.junit.Test
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import top.zbeboy.isy.service.util.MD5Utils
import top.zbeboy.isy.web.util.weixin.AesException
import top.zbeboy.isy.web.util.weixin.WXBizMsgCrypt
import java.io.IOException
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * Created by zbeboy 2017-10-29 .
 **/
class TestSimple{
    @Test
    fun testWeixinCheck() {
        val arr = arrayOf("23", "1", "34")
        Arrays.sort(arr)
        println(arr[0] + arr[1] + arr[2])
        val newArr = MD5Utils.sha_1(arr[0] + arr[1] + arr[2])
        println(newArr)
        println(RandomStringUtils.randomAlphabetic(10))
    }

    @Test
    @Throws(ParserConfigurationException::class, AesException::class, IOException::class, SAXException::class)
    fun testWeixinCode() {
        // 需要加密的明文
        val encodingAesKey = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFG"
        val token = "pamtest"
        val timestamp = "1409304348"
        val nonce = "xxxxxx"
        val appId = "wxb11529c136998cb6"
        val replyMsg = " 中文<xml><ToUserName><![CDATA[oia2TjjewbmiOUlr6X-1crbLOvLw]]></ToUserName><FromUserName><![CDATA[gh_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[video]]></MsgType><Video><MediaId><![CDATA[eYJ1MbwPRJtOvIEabaxHs7TX2D-HV71s79GUxqdUkjm6Gs2Ed1KF3ulAOA9H1xG0]]></MediaId><Title><![CDATA[testCallBackReplyVideo]]></Title><Description><![CDATA[testCallBackReplyVideo]]></Description></Video></xml>"

        val pc = WXBizMsgCrypt(token, encodingAesKey, appId)
        val mingwen = pc.encryptMsg(replyMsg, timestamp, nonce)
        println("加密后: " + mingwen)

        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val sr = StringReader(mingwen)
        val `is` = InputSource(sr)
        val document = db.parse(`is`)

        val root = document.documentElement
        val nodelist1 = root.getElementsByTagName("Encrypt")
        val nodelist2 = root.getElementsByTagName("MsgSignature")

        val encrypt = nodelist1.item(0).textContent
        val msgSignature = nodelist2.item(0).textContent

        val format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1\$s]]></Encrypt></xml>"
        val fromXML = String.format(format, encrypt)

        //
        // 公众平台发送消息给第三方，第三方处理
        //

        // 第三方收到公众号平台发送的消息
        val result2 = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML)
        println("解密后明文: " + result2)
    }
}