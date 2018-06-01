package top.zbeboy.isy.service.util

import org.slf4j.LoggerFactory
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

/**
 * Created by zbeboy 2017-11-30 .
 **/
class MD5Utils {
    companion object {
        private val log = LoggerFactory.getLogger(MD5Utils::class.java)

        /**
         * sha 1 加密
         *
         * @param decript 字符串
         * @return 加密后
         */
        @JvmStatic
        fun sha_1(decript: String): String {
            try {
                val digest = java.security.MessageDigest
                        .getInstance("SHA-1")
                digest.update(decript.toByteArray())
                val messageDigest = digest.digest()
                // Create Hex String
                val hexString = StringBuilder()
                // 字节数组转换为 十六进制 数
                for (aMessageDigest in messageDigest) {
                    val shaHex = Integer.toHexString((aMessageDigest and 0xFF.toByte()).toInt())
                    if (shaHex.length < 2) {
                        hexString.append(0)
                    }
                    hexString.append(shaHex)
                }
                return hexString.toString()

            } catch (e: NoSuchAlgorithmException) {
                log.error("MD5 sha1 error : {}", e)
            }

            return ""
        }
    }
}