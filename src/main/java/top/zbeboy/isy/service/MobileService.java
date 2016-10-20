package top.zbeboy.isy.service;

/**
 * Created by lenovo on 2016-05-17.
 */
public interface MobileService {

    /**
     * 发送短信
     *
     * @param mobile
     * @param content
     */
    void sendShortMessage(String mobile, String content);

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @param verificationCode
     */
    void sendValidMobileShortMessage(String mobile, String verificationCode);
}
