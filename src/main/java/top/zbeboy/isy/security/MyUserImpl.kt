package top.zbeboy.isy.security

import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.SpringSecurityCoreVersion
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.Assert
import top.zbeboy.isy.domain.tables.pojos.Users
import java.io.Serializable
import java.util.*

/**
 * Created by zbeboy 2017-11-02 .
 **/
class MyUserImpl : UserDetails, CredentialsContainer {

    private val serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID

    // ~ Instance fields
    // ================================================================================================
    private var password: String? = null
    private var users: Users? = null
    private var username: String = ""
    private var authorities: Set<GrantedAuthority>? = null
    private var accountNonExpired: Boolean = false
    private var accountNonLocked: Boolean = false
    private var credentialsNonExpired: Boolean = false
    private var enabled: Boolean = false

    // ~ Constructors
    // ===================================================================================================

    /**
     * Calls the more complex constructor with all boolean arguments set to `true`.
     */
    constructor(username: String, password: String, users: Users,
                authorities: Collection<GrantedAuthority>) {
        this(username, password, users, true, true, true, true, authorities)
    }

    constructor(username: String?, password: String?, users: Users, enabled: Boolean,
                accountNonExpired: Boolean, credentialsNonExpired: Boolean,
                accountNonLocked: Boolean, authorities: Collection<GrantedAuthority>) {
        this(username, password, users, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities)
    }

    private operator fun invoke(username: String?, password: String?, users: Users, enabled: Boolean,
                                accountNonExpired: Boolean, credentialsNonExpired: Boolean,
                                accountNonLocked: Boolean, authorities: Collection<GrantedAuthority>) {
        if (username == null || "" == username || password == null) {
            throw IllegalArgumentException(
                    "Cannot pass null or empty values to constructor")
        }

        this.username = username
        this.password = password
        this.users = users
        this.enabled = enabled
        this.accountNonExpired = accountNonExpired
        this.credentialsNonExpired = credentialsNonExpired
        this.accountNonLocked = accountNonLocked
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities))
    }

    // ~ Methods
    // ========================================================================================================

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities!!
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    fun getUsers(): Users {
        return users!!
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    override fun eraseCredentials() {
        password = null
    }

    private fun sortAuthorities(
            authorities: Collection<GrantedAuthority>): SortedSet<GrantedAuthority> {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection")
        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        val sortedAuthorities = TreeSet(
                AuthorityComparator())

        for (grantedAuthority in authorities) {
            Assert.notNull(grantedAuthority,
                    "GrantedAuthority list cannot contain any null elements")
            sortedAuthorities.add(grantedAuthority)
        }

        return sortedAuthorities
    }

    private class AuthorityComparator : Comparator<GrantedAuthority>, Serializable {

        override fun compare(g1: GrantedAuthority, g2: GrantedAuthority): Int {
            // Neither should ever be null as each entry is checked before adding it to
            // the set.
            // If the authority is null, it is a custom authority and should precede
            // others.
            if (g2.authority == null) {
                return -1
            }

            return if (g1.authority == null) {
                1
            } else g1.authority.compareTo(g2.authority)

        }

        companion object {
            private const val serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID
        }
    }

    /**
     * Returns `true` if the supplied object is a `User` instance with the
     * same `username` value.
     *
     *
     * In other words, the objects are equal if they have the same username, representing
     * the same principal.
     */
    override fun equals(other: Any?): Boolean {
        return if (other is MyUserImpl) {
            username == other.username
        } else false
    }

    /**
     * Returns the hashcode of the `username`.
     */
    override fun hashCode(): Int {
        return username.hashCode()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(super.toString()).append(": ")
        sb.append("Username: ").append(this.username).append("; ")
        sb.append("Password: [PROTECTED]; ")
        sb.append("Enabled: ").append(this.enabled).append("; ")
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ")
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
                .append("; ")
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ")

        if (!authorities!!.isEmpty()) {
            sb.append("Granted Authorities: ")

            var first = true
            authorities!!.forEach { auth ->
                if (!first) {
                    sb.append(",")
                }
                first = false

                sb.append(auth)
            }
        } else {
            sb.append("Not granted any authorities")
        }

        return sb.toString()
    }
}