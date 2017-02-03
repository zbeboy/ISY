package top.zbeboy.isy.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import top.zbeboy.isy.domain.tables.pojos.SystemLog;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.security.AjaxAuthenticationCode;
import top.zbeboy.isy.service.SystemLogService;
import top.zbeboy.isy.service.UsersService;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.jcaptcha.CaptchaUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Clock;

/**
 * Created by lenovo on 2016-09-03.
 * 安全登录配置
 */
public class SecurityLoginFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(SecurityLoginFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().endsWith("/login")) {
            String email = StringUtils.trimWhitespace(request.getParameter("username"));
            String password = StringUtils.trimWhitespace(request.getParameter("password"));
            String j_captcha_response = StringUtils.trimWhitespace(request.getParameter("j_captcha_response"));

            if (StringUtils.hasLength(email)) {// 邮箱不为空
                if (StringUtils.hasLength(password)) {// 密码不为空
                    if (StringUtils.hasLength(j_captcha_response)) {// 验证码不为空
                        if (CaptchaUtils.validCaptcha(j_captcha_response, request)) {// 检验验证码
                            ServletContext context = request.getSession().getServletContext();
                            ApplicationContext ctx = WebApplicationContextUtils
                                    .getWebApplicationContext(context);
                            UsersService usersService = (UsersService) ctx
                                    .getBean("usersService");
                            Users users = usersService.findByUsername(email);
                            if (!ObjectUtils.isEmpty(users)) {// 用户是否存在
                                if (!ObjectUtils.isEmpty(users.getEnabled()) && users.getEnabled() == 1) {// 用户是否已被注销
                                    if (!ObjectUtils.isEmpty(users.getVerifyMailbox()) && users.getVerifyMailbox() == 1) {// 用户邮箱是否已被验证
                                        boolean isDel = usersService.validSCDSOIsDel(users);
                                        if (!isDel) {// 用户所在院校是否已被注销
                                            String ip = RequestUtils.getIpAddress(request);
                                            SystemLog systemLog = new SystemLog(UUIDUtils.getUUID(), "登录系统", new Timestamp(Clock.systemDefaultZone().millis()), users.getUsername(), ip);
                                            SystemLogService systemLogService = (SystemLogService) ctx
                                                    .getBean("systemLogService");
                                            systemLogService.save(systemLog);
                                            filterChain.doFilter(servletRequest, servletResponse);
                                        } else {
                                            response.getWriter().print(AjaxAuthenticationCode.SCHOOL_IS_DEL_CODE);
                                        }
                                    } else {
                                        response.getWriter().print(AjaxAuthenticationCode.EMAIL_IS_NOT_VALID);
                                    }
                                } else {
                                    response.getWriter().print(AjaxAuthenticationCode.USERNAME_IS_ENABLES);
                                }
                            } else {
                                response.getWriter().print(AjaxAuthenticationCode.USERNAME_IS_NOT_EXIST_CODE);
                            }
                        } else {
                            response.getWriter().print(AjaxAuthenticationCode.CAPTCHA_ERROR_CODE);
                        }
                    } else {
                        response.getWriter().print(AjaxAuthenticationCode.CAPTCHA_IS_BLANK);
                    }
                } else {
                    response.getWriter().print(AjaxAuthenticationCode.PASSWORD_IS_BLANK);
                }
            } else {
                response.getWriter().print(AjaxAuthenticationCode.EMAIL_IS_BLANK);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }


}
