package top.zbeboy.isy.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.platform.UsersService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by lenovo on 2016-09-28.
 * 菜单权限配置
 */
@Slf4j
public class MenuInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ServletContext context = request.getSession().getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(context);
        UsersService usersService = (UsersService) ctx
                .getBean("usersService");
        CacheManageService cacheManageService = (CacheManageService) ctx
                .getBean("cacheManageService");
        Users users = usersService.getUserFromSession();
        if (!ObjectUtils.isEmpty(users)) {
            List<Role> roleList = cacheManageService.findByUsernameWithRole(users.getUsername());// 已缓存
            String menuHtml = cacheManageService.menuHtml(roleList, users.getUsername());
            request.setAttribute("menu", menuHtml);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
