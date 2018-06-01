package top.zbeboy.isy.web.util.weixin

import org.slf4j.LoggerFactory
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * XMLParse class
 * <p>
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 */
class XMLParse {

    companion object {

        private val log = LoggerFactory.getLogger(SHA1::class.java)

        /**
         * 提取出xml数据包中的加密消息
         *
         * @param xmltext 待提取的xml字符串
         * @return 提取出的加密消息字符串
         * @throws AesException
         */
        @JvmStatic
        @Throws(AesException::class)
        fun extract(xmltext: String): Array<Any?> {
            val result = arrayOfNulls<Any>(3)
            try {
                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()
                val sr = StringReader(xmltext)
                val `is` = InputSource(sr)
                val document = db.parse(`is`)

                val root = document.documentElement
                val nodelist1 = root.getElementsByTagName("Encrypt")
                val nodelist2 = root.getElementsByTagName("ToUserName")
                result[0] = 0
                result[1] = nodelist1.item(0).textContent
                result[2] = nodelist2.item(0).textContent
                return result
            } catch (e: Exception) {
                log.error("微信提取出xml数据包中的加密消息 error : {}", e)
                throw AesException(AesException.ParseXmlError)
            }

        }

        /**
         * 生成xml消息
         *
         * @param encrypt   加密后的消息密文
         * @param signature 安全签名
         * @param timestamp 时间戳
         * @param nonce     随机字符串
         * @return 生成的xml字符串
         */
        @JvmStatic
        fun generate(encrypt: String, signature: String, timestamp: String, nonce: String): String {

            val format = "<xml>\n" + "<Encrypt><![CDATA[%1\$s]]></Encrypt>\n" +
                    "<MsgSignature><![CDATA[%2\$s]]></MsgSignature>\n" +
                    "<TimeStamp>%3\$s</TimeStamp>\n" + "<Nonce><![CDATA[%4\$s]]></Nonce>\n" + "</xml>"
            return String.format(format, encrypt, signature, timestamp, nonce)

        }
    }


}