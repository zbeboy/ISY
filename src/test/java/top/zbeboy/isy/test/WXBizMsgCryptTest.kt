package top.zbeboy.isy.test

import org.junit.*
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import top.zbeboy.isy.web.util.weixin.AesException
import top.zbeboy.isy.web.util.weixin.WXBizMsgCrypt
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Created by zbeboy 2017-10-29 .
 **/
class WXBizMsgCryptTest {
    private val encodingAesKey = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFG"
    private val token = "pamtest"
    private val timestamp = "1409304348"
    private val nonce = "xxxxxx"
    private val appId = "wxb11529c136998cb6"
    private val replyMsg = "我是中文abcd123"
    private val xmlFormat = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1\$s]]></Encrypt></xml>"
    private val randomStr = "aaaabbbbccccdddd"

    @BeforeClass
    @Throws(Exception::class)
    fun setUpBeforeClass() {
    }

    @AfterClass
    @Throws(Exception::class)
    fun tearDownAfterClass() {
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {

    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }

    @Test
    @Ignore
    @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
    fun testNormal() {
        try {
            val pc = WXBizMsgCrypt(token, encodingAesKey, appId)
            val afterEncrpt = pc.encryptMsg(replyMsg, timestamp, nonce)

            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val sr = StringReader(afterEncrpt)
            val `is` = InputSource(sr)
            val document = db.parse(`is`)

            val root = document.documentElement
            val nodelist1 = root.getElementsByTagName("Encrypt")
            val nodelist2 = root.getElementsByTagName("MsgSignature")

            val encrypt = nodelist1.item(0).textContent
            val msgSignature = nodelist2.item(0).textContent
            val fromXML = String.format(xmlFormat, encrypt)

            // 第三方收到公众号平台发送的消息
            val afterDecrpt = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML)
            assertEquals(replyMsg, afterDecrpt)
        } catch (e: AesException) {
            fail("正常流程，怎么就抛出异常了？？？？？？")
        }

    }

    @Test
    @Ignore
    fun testAesEncrypt() {
        try {
            val pc = WXBizMsgCrypt(token, encodingAesKey, appId)
            val afterAesEncrypt = "jn1L23DB+6ELqJ+6bruv21Y6MD7KeIfP82D6gU39rmkgczbWwt5+3bnyg5K55bgVtVzd832WzZGMhkP72vVOfg=="
            assertEquals(afterAesEncrypt, pc.encrypt(randomStr, replyMsg))
        } catch (e: AesException) {
            e.printStackTrace()
            fail("no异常")
        }

    }

    @Test
    @Ignore
    fun testAesEncrypt2() {
        try {
            val pc = WXBizMsgCrypt(token, encodingAesKey, appId)
            val replyMsg2 = "<xml><ToUserName><![CDATA[oia2Tj我是中文jewbmiOUlr6X-1crbLOvLw]]></ToUserName><FromUserName><![CDATA[gh_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[video]]></MsgType><Video><MediaId><![CDATA[eYJ1MbwPRJtOvIEabaxHs7TX2D-HV71s79GUxqdUkjm6Gs2Ed1KF3ulAOA9H1xG0]]></MediaId><Title><![CDATA[testCallBackReplyVideo]]></Title><Description><![CDATA[testCallBackReplyVideo]]></Description></Video></xml>"
            val afterAesEncrypt2 = "jn1L23DB+6ELqJ+6bruv23M2GmYfkv0xBh2h+XTBOKVKcgDFHle6gqcZ1cZrk3e1qjPQ1F4RsLWzQRG9udbKWesxlkupqcEcW7ZQweImX9+wLMa0GaUzpkycA8+IamDBxn5loLgZpnS7fVAbExOkK5DYHBmv5tptA9tklE/fTIILHR8HLXa5nQvFb3tYPKAlHF3rtTeayNf0QuM+UW/wM9enGIDIJHF7CLHiDNAYxr+r+OrJCmPQyTy8cVWlu9iSvOHPT/77bZqJucQHQ04sq7KZI27OcqpQNSto2OdHCoTccjggX5Z9Mma0nMJBU+jLKJ38YB1fBIz+vBzsYjrTmFQ44YfeEuZ+xRTQwr92vhA9OxchWVINGC50qE/6lmkwWTwGX9wtQpsJKhP+oS7rvTY8+VdzETdfakjkwQ5/Xka042OlUb1/slTwo4RscuQ+RdxSGvDahxAJ6+EAjLt9d8igHngxIbf6YyqqROxuxqIeIch3CssH/LqRs+iAcILvApYZckqmA7FNERspKA5f8GoJ9sv8xmGvZ9Yrf57cExWtnX8aCMMaBropU/1k+hKP5LVdzbWCG0hGwx/dQudYR/eXp3P0XxjlFiy+9DMlaFExWUZQDajPkdPrEeOwofJb"
            assertEquals(afterAesEncrypt2, pc.encrypt(randomStr, replyMsg2))

        } catch (e: AesException) {
            e.printStackTrace()
            fail("no异常")
        }

    }

    @Test
    @Ignore
    fun testIllegalAesKey() {
        try {
            WXBizMsgCrypt(token, "abcde", appId)
        } catch (e: AesException) {
            assertEquals(AesException.IllegalAesKey.toLong(), e.code.toLong())
            return
        }

        fail("错误流程不抛出异常？？？")
    }

    @Test
    @Ignore
    @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
    fun testValidateSignatureError() {
        try {
            val pc = WXBizMsgCrypt(token, encodingAesKey, appId)
            val afterEncrpt = pc.encryptMsg(replyMsg, timestamp, nonce)
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val sr = StringReader(afterEncrpt)
            val `is` = InputSource(sr)
            val document = db.parse(`is`)

            val root = document.documentElement
            val nodelist1 = root.getElementsByTagName("Encrypt")

            val encrypt = nodelist1.item(0).textContent
            val fromXML = String.format(xmlFormat, encrypt)
            pc.decryptMsg("12345", timestamp, nonce, fromXML) // 这里签名错误
        } catch (e: AesException) {
            assertEquals(AesException.ValidateSignatureError.toLong(), e.code.toLong())
            return
        }

        fail("错误流程不抛出异常？？？")
    }

    @Test
    @Ignore
    @Throws(AesException::class)
    fun testVerifyUrl() {
        val wxcpt = WXBizMsgCrypt("QDG6eK",
                "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C", "wx5823bf96d3bd56c7")
        val verifyMsgSig = "5c45ff5e21c57e6ad56bac8758b79b1d9ac89fd3"
        val timeStamp = "1409659589"
        val nonce = "263014780"
        val echoStr = "P9nAzCzyDtyTWESHep1vC5X9xho/qYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp+4RPcs8TgAE7OaBO+FZXvnaqQ=="
        wxcpt.verifyUrl(verifyMsgSig, timeStamp, nonce, echoStr)
        // 只要不抛出异常就好
    }
}