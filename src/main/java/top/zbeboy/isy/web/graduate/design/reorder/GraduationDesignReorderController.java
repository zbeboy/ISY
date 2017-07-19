package top.zbeboy.isy.web.graduate.design.reorder;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement;
import top.zbeboy.isy.domain.tables.pojos.DefenseTime;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.graduate.design.DefenseArrangementService;
import top.zbeboy.isy.service.graduate.design.DefenseTimeService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.web.bean.error.ErrorBean;

import javax.annotation.Resource;
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
}
