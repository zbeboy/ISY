package top.zbeboy.isy.interceptor;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.ApplicationService;
import top.zbeboy.isy.service.UsersService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by lenovo on 2016-09-28.
 */
public class MenuInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(MenuInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!ObjectUtils.isEmpty(modelAndView)) {
            String web_path = request.getContextPath();
            ServletContext context = request.getSession().getServletContext();
            ApplicationContext ctx = WebApplicationContextUtils
                    .getWebApplicationContext(context);
            UsersService usersService = (UsersService) ctx
                    .getBean("usersService");
            ApplicationService applicationService = (ApplicationService) ctx
                    .getBean("applicationService");
            Users users = usersService.getUserFromSession();
            if (!ObjectUtils.isEmpty(users)) {
                Result<Record> roles = usersService.findByUsernameWithRole(users.getUsername());// 已缓存
                if (roles.isNotEmpty()) {
                    List<Role> roleList = roles.into(Role.class);
                    String menuHtml = applicationService.menuHtml(roleList, web_path, users.getUsername());
                    request.setAttribute("menu", menuHtml);
                }
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
