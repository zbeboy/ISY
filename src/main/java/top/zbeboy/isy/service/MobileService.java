package top.zbeboy.isy.service;

/**
 * Created by lenovo on 2016-05-17.
 */
public interface MobileService {

    /**
     * 发送短信
     *
     * @param mobile  手机号
     * @param content 内容
     */
    void sendShortMessage(String mobile, String content);

    /**
     * 发送短信验证码
     *
     * @param mobile           手机号
     * @param verificationCode 验证码
     */
    void sendValidMobileShortMessage(String mobile, String verificationCode);
}
