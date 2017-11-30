package top.zbeboy.isy.service.util

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.encoding.Md5PasswordEncoder
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

/**
 * Created by zbeboy 2017-11-30 .
 **/
class MD5Utils {
    companion object {
        private val log = LoggerFactory.getLogger(MD5Utils::class.java)
        /**
         * md5加密
         *
         * @param password 密码
         * @return 加密后
         */
        @JvmStatic
        fun md5(password: String): String {
            val md5 = Md5PasswordEncoder()
            // false 表示：生成32位的Hex版, 这也是encodeHashAsBase64的, Acegi 默认配置; true  表示：生成24位的Base64版
            md5.encodeHashAsBase64 = false
            return md5.encodePassword(password, null)
        }

        /**
         * sha 256 encode
         *
         * @param password 密码
         * @return 加密后
         * @throws NoSuchAlgorithmException
         */
        @JvmStatic
        @Throws(NoSuchAlgorithmException::class)
        fun sha_256(password: String): String {
            val sha = ShaPasswordEncoder(256)
            sha.encodeHashAsBase64 = true
            return sha.encodePassword(password, null)
        }

        /**
         * sha 256
         *
         * @param password 密码
         * @return 加密后
         */
        @JvmStatic
        fun sha_SHA_256(password: String): String {
            val sha = ShaPasswordEncoder()
            sha.encodeHashAsBase64 = false
            return sha.encodePassword(password, null)
        }

        /**
         * 加盐加密
         *
         * @param password 密码
         * @return 加密后
         */
        @JvmStatic
        fun md5_SystemWideSaltSource(password: String): String {
            val md5 = Md5PasswordEncoder()
            md5.encodeHashAsBase64 = false

            // 使用动态加密盐的只需要在注册用户的时候将第二个参数换成用户名即可
            return md5.encodePassword(password, "acegisalt")
        }

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