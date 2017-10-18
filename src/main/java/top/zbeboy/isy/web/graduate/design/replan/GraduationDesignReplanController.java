package top.zbeboy.isy.web.graduate.design.replan;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.domain.tables.records.DefenseGroupMemberRecord;
import top.zbeboy.isy.domain.tables.records.DefenseOrderRecord;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.BuildingService;
import top.zbeboy.isy.service.graduate.design.*;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupMemberBean;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseOrderBean;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.graduate.design.replan.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;

/**
 * Created by zbeboy on 2017/7/7.
 */
@Slf4j
@Controller
public class GraduationDesignReplanController {

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private DefenseArrangementService defenseArrangementService;

    @Resource
    private DefenseTimeService defenseTimeService;

    @Resource
    private DefenseGroupService defenseGroupService;

    @Resource
    private BuildingService buildingService;

    @Resource
    private DefenseOrderService defenseOrderService;

    @Resource
    private DefenseRateService defenseRateService;

    @Resource
    private DefenseGroupMemberService defenseGroupMemberService;

    @Resource
    private UsersService usersService;

    /**
     * 毕业设计答辩安排
     *
     * @return 毕业设计答辩安排页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/replan", method = RequestMethod.GET)
    public String replan() {
        return "web/graduate/design/replan/design_replan::#page-wrapper";
    }

    /**
     * 毕业设计答辩设置
     *
     * @return 毕业设计答辩设置页面
     */
    @RequestMapping(value = "/web/graduate/design/replan/arrange", method = RequestMethod.GET)
    public String arrange(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (record.isPresent()) {
                DefenseArrangement defenseArrangement = record.get().into(DefenseArrangement.class);
                Result<Record> defenseTimeRecord = defenseTimeService.findByDefenseArrangementId(defenseArrangement.getDefenseArrangementId());
                List<DefenseTime> defenseTimes = defenseTimeRecord.into(DefenseTime.class);
                modelMap.addAttribute("defenseArrangement", defenseArrangement);
                modelMap.addAttribute("defenseTimes", defenseTimes);
                page = "web/graduate/design/replan/design_replan_arrange_edit::#page-wrapper";
            } else {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                page = "web/graduate/design/replan/design_replan_arrange_add::#page-wrapper";
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 毕业设计答辩组管理
     *
     * @return 毕业设计答辩组管理页面
     */
    @RequestMapping(value = "/web/graduate/design/replan/group", method = RequestMethod.GET)
    public String group(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (record.isPresent()) {
                DefenseArrangement defenseArrangement = record.get().into(DefenseArrangement.class);
                modelMap.addAttribute("defenseArrangement", defenseArrangement);
                page = "web/graduate/design/replan/design_replan_group::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "请先进行毕业设计答辩设置");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 毕业设计答辩教师分组
     *
     * @return 毕业设计答辩教师分组页面
     */
    @RequestMapping(value = "/web/graduate/design/replan/divide", method = RequestMethod.GET)
    public String divide(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认毕业设计指导教师
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacher()) && graduationDesignRelease.getIsOkTeacher() == 1) {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                page = "web/graduate/design/replan/design_replan_divide::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未确认毕业设计指导教师");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 毕业设计答辩生成顺序
     *
     * @return 毕业设计答辩生成顺序页面
     */
    @RequestMapping(value = "/web/graduate/design/replan/order", method = RequestMethod.GET)
    public String order(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (record.isPresent()) {
                DefenseArrangement defenseArrangement = record.get().into(DefenseArrangement.class);
                modelMap.addAttribute("defenseArrangement", defenseArrangement);
                page = "web/graduate/design/replan/design_replan_order::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "请先进行毕业设计答辩设置");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 毕业设计答辩顺序
     *
     * @return 毕业设计答辩顺序页面
     */
    @RequestMapping(value = "/web/graduate/design/replan/order/look", method = RequestMethod.GET)
    public String orderLook(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("defenseGroupId") String defenseGroupId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("defenseGroupId", defenseGroupId);
            page = "web/graduate/design/replan/design_replan_order_look::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 教师分组数据
     *
     * @param condition 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/replan/divide/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignTeacherBean> divideData(GraduationDesignTeacherBean condition) {
        AjaxUtils<GraduationDesignTeacherBean> ajaxUtils = AjaxUtils.of();
        List<GraduationDesignTeacherBean> graduationDesignTeacherBeens = new ArrayList<>();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(condition.getGraduationDesignReleaseId());
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认毕业设计指导教师
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacher()) && graduationDesignRelease.getIsOkTeacher() == 1) {
                graduationDesignTeacherBeens = defenseGroupMemberService.findByGraduationDesignReleaseIdRelationForStaff(condition);
            }
        }
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens);
    }

    /**
     * 添加组
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/replan/group/add", method = RequestMethod.GET)
    public String groupAdd(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (record.isPresent()) {
                DefenseArrangement defenseArrangement = record.get().into(DefenseArrangement.class);
                modelMap.addAttribute("defenseArrangement", defenseArrangement);
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                page = "web/graduate/design/replan/design_replan_group_add::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "请先进行毕业设计答辩设置");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 编辑组
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/replan/group/edit", method = RequestMethod.GET)
    public String groupEdit(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("defenseGroupId") String defenseGroupId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = defenseGroupService.findByIdRelation(defenseGroupId);
            if (record.isPresent()) {
                DefenseGroupBean defenseGroup = record.get().into(DefenseGroupBean.class);
                modelMap.addAttribute("defenseGroup", defenseGroup);
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                page = "web/graduate/design/replan/design_replan_group_edit::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未查询到相关组信息");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 毕业设计安排保存
     *
     * @param graduationDesignReplanAddVo 数据
     * @param bindingResult               检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/arrange/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils arrangeSave(@Valid GraduationDesignReplanAddVo graduationDesignReplanAddVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReplanAddVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                String[] dayDefenseStartTime = graduationDesignReplanAddVo.getDayDefenseStartTime();
                String[] dayDefenseEndTime = graduationDesignReplanAddVo.getDayDefenseEndTime();
                if (dayDefenseStartTime.length > 0 && dayDefenseEndTime.length > 0 && dayDefenseStartTime.length == dayDefenseEndTime.length) {
                    DefenseArrangement defenseArrangement = new DefenseArrangement();
                    String defenseArrangementId = UUIDUtils.getUUID();
                    defenseArrangement.setDefenseArrangementId(defenseArrangementId);
                    saveOrUpdateTime(defenseArrangement, graduationDesignReplanAddVo.getPaperTime(), graduationDesignReplanAddVo.getDefenseTime());
                    defenseArrangement.setIntervalTime(graduationDesignReplanAddVo.getIntervalTime());
                    defenseArrangement.setDefenseNote(graduationDesignReplanAddVo.getDefenseNote());
                    defenseArrangement.setGraduationDesignReleaseId(graduationDesignReplanAddVo.getGraduationDesignReleaseId());
                    defenseArrangementService.save(defenseArrangement);
                    saveDefenseTime(dayDefenseStartTime, dayDefenseEndTime, defenseArrangementId);
                    ajaxUtils.success().msg("保存成功");
                } else {
                    ajaxUtils.fail().msg("每日答辩时间设置有误");
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
     * 毕业设计安排更新
     *
     * @param graduationDesignReplanUpdateVo 数据
     * @param bindingResult                  检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/arrange/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils arrangeUpdate(@Valid GraduationDesignReplanUpdateVo graduationDesignReplanUpdateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReplanUpdateVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                String[] dayDefenseStartTime = graduationDesignReplanUpdateVo.getDayDefenseStartTime();
                String[] dayDefenseEndTime = graduationDesignReplanUpdateVo.getDayDefenseEndTime();
                if (dayDefenseStartTime.length > 0 && dayDefenseEndTime.length > 0 && dayDefenseStartTime.length == dayDefenseEndTime.length) {
                    DefenseArrangement defenseArrangement = defenseArrangementService.findById(graduationDesignReplanUpdateVo.getDefenseArrangementId());
                    if (!ObjectUtils.isEmpty(defenseArrangement)) {
                        saveOrUpdateTime(defenseArrangement, graduationDesignReplanUpdateVo.getPaperTime(), graduationDesignReplanUpdateVo.getDefenseTime());
                        defenseArrangement.setIntervalTime(graduationDesignReplanUpdateVo.getIntervalTime());
                        defenseArrangement.setDefenseNote(graduationDesignReplanUpdateVo.getDefenseNote());
                        defenseArrangementService.update(defenseArrangement);
                        defenseTimeService.deleteByDefenseArrangementId(graduationDesignReplanUpdateVo.getDefenseArrangementId());
                        saveDefenseTime(dayDefenseStartTime, dayDefenseEndTime, graduationDesignReplanUpdateVo.getDefenseArrangementId());
                        ajaxUtils.success().msg("保存成功");
                    } else {
                        ajaxUtils.fail().msg("未查询到相关毕业设计答辩信息");
                    }
                } else {
                    ajaxUtils.fail().msg("每日答辩时间设置有误");
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
     * 毕业设计添加组
     *
     * @param defenseGroupAddVo 数据
     * @param bindingResult     检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/group/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils groupSave(@Valid DefenseGroupAddVo defenseGroupAddVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(defenseGroupAddVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                Optional<Record> record = defenseArrangementService.findByGraduationDesignReleaseId(defenseGroupAddVo.getGraduationDesignReleaseId());
                if (record.isPresent()) {
                    DefenseGroup defenseGroup = new DefenseGroup();
                    defenseGroup.setDefenseGroupId(UUIDUtils.getUUID());
                    defenseGroup.setCreateTime(DateTimeUtils.getNow());
                    defenseGroup.setDefenseArrangementId(defenseGroupAddVo.getDefenseArrangementId());
                    defenseGroup.setDefenseGroupName(defenseGroupAddVo.getDefenseGroupName());
                    defenseGroup.setSchoolroomId(defenseGroupAddVo.getSchoolroomId());
                    defenseGroup.setNote(defenseGroupAddVo.getNote());
                    defenseGroupService.save(defenseGroup);
                    ajaxUtils.success().msg("保存成功");
                } else {
                    ajaxUtils.fail().msg("请先进行毕业设计答辩设置");
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
     * 毕业设计添加组
     *
     * @param defenseGroupUpdateVo 数据
     * @param bindingResult        检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/group/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils groupUpdate(@Valid DefenseGroupUpdateVo defenseGroupUpdateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(defenseGroupUpdateVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                DefenseGroup defenseGroup = defenseGroupService.findById(defenseGroupUpdateVo.getDefenseGroupId());
                defenseGroup.setDefenseGroupName(defenseGroupUpdateVo.getDefenseGroupName());
                defenseGroup.setSchoolroomId(defenseGroupUpdateVo.getSchoolroomId());
                defenseGroup.setNote(defenseGroupUpdateVo.getNote());
                defenseGroupService.update(defenseGroup);
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
     * 组管理数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseArrangementId      毕业答辩安排 id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/replan/group/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<DefenseGroupBean> groupData(@RequestParam("id") String graduationDesignReleaseId,
                                                 @RequestParam("defenseArrangementId") String defenseArrangementId) {
        AjaxUtils<DefenseGroupBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("获取数据成功").listData(defenseGroupService.findByDefenseArrangementId(defenseArrangementId));
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 组数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/replan/divide/groups", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<DefenseGroupBean> groups(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId) {
        AjaxUtils<DefenseGroupBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            List<DefenseGroupBean> defenseGroupBeens = new ArrayList<>();
            DefenseGroupBean defenseGroupBean = new DefenseGroupBean();
            defenseGroupBean.setDefenseGroupId("");
            defenseGroupBean.setDefenseGroupName("请选择组");
            defenseGroupBeens.add(defenseGroupBean);
            Result<Record> records = defenseGroupService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (records.isNotEmpty()) {
                defenseGroupBeens.addAll(records.into(DefenseGroupBean.class));
            }
            ajaxUtils.success().msg("获取数据成功").listData(defenseGroupBeens);
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 批量删除组
     *
     * @param defenseGroupIds 组ids
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/group/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils groupDel(@RequestParam("id") String graduationDesignReleaseId, String defenseGroupIds) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(defenseGroupIds)) {
                List<String> ids = SmallPropsUtils.StringIdsToStringList(defenseGroupIds);
                ids.forEach(id -> {
                    List<DefenseOrder> defenseOrders = defenseOrderService.findByDefenseGroupId(id);
                    defenseOrders.forEach(order -> defenseRateService.deleteByDefenseOrderId(order.getDefenseOrderId()));
                    defenseOrderService.deleteByDefenseGroupId(id);
                    defenseGroupMemberService.deleteByDefenseGroupId(id);
                    defenseGroupService.deleteById(id);
                });
                ajaxUtils.success().msg("删除成功");
            } else {
                ajaxUtils.fail().msg("缺失参数");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 获取该教师组信息
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/divide/group", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils group(@RequestParam("graduationDesignTeacherId") String graduationDesignTeacherId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        DefenseGroupMember defenseGroupMember = new DefenseGroupMember();
        DefenseGroupMemberRecord defenseGroupMemberRecord = defenseGroupMemberService.findByGraduationDesignTeacherId(graduationDesignTeacherId);
        if (!ObjectUtils.isEmpty(defenseGroupMemberRecord)) {
            defenseGroupMember = defenseGroupMemberRecord.into(DefenseGroupMember.class);
        }
        return ajaxUtils.success().msg("获取数据成功").obj(defenseGroupMember);
    }

    /**
     * 保存设置
     *
     * @param defenseGroupMemberAddVo 数据
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/divide/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils divideSave(@Valid DefenseGroupMemberAddVo defenseGroupMemberAddVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(defenseGroupMemberAddVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                DefenseGroupMember defenseGroupMember = new DefenseGroupMember();
                defenseGroupMember.setGraduationDesignTeacherId(defenseGroupMemberAddVo.getGraduationDesignTeacherId());
                defenseGroupMember.setDefenseGroupId(defenseGroupMemberAddVo.getDefenseGroupId());
                defenseGroupMember.setNote(defenseGroupMemberAddVo.getNote());
                defenseGroupMemberService.saveOrUpdate(defenseGroupMember);
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
     * 设置组长
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @param defenseGroupId            组id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/divide/leader", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils divideLeader(@RequestParam("graduationDesignTeacherId") String graduationDesignTeacherId,
                                  @RequestParam("defenseGroupId") String defenseGroupId,
                                  @RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            DefenseGroup defenseGroup = defenseGroupService.findById(defenseGroupId);
            if (!ObjectUtils.isEmpty(defenseGroup)) {
                defenseGroup.setLeaderId(graduationDesignTeacherId);
                defenseGroupService.update(defenseGroup);
                ajaxUtils.success().msg("设置成功");
            } else {
                ajaxUtils.fail().msg("未查询到相关组信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 设置秘书
     *
     * @param secretaryId               用户账号
     * @param defenseGroupId            组id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/order/secretary", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils orderSecretary(@RequestParam("secretaryId") String secretaryId,
                                    @RequestParam("defenseGroupId") String defenseGroupId,
                                    @RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            DefenseGroup defenseGroup = defenseGroupService.findById(defenseGroupId);
            if (!ObjectUtils.isEmpty(defenseGroup)) {
                Users users = usersService.findByUsername(StringUtils.trimWhitespace(secretaryId));
                if (!ObjectUtils.isEmpty(users)) {
                    defenseGroup.setSecretaryId(users.getUsername());
                    defenseGroupService.update(defenseGroup);
                    ajaxUtils.success().msg("设置成功");
                } else {
                    ajaxUtils.fail().msg("未查询到该账号信息");
                }
            } else {
                ajaxUtils.fail().msg("未查询到相关组信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 生成顺序
     *
     * @param defenseGroupId            组id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseArrangementId      毕业设计答辩安排id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/replan/order/make", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils orderMake(@RequestParam("defenseGroupId") String defenseGroupId,
                               @RequestParam("id") String graduationDesignReleaseId,
                               @RequestParam("defenseArrangementId") String defenseArrangementId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        try {
            ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 是否已确认毕业设计指导教师调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    // 安排情况
                    DefenseArrangement defenseArrangement = defenseArrangementService.findById(defenseArrangementId);
                    if (!ObjectUtils.isEmpty(defenseArrangement)) {
                        // 删除之前顺序配置
                        defenseOrderService.deleteByDefenseGroupId(defenseGroupId);

                        List<DefenseTime> defenseTimes = new ArrayList<>();
                        // 每日答辩时间
                        Result<Record> defenseTimeRecord = defenseTimeService.findByDefenseArrangementId(defenseArrangementId);
                        if (defenseTimeRecord.isNotEmpty()) {
                            defenseTimes = defenseTimeRecord.into(DefenseTime.class);
                        }

                        // 查询小组学生
                        List<DefenseGroupMemberBean> defenseGroupMemberBeens = defenseGroupMemberService.findByDefenseGroupIdAndGraduationDesignReleaseIdForStudent(defenseGroupId, graduationDesignReleaseId);

                        String format1 = "yyyy-MM-dd HH:mm:ss";
                        String format2 = "yyyy-MM-dd";
                        String format3 = "HH:mm";
                        // 切分时间
                        DateTime defenseStartTime = new DateTime(defenseArrangement.getDefenseStartTime().getTime());
                        // 学生计数器
                        int sortNum = 0;
                        Date startTime;
                        Date endTime;
                        DateTime dateTime = null;
                        List<DefenseOrder> defenseOrders = new ArrayList<>();
                        // 先循环日期
                        while (defenseStartTime.isBefore(defenseArrangement.getDefenseEndTime().getTime())) {
                            String startDate = DateTimeUtils.formatDate(defenseStartTime.toDate(), format2);
                            // 循环时段
                            for (DefenseTime defenseTime : defenseTimes) {
                                startTime = DateTimeUtils.formatDateTime(startDate + " " + defenseTime.getDayDefenseStartTime() + ":00", format1);
                                endTime = DateTimeUtils.formatDateTime(startDate + " " + defenseTime.getDayDefenseEndTime() + ":00", format1);
                                dateTime = new DateTime(startTime.getTime());
                                // 循环学生
                                while (sortNum < defenseGroupMemberBeens.size()) {
                                    DefenseGroupMemberBean defenseGroupMemberBean = defenseGroupMemberBeens.get(sortNum);
                                    DefenseOrder defenseOrder = new DefenseOrder();
                                    defenseOrder.setDefenseOrderId(UUIDUtils.getUUID());
                                    // 在该时段
                                    if (dateTime.isBefore(endTime.getTime())) {
                                        defenseOrder.setStudentNumber(defenseGroupMemberBean.getStudentNumber());
                                        defenseOrder.setStudentName(defenseGroupMemberBean.getStudentName());
                                        defenseOrder.setSubject(defenseGroupMemberBean.getSubject());
                                        defenseOrder.setDefenseDate(new java.sql.Date(dateTime.toDate().getTime()));
                                        defenseOrder.setDefenseTime(DateTimeUtils.formatDate(dateTime.toDate(), format3));
                                        defenseOrder.setStaffName(defenseGroupMemberBean.getStaffName());
                                        defenseOrder.setSortNum(sortNum);
                                        defenseOrder.setStudentId(defenseGroupMemberBean.getStudentId());
                                        defenseOrder.setDefenseGroupId(defenseGroupMemberBean.getDefenseGroupId());
                                        defenseOrder.setDefenseStatus(0);
                                        defenseOrder.setStudentMobile(defenseGroupMemberBean.getStudentMobile());
                                    } else {
                                        break;
                                    }
                                    defenseOrders.add(defenseOrder);
                                    dateTime = dateTime.plusMinutes(defenseArrangement.getIntervalTime());
                                    sortNum++;
                                }
                            }
                            defenseStartTime = defenseStartTime.plusDays(1);
                        }

                        // 若已超过时间，但学生还有，补齐
                        if (sortNum < defenseGroupMemberBeens.size()) {
                            while (sortNum < defenseGroupMemberBeens.size()) {
                                DefenseGroupMemberBean defenseGroupMemberBean = defenseGroupMemberBeens.get(sortNum);
                                DefenseOrder defenseOrder = new DefenseOrder();
                                defenseOrder.setDefenseOrderId(UUIDUtils.getUUID());
                                // 在该时段
                                if (!ObjectUtils.isEmpty(dateTime)) {
                                    defenseOrder.setStudentNumber(defenseGroupMemberBean.getStudentNumber());
                                    defenseOrder.setStudentName(defenseGroupMemberBean.getStudentName());
                                    defenseOrder.setSubject(defenseGroupMemberBean.getSubject());
                                    defenseOrder.setDefenseDate(new java.sql.Date(dateTime.toDate().getTime()));
                                    defenseOrder.setDefenseTime(DateTimeUtils.formatDate(dateTime.toDate(), format3));
                                    defenseOrder.setStaffName(defenseGroupMemberBean.getStaffName());
                                    defenseOrder.setSortNum(sortNum);
                                    defenseOrder.setStudentId(defenseGroupMemberBean.getStudentId());
                                    defenseOrder.setDefenseGroupId(defenseGroupMemberBean.getDefenseGroupId());
                                    defenseOrder.setDefenseStatus(0);
                                    defenseOrder.setStudentMobile(defenseGroupMemberBean.getStudentMobile());
                                    dateTime = dateTime.plusMinutes(defenseArrangement.getIntervalTime());
                                }
                                defenseOrders.add(defenseOrder);
                                sortNum++;
                            }
                        }

                        defenseOrderService.save(defenseOrders);
                        ajaxUtils.success().msg("生成成功");
                    } else {
                        ajaxUtils.fail().msg("未查询到相关毕业答辩安排");
                    }
                } else {
                    ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                }

            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } catch (ParseException e) {
            log.error("Parse time error , error is ", e);
        }
        return ajaxUtils;
    }

    /**
     * 查看及调整顺序
     *
     * @param condition 条件
     * @return 数据
     */
    @RequestMapping(value = "/anyone/graduate/design/defense/order/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<DefenseOrderBean> orderLookData(DefenseOrderBean condition) {
        AjaxUtils<DefenseOrderBean> ajaxUtils = AjaxUtils.of();
        List<DefenseOrderBean> defenseOrderBeens = new ArrayList<>();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(condition.getGraduationDesignReleaseId());
        if (!errorBean.isHasError()) {
            Result<Record> records = defenseOrderService.findAll(condition);
            if (records.isNotEmpty()) {
                defenseOrderBeens = records.into(DefenseOrderBean.class);
                // 以下为保护个人隐私，仅在答辩时间内显示个人电话
                boolean isEncrypt = true;
                Optional<Record> defenseArrangementRecord = defenseArrangementService.findByGraduationDesignReleaseId(condition.getGraduationDesignReleaseId());
                if (defenseArrangementRecord.isPresent()) {
                    DefenseArrangement defenseArrangement = defenseArrangementRecord.get().into(DefenseArrangement.class);
                    // 答辩时间内显示
                    if (DateTimeUtils.timestampRangeDecide(defenseArrangement.getDefenseStartTime(), defenseArrangement.getDefenseEndTime())) {
                        isEncrypt = false;
                    }
                }
                if (isEncrypt) {
                    defenseOrderBeens.forEach(i -> i.setStudentMobile("******"));
                }
            }
        }
        return ajaxUtils.success().msg("获取数据成功").listData(defenseOrderBeens);
    }

    /**
     * 调换顺序
     *
     * @param defenseOrderBean 数据
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/replan/order/adjust", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils orderAdjust(DefenseOrderBean defenseOrderBean) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(defenseOrderBean.getGraduationDesignReleaseId());
        if (!errorBean.isHasError()) {
            // 当前学生
            DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderBean.getDefenseOrderId());
            if (!ObjectUtils.isEmpty(defenseOrder)) {
                if (!Objects.equals(defenseOrder.getSortNum(), defenseOrderBean.getSortNum())) {
                    // 要与之调换的学生
                    DefenseOrderRecord record = defenseOrderService.findBySortNumAndDefenseGroupId(defenseOrderBean.getSortNum(), defenseOrderBean.getDefenseGroupId());
                    if (!ObjectUtils.isEmpty(record)) {
                        DefenseOrder tempDefenseOrder = record.into(DefenseOrder.class);
                        // 进行调换
                        tempDefenseOrder.setSortNum(defenseOrder.getSortNum());
                        java.sql.Date tempDefenseDate = tempDefenseOrder.getDefenseDate();
                        String tempDefenseTime = tempDefenseOrder.getDefenseTime();
                        tempDefenseOrder.setDefenseDate(defenseOrder.getDefenseDate());
                        tempDefenseOrder.setDefenseTime(defenseOrder.getDefenseTime());
                        defenseOrder.setSortNum(defenseOrderBean.getSortNum());
                        defenseOrder.setDefenseDate(tempDefenseDate);
                        defenseOrder.setDefenseTime(tempDefenseTime);
                        defenseOrderService.update(tempDefenseOrder);
                        defenseOrderService.update(defenseOrder);
                        ajaxUtils.success().msg("调换成功");
                    } else {
                        ajaxUtils.fail().msg("未查询到与之调换的学生信息");
                    }
                } else {
                    ajaxUtils.fail().msg("序号一致，无法调换");
                }
            } else {
                ajaxUtils.fail().msg("未查询到当前学生信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 更新或保存时间
     *
     * @param defenseArrangement 毕业设计安排
     * @param paperDate          毕业论文评阅日期
     * @param defenseDate        答辩日期
     */
    private void saveOrUpdateTime(DefenseArrangement defenseArrangement, String paperDate, String defenseDate) {
        try {
            String format = "yyyy-MM-dd HH:mm:ss";
            String[] paperDateArr = DateTimeUtils.splitDateTime("至", paperDate);
            defenseArrangement.setPaperStartTime(DateTimeUtils.formatDateToTimestamp(paperDateArr[0], format));
            defenseArrangement.setPaperEndTime(DateTimeUtils.formatDateToTimestamp(paperDateArr[1], format));
            String[] defenseDateArr = DateTimeUtils.splitDateTime("至", defenseDate);
            defenseArrangement.setDefenseStartTime(DateTimeUtils.formatDateToTimestamp(defenseDateArr[0], format));
            defenseArrangement.setDefenseEndTime(DateTimeUtils.formatDateToTimestamp(defenseDateArr[1], format));
        } catch (ParseException e) {
            log.error(" format time is exception.", e);
        }
    }

    /**
     * 保存每日答辩时间
     *
     * @param dayDefenseStartTime  开始时间
     * @param dayDefenseEndTime    结束时间
     * @param defenseArrangementId 毕业设置安排id
     */
    private void saveDefenseTime(String[] dayDefenseStartTime, String[] dayDefenseEndTime, String defenseArrangementId) {
        for (int i = 0; i < dayDefenseStartTime.length; i++) {
            DefenseTime defenseTime = new DefenseTime();
            defenseTime.setDefenseArrangementId(defenseArrangementId);
            defenseTime.setDayDefenseStartTime(dayDefenseStartTime[i]);
            defenseTime.setDayDefenseEndTime(dayDefenseEndTime[i]);
            defenseTime.setSortTime(i);
            defenseTimeService.save(defenseTime);
        }
    }

    /**
     * 获取全部楼
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 全部楼
     */
    @RequestMapping(value = "/web/graduate/design/replan/buildings", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Building> buildings(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils<Building> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("获取楼数据成功！").listData(buildingService.generateBuildFromGraduationDesignRelease(errorBean.getData()));
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
    @RequestMapping(value = "/web/graduate/design/replan/group/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils groupCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = defenseArrangementService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (record.isPresent()) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg("请先进行毕业设计答辩设置");
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
    @RequestMapping(value = "/web/graduate/design/replan/divide/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils divideCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认毕业设计指导教师
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacher()) && graduationDesignRelease.getIsOkTeacher() == 1) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg("未确认毕业设计指导教师");
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
    @RequestMapping(value = "/web/graduate/design/replan/condition", method = RequestMethod.POST)
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
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
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
