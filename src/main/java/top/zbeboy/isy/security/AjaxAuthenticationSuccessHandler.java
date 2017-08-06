package top.zbeboy.isy.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import top.zbeboy.isy.elastic.pojo.SystemLogElastic;
import top.zbeboy.isy.glue.system.SystemLogGlue;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Clock;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Slf4j
@Component
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        ServletContext context = request.getSession().getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(context);
        SystemLogElastic systemLog = new SystemLogElastic(UUIDUtils.getUUID(), "登录系统成功", new Timestamp(Clock.systemDefaultZone().millis()), request.getParameter("username"), RequestUtils.getIpAddress(request));
        SystemLogGlue systemLogGlue = (SystemLogGlue) ctx
                .getBean("systemLogGlue");
        systemLogGlue.save(systemLog);
        response.getWriter().print(AjaxAuthenticationCode.OK_CODE);
    }
}
