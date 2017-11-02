package top.zbeboy.isy.interceptor

import org.springframework.util.ObjectUtils
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.platform.UsersService
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-11-02 .
 **/
class MenuInterceptor : HandlerInterceptor {
    override fun preHandle(p0: HttpServletRequest?, p1: HttpServletResponse?, p2: Any?): Boolean {
        return true
    }

    override fun postHandle(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?, modelAndView: ModelAndView?) {
        val context = request!!.session.servletContext
        val ctx = WebApplicationContextUtils
                .getWebApplicationContext(context)
        val usersService = ctx
                .getBean("usersService") as UsersService
        val cacheManageService = ctx
                .getBean("cacheManageService") as CacheManageService
        val users = usersService.userFromSession
        if (!ObjectUtils.isEmpty(users)) {
            val roleList = cacheManageService.findByUsernameWithRole(users.username)// 已缓存
            val menuHtml = cacheManageService.menuHtml(roleList, users.username)
            request.setAttribute("menu", menuHtml)
        }
    }

    override fun afterCompletion(p0: HttpServletRequest?, p1: HttpServletResponse?, p2: Any?, p3: Exception?) {}
}