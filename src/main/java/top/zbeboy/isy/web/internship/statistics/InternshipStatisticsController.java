package top.zbeboy.isy.web.internship.statistics;

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
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Organize;
import top.zbeboy.isy.domain.tables.pojos.Science;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.web.bean.data.department.DepartmentBean;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.bean.internship.statistics.InternshipStatisticsBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-12-10.
 */
@Controller
public class InternshipStatisticsController {

    private final Logger log = LoggerFactory.getLogger(InternshipStatisticsController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Resource
    private InternshipStatisticsService internshipStatisticsService;

    @Resource
    private OrganizeService organizeService;

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

    /**
     * 实习统计
     *
     * @return 实习统计页面
     */
    @RequestMapping(value = "/web/menu/internship/statistical", method = RequestMethod.GET)
    public String internshipStatistical() {
        return "/web/internship/statistics/internship_statistics::#page-wrapper";
    }

    /**
     * 已提交列表
     *
     * @return 已提交列表 统计页面
     */
    @RequestMapping(value = "/web/internship/statistical/submitted", method = RequestMethod.GET)
    public String statisticalSubmitted(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "/web/internship/statistics/internship_submitted::#page-wrapper";
    }

    /**
     * 已提交列表 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/submitted/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipStatisticsBean> submittedDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("student_name");
        headers.add("student_number");
        headers.add("science_name");
        headers.add("organize_name");
        headers.add("internship_apply_state");
        headers.add("operator");
        DataTablesUtils<InternshipStatisticsBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        InternshipStatisticsBean internshipStatisticsBean = new InternshipStatisticsBean();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            internshipStatisticsBean.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
            Result<Record> records = internshipStatisticsService.submittedFindAllByPage(dataTablesUtils, internshipStatisticsBean);
            List<InternshipStatisticsBean> internshipStatisticsBeens = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                internshipStatisticsBeens = records.into(InternshipStatisticsBean.class);
            }
            dataTablesUtils.setData(internshipStatisticsBeens);
            dataTablesUtils.setiTotalRecords(internshipStatisticsService.submittedCountAll(internshipStatisticsBean));
            dataTablesUtils.setiTotalDisplayRecords(internshipStatisticsService.submittedCountByCondition(dataTablesUtils, internshipStatisticsBean));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }
}
