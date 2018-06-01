package top.zbeboy.isy.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
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
 * Returns a 401 error code (Unauthorized) to the client, when Ajax authentication fails.
 **/
@Component
class AjaxAuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse,
                                         exception: AuthenticationException) {
        val context = request.session.servletContext
        val ctx = WebApplicationContextUtils
                .getWebApplicationContext(context)
        val systemLog = SystemLogElastic(UUIDUtils.getUUID(), "登录系统失败", Timestamp(Clock.systemDefaultZone().millis()), request.getParameter("username"), RequestUtils.getIpAddress(request)!!)
        val systemLogGlue = ctx!!
                .getBean("systemLogGlue") as SystemLogGlue
        systemLogGlue.save(systemLog)
        response.writer.print(AjaxAuthenticationCode.AU_ERROR_CODE)
    }
}