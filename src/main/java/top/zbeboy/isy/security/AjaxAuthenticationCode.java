package top.zbeboy.isy.security;

/**
 * Created by lenovo on 2016-09-04.
 */
public class AjaxAuthenticationCode {

    /*
    权限异常
     */
    public static final int AU_ERROR_CODE = 1;

    /*
    验证码错误
     */
    public static final int CAPTCHA_ERROR_CODE = 2;

    /*
    全部正确
     */
    public static final int OK_CODE = 3;

    /*
    用户所在院校或班级被注销
     */
    public static final int SCHOOL_IS_DEL_CODE = 4;

    /*
    账号不存在
     */
    public static final int USERNAME_IS_NOT_EXIST_CODE = 5;

    /*
    验证码为空
     */
    public static final int CAPTCHA_IS_BLANK = 6;

    /*
   密码为空
    */
    public static final int PASSWORD_IS_BLANK = 7;

    /*
   邮箱为空
    */
    public static final int EMAIL_IS_BLANK = 8;

    /*
    邮箱未验证
     */
    public static final int EMAIL_IS_NOT_VALID = 9;

    /*
    账号已被注销
     */
    public static final int USERNAME_IS_ENABLES = 10;
}
