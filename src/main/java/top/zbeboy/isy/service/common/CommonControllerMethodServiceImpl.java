package top.zbeboy.isy.service.common;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.internship.*;
import top.zbeboy.isy.service.platform.RoleApplicationService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.system.MailService;
import top.zbeboy.isy.service.system.SystemAlertService;
import top.zbeboy.isy.service.system.SystemAlertTypeService;
import top.zbeboy.isy.service.system.SystemMessageService;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.*;

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
    private InternshipTypeService internshipTypeService;

    @Resource
    private InternshipCollegeService internshipCollegeService;

    @Resource
    private InternshipCompanyService internshipCompanyService;

    @Resource
    private GraduationPracticeCollegeService graduationPracticeCollegeService;

    @Resource
    private GraduationPracticeCompanyService graduationPracticeCompanyService;

    @Resource
    private SystemAlertService systemAlertService;

    @Resource
    private SystemMessageService systemMessageService;

    @Resource
    private SystemAlertTypeService systemAlertTypeService;

    @Resource
    private GraduationPracticeUnifyService graduationPracticeUnifyService;

    @Resource
    private RoleApplicationService roleApplicationService;

    @Override
    public void currentUserRoleNameAndCollegeIdPageParam(ModelMap modelMap) {
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            modelMap.addAttribute("collegeId", collegeId);
        }
    }

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
    public void deleteInternshipApplyRecord(int internshipTypeId, String internshipReleaseId, int studentId) {
        InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipTypeId);
        switch (internshipType.getInternshipTypeName()) {
            case Workbook.INTERNSHIP_COLLEGE_TYPE:
                internshipCollegeService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                break;
            case Workbook.INTERNSHIP_COMPANY_TYPE:
                internshipCompanyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                break;
            case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                graduationPracticeCollegeService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                break;
            case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                graduationPracticeUnifyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                break;
            case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                graduationPracticeCompanyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                break;
        }
    }

    @Override
    public String showTip(ModelMap modelMap, String tip) {
        modelMap.addAttribute("showTip", true);
        modelMap.addAttribute("tip", tip);
        modelMap.addAttribute("showButton", true);
        modelMap.addAttribute("buttonText", "返回上一页");
        return Workbook.TIP_PAGE;
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
        SystemAlertType systemAlertType = systemAlertTypeService.findByType(Workbook.ALERT_MESSAGE_TYPE);
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

    @Override
    public void batchSaveRoleApplication(String applicationIds, int roleId) {
        if (StringUtils.hasLength(applicationIds) && SmallPropsUtils.StringIdsIsNumber(applicationIds)) {
            List<Integer> ids = SmallPropsUtils.StringIdsToList(applicationIds);
            List<RoleApplication> roleApplications = new ArrayList<>();
            ids.forEach(id -> roleApplications.add(new RoleApplication(roleId, id)));
            roleApplicationService.save(roleApplications);
        }
    }
}
