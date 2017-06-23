package top.zbeboy.isy.service.common;

import org.springframework.ui.ModelMap;
import top.zbeboy.isy.domain.tables.pojos.Users;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by lenovo on 2016-10-15.
 */
public interface CommonControllerMethodService {

    /**
     * 当前用户的角色名与院id
     *
     * @param modelMap 页面对象
     */
    void currentUserRoleNameAndCollegeIdPageParam(ModelMap modelMap);

    /**
     * 获取实习数据 判断角色
     *
     * @return 根据角色返回相应数据
     */
    Map<String, Integer> accessRoleCondition();

    /**
     * 组装提示信息
     *
     * @param modelMap 页面对象
     * @param tip      提示内容
     */
    String showTip(ModelMap modelMap, String tip);

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
