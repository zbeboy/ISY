package top.zbeboy.isy.service.common

import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import top.zbeboy.isy.config.ISYProperties
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

/**
 * Created by zbeboy 2017-12-31 .
 **/
@Service("desService")
open class DesServiceImpl : DesService {

    val DES = "DES"

    @Autowired
    lateinit open var isyProperties: ISYProperties

    override fun encrypt(data: String): String {
        val bt = encrypt(data.toByteArray(Charsets.UTF_8), isyProperties.getSecurity().desDefaultKey!!.toByteArray(Charsets.UTF_8))
        return Base64.encodeBase64String(bt)
    }

    override fun decrypt(data: String?): String? {
        if (data == null)
            return null
        val buf = Base64.decodeBase64(data)
        val bt = decrypt(buf, isyProperties.getSecurity().desDefaultKey!!.toByteArray(Charsets.UTF_8))
        return String(bt, Charsets.UTF_8)
    }

    override fun encrypt(data: String, key: String): String {
        val bt = encrypt(data.toByteArray(Charsets.UTF_8), key.toByteArray(Charsets.UTF_8))
        return Base64.encodeBase64String(bt)
    }

    override fun decrypt(data: String?, key: String): String? {
        if (data == null)
            return null
        val buf = Base64.decodeBase64(data)
        val bt = decrypt(buf, key.toByteArray(Charsets.UTF_8))
        return String(bt, Charsets.UTF_8)
    }

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key
     */
    private fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        // 生成一个可信任的随机数源
        val sr = SecureRandom()

        // 从原始密钥数据创建DESKeySpec对象
        val dks = DESKeySpec(key)

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        val keyFactory = SecretKeyFactory.getInstance(DES)
        val securekey = keyFactory.generateSecret(dks)

        // Cipher对象实际完成加密操作
        val cipher = Cipher.getInstance(DES)

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr)

        return cipher.doFinal(data)
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key
     */
    private fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        // 生成一个可信任的随机数源
        val sr = SecureRandom()

        // 从原始密钥数据创建DESKeySpec对象
        val dks = DESKeySpec(key)

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        val keyFactory = SecretKeyFactory.getInstance(DES)
        val securekey = keyFactory.generateSecret(dks)

        // Cipher对象实际完成解密操作
        val cipher = Cipher.getInstance(DES)

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr)

        return cipher.doFinal(data)
    }
}