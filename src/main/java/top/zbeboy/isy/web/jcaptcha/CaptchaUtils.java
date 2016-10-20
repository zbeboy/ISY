package top.zbeboy.isy.web.jcaptcha;

import com.octo.captcha.service.CaptchaServiceException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lenovo on 2016-09-04.
 */
public class CaptchaUtils {

    /**
     * 验证验证码
     *
     * @param captcha
     * @param request
     * @return
     */
    public static boolean validCaptcha(String captcha, HttpServletRequest request) {
        Boolean isResponseCorrect = Boolean.FALSE;
        // remenber that we need an id to validate!
        String captchaId = request.getSession().getId();
        // call the service method
        try {
            isResponseCorrect = CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId, captcha);
            return isResponseCorrect;
        } catch (CaptchaServiceException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
}
