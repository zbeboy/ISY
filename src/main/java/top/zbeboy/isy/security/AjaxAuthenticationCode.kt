package top.zbeboy.isy.security

/**
 * Created by zbeboy 2017-11-02 .
 * 安全登录错误码
 **/
class AjaxAuthenticationCode {
    companion object {
        /*
        权限异常
        */
        @JvmField
        val AU_ERROR_CODE = 1

        /*
        验证码错误
        */
        @JvmField
        val CAPTCHA_ERROR_CODE = 2

        /*
        全部正确
        */
        @JvmField
        val OK_CODE = 3

        /*
        用户所在院校或班级被注销
        */
        @JvmField
        val SCHOOL_IS_DEL_CODE = 4

        /*
        账号不存在
        */
        @JvmField
        val USERNAME_IS_NOT_EXIST_CODE = 5

        /*
        验证码为空
        */
        @JvmField
        val CAPTCHA_IS_BLANK = 6

        /*
        密码为空
        */
        @JvmField
        val PASSWORD_IS_BLANK = 7

        /*
        邮箱为空
        */
        @JvmField
        val EMAIL_IS_BLANK = 8

        /*
        邮箱未验证
        */
        @JvmField
        val EMAIL_IS_NOT_VALID = 9

        /*
        账号已被注销
        */
        @JvmField
        val USERNAME_IS_ENABLES = 10
    }
}