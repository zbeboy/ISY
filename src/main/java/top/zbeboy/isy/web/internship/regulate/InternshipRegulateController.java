package top.zbeboy.isy.web.internship.regulate;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean;
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean;
import top.zbeboy.isy.web.internship.journal.InternshipJournalController;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private InternshipRegulateService internshipRegulateService;

    @Resource
    private StaffService staffService;

    /**
     * 实习监管
     *
     * @return 实习监管页面
     */
    @RequestMapping(value = "/web/menu/internship/regulate", method = RequestMethod.GET)
    public String internshipRegulate() {
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

    /**
     * 我的监管记录列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/regulate/my/list", method = RequestMethod.GET)
    public String myRegulateList(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page;
        boolean canUse = false;
        Users users = usersService.getUserFromSession();
        Optional<Record> record = usersService.findUserSchoolInfo(users);
        if (record.isPresent() && usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Staff staff = record.get().into(Staff.class);
            ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, staff.getStaffId());
            canUse = !errorBean.isHasError();
            modelMap.addAttribute("staffId", staff.getStaffId());
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        }
        if (canUse) {
            page = "web/internship/regulate/internship_my_regulate::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 监管列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/regulate/list/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipRegulateBean> listData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("student_name");
        headers.add("student_number");
        headers.add("student_tel");
        headers.add("school_guidance_teacher");
        headers.add("create_date");
        headers.add("operator");
        DataTablesUtils<InternshipRegulateBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        InternshipRegulateBean otherCondition = new InternshipRegulateBean();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            otherCondition.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
            Result<Record> records = internshipRegulateService.findAllByPage(dataTablesUtils, otherCondition);
            List<InternshipRegulateBean> internshipRegulateBeans = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                internshipRegulateBeans = records.into(InternshipRegulateBean.class);
                internshipRegulateBeans.forEach(i -> i.setCreateDateStr(DateTimeUtils.formatDate(i.getCreateDate())));
            }
            dataTablesUtils.setData(internshipRegulateBeans);
            dataTablesUtils.setiTotalRecords(internshipRegulateService.countAll(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(internshipRegulateService.countByCondition(dataTablesUtils, otherCondition));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 实习监管添加
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教职工id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/regulate/list/add", method = RequestMethod.GET)
    public String regulateListAdd(@RequestParam("id") String internshipReleaseId, @RequestParam("staffId") int staffId, ModelMap modelMap) {
        InternshipRegulate internshipRegulate = new InternshipRegulate();
        internshipRegulate.setInternshipReleaseId(internshipReleaseId);
        internshipRegulate.setStaffId(staffId);
        modelMap.addAttribute("internshipRegulate", internshipRegulate);
        return "web/internship/regulate/internship_regulate_add::#page-wrapper";
    }

    /**
     * 获取该指导教师下的学生
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教师id
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/regulate/students", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<StudentBean> regulateStudents(@RequestParam("internshipReleaseId") String internshipReleaseId, @RequestParam("staffId") int staffId) {
        AjaxUtils<StudentBean> ajaxUtils = new AjaxUtils<>();
        List<StudentBean> studentBeens = new ArrayList<>();
        Result<Record> records = internshipTeacherDistributionService.findByInternshipReleaseIdAndStaffIdForStudent(internshipReleaseId, staffId);
        if (records.isNotEmpty()) {
            studentBeens = records.into(StudentBean.class);
        }
        return ajaxUtils.success().msg("获取学生数据成功").listData(studentBeens);
    }

    /**
     * 进入实习监管入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private ErrorBean<InternshipRelease> accessCondition(String internshipReleaseId, int staffId) {
        ErrorBean<InternshipRelease> errorBean = new ErrorBean<>();
        Map<String, Object> mapData = new HashMap<>();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        if (ObjectUtils.isEmpty(internshipRelease)) {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关实习信息");
            return errorBean;
        }
        errorBean.setData(internshipRelease);
        if (internshipRelease.getInternshipReleaseIsDel() == 1) {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("该实习已被注销");
            return errorBean;
        }
        Result<InternshipTeacherDistributionRecord> internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStaffId(internshipReleaseId, staffId);
        if (internshipTeacherDistributionRecord.isNotEmpty()) {
            errorBean.setHasError(false);
            mapData.put("internshipTeacherDistribution", internshipTeacherDistributionRecord.into(InternshipTeacherDistribution.class));
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("您不是该实习指导教师");
        }
        errorBean.setMapData(mapData);
        return errorBean;
    }
}
