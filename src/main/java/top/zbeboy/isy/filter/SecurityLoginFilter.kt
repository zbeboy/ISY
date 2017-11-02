package top.zbeboy.isy.filter

import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.context.support.WebApplicationContextUtils
import top.zbeboy.isy.security.AjaxAuthenticationCode
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.web.jcaptcha.CaptchaUtils
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-11-02 .
 **/
class SecurityLoginFilter : Filter {
    override fun destroy() {}

    override fun doFilter(servletRequest: ServletRequest?, servletResponse: ServletResponse?, filterChain: FilterChain?) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse
        if ("POST".equals(request.method, ignoreCase = true) && request.requestURI.endsWith("/login")) {
            val email = StringUtils.trimWhitespace(request.getParameter("username"))
            val password = StringUtils.trimWhitespace(request.getParameter("password"))
            val j_captcha_response = StringUtils.trimWhitespace(request.getParameter("j_captcha_response"))

            if (StringUtils.hasLength(email)) {// 邮箱不为空
                if (StringUtils.hasLength(password)) {// 密码不为空
                    if (StringUtils.hasLength(j_captcha_response)) {// 验证码不为空
                        if (CaptchaUtils.validCaptcha(j_captcha_response, request)) {// 检验验证码
                            val context = request.session.servletContext
                            val ctx = WebApplicationContextUtils
                                    .getWebApplicationContext(context)
                            val usersService = ctx
                                    .getBean("usersService") as UsersService
                            val users = usersService.findByUsername(email)
                            if (!ObjectUtils.isEmpty(users)) {// 用户是否存在
                                if (!ObjectUtils.isEmpty(users.enabled) && users.enabled > 0) {// 用户是否已被注销
                                    if (!ObjectUtils.isEmpty(users.verifyMailbox) && users.verifyMailbox > 0) {// 用户邮箱是否已被验证
                                        val isDel = usersService.validSCDSOIsDel(users)
                                        if (!isDel) {// 用户所在院校是否已被注销
                                            filterChain!!.doFilter(servletRequest, servletResponse)
                                        } else {
                                            response.writer.print(AjaxAuthenticationCode.SCHOOL_IS_DEL_CODE)
                                        }
                                    } else {
                                        response.writer.print(AjaxAuthenticationCode.EMAIL_IS_NOT_VALID)
                                    }
                                } else {
                                    response.writer.print(AjaxAuthenticationCode.USERNAME_IS_ENABLES)
                                }
                            } else {
                                response.writer.print(AjaxAuthenticationCode.USERNAME_IS_NOT_EXIST_CODE)
                            }
                        } else {
                            response.writer.print(AjaxAuthenticationCode.CAPTCHA_ERROR_CODE)
                        }
                    } else {
                        response.writer.print(AjaxAuthenticationCode.CAPTCHA_IS_BLANK)
                    }
                } else {
                    response.writer.print(AjaxAuthenticationCode.PASSWORD_IS_BLANK)
                }
            } else {
                response.writer.print(AjaxAuthenticationCode.EMAIL_IS_BLANK)
            }
        } else {
            filterChain!!.doFilter(servletRequest, servletResponse)
        }
    }

    override fun init(p0: FilterConfig?) {}
}