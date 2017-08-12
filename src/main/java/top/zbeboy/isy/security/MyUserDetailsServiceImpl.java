package top.zbeboy.isy.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.system.AuthoritiesService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * spring security userdetails实现类.
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service("myUserDetailsService")
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersService usersService;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.debug("Username is : {}", s);
        String username = StringUtils.trimWhitespace(s);
        Users users = usersService.findByUsername(username);
        List<AuthoritiesRecord> authoritiesRecords = authoritiesService.findByUsername(username);
        List<GrantedAuthority> authorities = buildUserAuthority(authoritiesRecords);
        return buildUserForAuthentication(users, authorities);
    }

    /**
     * 返回验证角色
     *
     * @param authoritiesRecords 权限
     * @return 组装
     */
    private List<GrantedAuthority> buildUserAuthority(List<AuthoritiesRecord> authoritiesRecords) {
        Set<GrantedAuthority> setAuths = new HashSet<>();
        for (AuthoritiesRecord userRole : authoritiesRecords) {
            setAuths.add(new SimpleGrantedAuthority(userRole.getAuthority()));
        }
        return new ArrayList<>(setAuths);
    }

    /**
     * 返回验证用户
     *
     * @param users       用户
     * @param authorities 权限
     * @return 组装
     */
    private MyUserImpl buildUserForAuthentication(Users users, List<GrantedAuthority> authorities) {
        boolean enable = false;
        String username = null;
        String password = null;
        if (!ObjectUtils.isEmpty(users)) {
            if (!ObjectUtils.isEmpty(users.getEnabled()) && users.getEnabled() == 1) {
                enable = true;
            }
            username = users.getUsername();
            password = users.getPassword();
        }
        return new MyUserImpl(username, password, users, enable, true, true, true, authorities);
    }
}
