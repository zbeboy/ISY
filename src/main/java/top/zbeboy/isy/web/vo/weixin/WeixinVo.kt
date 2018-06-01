package top.zbeboy.isy.web.vo.weixin

/**
 * Created by zbeboy 2017-11-20 .
 **/
open class WeixinVo {
    /*
    微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数
     */
    var signature: String? = null
    /*
    时间戳
     */
    var timestamp: String? = null
    /*
    随机数
     */
    var nonce: String? = null
    /*
    随机字符串
     */
    var echostr: String? = null
    /*
    加密类型，为aes
     */
    var encrypt_type: String? = null
    /*
    消息体签名，用于验证消息体的正确性
     */
    var msg_signature: String? = null
    /*
    token
     */
    var token: String? = null
    /*
    EncodingAESKey
     */
    var encodingAESKey: String? = null
    /*
    app
     */
    var appId: String? = null
    var appSecret: String? = null
}