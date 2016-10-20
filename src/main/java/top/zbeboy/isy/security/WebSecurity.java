package top.zbeboy.isy.security;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.ApplicationRecord;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;
import top.zbeboy.isy.service.ApplicationService;
import top.zbeboy.isy.service.RoleApplicationService;
import top.zbeboy.isy.service.UsersService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lenovo on 2016-07-26.
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
     * @param authentication
     * @param request
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
        if (uri.equals("/web/menu/backstage")) {
            return true;
        }
        boolean hasRole = false;
        Result<Record> roles = usersService.findByUsernameWithRole(users.getUsername());// 已缓存
        if (roles.isNotEmpty()) {
            List<Role> roleList = roles.into(Role.class);
            List<Integer> roleIds = new ArrayList<>();
            roleIds.addAll(roleList.stream().map(Role::getRoleId).collect(Collectors.toList()));

            Result<RoleApplicationRecord> roleApplicationRecords = roleApplicationService.findInRoleIdsWithUsername(roleIds,users.getUsername());// 已缓存
            if (roleApplicationRecords.isNotEmpty()) {
                List<Integer> applicationIds = new ArrayList<>();
                // 防止重复菜单加载
                roleApplicationRecords.stream().filter(roleApplicationRecord -> !applicationIds.contains(roleApplicationRecord.getApplicationId())).forEach(roleApplicationRecord -> {// 防止重复菜单加载
                    applicationIds.add(roleApplicationRecord.getApplicationId());
                });

                Result<ApplicationRecord> applicationRecords = applicationService.findInIdsWithUsername(applicationIds,users.getUsername());// 已缓存
                for (ApplicationRecord applicationRecord : applicationRecords) {
                    if (uri.equals(applicationRecord.getApplicationUrl())) {
                        hasRole = true;
                        break;
                    }
                    if(StringUtils.hasLength(applicationRecord.getApplicationDataUrlStartWith())){
                        List<String> urlMapping = applicationService.urlMapping(applicationRecord);// 已缓存
                        if (!ObjectUtils.isEmpty(urlMapping) && urlMapping.contains(uri)) {
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
