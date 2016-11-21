package top.zbeboy.isy.web.internship.distribution;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease;
import top.zbeboy.isy.domain.tables.pojos.Science;
import top.zbeboy.isy.service.InternshipReleaseScienceService;
import top.zbeboy.isy.service.InternshipReleaseService;
import top.zbeboy.isy.service.InternshipTeacherDistributionService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbeboy on 2016/11/21.
 */
@Controller
public class InternshipTeacherDistributionController {

    private final Logger log = LoggerFactory.getLogger(InternshipTeacherDistributionController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

    /**
     * 实习教师分配数据
     *
     * @return 实习教师分配数据页面
     */
    @RequestMapping(value = "/web/menu/internship/teacher_distribution", method = RequestMethod.GET)
    public String releaseData() {
        return "/web/internship/distribution/internship_teacher_distribution::#page-wrapper";
    }

    /**
     * 获取实习教师分配数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipReleaseBean> distributionDatas(PaginationUtils paginationUtils) {
        Byte isDel = 0;
        InternshipRelease internshipRelease = new InternshipRelease();
        internshipRelease.setInternshipReleaseIsDel(isDel);
        Result<Record> records = internshipReleaseService.findAllByPage(paginationUtils, internshipRelease);
        List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records);
        return new AjaxUtils<InternshipReleaseBean>().success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils);
    }

    /**
     * 分配情况页面
     *
     * @param internshipReleaseId 实习发布id
     * @return 页面
     */
    @RequestMapping("/web/internship/teacher_distribution/distribution/condition")
    public String distributionCondition(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "/web/internship/distribution/internship_distribution_condition::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/distribution/condition/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipTeacherDistributionBean> distributionConditionDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("internship_title");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("student_real_name");
        headers.add("student_username");
        headers.add("student_number");
        headers.add("teacher_real_name");
        headers.add("teacher_username");
        headers.add("real_name");
        headers.add("username");
        headers.add("operator");
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        internshipTeacherDistributionService.findAllByPage(dataTablesUtils,"71ec7f4d6b024b82937c76b82cb04e3f");
        return dataTablesUtils;
    }
}
