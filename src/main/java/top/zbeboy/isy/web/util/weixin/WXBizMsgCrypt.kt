package top.zbeboy.isy.web.util.weixin

import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.time.Clock
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

/**
 * 提供接收和推送给公众平台消息的加解密接口(UTF8编码的字符串).
 * <ol>
 * 	<li>第三方回复加密消息给公众平台</li>
 * 	<li>第三方收到公众平台发送的消息，验证消息的安全性，并对消息进行解密。</li>
 * </ol>
 * 说明：异常java.security.InvalidKeyException:illegal Key Size的解决方案
 * <ol>
 * 	<li>在官方网站下载JCE无限制权限策略文件（JDK7的下载地址：
 *      http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html</li>
 * 	<li>下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt</li>
 * 	<li>如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件</li>
 * 	<li>如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件</li>
 * </ol>
 */
class WXBizMsgCrypt
/**
 * 构造函数
 * @param token 公众平台上，开发者设置的token
 * @param encodingAesKey 公众平台上，开发者设置的EncodingAESKey
 * @param appId 公众平台appid
 *
 * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
 */ @Throws(AesException::class) constructor(private val token: String, encodingAesKey: String, private val appId: String) {

    private val log = LoggerFactory.getLogger(WXBizMsgCrypt::class.java)

    private val CHARSET = Charset.forName("utf-8")
    private val base64 = Base64()
    private var aesKey: ByteArray

    // 生成4个字节的网络字节序
    private fun getNetworkBytesOrder(sourceNumber: Int): ByteArray {
        val orderBytes = ByteArray(4)
        orderBytes[3] = (sourceNumber and 0xFF).toByte()
        orderBytes[2] = (sourceNumber shr 8 and 0xFF).toByte()
        orderBytes[1] = (sourceNumber shr 16 and 0xFF).toByte()
        orderBytes[0] = (sourceNumber shr 24 and 0xFF).toByte()
        return orderBytes
    }

    // 还原4个字节的网络字节序
    private fun recoverNetworkBytesOrder(orderBytes: ByteArray): Int {
        var sourceNumber = 0
        for (i in 0..3) {
            sourceNumber = sourceNumber shl 8
            sourceNumber = sourceNumber or ((orderBytes[i] and 0xff.toByte()).toInt())
        }
        return sourceNumber
    }

    // 随机生成16位字符串
    private fun getRandomStr(): String {
        val base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        val sb = StringBuilder()
        for (i in 0..15) {
            val number = random.nextInt(base.length)
            sb.append(base[number])
        }
        return sb.toString()
    }

    /**
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     * @throws AesException aes加密失败
     */
    @Throws(AesException::class)
    fun encrypt(randomStr: String, text: String): String {
        val byteCollector = ByteGroup()
        val randomStrBytes = randomStr.toByteArray(CHARSET)
        val textBytes = text.toByteArray(CHARSET)
        val networkBytesOrder = getNetworkBytesOrder(textBytes.size)
        val appidBytes = appId.toByteArray(CHARSET)

        // randomStr + networkBytesOrder + text + appid
        byteCollector.addBytes(randomStrBytes)
        byteCollector.addBytes(networkBytesOrder)
        byteCollector.addBytes(textBytes)
        byteCollector.addBytes(appidBytes)

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        val padBytes = PKCS7Encoder.encode(byteCollector.size())
        byteCollector.addBytes(padBytes)

        // 获得最终的字节流, 未加密
        val unencrypted = byteCollector.toBytes()

        try {
            // 设置加密模式为AES的CBC模式
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val keySpec = SecretKeySpec(aesKey, "AES")
            val iv = IvParameterSpec(aesKey, 0, 16)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)

            // 加密
            val encrypted = cipher.doFinal(unencrypted)

            // 使用BASE64对加密后的字符串进行编码

            return base64.encodeToString(encrypted)
        } catch (e: Exception) {
            log.error("微信对明文加密 error : {}", e)
            throw AesException(AesException.EncryptAESError)
        }

    }

    /**
     * 对密文进行解密.
     *
     * @param text 需要解密的密文
     * @return 解密得到的明文
     * @throws AesException aes解密失败
     */
    @Throws(AesException::class)
    private fun decrypt(text: String): String {
        val original: ByteArray
        try {
            // 设置解密模式为AES的CBC模式
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val key_spec = SecretKeySpec(aesKey, "AES")
            val iv = IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16))
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv)

            // 使用BASE64对密文进行解码
            val encrypted = Base64.decodeBase64(text)

            // 解密
            original = cipher.doFinal(encrypted)
        } catch (e: Exception) {
            log.error("微信对明文解密 error : {}", e)
            throw AesException(AesException.DecryptAESError)
        }

        val xmlContent: String
        val from_appid: String
        try {
            // 去除补位字符
            val bytes = PKCS7Encoder.decode(original)

            // 分离16位随机字符串,网络字节序和AppId
            val networkOrder = Arrays.copyOfRange(bytes, 16, 20)

            val xmlLength = recoverNetworkBytesOrder(networkOrder)

            xmlContent = String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET)
            from_appid = String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.size),
                    CHARSET)
        } catch (e: Exception) {
            log.error("微信去除补位字符 error : {}", e)
            throw AesException(AesException.IllegalBuffer)
        }

        // appid不相同的情况
        if (from_appid != appId) {
            throw AesException(AesException.ValidateAppidError)
        }
        return xmlContent

    }

    /**
     * 将公众平台回复用户的消息加密打包.
     *
     *  1. 对要发送的消息进行AES-CBC加密
     *  1. 生成安全签名
     *  1. 将消息密文和安全签名打包成xml格式
     *
     *
     * @param replyMsg 公众平台待回复用户的消息，xml格式的字符串
     * @param timeStamp 时间戳，可以自己生成，也可以用URL参数的timestamp
     * @param nonce 随机串，可以自己生成，也可以用URL参数的nonce
     *
     * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce, encrypt的xml格式的字符串
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    @Throws(AesException::class)
    fun encryptMsg(replyMsg: String, timeStamp: String, nonce: String): String {
        var tempTimeStamp = timeStamp
        // 加密
        val encrypt = encrypt(getRandomStr(), replyMsg)

        // 生成安全签名
        if (tempTimeStamp == "") {
            tempTimeStamp = java.lang.Long.toString(Clock.systemDefaultZone().millis())
        }

        val signature = SHA1.getSHA1(token, tempTimeStamp, nonce, encrypt)

        // System.out.println("发送给平台的签名是: " + signature[1].toString());
        // 生成发送的xml
        return XMLParse.generate(encrypt, signature, tempTimeStamp, nonce)
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     *
     *  1. 利用收到的密文生成安全签名，进行签名验证
     *  1. 若验证通过，则提取xml中的加密消息
     *  1. 对消息进行解密
     *
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param postData 密文，对应POST请求的数据
     *
     * @return 解密后的原文
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    @Throws(AesException::class)
    fun decryptMsg(msgSignature: String, timeStamp: String, nonce: String, postData: String): String {

        // 密钥，公众账号的app secret
        // 提取密文
        val encrypt = XMLParse.extract(postData)

        // 验证安全签名
        val signature = SHA1.getSHA1(token, timeStamp, nonce, encrypt[1].toString())

        // 和URL中的签名比较是否相等
        // System.out.println("第三方收到URL中的签名：" + msg_sign);
        // System.out.println("第三方校验签名：" + signature);
        if (signature != msgSignature) {
            throw AesException(AesException.ValidateSignatureError)
        }

        // 解密
        return decrypt(encrypt[1].toString())
    }

    /**
     * 验证URL
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param echoStr 随机串，对应URL参数的echostr
     *
     * @return 解密之后的echostr
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    @Throws(AesException::class)
    fun verifyUrl(msgSignature: String, timeStamp: String, nonce: String, echoStr: String): String {
        val signature = SHA1.getSHA1(token, timeStamp, nonce, echoStr)

        if (signature != msgSignature) {
            throw AesException(AesException.ValidateSignatureError)
        }

        return decrypt(echoStr)
    }

    init {
        if (encodingAesKey.length != 43) {
            throw AesException(AesException.IllegalAesKey)
        }
        aesKey = Base64.decodeBase64(encodingAesKey + "=")
    }
}