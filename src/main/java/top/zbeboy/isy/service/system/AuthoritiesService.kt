package top.zbeboy.isy.service.system

import top.zbeboy.isy.domain.tables.pojos.Authorities
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord

/**
 * Created by zbeboy 2017-11-17 .
 **/
interface AuthoritiesService {

    /**
     * 通过用户名查询
     *
     * @param username 账号
     * @return 权限
     */
    fun findByUsername(username: String): List<AuthoritiesRecord>

    /**
     * 保存
     *
     * @param authorities 权限
     */
    fun save(authorities: Authorities)

    /**
     * 通过用户账号删除
     *
     * @param username 用户账号
     */
    fun deleteByUsername(username: String)

    /**
     * 通过权限删除
     *
     * @param authorities 权限
     */
    fun deleteByAuthorities(authorities: String)

    /**
     * Check if user is login by remember me cookie, refer
     * org.springframework.security.authentication.AuthenticationTrustResolverImpl
     * @return true or false
     */
    fun isAnonymousAuthenticated(): Boolean
}