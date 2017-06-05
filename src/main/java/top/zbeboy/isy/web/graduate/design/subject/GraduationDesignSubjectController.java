package top.zbeboy.isy.web.graduate.design.subject;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignPresubjectService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.subject.GraduationDesignPresubjectBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.vo.graduate.design.subject.GraduationDesignPresubjectAddVo;
import top.zbeboy.isy.web.vo.graduate.design.subject.GraduationDesignPresubjectUpdateVo;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/6/5.
 */
@Slf4j
@Controller
public class GraduationDesignSubjectController {

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignPresubjectService graduationDesignPresubjectService;

    @Resource
    private UsersService usersService;

    @Resource
    private StudentService studentService;

    /**
     * 毕业设计题目
     *
     * @return 毕业设计题目页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/subject", method = RequestMethod.GET)
    public String subject() {
        return "web/graduate/design/subject/design_subject::#page-wrapper";
    }

    /**
     * 我的题目
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面c对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/my", method = RequestMethod.GET)
    public String students(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            Users users = usersService.getUserFromSession();
            Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
            if (studentRecord.isPresent()) {
                Student student = studentRecord.get().into(Student.class);
                GraduationDesignPresubjectRecord record = graduationDesignPresubjectService.findByStudentIdAndGraduationDesignReleaseId(student.getStudentId(), graduationDesignReleaseId);
                GraduationDesignPresubjectBean graduationDesignPresubject = new GraduationDesignPresubjectBean();
                if (!ObjectUtils.isEmpty(record)) {
                    graduationDesignPresubject = record.into(GraduationDesignPresubjectBean.class);
                    graduationDesignPresubject.setUpdateTimeStr(DateTimeUtils.formatDate(graduationDesignPresubject.getUpdateTime()));
                }
                modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject);
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                page = "web/graduate/design/subject/design_subject_my::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未在该毕业设计条件下查询到学生信息");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 编辑我的题目
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面c对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/edit", method = RequestMethod.GET)
    public String myEdit(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            Users users = usersService.getUserFromSession();
            Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
            if (studentRecord.isPresent()) {
                Student student = studentRecord.get().into(Student.class);
                GraduationDesignPresubjectRecord record = graduationDesignPresubjectService.findByStudentIdAndGraduationDesignReleaseId(student.getStudentId(), graduationDesignReleaseId);
                GraduationDesignPresubject graduationDesignPresubject;
                if (!ObjectUtils.isEmpty(record)) {
                    graduationDesignPresubject = record.into(GraduationDesignPresubject.class);
                    page = "web/graduate/design/subject/design_subject_my_edit::#page-wrapper";
                } else {
                    graduationDesignPresubject = new GraduationDesignPresubject();
                    page = "web/graduate/design/subject/design_subject_my_add::#page-wrapper";
                }
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject);
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未在该毕业设计条件下查询到学生信息");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 保存
     *
     * @param graduationDesignPresubjectAddVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils mySave(@Valid GraduationDesignPresubjectAddVo graduationDesignPresubjectAddVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignPresubjectAddVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                Users users = usersService.getUserFromSession();
                Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                if (studentRecord.isPresent()) {
                    Student student = studentRecord.get().into(Student.class);
                    GraduationDesignPresubject graduationDesignPresubject = new GraduationDesignPresubject();
                    graduationDesignPresubject.setGraduationDesignPresubjectId(UUIDUtils.getUUID());
                    graduationDesignPresubject.setStudentId(student.getStudentId());
                    graduationDesignPresubject.setGraduationDesignReleaseId(graduationDesignPresubjectAddVo.getGraduationDesignReleaseId());
                    graduationDesignPresubject.setPresubjectTitle(graduationDesignPresubjectAddVo.getPresubjectTitle());
                    graduationDesignPresubject.setPresubjectPlan(graduationDesignPresubjectAddVo.getPresubjectPlan());
                    graduationDesignPresubject.setPublicLevel(graduationDesignPresubjectAddVo.getPublicLevel());
                    graduationDesignPresubject.setUpdateTime(DateTimeUtils.getNow());
                    graduationDesignPresubjectService.save(graduationDesignPresubject);
                    ajaxUtils.success().msg("保存成功");
                } else {
                    ajaxUtils.fail().msg("未在该毕业设计条件下查询到学生信息");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 更新
     *
     * @param graduationDesignPresubjectUpdateVo 数据
     * @param bindingResult                      检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myUpdate(@Valid GraduationDesignPresubjectUpdateVo graduationDesignPresubjectUpdateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignPresubjectUpdateVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                Users users = usersService.getUserFromSession();
                Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                if (studentRecord.isPresent()) {
                    GraduationDesignPresubject graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectUpdateVo.getGraduationDesignPresubjectId());
                    graduationDesignPresubject.setPresubjectTitle(graduationDesignPresubjectUpdateVo.getPresubjectTitle());
                    graduationDesignPresubject.setPresubjectPlan(graduationDesignPresubjectUpdateVo.getPresubjectPlan());
                    graduationDesignPresubject.setPublicLevel(graduationDesignPresubjectUpdateVo.getPublicLevel());
                    graduationDesignPresubject.setUpdateTime(DateTimeUtils.getNow());
                    graduationDesignPresubjectService.update(graduationDesignPresubject);
                    ajaxUtils.success().msg("保存成功");
                } else {
                    ajaxUtils.fail().msg("未在该毕业设计条件下查询到学生信息");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 进入我的题目页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            Users users = usersService.getUserFromSession();
            Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
            if (studentRecord.isPresent()) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg("未在该毕业设计条件下查询到学生信息");
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
    @RequestMapping(value = "/web/graduate/design/subject/condition", method = RequestMethod.POST)
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
     * 进入入口条件
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
                errorBean.setHasError(false);
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关毕业设计信息");
        }
        return errorBean;
    }
}
