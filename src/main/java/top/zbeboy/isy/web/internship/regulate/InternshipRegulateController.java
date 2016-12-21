package top.zbeboy.isy.web.internship.regulate;

import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Staff;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.web.internship.journal.InternshipJournalController;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * Created by zbeboy on 2016/12/21.
 */
@Controller
public class InternshipRegulateController {

    private final Logger log = LoggerFactory.getLogger(InternshipJournalController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipApplyService internshipApplyService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

    @Resource
    private InternshipTypeService internshipTypeService;

    @Resource
    private UsersService usersService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private RoleService roleService;

    /**
     * 实习监管
     *
     * @return 实习监管页面
     */
    @RequestMapping(value = "/web/menu/internship/regulate", method = RequestMethod.GET)
    public String internshipJournal() {
        return "web/internship/regulate/internship_regulate::#page-wrapper";
    }

    /**
     * 实习监管列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/regulate/list", method = RequestMethod.GET)
    public String regulateList(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        Users users = usersService.getUserFromSession();
        Optional<Record> record = usersService.findUserSchoolInfo(users);
        if (record.isPresent() && usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Staff staff = record.get().into(Staff.class);
            modelMap.addAttribute("staffId", staff.getStaffId());
        }
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
        }
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "web/internship/regulate/internship_regulate_list::#page-wrapper";
    }
}
