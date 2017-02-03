package top.zbeboy.isy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.ApplicationService;
import top.zbeboy.isy.service.RoleApplicationService;
import top.zbeboy.isy.service.UsersService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Spring security 路径权限控制器
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
public class WebSecurity {

    private final Logger log = LoggerFactory.getLogger(WebSecurity.class);

    @Autowired
    private UsersService usersService;

    @Resource
    private RoleApplicationService roleApplicationService;

    @Resource
    private ApplicationService applicationService;

    /**
     * 权限控制检查
     *
     * @param authentication 权限对象
     * @param request        请求
     * @return true可访问 false 不可访问该路径
     */
    public boolean check(Authentication authentication, HttpServletRequest request) {
        Users users = usersService.getUserFromSession();
        if (ObjectUtils.isEmpty(users)) {
            return false;
        }
        // 权限控制
        String uri = StringUtils.trimAllWhitespace(request.getRequestURI());
        // 欢迎页
        if (uri.endsWith("/web/menu/backstage")) {
            return true;
        }
        boolean hasRole = false;
        List<Role> roleList = usersService.findByUsernameWithRole(users.getUsername());// 已缓存
        List<Integer> roleIds = new ArrayList<>();
        roleIds.addAll(roleList.stream().map(Role::getRoleId).collect(Collectors.toList()));

        List<RoleApplication> roleApplications = roleApplicationService.findInRoleIdsWithUsername(roleIds, users.getUsername());// 已缓存
        if (!roleApplications.isEmpty()) {
            List<Integer> applicationIds = new ArrayList<>();
            // 防止重复菜单加载
            roleApplications.stream().filter(roleApplication -> !applicationIds.contains(roleApplication.getApplicationId())).forEach(roleApplication -> {// 防止重复菜单加载
                applicationIds.add(roleApplication.getApplicationId());
            });

            List<Application> applications = applicationService.findInIdsWithUsername(applicationIds, users.getUsername());// 已缓存
            for (Application application : applications) {
                if (uri.endsWith(application.getApplicationUrl())) {
                    hasRole = true;
                    break;
                }
                if (StringUtils.hasLength(application.getApplicationDataUrlStartWith())) {
                    List<String> urlMapping = applicationService.urlMapping(application);// 已缓存
                    if (!ObjectUtils.isEmpty(urlMapping)) {
                        Optional<String> urlOne = urlMapping.stream().filter(uri::endsWith).findFirst();
                        if (urlOne.isPresent()) {
                            hasRole = true;
                            break;
                        }
                    }
                }
            }
        }
        return hasRole;
    }
}
