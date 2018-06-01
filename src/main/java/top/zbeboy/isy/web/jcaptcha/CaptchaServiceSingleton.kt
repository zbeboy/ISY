package top.zbeboy.isy.web.jcaptcha

import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService
import com.octo.captcha.service.image.ImageCaptchaService

/**
 * Created by zbeboy 2017-11-20 .
 **/
open class CaptchaServiceSingleton {
    companion object {
        private val instance = DefaultManageableImageCaptchaService(FastHashMapCaptchaStore(), GMailEngine(), 180, 100000, 75000)

        @JvmStatic
        fun getInstance(): ImageCaptchaService {
            return instance
        }
    }
}