package top.zbeboy.isy.web.graduate.design.adjustech;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2017-05-21.
 */
@Slf4j
@Controller
public class GraduationDesignAdjustechController {

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignTutorService graduationDesignTutorService;

    /**
     * 调整填报教师
     *
     * @return 调整填报教师页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/adjustech", method = RequestMethod.GET)
    public String releaseData() {
        return "web/graduate/design/adjustech/design_adjustech::#page-wrapper";
    }

    @RequestMapping(value = "/web/graduate/design/adjustech/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> adjustechDatas(PaginationUtils paginationUtils) {
        AjaxUtils<GraduationDesignReleaseBean> ajaxUtils = AjaxUtils.of();
        Byte isDel = 0;
        GraduationDesignReleaseBean graduationDesignReleaseBean = new GraduationDesignReleaseBean();
        graduationDesignReleaseBean.setGraduationDesignIsDel(isDel);
        Map<String, Integer> commonData = commonControllerMethodService.accessRoleCondition();
        graduationDesignReleaseBean.setDepartmentId(StringUtils.isEmpty(commonData.get("departmentId")) ? -1 : commonData.get("departmentId"));
        graduationDesignReleaseBean.setCollegeId(StringUtils.isEmpty(commonData.get("collegeId")) ? -1 : commonData.get("collegeId"));
        Result<Record> records = graduationDesignReleaseService.findAllByPage(paginationUtils, graduationDesignReleaseBean);
        List<GraduationDesignReleaseBean> graduationDesignReleaseBeens = graduationDesignReleaseService.dealData(paginationUtils, records, graduationDesignReleaseBean);
        graduationDesignReleaseBeens.forEach(graduationDesignRelease -> {
                    graduationDesignRelease.setStudentNotFillCount(graduationDesignTutorService.countNotFillStudent(graduationDesignRelease));
                    graduationDesignRelease.setStudentFillCount(graduationDesignTutorService.countFillStudent(graduationDesignRelease));
                }
        );
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignReleaseBeens).paginationUtils(paginationUtils);
    }
}
