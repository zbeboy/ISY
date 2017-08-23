package top.zbeboy.isy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
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

    /**
     * oauth jdbc token
     *
     * @return token
     */
    @Bean
    public JdbcTokenStore tokenStore() {
        return new JdbcTokenStore(this.dataSource);
    }

    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setClientDetailsService(clientDetailsService());
        tokenServices.setSupportRefreshToken(true);
        return tokenServices;
    }

    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(this.dataSource);
    }

    @Bean
    public OAuth2RequestFactory oAuth2RequestFactory() {
        DefaultOAuth2RequestFactory defaultOAuth2RequestFactory = new DefaultOAuth2RequestFactory(clientDetailsService());
        defaultOAuth2RequestFactory.setCheckUserScopes(true);
        return defaultOAuth2RequestFactory;
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(this.dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /*clients.jdbc(dataSource).withClient("clientapp")
                .authorizedGrantTypes("password", "refresh_token")
                .authorities("USER")
                .scopes("read", "write")
                .resourceIds("marri")
                .secret("123456");*/
        /*
        clients.jdbc(dataSource())
               .withClient("sampleClientId")
               .authorizedGrantTypes("implicit")
               .scopes("read")
               .autoApprove(true)
               .and()
               .withClient("clientIdPassword")
               .secret("secret")
               .authorizedGrantTypes(
                 "password","authorization_code", "refresh_token")
               .scopes("read");
         */
        clients.jdbc(this.dataSource).withClient("isy-base-client")
                .authorizedGrantTypes("password")
                .scopes("read")
                .secret("bar");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.userDetailsService(this.myUserDetailsService)
                .authorizationCodeServices(authorizationCodeServices())
                .authenticationManager(this.authenticationManager)
                .tokenStore(tokenStore())
                .tokenServices(tokenServices())
                .requestFactory(oAuth2RequestFactory())
                .approvalStoreDisabled();
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients();
    }
}
