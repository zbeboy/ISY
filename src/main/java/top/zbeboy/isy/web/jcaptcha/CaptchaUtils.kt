package top.zbeboy.isy.web.jcaptcha

import com.octo.captcha.service.CaptchaServiceException
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-11-20 .
 **/
open class CaptchaUtils {
    companion object {

        private val log = LoggerFactory.getLogger(CaptchaUtils::class.java)

        /**
         * 验证验证码
         *
         * @param captcha 验证码
         * @param request 请求
         * @return true or false
         */
        @JvmStatic
        fun validCaptcha(captcha: String, request: HttpServletRequest): Boolean {
            val isResponseCorrect: Boolean?
            // remenber that we need an id to validate!
            val captchaId = request.session.id
            // call the service method
            try {
                isResponseCorrect = CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId, captcha)
                return isResponseCorrect!!
            } catch (e: CaptchaServiceException) {
                log.error("Captcha error : {}", e)
                return java.lang.Boolean.FALSE
            }

        }
    }
}