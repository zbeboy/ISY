package top.zbeboy.isy.web.graduate.design.reorder;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.Building;
import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement;
import top.zbeboy.isy.domain.tables.pojos.DefenseTime;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.graduate.design.*;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupMemberBean;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private DefenseGroupService defenseGroupService;

    @Resource
    private DefenseGroupMemberService defenseGroupMemberService;

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
     * 毕业答辩安排
     *
     * @return 毕业答辩安排页面
     */
    @RequestMapping(value = "/web/graduate/design/reorder/arrange", method = RequestMethod.GET)
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
                page = "web/graduate/design/reorder/design_reorder_arrange::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未进行毕业答辩设置");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
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
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
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
            ajaxUtils.success().msg("获取楼数据成功！").listData(defenseGroupBeens);
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }
}
