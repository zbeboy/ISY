package top.zbeboy.isy.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
import top.zbeboy.isy.filter.SecurityLoginFilter
import top.zbeboy.isy.security.AjaxAuthenticationFailureHandler
import top.zbeboy.isy.security.AjaxAuthenticationSuccessHandler
import top.zbeboy.isy.security.MyUserDetailsServiceImpl
import top.zbeboy.isy.security.WebSecurity
import javax.inject.Inject
import javax.sql.DataSource

/**
 * spring security 配置.
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
open class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Qualifier("dataSource")
    @Autowired
    open lateinit var dataSource: DataSource

    @Autowired
    open lateinit var myUserDetailsService: MyUserDetailsServiceImpl

    @Inject
    open lateinit var ajaxAuthenticationSuccessHandler: AjaxAuthenticationSuccessHandler

    @Inject
    open lateinit var ajaxAuthenticationFailureHandler: AjaxAuthenticationFailureHandler

    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    open fun jdbcTokenRepository(): JdbcTokenRepositoryImpl {
        val j = JdbcTokenRepositoryImpl()
        j.dataSource = this.dataSource
        return j
    }

    @Bean
    open fun webSecurity(): WebSecurity {
        return WebSecurity()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .csrf()
                .ignoringAntMatchers("/remind/**", "/oauth/authorize")
                .and()
                .headers()
                // allow same origin to frame our site to support iframe SockJS
                .frameOptions().sameOrigin()
                .and()
                .authorizeRequests().antMatchers("/css/**", "/js/**", "/fonts/**", "/images/**", "/plugin/**", "/files/**", "/webjars/**", "/webjarsjs/**").permitAll()
                .and().formLogin().loginPage("/login")
                .successHandler(this.ajaxAuthenticationSuccessHandler)
                .failureHandler(this.ajaxAuthenticationFailureHandler)
                .and().sessionManagement().invalidSessionUrl("/login")
                .and().logout().logoutSuccessUrl("/")
                .permitAll().invalidateHttpSession(true)
                .and().rememberMe().tokenValiditySeconds(2419200).rememberMeParameter("remember-me").tokenRepository(jdbcTokenRepository())
                .and().authorizeRequests().antMatchers("/web/**").access("@webSecurity.check(authentication,request)")
                .and().authorizeRequests().antMatchers("/special/channel/**").hasAnyRole("SYSTEM", "ADMIN") // 特别通道 跨controller调用共同方法使用
                .and().authorizeRequests().antMatchers("/anyone/**", "/remind/**", "/oauth/authorize").authenticated()
                .and().authorizeRequests().antMatchers("/user/**", "/index", "/weixin/**", "/server/probe").permitAll()
                .antMatchers("/metrics/**").hasRole("ACTUATOR")
                .antMatchers("/health/**").hasRole("ACTUATOR")
                .antMatchers("/trace/**").hasRole("ACTUATOR")
                .antMatchers("/dump/**").hasRole("ACTUATOR")
                .antMatchers("/shutdown/**").hasRole("ACTUATOR")
                .antMatchers("/beans/**").hasRole("ACTUATOR")
                .antMatchers("/configprops/**").hasRole("ACTUATOR")
                .antMatchers("/info/**").hasRole("ACTUATOR")
                .antMatchers("/autoconfig/**").hasRole("ACTUATOR")
                .antMatchers("/env/**").hasRole("ACTUATOR")
                .antMatchers("/mappings/**").hasRole("ACTUATOR")
                .and().addFilterBefore(SecurityLoginFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    @Throws(Exception::class)
    public override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService<MyUserDetailsServiceImpl>(this.myUserDetailsService).passwordEncoder(passwordEncoder()).and().eraseCredentials(false)
    }
}