package top.zbeboy.isy.web.graduate.design.pharmtech;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.GraduationDesignHopeTutorRecord;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignHopeTutorService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
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
        // 是否为学生用户
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Users users = usersService.getUserFromSession();
            Student student = studentService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(student)) {
                // 是否已达到志愿数量
                int count = graduationDesignHopeTutorService.countByStudentId(student.getStudentId());
                if (count < 3) {
                    // 生成操作按钮
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
                    ajaxUtils.fail().msg("最大仅能选择三位志愿指导教师");
                }

            } else {
                ajaxUtils.fail().msg("查询学生信息失败");
            }

        } else {
            ajaxUtils.fail().msg("您不是学生用户");
        }
        return ajaxUtils;
    }
}
