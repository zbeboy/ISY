package top.zbeboy.isy.test

import junit.framework.TestCase
import top.zbeboy.isy.service.util.BCryptUtils
import top.zbeboy.isy.service.util.MD5Utils
import top.zbeboy.isy.service.util.UUIDUtils
import java.security.NoSuchAlgorithmException

/**
 * Created by zbeboy 2017-10-29 .
 **/
class TestUtil : TestCase() {

    fun testBCryptUtils() {
        BCryptUtils.bCryptPassword("123456")
    }

    @Throws(NoSuchAlgorithmException::class)
    fun testMD5Utils() {
        MD5Utils.md5("1234") // 使用简单的MD5加密方式

        MD5Utils.sha_256("1234") // 使用256的哈希算法(SHA)加密

        MD5Utils.sha_SHA_256("1234") // 使用SHA-256的哈希算法(SHA)加密

        MD5Utils.md5_SystemWideSaltSource("1234") // 使用MD5再加全局加密盐加密的方式加密
    }

    fun testUUIDUtils() {
        println("::" + UUIDUtils.getUUID())
        val ss = UUIDUtils.getUUID(20)
        for (s in ss) {
            println(s)
        }
    }
}