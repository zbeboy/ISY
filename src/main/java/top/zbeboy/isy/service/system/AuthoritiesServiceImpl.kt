package top.zbeboy.isy.service.system

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.RememberMeAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import top.zbeboy.isy.domain.tables.pojos.Authorities
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord

import top.zbeboy.isy.domain.Tables.AUTHORITIES
/**
 * Created by zbeboy 2017-11-17 .
 **/
@Service("authoritiesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class AuthoritiesServiceImpl @Autowired constructor(dslContext: DSLContext) : AuthoritiesService{

    private val create: DSLContext = dslContext

    override fun findByUsername(username: String): List<AuthoritiesRecord> {
        return create.selectFrom<AuthoritiesRecord>(AUTHORITIES).where(AUTHORITIES.USERNAME.eq(username)).fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(authorities: Authorities) {
        create.insertInto<AuthoritiesRecord>(AUTHORITIES)
                .set(AUTHORITIES.USERNAME, authorities.username)
                .set(AUTHORITIES.AUTHORITY, authorities.authority)
                .execute()
    }

    override fun deleteByUsername(username: String) {
        create.deleteFrom<AuthoritiesRecord>(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(username))
                .execute()
    }

    override fun deleteByAuthorities(authorities: String) {
        create.deleteFrom<AuthoritiesRecord>(AUTHORITIES)
                .where(AUTHORITIES.AUTHORITY.eq(authorities))
                .execute()
    }

    override fun isRememberMeAuthenticated(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return !ObjectUtils.isEmpty(authentication) && RememberMeAuthenticationToken::class.java.isAssignableFrom(authentication.javaClass)
    }

}