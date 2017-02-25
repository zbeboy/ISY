package top.zbeboy.isy.service;


import io.jstack.sendcloud4j.SendCloud;
import io.jstack.sendcloud4j.mail.Email;
import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.domain.tables.pojos.SystemMailbox;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.util.UUIDUtils;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by Administrator on 2016/3/29.
 */
@Service("mailService")
public class MailServiceImpl implements MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private MessageSource messageSource;

    @Resource
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    private ISYProperties isyProperties;

    @Resource
    private SystemMailboxService systemMailboxService;

    @Async
    @Override
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {

        if (!isyProperties.getMail().isOpen()) {
            log.info(" 管理员已关闭邮件发送 ");
            return;
        }
        boolean isSend = false;
        switch (isyProperties.getMail().getSendMethod()) {
            case 1:
                sendDefaultMail(to, subject, content, isMultipart, isHtml);
                isSend = true;
                log.info("使用默认邮件服务发送");
                break;
            case 2:
                sendAliDMMail(to, subject, content);
                isSend = true;
                log.info("使用阿里云邮件服务发送");
                break;
            case 3:
                sendCloudMail(to, subject, content);
                isSend = true;
                log.info("使用sendCloud邮件服务发送");
                break;
            default:
                log.info("未配置邮箱发送方式");
        }

        if (isSend) {
            SystemMailbox systemMailbox = new SystemMailbox(UUIDUtils.getUUID(), new Timestamp(Clock.systemDefaultZone().millis()), to);
            systemMailboxService.save(systemMailbox);
        }
    }

    @Async
    @Override
    public void sendActivationEmail(Users users, String baseUrl) {
        log.debug("Sending activation e-mail to '{}'", users.getUsername());
        Locale locale = Locale.forLanguageTag(users.getLangKey());
        Context data = new Context();
        data.setLocale(locale);
        data.setVariable("user", users);
        data.setVariable("baseUrl", baseUrl);
        sendEmail(users.getUsername(), messageSource.getMessage("email.activation.title", null, locale), springTemplateEngine.process("mails/activationemail", data), false, true);
    }

    @Async
    @Override
    public void sendCreationEmail(Users users, String baseUrl) {
        log.debug("Sending creation e-mail to '{}'", users.getUsername());
        Locale locale = Locale.forLanguageTag(users.getLangKey());
        Context data = new Context();
        data.setLocale(locale);
        data.setVariable("user", users);
        data.setVariable("baseUrl", baseUrl);
        sendEmail(users.getUsername(), messageSource.getMessage("email.creation.title", null, locale), springTemplateEngine.process("mails/creationemail", data), false, true);

    }

    @Async
    @Override
    public void sendPasswordResetMail(Users users, String baseUrl) {
        log.debug("Sending password reset e-mail to '{}'", users.getUsername());
        Locale locale = Locale.forLanguageTag(users.getLangKey());
        Context data = new Context();
        data.setLocale(locale);
        data.setVariable("user", users);
        data.setVariable("resetLink", baseUrl + "/user/login/password/forget/reset?key=" + users.getPasswordResetKey() + "&username=" + users.getUsername());
        String subject = messageSource.getMessage("email.reset.title", null, locale);
        String content = springTemplateEngine.process("mails/passwordresetemail", data);
        sendEmail(users.getUsername(), subject, content, false, true);
    }

    @Async
    @Override
    public void sendValidEmailMail(Users users, String baseUrl) {
        log.debug("Sending valid e-mail to '{}'", users.getUsername());
        Locale locale = Locale.forLanguageTag(users.getLangKey());
        Context data = new Context();
        data.setLocale(locale);
        data.setVariable("user", users);
        data.setVariable("validLink", baseUrl + "/user/register/mailbox/valid?key=" + users.getMailboxVerifyCode() + "&username=" + users.getUsername());
        String subject = messageSource.getMessage("email.valid.title", null, locale);
        String content = springTemplateEngine.process("mails/validemail", data);
        sendEmail(users.getUsername(), subject, content, false, true);
    }

    @Async
    @Override
    public void sendNotifyMail(Users users, String baseUrl, String notify) {
        log.debug("Sending notify e-mail to '{}'", users.getUsername());
        Locale locale = Locale.forLanguageTag(users.getLangKey());
        Context data = new Context();
        data.setLocale(locale);
        data.setVariable("user", users);
        data.setVariable("notifyLink", baseUrl + "/login");
        data.setVariable("notify", notify);
        String subject = messageSource.getMessage("email.notify.title", null, locale);
        String content = springTemplateEngine.process("mails/notifyemail", data);
        sendEmail(users.getUsername(), subject, content, false, true);
    }

    @Override
    public void sendDefaultMail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(isyProperties.getConstants().getMailForm());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.info("E-mail could not be sent to user '{}', exception is: {}", to, e);
        }
    }

    @Async
    @Override
    public void sendAliDMMail(String userMail, String subject, String content) {
        try {
            // 配置发送邮件的环境属性
            final Properties props = new Properties();
            // 表示SMTP发送邮件，需要进行身份验证
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", isyProperties.getMail().getHost());
            props.put("mail.smtp.port", isyProperties.getMail().getPort());
            // 如果使用ssl，则去掉使用25端口的配置，进行如下配置,
            // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            // props.put("mail.smtp.socketFactory.port", "465");
            // props.put("mail.smtp.port", "465");

            String mailUser = "mail.user";
            // 发件人的账号
            props.put(mailUser, isyProperties.getMail().getUser()); //是发信地址啊！！！
            // 访问SMTP服务时需要提供的密码
            props.put("mail.password", isyProperties.getMail().getPassword());

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty(mailUser);
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            InternetAddress form = new InternetAddress(
                    props.getProperty(mailUser));
            message.setFrom(form);

            // 设置收件人
            InternetAddress to = new InternetAddress(userMail);
            message.setRecipient(MimeMessage.RecipientType.TO, to);

            // 设置邮件标题
            message.setSubject(subject);
            // 设置邮件的内容体
            message.setContent(content, "text/html;charset=UTF-8");
            log.debug("Sent e-mail to User '{}'", userMail);
            // 发送邮件
            Transport.send(message);
        } catch (MessagingException e) {
            log.info("E-mail could not be sent to user '{}', exception is: {}", userMail, e);
        }
    }

    @Override
    public void sendCloudMail(String userMail, String subject, String content) {
        SendCloud webapi = SendCloud.createWebApi(isyProperties.getMail().getApiUser(), isyProperties.getMail().getApiKey());
        Email email = Email.general()
                .from(isyProperties.getMail().getUser())
                .fromName(isyProperties.getMail().getFromName())
                .html(content)          // or .plain()
                .subject(subject)
                .to(userMail);
        webapi.mail().send(email);
    }

}
