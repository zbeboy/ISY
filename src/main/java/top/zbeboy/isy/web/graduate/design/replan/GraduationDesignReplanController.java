package top.zbeboy.isy.web.graduate.design.replan;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
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
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.BuildingService;
import top.zbeboy.isy.service.graduate.design.*;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.graduate.design.replan.DefenseGroupAddVo;
import top.zbeboy.isy.web.vo.graduate.design.replan.DefenseGroupUpdateVo;
import top.zbeboy.isy.web.vo.graduate.design.replan.GraduationDesignReplanAddVo;
import top.zbeboy.isy.web.vo.graduate.design.replan.GraduationDesignReplanUpdateVo;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

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
    private DefenseGroupMemberService defenseGroupMemberService;

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
                page = commonControllerMethodService.showTip(modelMap, "请先进行毕业答辩设置");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
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
                page = commonControllerMethodService.showTip(modelMap, "请先进行毕业答辩设置");
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
                String[] defenseStartTime = graduationDesignReplanAddVo.getDefenseStartTime();
                String[] defenseEndTime = graduationDesignReplanAddVo.getDefenseEndTime();
                if (defenseStartTime.length > 0 && defenseEndTime.length > 0 && defenseStartTime.length == defenseEndTime.length) {
                    if (validDefenseTime(defenseStartTime, defenseEndTime)) {
                        DefenseArrangement defenseArrangement = new DefenseArrangement();
                        String defenseArrangementId = UUIDUtils.getUUID();
                        defenseArrangement.setDefenseArrangementId(defenseArrangementId);
                        saveOrUpdateTime(defenseArrangement, graduationDesignReplanAddVo.getPaperDate(), graduationDesignReplanAddVo.getDefenseDate());
                        defenseArrangement.setIntervalTime(graduationDesignReplanAddVo.getIntervalTime());
                        defenseArrangement.setDefenseNote(graduationDesignReplanAddVo.getDefenseNote());
                        defenseArrangement.setGraduationDesignReleaseId(graduationDesignReplanAddVo.getGraduationDesignReleaseId());
                        defenseArrangementService.save(defenseArrangement);
                        saveDefenseTime(defenseStartTime, defenseEndTime, defenseArrangementId);
                        ajaxUtils.success().msg("保存成功");
                    } else {
                        ajaxUtils.fail().msg("每日答辩时间大小设置有误");
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
                String[] defenseStartTime = graduationDesignReplanUpdateVo.getDefenseStartTime();
                String[] defenseEndTime = graduationDesignReplanUpdateVo.getDefenseEndTime();
                if (defenseStartTime.length > 0 && defenseEndTime.length > 0 && defenseStartTime.length == defenseEndTime.length) {
                    if (validDefenseTime(defenseStartTime, defenseEndTime)) {
                        DefenseArrangement defenseArrangement = defenseArrangementService.findById(graduationDesignReplanUpdateVo.getDefenseArrangementId());
                        if (!ObjectUtils.isEmpty(defenseArrangement)) {
                            saveOrUpdateTime(defenseArrangement, graduationDesignReplanUpdateVo.getPaperDate(), graduationDesignReplanUpdateVo.getDefenseDate());
                            defenseArrangement.setIntervalTime(graduationDesignReplanUpdateVo.getIntervalTime());
                            defenseArrangement.setDefenseNote(graduationDesignReplanUpdateVo.getDefenseNote());
                            defenseArrangementService.update(defenseArrangement);
                            defenseTimeService.deleteByDefenseArrangementId(graduationDesignReplanUpdateVo.getDefenseArrangementId());
                            saveDefenseTime(defenseStartTime, defenseEndTime, graduationDesignReplanUpdateVo.getDefenseArrangementId());
                            ajaxUtils.success().msg("保存成功");
                        } else {
                            ajaxUtils.fail().msg("未查询到相关设置信息");
                        }
                    } else {
                        ajaxUtils.fail().msg("每日答辩时间大小设置有误");
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
                    ajaxUtils.fail().msg("请先进行毕业答辩设置");
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
     * 更新或保存时间
     *
     * @param defenseArrangement 毕业设计安排
     * @param paperDate          毕业论文评阅日期
     * @param defenseDate        答辩日期
     */
    private void saveOrUpdateTime(DefenseArrangement defenseArrangement, String paperDate, String defenseDate) {
        try {
            String format = "yyyy-MM-dd";
            String[] paperDateArr = DateTimeUtils.splitDateTime("至", paperDate);
            defenseArrangement.setPaperStartDate(DateTimeUtils.formatDate(paperDateArr[0], format));
            defenseArrangement.setPaperEndDate(DateTimeUtils.formatDate(paperDateArr[1], format));
            String[] defenseDateArr = DateTimeUtils.splitDateTime("至", defenseDate);
            defenseArrangement.setDefenseStartDate(DateTimeUtils.formatDate(defenseDateArr[0], format));
            defenseArrangement.setDefenseEndDate(DateTimeUtils.formatDate(defenseDateArr[1], format));
        } catch (ParseException e) {
            log.error(" format time is exception.", e);
        }
    }

    /**
     * 检验每日答辩时间
     *
     * @param defenseStartTime 开始时间
     * @param defenseEndTime   结束时间
     * @return true or false
     */
    private boolean validDefenseTime(String[] defenseStartTime, String[] defenseEndTime) {
        boolean isValid = true;
        for (int i = 0; i < defenseStartTime.length; i++) {
            if (i > 0) {
                String[] cs = defenseEndTime[i].split(":");
                String[] ds = defenseStartTime[i - 1].split(":");
                int cn = NumberUtils.toInt(cs[0]);
                int dn = NumberUtils.toInt(ds[0]);
                if (cn < dn) {
                    isValid = false;
                    break;
                } else if (cn == dn) {
                    if (NumberUtils.toInt(cs[1]) <= NumberUtils.toInt(ds[1])) {
                        isValid = false;
                        break;
                    }
                }
            }
        }
        return isValid;
    }

    /**
     * 保存每日答辩时间
     *
     * @param defenseStartTime     开始时间
     * @param defenseEndTime       结束时间
     * @param defenseArrangementId 毕业设置安排id
     */
    private void saveDefenseTime(String[] defenseStartTime, String[] defenseEndTime, String defenseArrangementId) {
        for (int i = 0; i < defenseStartTime.length; i++) {
            DefenseTime defenseTime = new DefenseTime();
            defenseTime.setDefenseArrangementId(defenseArrangementId);
            defenseTime.setDefenseStartTime(defenseStartTime[i]);
            defenseTime.setDefenseEndTime(defenseEndTime[i]);
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
                ajaxUtils.fail().msg("请先进行毕业答辩设置");
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
