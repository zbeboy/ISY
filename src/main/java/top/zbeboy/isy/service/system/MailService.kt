package top.zbeboy.isy.service.system

import top.zbeboy.isy.domain.tables.pojos.Users

/**
 * Created by zbeboy 2017-11-11 .
 **/
interface MailService {

    /**
     * 发送邮件
     *
     * @param to          接收方
     * @param subject     标题
     * @param content     内容
     * @param isMultipart 多段
     * @param isHtml      是html?
     */
    fun sendEmail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean)

    /**
     * 发送激活邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     */
    fun sendActivationEmail(users: Users, baseUrl: String)

    /**
     * 发送账号创建成功邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     */
    fun sendCreationEmail(users: Users, baseUrl: String)

    /**
     * 发送密码重置邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     */
    fun sendPasswordResetMail(users: Users, baseUrl: String)

    /**
     * 发送邮箱验证邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     */
    fun sendValidEmailMail(users: Users, baseUrl: String)

    /**
     * 发送通知邮件
     *
     * @param users   用户
     * @param baseUrl 服务路径
     * @param notify  通知内容
     */
    fun sendNotifyMail(users: Users, baseUrl: String, notify: String)

    /**
     * 使用内置方式发送
     *
     * @param to          接收方
     * @param subject     标题
     * @param content     内容
     * @param isMultipart 多段
     * @param isHtml      是html?
     */
    fun sendDefaultMail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean)

    /**
     * 阿里云邮箱服务
     *
     * @param userMail 用户邮箱
     * @param subject  标题
     * @param content  内容
     */
    fun sendAliDMMail(userMail: String, subject: String, content: String)

    /**
     * sendCloud邮箱服务
     *
     * @param userMail 用户邮箱
     * @param subject  标题
     * @param content  内容
     */
    fun sendCloudMail(userMail: String, subject: String, content: String)
}