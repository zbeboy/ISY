package top.zbeboy.isy.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.platform.UsersService;

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
@Slf4j
public class WebSecurity {

    @Autowired
    private UsersService usersService;

    @Resource
    private CacheManageService cacheManageService;

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
        List<Role> roleList = cacheManageService.findByUsernameWithRole(users.getUsername());// 已缓存
        List<String> roleIds = new ArrayList<>();
        roleIds.addAll(roleList.stream().map(Role::getRoleId).collect(Collectors.toList()));

        List<RoleApplication> roleApplications = cacheManageService.findInRoleIdsWithUsername(roleIds, users.getUsername());// 已缓存
        if (!roleApplications.isEmpty()) {
            List<String> applicationIds = new ArrayList<>();
            // 防止重复菜单加载
            roleApplications.stream().filter(roleApplication -> !applicationIds.contains(roleApplication.getApplicationId())).forEach(roleApplication -> {// 防止重复菜单加载
                applicationIds.add(roleApplication.getApplicationId());
            });

            List<Application> applications = cacheManageService.findInIdsWithUsername(applicationIds, users.getUsername());// 已缓存
            for (Application application : applications) {
                if (uri.endsWith(application.getApplicationUrl())) {
                    hasRole = true;
                    break;
                }
                if (StringUtils.hasLength(application.getApplicationDataUrlStartWith())) {
                    List<String> urlMapping = cacheManageService.urlMapping(application);// 已缓存
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
