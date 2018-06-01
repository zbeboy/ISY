package top.zbeboy.isy.web.util.weixin

import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

/**
 * Created by zbeboy 2017-11-20 .
 * 计算公众平台的消息签名接口.
 **/
class SHA1 {

    companion object {

        private val log = LoggerFactory.getLogger(SHA1::class.java)

        /**
         * 用SHA1算法生成安全签名
         *
         * @param token     票据
         * @param timestamp 时间戳
         * @param nonce     随机字符串
         * @param encrypt   密文
         * @return 安全签名
         * @throws AesException
         */
        @JvmStatic
        @Throws(AesException::class)
        fun getSHA1(token: String, timestamp: String, nonce: String, encrypt: String): String {
            try {
                val array = arrayOf(token, timestamp, nonce, encrypt)
                val sb = StringBuilder()
                // 字符串排序
                Arrays.sort(array)
                for (i in 0..3) {
                    sb.append(array[i])
                }
                val str = sb.toString()
                // SHA1签名生成
                val md = MessageDigest.getInstance("SHA-1")
                md.update(str.toByteArray())
                val digest = md.digest()

                val hexstr = StringBuilder()
                var shaHex:String
                for (i in digest.indices) {
                    shaHex = Integer.toHexString((digest[i] and 0xFF.toByte()).toInt())
                    if (shaHex.length < 2) {
                        hexstr.append(0)
                    }
                    hexstr.append(shaHex)
                }
                return hexstr.toString()
            } catch (e: Exception) {
                log.error("SHA error : {}", e)
                throw AesException(AesException.ComputeSignatureError)
            }

        }
    }
}