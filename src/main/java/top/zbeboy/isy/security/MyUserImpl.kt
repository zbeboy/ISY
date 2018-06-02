package top.zbeboy.isy.security

import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.SpringSecurityCoreVersion
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.Assert
import top.zbeboy.isy.domain.tables.pojos.Users
import java.io.Serializable
import java.util.*

/**
 * Created by zbeboy 2017-11-02 .
 **/
class MyUserImpl(username: String?, password: String?, users: Users, enabled: Boolean, accountNonExpired: Boolean, credentialsNonExpired: Boolean, accountNonLocked: Boolean, authorities: Collection<GrantedAuthority>) : User(username, password, enabled,
        accountNonExpired, credentialsNonExpired,
        accountNonLocked, authorities) {
    var users: Users? = users
}