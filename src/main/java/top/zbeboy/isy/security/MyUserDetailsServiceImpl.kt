package top.zbeboy.isy.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.AuthoritiesService
import java.util.*

/**
 * Created by zbeboy 2017-11-02 .
 **/
@Service("myUserDetailsService")
class MyUserDetailsServiceImpl : UserDetailsService {

    private val log = LoggerFactory.getLogger(MyUserDetailsServiceImpl::class.java)

    @Autowired
    private lateinit var usersService: UsersService

    @Autowired
    private lateinit var authoritiesService: AuthoritiesService

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(s: String): UserDetails {
        log.debug("Username is : {}", s)
        val username = StringUtils.trimWhitespace(s)
        val users = usersService.findByUsername(username)
        val authoritiesRecords = authoritiesService.findByUsername(username)
        val authorities = buildUserAuthority(authoritiesRecords)
        return buildUserForAuthentication(users, authorities)
    }

    /**
     * 返回验证角色
     *
     * @param authoritiesRecords 权限
     * @return 组装
     */
    private fun buildUserAuthority(authoritiesRecords: List<AuthoritiesRecord>): List<GrantedAuthority> {
        val setAuths = authoritiesRecords
                .map { SimpleGrantedAuthority(it.authority) }
                .toSet()
        return ArrayList(setAuths)
    }

    /**
     * 返回验证用户
     *
     * @param users       用户
     * @param authorities 权限
     * @return 组装
     */
    private fun buildUserForAuthentication(users: Users, authorities: List<GrantedAuthority>): MyUserImpl {
        var enable = false
        var username: String? = null
        var password: String? = null
        if (!ObjectUtils.isEmpty(users)) {
            if (!ObjectUtils.isEmpty(users.enabled) && users.enabled > 0) {
                enable = true
            }
            username = users.username
            password = users.password
        }
        return MyUserImpl(username, password, users, enable, true, true, true, authorities)
    }
}