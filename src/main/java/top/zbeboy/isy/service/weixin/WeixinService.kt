package top.zbeboy.isy.service.weixin

import top.zbeboy.isy.web.vo.weixin.WeixinVo

/**
 * Created by zbeboy 2017-11-20 .
 **/
interface WeixinService {
    /**
     * 检验签名
     *
     * @param weixinVo 参数
     * @return true or false
     */
    fun checkSignature(weixinVo: WeixinVo): Boolean

    /**
     * 加密消息
     *
     * @param msg      消息
     * @param weixinVo 微信参数
     * @return 加密后的内容
     */
    fun encryptMsg(msg: String, weixinVo: WeixinVo): String

    /**
     * 解密消息
     *
     * @param msg      消息
     * @param weixinVo 微信参数
     * @return 加密后的内容
     */
    fun decryptMsg(msg: String, weixinVo: WeixinVo): String
}