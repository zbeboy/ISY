package top.zbeboy.isy.service.system;


import top.zbeboy.isy.domain.tables.pojos.Users;

/**
 * Created by Administrator on 2016/3/29.
 */
public interface MailService {

    /**
     * 发送邮件
     *
     * @param to          接收方
     * @param subject     标题
     * @param content     内容
     * @param isMultipart 多段
     * @param isHtml      是html?
     */
    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    /**
     * 发送激活邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     */
    void sendActivationEmail(Users users, String baseUrl);

    /**
     * 发送账号创建成功邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     */
    void sendCreationEmail(Users users, String baseUrl);

    /**
     * 发送密码重置邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     */
    void sendPasswordResetMail(Users users, String baseUrl);

    /**
     * 发送邮箱验证邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     */
    void sendValidEmailMail(Users users, String baseUrl);

    /**
     * 发送通知邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     * @param notify  通知内容
     */
    void sendNotifyMail(Users users, String baseUrl, String notify);

    /**
     * 使用内置方式发送
     *
     * @param to          接收方
     * @param subject     标题
     * @param content     内容
     * @param isMultipart 多段
     * @param isHtml      是html?
     */
    void sendDefaultMail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    /**
     * 阿里云邮箱服务
     *
     * @param userMail 用户邮箱
     * @param subject  标题
     * @param content  内容
     */
    void sendAliDMMail(String userMail, String subject, String content);

    /**
     * sendCloud邮箱服务
     *
     * @param userMail 用户邮箱
     * @param subject  标题
     * @param content  内容
     */
    void sendCloudMail(String userMail, String subject, String content);
}
