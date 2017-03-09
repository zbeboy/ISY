package top.zbeboy.isy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import top.zbeboy.isy.security.MyUserDetailsServiceImpl;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Created by zbeboy on 2017/3/9.
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MyUserDetailsServiceImpl myUserDetailsService;

    @Inject
    private AuthenticationManager authenticationManager;

    @Bean
    public JdbcTokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /*clients.jdbc(dataSource).withClient("clientapp")
                .authorizedGrantTypes("password", "refresh_token")
                .authorities("USER")
                .scopes("read", "write")
                .resourceIds("marri")
                .secret("123456");*/
        clients.jdbc(dataSource).withClient("my-trusted-client").authorizedGrantTypes("password");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.userDetailsService(myUserDetailsService).authorizationCodeServices(authorizationCodeServices()).authenticationManager(this.authenticationManager).tokenStore(tokenStore()).approvalStoreDisabled();
    }
}
