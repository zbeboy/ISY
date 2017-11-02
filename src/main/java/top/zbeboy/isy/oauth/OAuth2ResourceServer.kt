package top.zbeboy.isy.oauth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore

/**
 * Created by zbeboy 2017-11-02 .
 * oauth resource server.
 **/
@Configuration
@EnableResourceServer
open class OAuth2ResourceServer : ResourceServerConfigurerAdapter() {

    @Autowired
    private val tokenStore: TokenStore? = null

    @Throws(Exception::class)
    override fun configure(resources: ResourceServerSecurityConfigurer?) {
        resources!!.tokenStore(tokenStore).resourceId(OAuth2ResourceIdsBook.ISY_BASE_RESOURCE)
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // @formatter:off
        http
                // Since we want the protected resources to be accessible in the UI as well we need
                // session creation to be allowed (it's disabled by default in 2.0.6)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .requestMatchers().antMatchers("/rest/**", "/me")
                .and()
                .authorizeRequests()
                .antMatchers("/me").access("#oauth2.hasScope('read')")
                .antMatchers("/rest/**").access("#oauth2.hasScope('read')")
        // @formatter:on
    }
}