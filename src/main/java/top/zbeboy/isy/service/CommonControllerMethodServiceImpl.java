package top.zbeboy.isy.service;

import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.InternshipType;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * Created by lenovo on 2016-10-15.
 */
@Service("commonControllerMethodService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CommonControllerMethodServiceImpl implements CommonControllerMethodService {

    private final Logger log = LoggerFactory.getLogger(CommonControllerMethodServiceImpl.class);

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

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
    private GraduationPracticeUnifyService graduationPracticeUnifyService;

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
    public void accessRoleCondition(InternshipReleaseBean internshipReleaseBean) {
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)
                && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int departmentId = roleService.getRoleDepartmentId(record);
            internshipReleaseBean.setDepartmentId(departmentId);
        }
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            internshipReleaseBean.setCollegeId(collegeId);
        }
    }

    @Override
    public void deleteInternshipApplyRecord(int internshipTypeId,String internshipReleaseId, int studentId) {
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
        modelMap.addAttribute("showTip",true);
        modelMap.addAttribute("tip",tip);
        modelMap.addAttribute("showButton",true);
        modelMap.addAttribute("buttonText","返回上一页");
        return Workbook.TIP_PAGE;
    }
}
