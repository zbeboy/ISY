package top.zbeboy.isy.web.graduate.design.reorder;

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
import top.zbeboy.isy.domain.tables.records.DefenseRateRecord;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.graduate.design.*;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.bean.graduate.design.reorder.DefenseRateBean;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupMemberBean;
import top.zbeboy.isy.web.common.MethodControllerCommon;
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon;
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;
import top.zbeboy.isy.web.vo.graduate.design.reorder.DefenseOrderVo;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/7/19.
 */
@Slf4j
@Controller
public class GraduationDesignReorderController {

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private DefenseArrangementService defenseArrangementService;

    @Resource
    private DefenseTimeService defenseTimeService;

    @Resource
    private MethodControllerCommon methodControllerCommon;

    @Resource
    private DefenseGroupService defenseGroupService;

    @Resource
    private DefenseGroupMemberService defenseGroupMemberService;

    @Resource
    private DefenseOrderService defenseOrderService;

    @Resource
    private RoleService roleService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private UsersService usersService;

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private StaffService staffService;

    @Resource
    private DefenseRateService defenseRateService;

    @Resource
    private GraduationDesignMethodControllerCommon graduationDesignMethodControllerCommon;

    @Resource
    private GraduationDesignConditionCommon graduationDesignConditionCommon;

    /**
     * 毕业答辩顺序
     *
     * @return 毕业答辩顺序页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/reorder", method = RequestMethod.GET)
    public String reorder() {
        return "web/graduate/design/reorder/design_reorder::#page-wrapper";
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/reorder/design/data")
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> designDatas(PaginationUtils paginationUtils) {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils);
    }

    /**
     * 毕业答辩安排
     *
     * @return 毕业答辩安排页面
     */
    @RequestMapping(value = "/web/graduate/design/reorder/arrange", method = RequestMethod.GET)
    public String arrange(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (record.isPresent()) {
                DefenseArrangement defenseArrangement = record.get().into(DefenseArrangement.class);
                Result<Record> defenseTimeRecord = defenseTimeService.findByDefenseArrangementId(defenseArrangement.getDefenseArrangementId());
                List<DefenseTime> defenseTimes = defenseTimeRecord.into(DefenseTime.class);
                modelMap.addAttribute("defenseArrangement", defenseArrangement);
                modelMap.addAttribute("defenseTimes", defenseTimes);
                page = "web/graduate/design/reorder/design_reorder_arrange::#page-wrapper";
            } else {
                page = methodControllerCommon.showTip(modelMap, "未进行毕业设计答辩设置");
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 毕业设计答辩顺序
     *
     * @return 毕业设计答辩顺序页面
     */
    @RequestMapping(value = "/web/graduate/design/reorder/order", method = RequestMethod.GET)
    public String orderLook(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("defenseGroupId") String defenseGroupId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            // 是管理员或系统
            boolean reorderIsSuper = false;
            // 是组长
            boolean reorderIsLeader = false;
            // 是秘书
            boolean reorderIsSecretary = false;
            // 是组员
            boolean reorderIsMember = false;

            // 是否是管理员或系统
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                reorderIsSuper = true;
            } else {
                Users users = usersService.getUserFromSession();
                DefenseGroup defenseGroup = defenseGroupService.findById(defenseGroupId);
                if (!ObjectUtils.isEmpty(defenseGroup)) {
                    // 教职工
                    if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                        Staff staff = staffService.findByUsername(users.getUsername());
                        // 是否为组长
                        if (StringUtils.hasLength(defenseGroup.getLeaderId())) {
                            GraduationDesignTeacher graduationDesignTeacher = graduationDesignTeacherService.findById(defenseGroup.getLeaderId());
                            if (!ObjectUtils.isEmpty(graduationDesignTeacher)) {
                                if (!ObjectUtils.isEmpty(staff)) {
                                    if (Objects.equals(graduationDesignTeacher.getStaffId(), staff.getStaffId())) {
                                        reorderIsLeader = true;
                                    }
                                }
                            }
                        }

                        // 是否为组员
                        if (!reorderIsLeader) {
                            if (!ObjectUtils.isEmpty(staff)) {
                                Optional<Record> record = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                                if (record.isPresent()) {
                                    GraduationDesignTeacher graduationDesignTeacher = record.get().into(GraduationDesignTeacher.class);
                                    Optional<Record> groupMemberRecord = defenseGroupMemberService.findByDefenseGroupIdAndGraduationDesignTeacherId(defenseGroupId, graduationDesignTeacher.getGraduationDesignTeacherId());
                                    if (groupMemberRecord.isPresent()) {
                                        reorderIsMember = true;
                                    }
                                }
                            }
                        }

                        // 是否秘书
                        if (users.getUsername().equals(defenseGroup.getSecretaryId())) {
                            reorderIsSecretary = true;
                        }


                    } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) { // 学生
                        // 是否秘书
                        if (users.getUsername().equals(defenseGroup.getSecretaryId())) {
                            reorderIsSecretary = true;
                        }
                    }
                }
            }

            modelMap.addAttribute("reorderIsSuper", reorderIsSuper);
            modelMap.addAttribute("reorderIsLeader", reorderIsLeader);
            modelMap.addAttribute("reorderIsSecretary", reorderIsSecretary);
            modelMap.addAttribute("reorderIsMember", reorderIsMember);
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("defenseGroupId", defenseGroupId);
            page = "web/graduate/design/reorder/design_reorder_order::#page-wrapper";
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 计时页面
     *
     * @param defenseOrderId 序号id
     * @param modelMap       页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/reorder/timer", method = RequestMethod.GET)
    public String timer(@RequestParam("defenseOrderId") String defenseOrderId, int timer, ModelMap modelMap) {
        String page;
        DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderId);
        if (!ObjectUtils.isEmpty(defenseOrder)) {
            modelMap.addAttribute("defenseOrder", defenseOrder);
            modelMap.addAttribute("timer", timer);
            page = "web/graduate/design/reorder/design_reorder_timer";
        } else {
            modelMap.put("msg", "未查询到组信息");
            page = "msg";
        }
        return page;
    }

    /**
     * 查询顺序信息
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseOrderId            顺序id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/reorder/info", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils info(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("defenseOrderId") String defenseOrderId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderId);
            if (!ObjectUtils.isEmpty(defenseOrder)) {
                ajaxUtils.success().msg("获取数据成功").obj(defenseOrder);
            } else {
                ajaxUtils.fail().msg("未查询到相关顺序");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 更新答辩顺序状态
     *
     * @param defenseOrderVo 页面参数
     * @param bindingResult  检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/reorder/status", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils status(@Valid DefenseOrderVo defenseOrderVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(defenseOrderVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                Optional<Record> defenseArrangementRecord = defenseArrangementService.findByGraduationDesignReleaseId(defenseOrderVo.getGraduationDesignReleaseId());
                if (defenseArrangementRecord.isPresent()) {
                    DefenseArrangement defenseArrangement = defenseArrangementRecord.get().into(DefenseArrangement.class);
                    // 答辩开始时间之后可用
                    if (DateTimeUtils.timestampAfterDecide(defenseArrangement.getDefenseStartTime())) {
                        if (groupCondition(defenseOrderVo)) {
                            DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderVo.getDefenseOrderId());
                            if (!ObjectUtils.isEmpty(defenseOrder)) {
                                defenseOrder.setDefenseStatus(defenseOrderVo.getDefenseStatus());
                                defenseOrderService.update(defenseOrder);
                                ajaxUtils.success().msg("更新状态成功");
                            } else {
                                ajaxUtils.fail().msg("未查询到相关顺序");
                            }
                        } else {
                            ajaxUtils.fail().msg("您不符合更改条件");
                        }

                    } else {
                        ajaxUtils.fail().msg("请在答辩开始之后操作");
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关毕业设计答辩设置");
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
     * 获取当前教师打分信息
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  校验
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/reorder/grade/info", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils gradeInfo(@Valid DefenseOrderVo defenseOrderVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(defenseOrderVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderVo.getDefenseOrderId());
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    // 答辩状态为 已进行
                    if (!ObjectUtils.isEmpty(defenseOrder.getDefenseStatus()) && defenseOrder.getDefenseStatus() == 1) {
                        // 判断资格
                        boolean canUse = false;
                        Users users = usersService.getUserFromSession();
                        DefenseGroup defenseGroup = defenseGroupService.findById(defenseOrderVo.getDefenseGroupId());
                        String graduationDesignTeacherId = null;
                        if (!ObjectUtils.isEmpty(defenseGroup)) {
                            // 是组长
                            boolean reorderIsLeader = false;
                            // 教职工
                            if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                                Staff staff = staffService.findByUsername(users.getUsername());
                                // 是否为组长
                                if (StringUtils.hasLength(defenseGroup.getLeaderId())) {
                                    GraduationDesignTeacher graduationDesignTeacher = graduationDesignTeacherService.findById(defenseGroup.getLeaderId());
                                    if (!ObjectUtils.isEmpty(graduationDesignTeacher)) {
                                        if (!ObjectUtils.isEmpty(staff)) {
                                            if (Objects.equals(graduationDesignTeacher.getStaffId(), staff.getStaffId())) {
                                                canUse = true;
                                                reorderIsLeader = true;
                                                graduationDesignTeacherId = graduationDesignTeacher.getGraduationDesignTeacherId();
                                            }
                                        }
                                    }
                                }
                                // 是否为组员
                                if (!reorderIsLeader) {
                                    if (!ObjectUtils.isEmpty(staff)) {
                                        Optional<Record> record = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(defenseOrderVo.getGraduationDesignReleaseId(), staff.getStaffId());
                                        if (record.isPresent()) {
                                            GraduationDesignTeacher graduationDesignTeacher = record.get().into(GraduationDesignTeacher.class);
                                            Optional<Record> groupMemberRecord = defenseGroupMemberService.findByDefenseGroupIdAndGraduationDesignTeacherId(defenseOrderVo.getDefenseGroupId(), graduationDesignTeacher.getGraduationDesignTeacherId());
                                            if (groupMemberRecord.isPresent()) {
                                                canUse = true;
                                                graduationDesignTeacherId = graduationDesignTeacher.getGraduationDesignTeacherId();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (canUse) {
                            double grade = 0d;
                            DefenseRateRecord defenseRateRecord = defenseRateService.findByDefenseOrderIdAndGraduationDesignTeacherId(defenseOrderVo.getDefenseOrderId(), graduationDesignTeacherId);
                            if (!ObjectUtils.isEmpty(defenseRateRecord)) {
                                grade = defenseRateRecord.getGrade();
                            }
                            ajaxUtils.success().msg("获取分数成功").obj(grade);
                        } else {
                            ajaxUtils.fail().msg("您不符合查询条件");
                        }
                    } else {
                        ajaxUtils.fail().msg("未开始答辩，无法操作");
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关毕业设计答辩设置");
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
     * 更新当前教师打分信息
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  校验
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/reorder/grade", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils grade(@Valid DefenseOrderVo defenseOrderVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(defenseOrderVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderVo.getDefenseOrderId());
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    // 答辩状态为 已进行
                    if (!ObjectUtils.isEmpty(defenseOrder.getDefenseStatus()) && defenseOrder.getDefenseStatus() == 1) {
                        // 判断资格
                        boolean canUse = false;
                        Users users = usersService.getUserFromSession();
                        DefenseGroup defenseGroup = defenseGroupService.findById(defenseOrderVo.getDefenseGroupId());
                        String graduationDesignTeacherId = null;
                        if (!ObjectUtils.isEmpty(defenseGroup)) {
                            // 是组长
                            boolean reorderIsLeader = false;
                            // 教职工
                            if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                                Staff staff = staffService.findByUsername(users.getUsername());
                                // 是否为组长
                                if (StringUtils.hasLength(defenseGroup.getLeaderId())) {
                                    GraduationDesignTeacher graduationDesignTeacher = graduationDesignTeacherService.findById(defenseGroup.getLeaderId());
                                    if (!ObjectUtils.isEmpty(graduationDesignTeacher)) {
                                        if (!ObjectUtils.isEmpty(staff)) {
                                            if (Objects.equals(graduationDesignTeacher.getStaffId(), staff.getStaffId())) {
                                                canUse = true;
                                                reorderIsLeader = true;
                                                graduationDesignTeacherId = graduationDesignTeacher.getGraduationDesignTeacherId();
                                            }
                                        }
                                    }
                                }
                                // 是否为组员
                                if (!reorderIsLeader) {
                                    if (!ObjectUtils.isEmpty(staff)) {
                                        Optional<Record> record = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(defenseOrderVo.getGraduationDesignReleaseId(), staff.getStaffId());
                                        if (record.isPresent()) {
                                            GraduationDesignTeacher graduationDesignTeacher = record.get().into(GraduationDesignTeacher.class);
                                            Optional<Record> groupMemberRecord = defenseGroupMemberService.findByDefenseGroupIdAndGraduationDesignTeacherId(defenseOrderVo.getDefenseGroupId(), graduationDesignTeacher.getGraduationDesignTeacherId());
                                            if (groupMemberRecord.isPresent()) {
                                                canUse = true;
                                                graduationDesignTeacherId = graduationDesignTeacher.getGraduationDesignTeacherId();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (canUse) {
                            DefenseRate defenseRate;
                            DefenseRateRecord defenseRateRecord = defenseRateService.findByDefenseOrderIdAndGraduationDesignTeacherId(defenseOrderVo.getDefenseOrderId(), graduationDesignTeacherId);
                            if (!ObjectUtils.isEmpty(defenseRateRecord)) {
                                defenseRate = defenseRateRecord.into(DefenseRate.class);
                                defenseRate.setGrade(defenseOrderVo.getGrade());
                            } else {
                                defenseRate = new DefenseRate();
                                defenseRate.setDefenseOrderId(defenseOrderVo.getDefenseOrderId());
                                defenseRate.setGraduationDesignTeacherId(graduationDesignTeacherId);
                                defenseRate.setGrade(defenseOrderVo.getGrade());
                            }
                            defenseRateService.saveOrUpdate(defenseRate);
                            ajaxUtils.success().msg("保存成功");
                        } else {
                            ajaxUtils.fail().msg("您不符合更改条件");
                        }
                    } else {
                        ajaxUtils.fail().msg("未开始答辩，无法操作");
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关毕业设计答辩设置");
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
     * 获取组及组员信息
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/reorder/groups", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<DefenseGroupBean> groups(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils<DefenseGroupBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            List<DefenseGroupBean> defenseGroupBeens = defenseGroupService.findByGraduationDesignReleaseIdRelation(graduationDesignReleaseId);
            defenseGroupBeens.forEach(defenseGroupBean -> {
                List<String> memberName = new ArrayList<>();
                List<DefenseGroupMemberBean> defenseGroupMemberBeens = defenseGroupMemberService.findByDefenseGroupIdForStaff(defenseGroupBean.getDefenseGroupId());
                defenseGroupMemberBeens.forEach(defenseGroupMemberBean ->
                        memberName.add(defenseGroupMemberBean.getStaffName())
                );
                defenseGroupBean.setMemberName(memberName);
            });
            ajaxUtils.success().msg("获取数据成功！").listData(defenseGroupBeens);
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 查询各教师打分及成绩信息
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  检验
     * @return 结果
     */
    @RequestMapping(value = "/web/graduate/design/reorder/mark/info", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<DefenseRateBean> markInfo(@Valid DefenseOrderVo defenseOrderVo, BindingResult bindingResult) {
        AjaxUtils<DefenseRateBean> ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(defenseOrderVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderVo.getDefenseOrderId());
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    List<DefenseRateBean> defenseRateBeans = defenseRateService.findByDefenseOrderIdAndDefenseGroupId(defenseOrderVo.getDefenseOrderId(), defenseOrderVo.getDefenseGroupId());
                    ajaxUtils.success().msg("获取数据成功！").listData(defenseRateBeans).obj(defenseOrder);
                } else {
                    ajaxUtils.fail().msg("未获取到相关顺序");
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
     * 修改成绩
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/reorder/mark", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils mark(@Valid DefenseOrderVo defenseOrderVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(defenseOrderVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderVo.getDefenseOrderId());
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    defenseOrder.setScoreTypeId(defenseOrderVo.getScoreTypeId());
                    defenseOrderService.update(defenseOrder);
                    ajaxUtils.success().msg("修改成绩成功");
                } else {
                    ajaxUtils.fail().msg("未获取到相关顺序");
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
     * 问题条件
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  校验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/reorder/question/info", method = RequestMethod.GET)
    public String questionInfo(@Valid DefenseOrderVo defenseOrderVo, BindingResult bindingResult, ModelMap modelMap) {
        String page;
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(defenseOrderVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderVo.getDefenseOrderId());
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    modelMap.addAttribute("defenseOrder", defenseOrder);
                    modelMap.addAttribute("defenseOrderVo", defenseOrderVo);
                    if (groupCondition(defenseOrderVo)) {
                        page = "web/graduate/design/reorder/design_reorder_question_edit::#page-wrapper";
                    } else {
                        page = "web/graduate/design/reorder/design_reorder_question::#page-wrapper";
                    }
                } else {
                    page = methodControllerCommon.showTip(modelMap, "未查询到相关顺序");
                }
            } else {
                page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, "参数异常");
        }
        return page;
    }

    /**
     * 更新问题
     *
     * @param defenseOrderVo 数据
     * @param bindingResult  校验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/reorder/question", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils question(@Valid DefenseOrderVo defenseOrderVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(defenseOrderVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                // 判断资格
                if (groupCondition(defenseOrderVo)) {
                    DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderVo.getDefenseOrderId());
                    if (!ObjectUtils.isEmpty(defenseOrder)) {
                        defenseOrder.setDefenseQuestion(defenseOrderVo.getDefenseQuestion());
                        defenseOrderService.update(defenseOrder);
                        ajaxUtils.success().msg("更新成功");
                    } else {
                        ajaxUtils.fail().msg("未查询到相关顺序");
                    }
                } else {
                    ajaxUtils.fail().msg("不符合编辑条件");
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
     * 安排页面进入条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or falseo
     */
    @RequestMapping(value = "/web/graduate/design/reorder/arrange/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils arrangeCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (record.isPresent()) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg("未进行毕业设计答辩设置");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 组条件
     *
     * @param defenseOrderVo 数据
     * @return true or false
     */
    private boolean groupCondition(DefenseOrderVo defenseOrderVo) {
        // 判断资格
        boolean canUse = false;
        // 是否是管理员或系统
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            canUse = true;
        } else {
            Users users = usersService.getUserFromSession();
            DefenseGroup defenseGroup = defenseGroupService.findById(defenseOrderVo.getDefenseGroupId());
            if (!ObjectUtils.isEmpty(defenseGroup)) {
                // 教职工
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    Staff staff = staffService.findByUsername(users.getUsername());
                    // 是否为组长
                    if (StringUtils.hasLength(defenseGroup.getLeaderId())) {
                        GraduationDesignTeacher graduationDesignTeacher = graduationDesignTeacherService.findById(defenseGroup.getLeaderId());
                        if (!ObjectUtils.isEmpty(graduationDesignTeacher)) {
                            if (!ObjectUtils.isEmpty(staff)) {
                                if (Objects.equals(graduationDesignTeacher.getStaffId(), staff.getStaffId())) {
                                    canUse = true;
                                }
                            }
                        }
                    }
                    // 是否秘书
                    if (users.getUsername().equals(defenseGroup.getSecretaryId())) {
                        canUse = true;
                    }
                } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) { // 学生
                    // 是否秘书
                    if (users.getUsername().equals(defenseGroup.getSecretaryId())) {
                        canUse = true;
                    }
                }
            }
        }
        return canUse;
    }

    /**
     * 进入入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> accessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                errorBean.setHasError(false);
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("不在毕业时间范围，无法操作");
            }
        }
        return errorBean;
    }
}
