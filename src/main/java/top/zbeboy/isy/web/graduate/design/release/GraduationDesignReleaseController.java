package top.zbeboy.isy.web.graduate.design.release;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by zbeboy on 2017/5/5.
 */
@Controller
public class GraduationDesignReleaseController {

    private final Logger log = LoggerFactory.getLogger(GraduationDesignReleaseController.class);

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    /**
     * 毕业发布
     *
     * @return 毕业发布页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/release", method = RequestMethod.GET)
    public String releaseData() {
        return "web/graduate/design/release/design_release::#page-wrapper";
    }

    /**
     * 获取毕业发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/release/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> releaseDatas(PaginationUtils paginationUtils) {
        GraduationDesignReleaseBean graduationDesignReleaseBean = new GraduationDesignReleaseBean();
        Map<String, Integer> commonData = commonControllerMethodService.accessRoleCondition();
        graduationDesignReleaseBean.setDepartmentId(StringUtils.isEmpty(commonData.get("departmentId")) ? -1 : commonData.get("departmentId"));
        graduationDesignReleaseBean.setCollegeId(StringUtils.isEmpty(commonData.get("collegeId")) ? -1 : commonData.get("collegeId"));
        Result<Record> records = graduationDesignReleaseService.findAllByPage(paginationUtils, graduationDesignReleaseBean);
        List<GraduationDesignReleaseBean> graduationDesignReleaseBeans = graduationDesignReleaseService.dealData(paginationUtils, records, graduationDesignReleaseBean);
        return new AjaxUtils<GraduationDesignReleaseBean>().success().msg("获取数据成功").listData(graduationDesignReleaseBeans).paginationUtils(paginationUtils);
    }
}
