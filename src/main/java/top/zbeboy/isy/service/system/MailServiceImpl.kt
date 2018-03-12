package top.zbeboy.isy.service.system

import io.jstack.sendcloud4j.SendCloud
import io.jstack.sendcloud4j.mail.Email
import org.apache.commons.lang.CharEncoding
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.elastic.pojo.SystemMailboxElastic
import top.zbeboy.isy.glue.system.SystemMailboxGlue
import top.zbeboy.isy.service.util.UUIDUtils
import java.sql.Timestamp
import java.time.Clock
import java.util.*
import javax.annotation.Resource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Service("mailService")
open class MailServiceImpl : MailService {

    private val log = LoggerFactory.getLogger(MailServiceImpl::class.java)

    @Resource
    open lateinit var javaMailSender: JavaMailSenderImpl

    @Resource
    open lateinit var messageSource: MessageSource

    @Resource
    open lateinit var springTemplateEngine: SpringTemplateEngine

    @Autowired
    open lateinit var isyProperties: ISYProperties

    @Resource
    open lateinit var systemMailboxGlue: SystemMailboxGlue

    @Async
    override fun sendEmail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean) {

        if (!isyProperties.getMail().isOpen) {
            log.info(" 管理员已关闭邮件发送 ")
            return
        }
        when (isyProperties.getMail().sendMethod) {
            1 -> {
                sendDefaultMail(to, subject, content, isMultipart, isHtml)
                log.info("使用默认邮件服务发送")
            }
            2 -> {
                sendAliDMMail(to, subject, content)
                log.info("使用阿里云邮件服务发送")
            }
            3 -> {
                sendCloudMail(to, subject, content)
                log.info("使用sendCloud邮件服务发送")
            }
            else -> log.info("未配置邮箱发送方式")
        }
    }

    @Async
    override fun sendActivationEmail(users: Users, baseUrl: String) {
        log.debug("Sending activation e-mail to '{}'", users.username)
        val locale = Locale.forLanguageTag(users.langKey)
        val data = Context()
        data.locale = locale
        data.setVariable("user", users)
        data.setVariable("baseUrl", baseUrl)
        sendEmail(users.username, messageSource.getMessage("email.activation.title", null, locale), springTemplateEngine.process("mails/activationemail", data), false, true)
    }

    @Async
    override fun sendCreationEmail(users: Users, baseUrl: String) {
        log.debug("Sending creation e-mail to '{}'", users.username)
        val locale = Locale.forLanguageTag(users.langKey)
        val data = Context()
        data.locale = locale
        data.setVariable("user", users)
        data.setVariable("baseUrl", baseUrl)
        sendEmail(users.username, messageSource.getMessage("email.creation.title", null, locale), springTemplateEngine.process("mails/creationemail", data), false, true)

    }

    @Async
    override fun sendPasswordResetMail(users: Users, baseUrl: String) {
        log.debug("Sending password reset e-mail to '{}'", users.username)
        val locale = Locale.forLanguageTag(users.langKey)
        val data = Context()
        data.locale = locale
        data.setVariable("user", users)
        data.setVariable("resetLink", baseUrl + "/user/login/password/forget/reset?key=" + users.passwordResetKey + "&username=" + users.username)
        data.setVariable("baseUrl", baseUrl)
        val subject = messageSource.getMessage("email.reset.title", null, locale)
        val content = springTemplateEngine.process("mails/passwordresetemail", data)
        sendEmail(users.username, subject, content, false, true)
    }

    @Async
    override fun sendValidEmailMail(users: Users, baseUrl: String) {
        log.debug("Sending valid e-mail to '{}'", users.username)
        val locale = Locale.forLanguageTag(users.langKey)
        val data = Context()
        data.locale = locale
        data.setVariable("user", users)
        data.setVariable("validLink", baseUrl + "/user/register/mailbox/valid?key=" + users.mailboxVerifyCode + "&username=" + users.username)
        data.setVariable("baseUrl", baseUrl)
        val subject = messageSource.getMessage("email.valid.title", null, locale)
        val content = springTemplateEngine.process("mails/validemail", data)
        sendEmail(users.username, subject, content, false, true)
    }

    @Async
    override fun sendNotifyMail(users: Users, baseUrl: String, notify: String) {
        log.debug("Sending notify e-mail to '{}'", users.username)
        val locale = Locale.forLanguageTag(users.langKey)
        val data = Context()
        data.locale = locale
        data.setVariable("user", users)
        data.setVariable("notifyLink", baseUrl + "/login")
        data.setVariable("notify", notify)
        data.setVariable("baseUrl", baseUrl)
        val subject = messageSource.getMessage("email.notify.title", null, locale)
        val content = springTemplateEngine.process("mails/notifyemail", data)
        sendEmail(users.username, subject, content, false, true)
    }

    override fun sendDefaultMail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content)
        var sendCondition:String?
        // Prepare message using a Spring helper
        val mimeMessage = javaMailSender.createMimeMessage()
        try {
            val message = MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8)
            message.setTo(to)
            message.setFrom(isyProperties.getConstants().mailForm!!)
            message.setSubject(subject)
            message.setText(content, isHtml)
            javaMailSender.send(mimeMessage)
            log.debug("Sent e-mail to User '{}'", to)
            sendCondition = "方式:默认邮箱, 发送成功"
        } catch (e: Exception) {
            log.info("E-mail could not be sent to user '{}', exception is: {}", to, e)
            sendCondition = "方式:默认邮箱, 发送失败 " + e.message
        }

        val systemMailbox = SystemMailboxElastic(UUIDUtils.getUUID(), Timestamp(Clock.systemDefaultZone().millis()), to, sendCondition)
        systemMailboxGlue.save(systemMailbox)
    }

    @Async
    override fun sendAliDMMail(userMail: String, subject: String, content: String) {
        var sendCondition:String?
        try {
            // 配置发送邮件的环境属性
            val props = Properties()
            // 表示SMTP发送邮件，需要进行身份验证
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.host", isyProperties.getMail().host!!)
            props.put("mail.smtp.port", isyProperties.getMail().port)
            // 如果使用ssl，则去掉使用25端口的配置，进行如下配置,
            // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            // props.put("mail.smtp.socketFactory.port", "465");
            // props.put("mail.smtp.port", "465");

            val mailUser = "mail.user"
            // 发件人的账号
            props.put(mailUser, isyProperties.getMail().user!!) //是发信地址啊！！！
            // 访问SMTP服务时需要提供的密码
            props.put("mail.password", isyProperties.getMail().password!!)

            // 构建授权信息，用于进行SMTP进行身份验证
            val authenticator = object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    // 用户名、密码
                    val userName = props.getProperty(mailUser)
                    val password = props.getProperty("mail.password")
                    return PasswordAuthentication(userName, password)
                }
            }
            // 使用环境属性和授权信息，创建邮件会话
            val mailSession = Session.getInstance(props, authenticator)
            // 创建邮件消息
            val message = MimeMessage(mailSession)
            // 设置发件人
            val form = InternetAddress(
                    props.getProperty(mailUser))
            message.setFrom(form)

            // 设置收件人
            val to = InternetAddress(userMail)
            message.setRecipient(MimeMessage.RecipientType.TO, to)

            // 设置邮件标题
            message.subject = subject
            // 设置邮件的内容体
            message.setContent(content, "text/html;charset=UTF-8")
            // 发送邮件
            Transport.send(message)
            log.debug("Sent e-mail to User '{}'", userMail)
            sendCondition = "方式:阿里云邮箱, 发送成功"
        } catch (e: MessagingException) {
            log.info("E-mail could not be sent to user '{}', exception is: {}", userMail, e)
            sendCondition = "方式:阿里云邮箱, 发送失败 " + e.message
        }

        val systemMailbox = SystemMailboxElastic(UUIDUtils.getUUID(), Timestamp(Clock.systemDefaultZone().millis()), userMail, sendCondition)
        systemMailboxGlue.save(systemMailbox)
    }

    override fun sendCloudMail(userMail: String, subject: String, content: String) {
        val webapi = SendCloud.createWebApi(isyProperties.getMail().apiUser, isyProperties.getMail().apiKey)
        val email = Email.general()
                .from(isyProperties.getMail().user)
                .fromName(isyProperties.getMail().fromName)
                .html(content)          // or .plain()
                .subject(subject)
                .to(userMail)
        val result = webapi.mail().send(email)
        val sendCondition:String?
        if (result.isSuccess) {
            sendCondition = "方式:sendCloud邮箱, 发送成功 " + result.statusCode + " : " + result.message
        } else {
            sendCondition = "方式:sendCloud邮箱, 发送失败 " + result.statusCode + " : " + result.message
        }
        val systemMailbox = SystemMailboxElastic(UUIDUtils.getUUID(), Timestamp(Clock.systemDefaultZone().millis()), userMail, sendCondition)
        systemMailboxGlue.save(systemMailbox)
    }
}