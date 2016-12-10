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
     * 获取实习统计数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipReleaseBean> reviewDatas(PaginationUtils paginationUtils) {
        Byte isDel = 0;
        InternshipReleaseBean internshipReleaseBean = new InternshipReleaseBean();
        internshipReleaseBean.setInternshipReleaseIsDel(isDel);
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)
                && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int departmentId = roleService.getRoleDepartmentId(record);
            internshipReleaseBean.setDepartmentId(departmentId);
        }
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            internshipReleaseBean.setCollegeId(collegeId);
        }
        Result<Record> records = internshipReleaseService.findAllByPage(paginationUtils, internshipReleaseBean);
        List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipReleaseBean);
        return new AjaxUtils<InternshipReleaseBean>().success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils);
    }

    /**
     * 获取专业数据
     *
     * @param internshipReleaseId 实习发布id
     * @return 专业数据
     */
    @RequestMapping(value = "/web/internship/review/statistics/sciences", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Science> auditSciences(@RequestParam("internshipReleaseId") String internshipReleaseId) {
        AjaxUtils<Science> ajaxUtils = new AjaxUtils<>();
        List<Science> sciences = new ArrayList<>();
        Science science = new Science();
        science.setScienceId(0);
        science.setScienceName("请选择专业");
        sciences.add(science);
        Result<Record> records = internshipReleaseScienceService.findByInternshipReleaseIdRelation(internshipReleaseId);
        if (records.isNotEmpty()) {
            sciences.addAll(records.into(Science.class));
        }
        return ajaxUtils.success().msg("获取专业数据成功").listData(sciences);
    }

    /**
     * 获取班级数据
     *
     * @param scienceId 专业id
     * @return 班级
     */
    @RequestMapping(value = "/web/internship/review/statistics/organizes", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Organize> auditOrganizes(@RequestParam("scienceId") int scienceId) {
        List<Organize> organizes = new ArrayList<>();
        Organize organize = new Organize();
        organize.setOrganizeId(0);
        organize.setOrganizeName("请选择班级");
        organizes.add(organize);
        organizes.addAll(organizeService.findByScienceId(scienceId));
        return new AjaxUtils<Organize>().success().msg("获取班级数据成功").listData(organizes);
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
