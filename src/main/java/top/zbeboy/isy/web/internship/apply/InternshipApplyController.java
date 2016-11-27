package top.zbeboy.isy.web.internship.apply;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by zbeboy on 2016/11/25.
 */
@Controller
public class InternshipApplyController {

    private final Logger log = LoggerFactory.getLogger(InternshipApplyController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private StudentService studentService;

    @Resource
    private StaffService staffService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

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

    /**
     * 实习申请
     *
     * @return 实习申请页面
     */
    @RequestMapping(value = "/web/menu/internship/apply", method = RequestMethod.GET)
    public String internshipApply() {
        return "/web/internship/apply/internship_apply::#page-wrapper";
    }

    /**
     * 获取实习申请数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/apply/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipReleaseBean> applyDatas(PaginationUtils paginationUtils) {
        Byte isDel = 0;
        InternshipReleaseBean internshipReleaseBean = new InternshipReleaseBean();
        internshipReleaseBean.setInternshipReleaseIsDel(isDel);
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)
                && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int departmentId = roleService.getRoleDepartmentId(record);
            internshipReleaseBean.setDepartmentId(departmentId);
            if (record.isPresent() && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Organize organize = record.get().into(Organize.class);
                internshipReleaseBean.setAllowGrade(organize.getGrade());
            }
        }
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            internshipReleaseBean.setCollegeId(collegeId);
            if (record.isPresent() && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Organize organize = record.get().into(Organize.class);
                internshipReleaseBean.setAllowGrade(organize.getGrade());
            }
        }
        Result<Record> records = internshipReleaseService.findAllByPage(paginationUtils, internshipReleaseBean);
        List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipReleaseBean);
        return new AjaxUtils<InternshipReleaseBean>().success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils);
    }

    /**
     * 进入申请页
     *
     * @param internshipReleaseId 实习发布id
     * @return 申请页
     */
    @RequestMapping(value = "/web/internship/apply/access", method = RequestMethod.GET)
    public String applyAccess(@RequestParam("id") String internshipReleaseId, int studentId, ModelMap modelMap) {
        String page = "/web/internship/apply/internship_apply::#page-wrapper";
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, studentId);
        if (!errorBean.isHasError()) {
            InternshipRelease internshipRelease = errorBean.getData();
            InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
            switch (internshipType.getInternshipTypeName()) {
                case Workbook.INTERNSHIP_COLLEGE_TYPE:
                    Optional<Record> internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (internshipCollegeRecord.isPresent()) {
                        InternshipCollege internshipCollege = internshipCollegeRecord.get().into(InternshipCollege.class);
                        modelMap.addAttribute("internshipData", internshipCollege);
                        page = "/web/internship/apply/internship_college_edit::#page-wrapper";
                    } else {
                        internshipCollegePageParam(modelMap,errorBean);
                        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
                        page = "/web/internship/apply/internship_college_add::#page-wrapper";
                    }
                    break;
                case Workbook.INTERNSHIP_COMPANY_TYPE:
                    Optional<Record> internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (internshipCompanyRecord.isPresent()) {
                        InternshipCompany internshipCompany = internshipCompanyRecord.get().into(InternshipCompany.class);
                        modelMap.addAttribute("internshipData", internshipCompany);
                        page = "/web/internship/apply/internship_company_edit::#page-wrapper";
                    } else {
                        page = "/web/internship/apply/internship_company_add::#page-wrapper";
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                    Optional<Record> graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeCollegeRecord.isPresent()) {
                        GraduationPracticeCollege graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege.class);
                        modelMap.addAttribute("internshipData", graduationPracticeCollege);
                        page = "/web/internship/apply/graduation_practice_college_edit::#page-wrapper";
                    } else {
                        page = "/web/internship/apply/graduation_practice_college_add::#page-wrapper";
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                    Optional<Record> graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeUnifyRecord.isPresent()) {
                        GraduationPracticeUnify graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify.class);
                        modelMap.addAttribute("internshipData", graduationPracticeUnify);
                        page = "/web/internship/apply/graduation_practice_unify_edit::#page-wrapper";
                    } else {
                        page = "/web/internship/apply/graduation_practice_unify_add::#page-wrapper";
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                    Optional<Record> graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeCompanyRecord.isPresent()) {
                        GraduationPracticeCompany graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany.class);
                        modelMap.addAttribute("internshipData", graduationPracticeCompany);
                        page = "/web/internship/apply/graduation_practice_company_edit::#page-wrapper";
                    } else {
                        page = "/web/internship/apply/graduation_practice_company_add::#page-wrapper";
                    }
                    break;
                default:
                    page = "/web/internship/apply/internship_apply::#page-wrapper";
            }
        }
        return page;
    }

    /**
     * 顶岗实习(留学院) 页面数据
     *
     * @param modelMap  页面对象
     * @param errorBean 判断条件
     */
    private void internshipCollegePageParam(ModelMap modelMap, ErrorBean<InternshipRelease> errorBean) {
        StudentBean studentBean = (StudentBean) errorBean.getMapData().get("student");
        String qqMail = "";
        if (studentBean.getUsername().toLowerCase().contains("@qq.com")) {
            qqMail = studentBean.getUsername();
        }
        modelMap.addAttribute("qqMail", qqMail);
        modelMap.addAttribute("student", studentBean);
        InternshipTeacherDistribution internshipTeacherDistribution = (InternshipTeacherDistribution) errorBean.getMapData().get("internshipTeacherDistribution");
        int staffId = internshipTeacherDistribution.getStaffId();
        Optional<Record> staffRecord = staffService.findByIdRelation(staffId);
        String internshipTeacher = "";
        if (staffRecord.isPresent()) {
            StaffBean staffBean = staffRecord.get().into(StaffBean.class);
            internshipTeacher = staffBean.getRealName() + " " + staffBean.getMobile();
        }
        modelMap.addAttribute("internshipTeacher", internshipTeacher);
    }

    /**
     * 进入实习申请页面判断条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/apply/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canUse(@RequestParam("id") String internshipReleaseId, int studentId) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, studentId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 检验学生
     *
     * @param info 学生信息
     * @param type 检验类型
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/apply/valid/student", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validStudent(@RequestParam("student") String info, int type) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        Student student = null;
        if (type == 0) {
            student = studentService.findByUsername(info);
        } else if (type == 1) {
            student = studentService.findByStudentNumber(info);
        }
        if (!ObjectUtils.isEmpty(student)) {
            ajaxUtils.success().msg("查询学生数据成功").obj(student.getStudentId());
        } else {
            ajaxUtils.fail().msg("查询学生数据失败");
        }
        return ajaxUtils;
    }

    /**
     * 获取班主任数据
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/apply/teachers", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<StaffBean> teachers(@RequestParam("id") String internshipReleaseId, int studentId) {
        AjaxUtils<StaffBean> ajaxUtils = new AjaxUtils<>();
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, studentId);
        if (!errorBean.isHasError()) {
            InternshipRelease internshipRelease = errorBean.getData();
            List<StaffBean> staffs = new ArrayList<>();
            Result<Record> staffRecord = staffService.findByDepartmentIdRelation(internshipRelease.getDepartmentId());
            if (staffRecord.isNotEmpty()) {
                staffs = staffRecord.into(StaffBean.class);
            }
            ajaxUtils.success().msg("获取班主任数据成功").listData(staffs);
        } else {
            ajaxUtils.fail().msg("您不符合申请条件，无法获取数据");
        }
        return ajaxUtils;
    }

    /**
     * 进入实习申请入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private ErrorBean<InternshipRelease> accessCondition(String internshipReleaseId, int studentId) {
        ErrorBean<InternshipRelease> errorBean = new ErrorBean<>();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        Optional<Record> studentRecord = studentService.findByIdRelation(studentId);
        errorBean.setData(internshipRelease);
        Map<String, Object> mapData = new HashMap<>();
        if (DateTimeUtils.timestampRangeDecide(internshipRelease.getTeacherDistributionStartTime(), internshipRelease.getTeacherDistributionEndTime())) {
            if (studentRecord.isPresent()) {
                StudentBean studentBean = studentRecord.get().into(StudentBean.class);
                mapData.put("student", studentBean);
                errorBean.setMapData(mapData);
                Optional<Record> internshipReleaseScienceRecord = internshipReleaseScienceService.findByInternshipReleaseIdAndScienceId(internshipReleaseId, studentBean.getScienceId());
                if (internshipReleaseScienceRecord.isPresent()) { // 判断专业
                    Optional<Record> internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentBean.getStudentId());
                    if (internshipTeacherDistributionRecord.isPresent()) { // 判断指导教师
                        InternshipTeacherDistribution internshipTeacherDistribution = internshipTeacherDistributionRecord.get().into(InternshipTeacherDistribution.class);
                        mapData.put("internshipTeacherDistribution", internshipTeacherDistribution);
                        errorBean.setHasError(false);
                    } else {
                        errorBean.setHasError(true);
                        errorBean.setErrorMsg("未分配指导教师，无法进入");
                    }
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("不在专业范围，无法进入");
                }
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("未查询到学生信息，无法进入");
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("不在时间范围，无法进入");
        }
        return errorBean;
    }
}
