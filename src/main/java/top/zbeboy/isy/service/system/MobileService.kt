package top.zbeboy.isy.service.system

/**
 * Created by zbeboy 2017-11-11 .
 **/
interface MobileService {

    /*
     * @param url
     * ：必填--发送连接地址URL——http://sms.kingtto.com:9999/sms.aspx
     * @param userId
     * ：必填--用户ID，为数字
     * @param account
     * ：必填--用户帐号
     * @param password
     * ：必填--用户密码
     * @param mobile
     * ：必填--发送的手机号码，多个可以用逗号隔比如>130xxxxxxxx,131xxxxxxxx
     * @param content
     * ：必填--实际发送内容，
     * @param action
     * ：选填--访问的事件，默认为send
     * @param sendType
     * ：选填--发送方式，默认为POST
     * @param codingType
     * ：选填--发送内容编码方式，默认为UTF-8
     * @param backEncodeType
     * ：选填--返回内容编码方式，默认为UTF-8
     * @return 返回发送之后收到的信息
     */
    fun sendShortMessage(mobile: String, content: String, action: String?,
                         sendType: String?, codingType: String?, backEncodeType: String?)

    /**
     * 发送短信验证码
     *
     * @param mobile           手机号
     * @param verificationCode 验证码
     */
    fun sendValidMobileShortMessage(mobile: String, verificationCode: String)
}