package top.zbeboy.isy.config

import io.undertow.servlet.api.SecurityConstraint
import io.undertow.servlet.api.SecurityInfo
import io.undertow.servlet.api.TransportGuaranteeType
import io.undertow.servlet.api.WebResourceCollection
import org.apache.commons.lang3.CharEncoding
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.core.env.Environment
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.thymeleaf.spring4.view.ThymeleafView
import top.zbeboy.isy.interceptor.MenuInterceptor
import java.io.File
import javax.inject.Inject

/**
 * Spring boot web init.
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
@Configuration
open class WebConfiguration : WebMvcConfigurerAdapter() {

    @Autowired
    private val isyProperties: ISYProperties? = null

    @Inject
    private val env: Environment? = null

    /**
     * 切换语言
     *
     * @return 语言环境
     */
    @Bean
    open fun localeResolver(): LocaleResolver {
        return SessionLocaleResolver()
    }

    /**
     * ajax 返回页面
     *
     * @return 页面节点
     */
    @Bean
    @Scope("prototype")
    open fun thymeleafView(): ThymeleafView {
        val thymeleafView = ThymeleafView()
        thymeleafView.markupSelector = "#page-wrapper"
        return thymeleafView
    }

    /**
     * undertow http 重定向 https
     *
     * @return factory
     */
    @Bean
    open fun undertow(): EmbeddedServletContainerFactory {
        val undertow = UndertowEmbeddedServletContainerFactory()
        undertow.addBuilderCustomizers(UndertowBuilderCustomizer { builder -> builder.addHttpListener(isyProperties!!.getConstants().serverHttpPort, isyProperties.getConstants().undertowListenerIp) })
        undertow.addDeploymentInfoCustomizers(
                UndertowDeploymentInfoCustomizer { deploymentInfo ->
                    deploymentInfo.addSecurityConstraint(SecurityConstraint()
                            .addWebResourceCollection(WebResourceCollection()
                                    .addUrlPattern("/*"))
                            .setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL)
                            .setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT))
                            .setDefaultEncoding(CharEncoding.UTF_8)
                            .setUrlEncoding(CharEncoding.UTF_8)
                            .setConfidentialPortManager { isyProperties!!.getConstants().serverHttpsPort }
                }
        )
        if (this.env!!.acceptsProfiles(Workbook.SPRING_PROFILE_PRODUCTION, Workbook.SPRING_PROFILE_TEST)) {
            val documentRoot = File(System.getProperty("user.dir") + "/" + this.isyProperties!!.getConstants().tempDir)
            if (!documentRoot.exists()) {
                documentRoot.mkdirs()
            }
            undertow.documentRoot = documentRoot
        }
        return undertow
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

    override fun addInterceptors(registry: InterceptorRegistry?) {
        registry!!.addInterceptor(MenuInterceptor()).addPathPatterns("/web/menu/backstage")
    }
}