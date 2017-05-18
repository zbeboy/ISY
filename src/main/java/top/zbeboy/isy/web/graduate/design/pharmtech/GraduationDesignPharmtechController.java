package top.zbeboy.isy.web.graduate.design.pharmtech;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.loadtime.Aj;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.GraduationDesignHopeTutorRecord;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignHopeTutorService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zbeboy on 2017/5/15.
 */
@Slf4j
@Controller
public class GraduationDesignPharmtechController {

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private GraduationDesignHopeTutorService graduationDesignHopeTutorService;

    @Resource
    private UsersService usersService;

    @Resource
    private StudentService studentService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    /**
     * 填报指导教师
     *
     * @return 填报指导教师页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/pharmtech", method = RequestMethod.GET)
    public String pharmtech() {
        return "web/graduate/design/pharmtech/design_pharmtech::#page-wrapper";
    }

    /**
     * 志愿页面
     *
     * @param graduationDesignReleaseId 发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish", method = RequestMethod.GET)
    public String pharmtechWish(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
        return "web/graduate/design/pharmtech/design_pharmtech_wish::#page-wrapper";
    }

    /**
     * 填报指导教师志愿数据
     *
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignTeacherBean> wishData(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils<GraduationDesignTeacherBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Users users = usersService.getUserFromSession();
            Student student = studentService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(student)) {
                Result<GraduationDesignHopeTutorRecord> designHopeTutorRecords = graduationDesignHopeTutorService.findByStudentId(student.getStudentId());
                List<GraduationDesignTeacherBean> graduationDesignTeachers = graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId);
                for (GraduationDesignTeacherBean designTeacherBean : graduationDesignTeachers) {
                    boolean selectedTeacher = false;
                    for (GraduationDesignHopeTutorRecord record : designHopeTutorRecords) {
                        if (designTeacherBean.getGraduationDesignTeacherId().equals(record.getGraduationDesignTeacherId())) {
                            selectedTeacher = true;
                            break;
                        }
                    }
                    designTeacherBean.setSelected(selectedTeacher);
                }
                ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeachers);
            } else {
                ajaxUtils.fail().msg("查询学生信息失败");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 选择教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish/selected", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils wishSelected(@RequestParam("graduationDesignTeacherId") String graduationDesignTeacherId,
                                  @RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Users users = usersService.getUserFromSession();
            Student student = studentService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(student)) {
                // 是否已达到志愿数量
                int count = graduationDesignHopeTutorService.countByStudentId(student.getStudentId());
                if (count < 3) {
                    GraduationDesignHopeTutor graduationDesignHopeTutor = new GraduationDesignHopeTutor();
                    graduationDesignHopeTutor.setGraduationDesignTeacherId(graduationDesignTeacherId);
                    graduationDesignHopeTutor.setStudentId(student.getStudentId());
                    graduationDesignHopeTutorService.save(graduationDesignHopeTutor);
                    ajaxUtils.success().msg("保存成功");
                } else {
                    ajaxUtils.fail().msg("仅支持志愿三位指导教师");
                }
            } else {
                ajaxUtils.fail().msg("未查询相关学生信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 取消教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish/cancel", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils wishCancel(@RequestParam("graduationDesignTeacherId") String graduationDesignTeacherId,
                                @RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Users users = usersService.getUserFromSession();
            Student student = studentService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(student)) {
                GraduationDesignHopeTutor graduationDesignHopeTutor = new GraduationDesignHopeTutor();
                graduationDesignHopeTutor.setGraduationDesignTeacherId(graduationDesignTeacherId);
                graduationDesignHopeTutor.setStudentId(student.getStudentId());
                graduationDesignHopeTutorService.delete(graduationDesignHopeTutor);
                ajaxUtils.success().msg("取消成功");
            } else {
                ajaxUtils.fail().msg("未查询相关学生信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canUse(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入填报教师入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> accessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = ErrorBean.of();
        GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
        if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
            errorBean.setData(graduationDesignRelease);
            if (graduationDesignRelease.getGraduationDesignIsDel() == 1) {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("该毕业设计已被注销");
            } else {
                // 是否为学生用户
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    // 毕业时间范围
                    if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                        // 是否已确认
                        if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacher()) && graduationDesignRelease.getIsOkTeacher() == 1) {
                            errorBean.setHasError(false);
                        } else {
                            errorBean.setHasError(true);
                            errorBean.setErrorMsg("未确认毕业设计指导教师，无法进行操作");
                        }
                    } else {
                        errorBean.setHasError(true);
                        errorBean.setErrorMsg("不在毕业设计时间范围，无法进入");
                    }
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("您的注册类型不是学生用户");
                }
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关毕业设计信息");
        }
        return errorBean;
    }
}
