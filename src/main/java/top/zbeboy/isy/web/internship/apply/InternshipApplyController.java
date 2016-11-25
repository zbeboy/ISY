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
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private AuthoritiesService authoritiesService;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

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
        InternshipRelease internshipRelease = new InternshipRelease();
        internshipRelease.setInternshipReleaseIsDel(isDel);
        if (!authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)
                && !authoritiesService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)
                && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> studentRecord = studentService.findByUsernameRelation(users.getUsername());
            if(studentRecord.isPresent()){
                StudentBean studentBean = studentRecord.get().into(StudentBean.class);
                internshipRelease.setAllowGrade(studentBean.getGrade());
                internshipRelease.setDepartmentId(studentBean.getDepartmentId());
            }
        }
        if (!authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)
                && !authoritiesService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)
                && usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> staffRecord = staffService.findByUsernameRelation(users.getUsername());
            if(staffRecord.isPresent()){
                StaffBean staffBean = staffRecord.get().into(StaffBean.class);
                internshipRelease.setDepartmentId(staffBean.getDepartmentId());
            }
        }
        Result<Record> records = internshipReleaseService.findAllByPage(paginationUtils, internshipRelease);
        List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipRelease);
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
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId,studentId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "/web/internship/apply/internship_college::#page-wrapper";
        }
        return page;
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
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId,studentId);
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
     * 进入实习申请入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private ErrorBean<InternshipRelease> accessCondition(String internshipReleaseId,int studentId) {
        ErrorBean<InternshipRelease> errorBean = new ErrorBean<>();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        Optional<Record> studentRecord = studentService.findByIdRelation(studentId);
        errorBean.setData(internshipRelease);
        if (DateTimeUtils.timestampRangeDecide(internshipRelease.getTeacherDistributionStartTime(), internshipRelease.getTeacherDistributionEndTime())) {
            if(studentRecord.isPresent()){
                StudentBean studentBean = studentRecord.get().into(StudentBean.class);
                if(Objects.equals(studentBean.getDepartmentId(), internshipRelease.getDepartmentId())){ // 判断系
                    if(studentBean.getGrade().equals(internshipRelease.getAllowGrade())){ // 判断年级
                        Optional<Record> internshipReleaseScienceRecord = internshipReleaseScienceService.findByInternshipReleaseIdAndScienceId(internshipReleaseId,studentBean.getScienceId());
                        if(internshipReleaseScienceRecord.isPresent()){ // 判断专业
                            Optional<Record> internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId,studentBean.getStudentId());
                            if(internshipTeacherDistributionRecord.isPresent()){ // 判断指导教师
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
                        errorBean.setErrorMsg("不在年级范围，无法进入");
                    }
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("不在系范围，无法进入");
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
