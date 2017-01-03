package top.zbeboy.isy.config;


import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.api.WebResourceCollection;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring4.view.AjaxThymeleafView;
import org.thymeleaf.standard.fragment.StandardDOMSelectorFragmentSpec;
import top.zbeboy.isy.interceptor.MenuInterceptor;

import javax.inject.Inject;
import java.io.File;

/**
 * Spring boot web init.
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private ISYProperties isyProperties;

    @Inject
    private Environment env;

    @Bean
    public LocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    /**
     * ajax 返回页面
     *
     * @return 页面节点
     */
    @Bean
    public AjaxThymeleafView ajaxThymeleafView() {
        AjaxThymeleafView ajaxThymeleafView = new AjaxThymeleafView();
        ajaxThymeleafView.setFragmentSpec(new StandardDOMSelectorFragmentSpec("#page-wrapper"));
        return ajaxThymeleafView;
    }

    /**
     * undertow http 重定向 https
     *
     * @return factory
     */
    @Bean
    public EmbeddedServletContainerFactory undertow() {
        UndertowEmbeddedServletContainerFactory undertow = new UndertowEmbeddedServletContainerFactory();
        undertow.addBuilderCustomizers(builder -> builder.addHttpListener(isyProperties.getConstants().getServerHttpPort(), "0.0.0.0"));
        undertow.addDeploymentInfoCustomizers(deploymentInfo -> {
            deploymentInfo.addSecurityConstraint(new SecurityConstraint()
                    .addWebResourceCollection(new WebResourceCollection()
                            .addUrlPattern("/*"))
                    .setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL)
                    .setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT))
                    .setDefaultEncoding(CharEncoding.UTF_8)
                    .setUrlEncoding(CharEncoding.UTF_8)
                    .setConfidentialPortManager(exchange -> isyProperties.getConstants().getServerHttpsPort());
        });
        if (env.acceptsProfiles(Workbook.SPRING_PROFILE_PRODUCTION)) {
            File documentRoot = new File(System.getProperty("user.dir") + "/" + isyProperties.getConstants().getTempDir());
            if (!documentRoot.exists()) {
                documentRoot.mkdirs();
            }
            undertow.setDocumentRoot(documentRoot);
        }
        return undertow;
    }

    /*
     tomcat配置http 重定向到 https
      */
  /*  @Bean
    public EmbeddedServletContainerFactory servletContainerFactory() {
        TomcatEmbeddedServletContainerFactory factory =
                new TomcatEmbeddedServletContainerFactory() {
                    @Override
                    protected void postProcessContext(Context context) {
                        //SecurityConstraint必须存在，可以通过其为不同的URL设置不同的重定向策略。
                        SecurityConstraint securityConstraint = new SecurityConstraint();
                        securityConstraint.setUserConstraint("CONFIDENTIAL");
                        SecurityCollection collection = new SecurityCollection();
                        collection.addPattern("*//**//*");
                        securityConstraint.addCollection(collection);
                        context.addConstraint(securityConstraint);
                    }
                };
        factory.addAdditionalTomcatConnectors(createHttpConnector());
        return factory;
    }

    private Connector createHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setPort(isyProperties.getConstants().getServerHttpPort());
        connector.setRedirectPort(isyProperties.getConstants().getServerHttpsPort());
        return connector;
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MenuInterceptor()).addPathPatterns("/web/menu/backstage");
    }
}
