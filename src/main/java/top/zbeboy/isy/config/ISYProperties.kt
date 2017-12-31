package top.zbeboy.isy.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Spring boot 配置属性加载.
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@ConfigurationProperties(prefix = "isy", ignoreUnknownFields = false)
class ISYProperties {

    private val async = Async()

    private val mobile = Mobile()

    private val mail = Mail()

    private val constants = Constants()

    private val weixin = Weixin()

    private val oAuth2 = OAuth2()

    private val security = Security()

    fun getAsync(): Async {
        return async
    }

    fun getMobile(): Mobile {
        return mobile
    }

    fun getMail(): Mail {
        return mail
    }

    fun getConstants(): Constants {
        return constants
    }

    fun getWeixin(): Weixin {
        return weixin
    }

    fun getoAuth2(): OAuth2 {
        return oAuth2
    }

    fun getSecurity(): Security {
        return security
    }

    /**
     * 异常初始化参数
     */
    class Async {

        var corePoolSize = 2

        var maxPoolSize = 50

        var queueCapacity = 10000
    }

    /**
     * 短信初始化参数
     */
    class Mobile {

        var apiKey: String? = null

        var isOpen: Boolean = false

        var sign: String? = null
    }

    /**
     * 邮件初始化参数
     */
    class Mail {

        var user: String? = null

        var password: String? = null

        var host: String? = null

        var port: Int = 0

        var apiUser: String? = null

        var apiKey: String? = null

        var fromName: String? = null

        var sendMethod: Int = 0

        var isOpen: Boolean = false
    }

    /**
     * 通用初始化参数
     */
    class Constants {

        var serverName: String? = null

        var mailForm: String? = null

        var jooqSqlDialect: String? = null

        var serverHttpPort: Int = 0

        var serverHttpsPort: Int = 0

        var tempDir: String? = null

        var undertowListenerIp: String? = null
    }

    /**
     * 微信初始化参数
     */
    class Weixin {
        var token: String? = null
        var appId: String? = null
        var appSecret: String? = null
        var encodingAESKey: String? = null
        var smallToken: String? = null
        var smallEncodingAESKey: String? = null
    }

    /**
     * OAuth2初始化参数
     */
    class OAuth2 {
        var redirectUrisPrefix: String? = null
    }

    /**
     * Security初始化参数
     */
    class Security {
        var desDefaultKey: String? = null
    }
}