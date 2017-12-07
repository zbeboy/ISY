package top.zbeboy.isy.service.common;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.system.MailService;
import top.zbeboy.isy.service.system.SystemAlertService;
import top.zbeboy.isy.service.system.SystemMessageService;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by lenovo on 2016-10-15.
 */
@Slf4j
@Service("commonControllerMethodService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CommonControllerMethodServiceImpl implements CommonControllerMethodService {

    @Autowired
    private ISYProperties isyProperties;

    @Autowired
    private RequestUtils requestUtils;

    @Resource
    private UsersService usersService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private StudentService studentService;

    @Resource
    private RoleService roleService;

    @Resource
    private MailService mailService;

    @Resource
    private SystemAlertService systemAlertService;

    @Resource
    private SystemMessageService systemMessageService;

    @Resource
    private CacheManageService cacheManageService;

    @Override
    public Map<String, Integer> accessRoleCondition() {
        Map<String, Integer> map = new HashMap<>();
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)
                && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int departmentId = roleService.getRoleDepartmentId(record);
            map.put("departmentId", departmentId);
        }
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            map.put("collegeId", collegeId);
        }
        return map;
    }

    @Override
    public void sendNotify(Users users, Users curUsers, String messageTitle, String notify, HttpServletRequest request) {
        //发送验证邮件
        if (isyProperties.getMail().isOpen()) {
            mailService.sendNotifyMail(users, requestUtils.getBaseUrl(request), notify);
        }

        // 保存消息
        SystemMessage systemMessage = new SystemMessage();
        String messageId = UUIDUtils.getUUID();
        Byte isSee = 0;
        Timestamp now = new Timestamp(Clock.systemDefaultZone().millis());
        systemMessage.setSystemMessageId(messageId);
        systemMessage.setAcceptUsers(users.getUsername());
        systemMessage.setIsSee(isSee);
        systemMessage.setSendUsers(curUsers.getUsername());
        systemMessage.setMessageTitle(messageTitle);
        systemMessage.setMessageContent(notify);
        systemMessage.setMessageDate(now);
        systemMessageService.save(systemMessage);
        // 保存提醒
        SystemAlert systemAlert = new SystemAlert();
        SystemAlertType systemAlertType = cacheManageService.findBySystemAlertTypeName(Workbook.ALERT_MESSAGE_TYPE);
        systemAlert.setSystemAlertId(UUIDUtils.getUUID());
        systemAlert.setIsSee(isSee);
        systemAlert.setAlertContent("新消息");
        systemAlert.setLinkId(messageId);
        systemAlert.setSystemAlertTypeId(systemAlertType.getSystemAlertTypeId());
        systemAlert.setUsername(users.getUsername());
        systemAlert.setAlertDate(now);
        systemAlertService.save(systemAlert);
    }

    @Override
    public boolean limitCurrentStudent(int studentId) {
        // 强制身份判断
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Users users = usersService.getUserFromSession();
                Student student = studentService.findByUsername(users.getUsername());
                if (!ObjectUtils.isEmpty(student) && student.getStudentId() != studentId) {
                    return false;
                }
            }
        }
        return true;
    }
}
