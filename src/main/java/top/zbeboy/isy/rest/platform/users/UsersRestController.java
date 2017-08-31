/*
 * Copyright (ISY Team) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package top.zbeboy.isy.rest.platform.users;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.pojos.UsersType;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
import java.security.Principal;

@Slf4j
@RestController
public class UsersRestController {

    @Resource
    private UsersService usersService;

    @Resource
    private CacheManageService cacheManageService;

    /**
     * 获取用户类型
     *
     * @param user 用户凭证
     * @return 类型
     */
    @GetMapping("/rest/user/type")
    public AjaxUtils userType(Principal user) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!ObjectUtils.isEmpty(user) && StringUtils.isNotBlank(user.getName())) {
            Users users = usersService.findByUsername(user.getName());
            if (!ObjectUtils.isEmpty(user)) {
                UsersType usersType = cacheManageService.findByUsersTypeId(users.getUsersTypeId());
                if (!ObjectUtils.isEmpty(usersType)) {
                    ajaxUtils.success().msg("获取用户类型成功").obj(usersType);
                } else {
                    ajaxUtils.fail().msg("未获取到用户类型");
                }
            } else {
                ajaxUtils.fail().msg("未获取到用户相关信息");
            }
        } else {
            ajaxUtils.fail().msg("用户可能未授权");
        }
        return ajaxUtils;
    }
}
