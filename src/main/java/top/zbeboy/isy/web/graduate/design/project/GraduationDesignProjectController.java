package top.zbeboy.isy.web.graduate.design.project;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher;
import top.zbeboy.isy.domain.tables.pojos.Staff;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/26.
 */
@Slf4j
@Controller
public class GraduationDesignProjectController {

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private UsersService usersService;

    @Resource
    private StaffService staffService;

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    /**
     * 毕业设计规划
     *
     * @return 毕业设计规划页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/project", method = RequestMethod.GET)
    public String project() {
        return "web/graduate/design/project/design_project::#page-wrapper";
    }

    /**
     * 规划
     *
     * @return 规划页面
     */
    @RequestMapping(value = "/web/graduate/design/project/list", method = RequestMethod.GET)
    public String projectList(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            page = "web/graduate/design/project/design_project_list::#page-wrapper";
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 我的学生
     *
     * @return 我的学生页面
     */
    @RequestMapping(value = "/web/graduate/design/project/students", method = RequestMethod.GET)
    public String students(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                page = "web/graduate/design/project/design_project_list::#page-wrapper";
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未确认学生填报，无法操作");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 进入我的学生页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/project/student/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils studentCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg("未确认学生填报，无法操作");
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
    @RequestMapping(value = "/web/graduate/design/project/condition", method = RequestMethod.POST)
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
        Map<String, Object> mapData = new HashMap<>();
        GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
        if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
            errorBean.setData(graduationDesignRelease);
            if (graduationDesignRelease.getGraduationDesignIsDel() == 1) {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("该毕业设计已被注销");
            } else {
                // 是否已确认毕业设计指导教师
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacher()) && graduationDesignRelease.getIsOkTeacher() == 1) {
                    // 是否为该次毕业设计指导教师
                    Users users = usersService.getUserFromSession();
                    Staff staff = staffService.findByUsername(users.getUsername());
                    if (!ObjectUtils.isEmpty(staff)) {
                        Optional<Record> record = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                        if (record.isPresent()) {
                            GraduationDesignTeacher graduationDesignTeacher = record.get().into(GraduationDesignTeacher.class);
                            mapData.put("graduationDesignTeacher", graduationDesignTeacher);
                            // 毕业时间范围
                            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                                errorBean.setHasError(false);
                            } else {
                                errorBean.setHasError(true);
                                errorBean.setErrorMsg("不在毕业设计时间范围，无法操作");
                            }
                        } else {
                            errorBean.setHasError(true);
                            errorBean.setErrorMsg("您不是该次毕业设计的指导教师");
                        }
                    } else {
                        errorBean.setHasError(true);
                        errorBean.setErrorMsg("未查询到相关教职工信息");
                    }
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("未确认毕业设计指导教师，无法操作");
                }
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关毕业设计信息");
        }
        errorBean.setMapData(mapData);
        return errorBean;
    }
}
