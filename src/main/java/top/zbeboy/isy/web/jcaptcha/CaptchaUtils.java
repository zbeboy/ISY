package top.zbeboy.isy.web.jcaptcha;

import com.octo.captcha.service.CaptchaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lenovo on 2016-09-04.
 */
public class CaptchaUtils {

    private final Logger log = LoggerFactory.getLogger(CaptchaUtils.class);

    /**
     * 验证验证码
     *
     * @param captcha 验证码
     * @param request 请求
     * @return true or false
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
