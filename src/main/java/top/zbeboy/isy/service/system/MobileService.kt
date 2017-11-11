package top.zbeboy.isy.service.system

/**
 * Created by zbeboy 2017-11-11 .
 **/
interface MobileService {

    /**
     * 发送短信
     *
     * @param mobile  手机号
     * @param content 内容
     */
    fun sendShortMessage(mobile: String, content: String)

    /**
     * 发送短信验证码
     *
     * @param mobile           手机号
     * @param verificationCode 验证码
     */
    fun sendValidMobileShortMessage(mobile: String, verificationCode: String)
}