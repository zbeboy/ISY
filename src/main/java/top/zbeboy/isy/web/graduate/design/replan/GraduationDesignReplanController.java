package top.zbeboy.isy.web.graduate.design.replan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            page = "web/graduate/design/replan/design_replan_arrange::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
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
            Map<String, Object> mapData = new HashMap<>();
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                errorBean.setHasError(false);
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("不在毕业时间范围，无法操作");
            }
            errorBean.setMapData(mapData);
        }
        return errorBean;
    }
}
