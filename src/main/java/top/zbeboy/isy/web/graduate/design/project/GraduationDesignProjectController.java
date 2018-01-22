package top.zbeboy.isy.web.graduate.design.project;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.common.FilesService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignPlanService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean;
import top.zbeboy.isy.web.bean.graduate.design.project.GraduationDesignPlanBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.common.MethodControllerCommon;
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon;
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.graduate.design.project.GraduationDesignProjectAddVo;
import top.zbeboy.isy.web.vo.graduate.design.project.GraduationDesignProjectUpdateVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by zbeboy on 2017/5/26.
 */
@Slf4j
@Controller
public class GraduationDesignProjectController {

    @Resource
    private UsersService usersService;

    @Resource
    private StaffService staffService;

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private GraduationDesignPlanService graduationDesignPlanService;

    @Resource
    private GraduationDesignTutorService graduationDesignTutorService;

    @Resource
    private FilesService filesService;

    @Resource
    private UploadService uploadService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private MethodControllerCommon methodControllerCommon;

    @Resource
    private GraduationDesignMethodControllerCommon graduationDesignMethodControllerCommon;

    @Resource
    private GraduationDesignConditionCommon graduationDesignConditionCommon;

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
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/project/design/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> designDatas(PaginationUtils paginationUtils) {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils);
    }

    /**
     * 列表
     *
     * @return 列表页面
     */
    @RequestMapping(value = "/web/graduate/design/project/list", method = RequestMethod.GET)
    public String projectList(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                Users users = usersService.getUserFromSession();
                Staff staff = staffService.findByUsername(users.getUsername());
                if (!ObjectUtils.isEmpty(staff)) {
                    modelMap.addAttribute("staffId", staff.getStaffId());
                }
            }
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            page = "web/graduate/design/project/design_project_list::#page-wrapper";
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * Ta的规划
     *
     * @return Ta的规划页面
     */
    @RequestMapping(value = "/web/graduate/design/project/list/detail", method = RequestMethod.GET)
    public String projectDetail(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("staffId") int staffId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            // 当前毕业设计指导教师查看自己的规划
            if (isOwner(staffId)) {
                page = "web/graduate/design/project/design_project_my::#page-wrapper";
            } else {
                Optional<Record> staffRecord = staffService.findByIdRelationForUsers(staffId);
                if (staffRecord.isPresent()) {
                    Users staffUser = staffRecord.get().into(Users.class);
                    modelMap.addAttribute("staffRealName", staffUser.getRealName());
                    page = "web/graduate/design/project/design_project_detail::#page-wrapper";
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到教师信息");
                }
            }
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("staffId", staffId);
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * Ta的学生
     *
     * @return Ta的学生页面
     */
    @RequestMapping(value = "/web/graduate/design/project/list/students", method = RequestMethod.GET)
    public String projectStudents(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("staffId") int staffId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            // 当前毕业设计指导教师查看自己的规划
            if (isOwner(staffId)) {
                page = "web/graduate/design/project/design_project_my_students::#page-wrapper";
            } else {
                Optional<Record> staffRecord = staffService.findByIdRelationForUsers(staffId);
                if (staffRecord.isPresent()) {
                    Users staffUser = staffRecord.get().into(Users.class);
                    modelMap.addAttribute("staffRealName", staffUser.getRealName());
                    page = "web/graduate/design/project/design_project_students::#page-wrapper";
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到教师信息");
                }
            }
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("staffId", staffId);
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 检测当前用户
     *
     * @param staffId 教职工id
     * @return true or false
     */
    private boolean isOwner(int staffId) {
        boolean isOwner = false;
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Users users = usersService.getUserFromSession();
            Staff staff = staffService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(staff)) {
                if (staff.getStaffId() == staffId) {
                    isOwner = true;
                }
            }
        }
        return isOwner;
    }

    /**
     * 我的规划
     *
     * @return 我的规划页面
     */
    @RequestMapping(value = "/web/graduate/design/project/my/list", method = RequestMethod.GET)
    public String myProjects(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = myCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Staff staff = (Staff) errorBean.getMapData().get("staff");
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("staffId", staff.getStaffId());
            page = "web/graduate/design/project/design_project_my::#page-wrapper";
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 我的学生
     *
     * @return 我的学生页面
     */
    @RequestMapping(value = "/web/graduate/design/project/my/students", method = RequestMethod.GET)
    public String myStudents(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = myCondition(graduationDesignReleaseId);
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean);
        if (!errorBean.isHasError()) {
            Staff staff = (Staff) errorBean.getMapData().get("staff");
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("staffId", staff.getStaffId());
            page = "web/graduate/design/project/design_project_my_students::#page-wrapper";
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 教师数据
     *
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/project/list/teachers", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignTeacherBean> teachers(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils<GraduationDesignTeacherBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            List<GraduationDesignTeacherBean> graduationDesignTeacherBeen = graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId);
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeen);
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 规划添加
     *
     * @return 规划添加页面
     */
    @RequestMapping(value = "/web/graduate/design/project/list/add", method = RequestMethod.GET)
    public String projectListAdd(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            // 查询最近的一条件记录，时间为当前
            GraduationDesignTeacher graduationDesignTeacher = (GraduationDesignTeacher) errorBean.getMapData().get("graduationDesignTeacher");
            Record record =
                    graduationDesignPlanService.findByGraduationDesignTeacherIdAndLessThanAddTime(graduationDesignTeacher.getGraduationDesignTeacherId(), DateTimeUtils.getNow());
            GraduationDesignPlanBean graduationDesignPlan;
            if (!ObjectUtils.isEmpty(record)) {
                graduationDesignPlan = record.into(GraduationDesignPlanBean.class);
            } else {
                graduationDesignPlan = new GraduationDesignPlanBean();
            }
            page = "web/graduate/design/project/design_project_add::#page-wrapper";
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("graduationDesignPlanRecently", graduationDesignPlan);
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 规划编辑
     *
     * @return 规划编辑页面
     */
    @RequestMapping(value = "/web/graduate/design/project/list/edit", method = RequestMethod.GET)
    public String projectListEdit(@RequestParam("id") String graduationDesignReleaseId,
                                  @RequestParam("graduationDesignPlanId") String graduationDesignPlanId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> graduationDesignPlanRecord = graduationDesignPlanService.findByIdRelation(graduationDesignPlanId);
            if (graduationDesignPlanRecord.isPresent()) {
                GraduationDesignPlanBean graduationDesignPlan = graduationDesignPlanRecord.get().into(GraduationDesignPlanBean.class);
                // 查询上一条件记录，时间为当前计划时间
                GraduationDesignTeacher graduationDesignTeacher = (GraduationDesignTeacher) errorBean.getMapData().get("graduationDesignTeacher");
                Record record =
                        graduationDesignPlanService.findByGraduationDesignTeacherIdAndLessThanAddTime(graduationDesignTeacher.getGraduationDesignTeacherId(), graduationDesignPlan.getAddTime());
                GraduationDesignPlanBean graduationDesignPlanRecently;
                if (!ObjectUtils.isEmpty(record)) {
                    graduationDesignPlanRecently = record.into(GraduationDesignPlanBean.class);
                } else {
                    graduationDesignPlanRecently = new GraduationDesignPlanBean();
                }
                page = "web/graduate/design/project/design_project_edit::#page-wrapper";
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                modelMap.addAttribute("graduationDesignPlanRecently", graduationDesignPlanRecently);
                modelMap.addAttribute("graduationDesignPlan", graduationDesignPlan);
            } else {
                page = methodControllerCommon.showTip(modelMap, "未查询到相关信息");
            }

        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 规划下载
     */
    @RequestMapping(value = "/web/graduate/design/project/list/download", method = RequestMethod.GET)
    public void projectListDownload(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("staffId") int staffId, HttpServletRequest request, HttpServletResponse response) {
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Result<Record> graduationDesignTutorRecord =
                    graduationDesignTutorService.findByStaffIdAndGraduationDesignReleaseIdRelationForStudent(staffId, graduationDesignReleaseId);
            List<GraduationDesignTutorBean> graduationDesignTutorBeanList = new ArrayList<>();
            if (graduationDesignTutorRecord.isNotEmpty()) {
                graduationDesignTutorBeanList = graduationDesignTutorRecord.into(GraduationDesignTutorBean.class);
            }
            List<GraduationDesignPlanBean> graduationDesignPlanBeanList = new ArrayList<>();
            Result<Record> graduationDesignPlanRecord = graduationDesignPlanService.findByGraduationDesignReleaseIdAndStaffIdOrderByAddTime(graduationDesignReleaseId, staffId);
            if (graduationDesignPlanRecord.isNotEmpty()) {
                graduationDesignPlanBeanList = graduationDesignPlanRecord.into(GraduationDesignPlanBean.class);
            }
            Optional<Record> staffRecord = staffService.findByIdRelation(staffId);
            if (staffRecord.isPresent()) {
                Users users = staffRecord.get().into(Users.class);
                String path = filesService.saveGraduationDesignPlan(users, request, graduationDesignTutorBeanList, graduationDesignPlanBeanList);
                uploadService.download("毕业设计指导计划（" + users.getRealName() + "）", path, response, request);
            }
        }
    }

    /**
     * 获取列表数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 列表数据
     */
    @RequestMapping(value = "/web/graduate/design/project/list/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignPlanBean> listData(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("staffId") int staffId) {
        AjaxUtils<GraduationDesignPlanBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            List<GraduationDesignPlanBean> graduationDesignPlans = new ArrayList<>();
            Result<Record> records = graduationDesignPlanService.findByGraduationDesignReleaseIdAndStaffIdOrderByAddTime(graduationDesignReleaseId, staffId);
            if (records.isNotEmpty()) {
                graduationDesignPlans = records.into(GraduationDesignPlanBean.class);
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignPlans);
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 获取学生列表数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 学生列表数据
     */
    @RequestMapping(value = "/web/graduate/design/project/students/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignTutorBean> studentListData(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("staffId") int staffId) {
        AjaxUtils<GraduationDesignTutorBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean);
        if (!errorBean.isHasError()) {
            Result<Record> records =
                    graduationDesignTutorService.findByStaffIdAndGraduationDesignReleaseIdRelationForStudent(staffId, graduationDesignReleaseId);
            List<GraduationDesignTutorBean> graduationDesignTutorBeans = new ArrayList<>();
            if (records.isNotEmpty()) {
                graduationDesignTutorBeans = records.into(GraduationDesignTutorBean.class);
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTutorBeans);
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 保存
     *
     * @param graduationDesignProjectAddVo 数据
     * @param bindingResult                检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/project/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils save(@Valid GraduationDesignProjectAddVo graduationDesignProjectAddVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignProjectAddVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                GraduationDesignPlan graduationDesignPlan = new GraduationDesignPlan();
                GraduationDesignTeacher graduationDesignTeacher = (GraduationDesignTeacher) errorBean.getMapData().get("graduationDesignTeacher");
                graduationDesignPlan.setGraduationDesignPlanId(UUIDUtils.getUUID());
                graduationDesignPlan.setGraduationDesignTeacherId(graduationDesignTeacher.getGraduationDesignTeacherId());
                graduationDesignPlan.setScheduling(graduationDesignProjectAddVo.getScheduling());
                graduationDesignPlan.setSupervisionTime(graduationDesignProjectAddVo.getSupervisionTime());
                graduationDesignPlan.setGuideContent(graduationDesignProjectAddVo.getGuideContent());
                graduationDesignPlan.setNote(graduationDesignProjectAddVo.getNote());
                graduationDesignPlan.setSchoolroomId(graduationDesignProjectAddVo.getSchoolroomId());
                graduationDesignPlan.setAddTime(DateTimeUtils.getNow());
                graduationDesignPlanService.save(graduationDesignPlan);
                ajaxUtils.success().msg("保存成功");
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
     * @param graduationDesignProjectUpdateVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/project/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils save(@Valid GraduationDesignProjectUpdateVo graduationDesignProjectUpdateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignProjectUpdateVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                GraduationDesignPlan graduationDesignPlan = graduationDesignPlanService.findById(graduationDesignProjectUpdateVo.getGraduationDesignPlanId());
                graduationDesignPlan.setScheduling(graduationDesignProjectUpdateVo.getScheduling());
                graduationDesignPlan.setSupervisionTime(graduationDesignProjectUpdateVo.getSupervisionTime());
                graduationDesignPlan.setGuideContent(graduationDesignProjectUpdateVo.getGuideContent());
                graduationDesignPlan.setNote(graduationDesignProjectUpdateVo.getNote());
                graduationDesignPlan.setSchoolroomId(graduationDesignProjectUpdateVo.getSchoolroomId());
                graduationDesignPlanService.update(graduationDesignPlan);
                ajaxUtils.success().msg("保存成功");
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 删除
     *
     * @param graduationDesignPlanIds   规划id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/project/list/delete", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils collegeUpdateDel(String graduationDesignPlanIds, @RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(graduationDesignPlanIds)) {
                graduationDesignPlanService.deleteById(SmallPropsUtils.StringIdsToStringList(graduationDesignPlanIds));
                ajaxUtils.success().msg("删除成功");
            } else {
                ajaxUtils.fail().msg("未发现选中信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 列表判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/project/list/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils listCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入Ta的学生页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/project/students/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils studentsCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = simpleCondition(graduationDesignReleaseId);
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入我的规划页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/project/my/list/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myListCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = myCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入我的学生页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/project/my/students/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myStudentsCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = myCondition(graduationDesignReleaseId);
        graduationDesignConditionCommon.isNotOkTeacherAdjust(errorBean);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
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
     * 简单条件
     *
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> simpleCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        graduationDesignConditionCommon.isNotOkTeacherCondition(errorBean);
        return errorBean;
    }

    /**
     * 我的数据条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> myCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        graduationDesignConditionCommon.isNotOkTeacherCondition(errorBean);
        if (!errorBean.isHasError()) {
            Map<String, Object> mapData = new HashMap<>();
            okCurrentTeacher(errorBean, mapData);
            errorBean.setMapData(mapData);
        }
        return errorBean;
    }

    /**
     * 进入入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> accessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.isNotOkTeacherCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Map<String, Object> mapData = new HashMap<>();
            okCurrentTeacher(errorBean, mapData);
            errorBean.setMapData(mapData);
        }
        return errorBean;
    }

    /**
     * 毕业设计指导教师判断
     *
     * @param errorBean 条件
     * @param mapData   data
     */
    private void okCurrentTeacher(ErrorBean<GraduationDesignRelease> errorBean, Map<String, Object> mapData) {
        GraduationDesignRelease graduationDesignRelease = errorBean.getData();
        // 是否为该次毕业设计指导教师
        Users users = usersService.getUserFromSession();
        Staff staff = staffService.findByUsername(users.getUsername());
        if (!ObjectUtils.isEmpty(staff)) {
            mapData.put("staff", staff);
            Optional<Record> record = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignRelease.getGraduationDesignReleaseId(), staff.getStaffId());
            if (record.isPresent()) {
                GraduationDesignTeacher graduationDesignTeacher = record.get().into(GraduationDesignTeacher.class);
                mapData.put("graduationDesignTeacher", graduationDesignTeacher);
                errorBean.setHasError(false);
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("您不是该毕业设计指导教师");
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关教职工信息");
        }
    }
}
