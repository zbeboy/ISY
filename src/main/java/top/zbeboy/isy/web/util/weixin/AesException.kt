package top.zbeboy.isy.web.util.weixin

/**
 * Created by zbeboy 2017-11-20 .
 **/
open class AesException(val code: Int) : Exception(AesException(code).getMessage(code)) {

    companion object {
        @JvmField
        val OK = 0
        @JvmField
        val ValidateSignatureError = -40001
        @JvmField
        val ParseXmlError = -40002
        @JvmField
        val ComputeSignatureError = -40003
        @JvmField
        val IllegalAesKey = -40004
        @JvmField
        val ValidateAppidError = -40005
        @JvmField
        val EncryptAESError = -40006
        @JvmField
        val DecryptAESError = -40007
        @JvmField
        val IllegalBuffer = -40008
    }

    private fun getMessage(code: Int): String? {
        return when (code) {
            ValidateSignatureError -> "签名验证错误"
            ParseXmlError -> "xml解析失败"
            ComputeSignatureError -> "sha加密生成签名失败"
            IllegalAesKey -> "SymmetricKey非法"
            ValidateAppidError -> "appid校验失败"
            EncryptAESError -> "aes加密失败"
            DecryptAESError -> "aes解密失败"
            IllegalBuffer -> "解密后得到的buffer非法"
            else -> null // cannot be
        }
    }

}