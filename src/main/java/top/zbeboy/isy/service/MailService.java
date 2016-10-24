package top.zbeboy.isy.service;


import top.zbeboy.isy.domain.tables.pojos.Users;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2016/3/29.
 */
public interface MailService {

    /**
     * 发送邮件
     *
     * @param to
     * @param subject
     * @param content
     * @param isMultipart
     * @param isHtml
     */
    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    /**
     * 发送激活邮件
     *
     * @param users
     * @param baseUrl
     */
    void sendActivationEmail(Users users, String baseUrl);

    /**
     * 发送账号创建成功邮件
     *
     * @param users
     * @param baseUrl
     */
    void sendCreationEmail(Users users, String baseUrl);

    /**
     * 发送密码重置邮件
     *
     * @param users
     * @param baseUrl
     */
    void sendPasswordResetMail(Users users, String baseUrl);

    /**
     * 发送邮箱验证邮件
     *
     * @param users
     * @param baseUrl
     */
    void sendValidEmailMail(Users users, String baseUrl);

    /**
     * 阿里云邮箱服务
     *
     * @param userMail
     * @param subject
     * @param content
     */
    void sendAliDMMail(String userMail, String subject, String content);
}
