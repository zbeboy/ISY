package top.zbeboy.isy.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.context.support.WebApplicationContextUtils
import top.zbeboy.isy.elastic.pojo.SystemLogElastic
import top.zbeboy.isy.glue.system.SystemLogGlue
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import java.io.IOException
import java.sql.Timestamp
import java.time.Clock
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-11-02 .
 * Spring Security success handler, specialized for Ajax requests.
 **/
@Component
class AjaxAuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse,
                                         authentication: Authentication) {
        val context = request.session.servletContext
        val ctx = WebApplicationContextUtils
                .getWebApplicationContext(context)
        val systemLog = SystemLogElastic(UUIDUtils.getUUID(), "登录系统成功", Timestamp(Clock.systemDefaultZone().millis()), request.getParameter("username"), RequestUtils.getIpAddress(request)!!)
        val systemLogGlue = ctx!!
                .getBean("systemLogGlue") as SystemLogGlue
        systemLogGlue.save(systemLog)
        response.writer.print(AjaxAuthenticationCode.OK_CODE)
    }
}