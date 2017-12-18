package top.zbeboy.isy.service.common;

import top.zbeboy.isy.domain.tables.pojos.Users;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lenovo on 2016-10-15.
 */
public interface CommonControllerMethodService {

    /**
     * 发送邮件通知
     *
     * @param users        接收者
     * @param curUsers     发送者
     * @param messageTitle 消息标题
     * @param notify       通知内容
     */
    void sendNotify(Users users, Users curUsers, String messageTitle, String notify, HttpServletRequest request);

    /**
     * 限制当前学生用户操作行为
     *
     * @param studentId 学生id
     * @return 是否可操作
     */
    boolean limitCurrentStudent(int studentId);
}
