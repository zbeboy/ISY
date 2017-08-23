package top.zbeboy.isy.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import top.zbeboy.isy.filter.SecurityLoginFilter;
import top.zbeboy.isy.security.AjaxAuthenticationFailureHandler;
import top.zbeboy.isy.security.AjaxAuthenticationSuccessHandler;
import top.zbeboy.isy.security.MyUserDetailsServiceImpl;
import top.zbeboy.isy.security.WebSecurity;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * spring security 配置.
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MyUserDetailsServiceImpl myUserDetailsService;

    @Inject
    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl j = new JdbcTokenRepositoryImpl();
        j.setDataSource(dataSource);
        return j;
    }

    @Bean
    public WebSecurity webSecurity() {
        return new WebSecurity();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .ignoringAntMatchers("/remind/**")
                .and()
                .headers()
                // allow same origin to frame our site to support iframe SockJS
                .frameOptions().sameOrigin()
                .and()
                .authorizeRequests().antMatchers("/css/**", "/js/**", "/fonts/**", "/images/**", "/plugin/**", "/files/**", "/webjars/**", "/webjarsjs/**").permitAll()
                .and().formLogin().loginPage("/login")
                .successHandler(ajaxAuthenticationSuccessHandler)
                .failureHandler(ajaxAuthenticationFailureHandler)
                .and().sessionManagement().invalidSessionUrl("/login")
                .and().logout().logoutSuccessUrl("/")
                .permitAll().invalidateHttpSession(true)
                .and().rememberMe().tokenValiditySeconds(2419200).rememberMeParameter("remember-me").tokenRepository(jdbcTokenRepository())
                .and().authorizeRequests().antMatchers("/web/**").access("@webSecurity.check(authentication,request)")
                .and().authorizeRequests().antMatchers("/special/channel/**").hasAnyRole("SYSTEM", "ADMIN") // 特别通道 跨controller调用共同方法使用
                .and().authorizeRequests().antMatchers("/anyone/**", "/remind/**").authenticated()
                .and().authorizeRequests().antMatchers("/user/**", "/index", "/weixin/**").permitAll()
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
                .and().addFilterBefore(new SecurityLoginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder()).and().eraseCredentials(false);
    }
}
