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
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.bean.internship.review.GraduationPracticeCollegeBean;
import top.zbeboy.isy.web.bean.internship.review.GraduationPracticeUnifyBean;
import top.zbeboy.isy.web.bean.internship.statistics.InternshipChangeCompanyHistoryBean;
import top.zbeboy.isy.web.bean.internship.statistics.InternshipChangeHistoryBean;
import top.zbeboy.isy.web.bean.internship.statistics.InternshipStatisticsBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-12-10.
 */
@Controller
public class InternshipStatisticsController {

    private final Logger log = LoggerFactory.getLogger(InternshipStatisticsController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipTypeService internshipTypeService;

    @Resource
    private InternshipStatisticsService internshipStatisticsService;

    @Resource
    private InternshipCollegeService internshipCollegeService;

    @Resource
    private InternshipCompanyService internshipCompanyService;

    @Resource
    private GraduationPracticeCompanyService graduationPracticeCompanyService;

    @Resource
    private GraduationPracticeCollegeService graduationPracticeCollegeService;

    @Resource
    private GraduationPracticeUnifyService graduationPracticeUnifyService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private InternshipChangeHistoryService internshipChangeHistoryService;

    @Resource
    private InternshipChangeCompanyHistoryService internshipChangeCompanyHistoryService;

    /**
     * 实习统计
     *
     * @return 实习统计页面
     */
    @RequestMapping(value = "/web/menu/internship/statistical", method = RequestMethod.GET)
    public String internshipStatistical() {
        return "web/internship/statistics/internship_statistics::#page-wrapper";
    }

    /**
     * 已提交列表
     *
     * @return 已提交列表 统计页面
     */
    @RequestMapping(value = "/web/internship/statistical/submitted", method = RequestMethod.GET)
    public String statisticalSubmitted(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "web/internship/statistics/internship_submitted::#page-wrapper";
    }

    /**
     * 申请记录列表
     *
     * @return 申请记录列表页面
     */
    @RequestMapping(value = "/web/internship/statistical/record/apply", method = RequestMethod.GET)
    public String changeHistory(@RequestParam("id") String internshipReleaseId, @RequestParam("studentId") int studentId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        modelMap.addAttribute("studentId", studentId);
        return "web/internship/statistics/internship_change_history::#page-wrapper";
    }

    /**
     * 单位变更记录列表
     *
     * @return 单位变更记录列表页面
     */
    @RequestMapping(value = "/web/internship/statistical/record/company", method = RequestMethod.GET)
    public String changeCompanyHistory(@RequestParam("id") String internshipReleaseId, @RequestParam("studentId") int studentId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        modelMap.addAttribute("studentId", studentId);
        return "web/internship/statistics/internship_change_company_history::#page-wrapper";
    }

    /**
     * 未提交列表
     *
     * @return 未提交列表 统计页面
     */
    @RequestMapping(value = "/web/internship/statistical/unsubmitted", method = RequestMethod.GET)
    public String statisticalUnSubmitted(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "web/internship/statistics/internship_unsubmitted::#page-wrapper";
    }

    /**
     * 数据列表
     *
     * @return 数据列表 统计页面
     */
    @RequestMapping(value = "/web/internship/statistical/data_list", method = RequestMethod.GET)
    public String statisticalDataList(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page = "web/internship/statistics/internship_statistics::#page-wrapper";
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        if (!ObjectUtils.isEmpty(internshipRelease)) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
            switch (internshipType.getInternshipTypeName()) {
                case Workbook.INTERNSHIP_COLLEGE_TYPE:
                    page = "web/internship/statistics/internship_college_data::#page-wrapper";
                    break;
                case Workbook.INTERNSHIP_COMPANY_TYPE:
                    page = "web/internship/statistics/internship_company_data::#page-wrapper";
                    break;
                case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                    page = "web/internship/statistics/graduation_practice_college_data::#page-wrapper";
                    break;
                case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                    page = "web/internship/statistics/graduation_practice_unify_data::#page-wrapper";
                    break;
                case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                    page = "web/internship/statistics/graduation_practice_company_data::#page-wrapper";
                    break;
            }
        }
        return page;
    }

    /**
     * 获取实习统计数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipReleaseBean> internshipListDatas(PaginationUtils paginationUtils) {
        Byte isDel = 0;
        InternshipReleaseBean internshipReleaseBean = new InternshipReleaseBean();
        internshipReleaseBean.setInternshipReleaseIsDel(isDel);
        commonControllerMethodService.accessRoleCondition(internshipReleaseBean);
        Result<Record> records = internshipReleaseService.findAllByPage(paginationUtils, internshipReleaseBean);
        List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipReleaseBean);
        internshipReleaseBeens.forEach(r -> {
            InternshipStatisticsBean internshipStatisticsBean = new InternshipStatisticsBean();
            internshipStatisticsBean.setInternshipReleaseId(r.getInternshipReleaseId());
            r.setSubmittedTotalData(internshipStatisticsService.submittedCountAll(internshipStatisticsBean));
            r.setUnsubmittedTotalData(internshipStatisticsService.unsubmittedCountAll(internshipStatisticsBean));
        });
        return new AjaxUtils<InternshipReleaseBean>().success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils);
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

    /**
     * 申请变更记录数据
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/record/apply/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipChangeHistoryBean> changeHistoryDatas(@RequestParam("internshipReleaseId") String internshipReleaseId, @RequestParam("studentId") int studentId) {
        List<InternshipChangeHistoryBean> internshipChangeHistoryBeans = new ArrayList<>();
        Result<Record> records = internshipChangeHistoryService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
        if (records.isNotEmpty()) {
            internshipChangeHistoryBeans = records.into(InternshipChangeHistoryBean.class);
            internshipChangeHistoryBeans.forEach(i->i.setApplyTimeStr(DateTimeUtils.formatDate(i.getApplyTime())));
        }
        return new AjaxUtils<InternshipChangeHistoryBean>().success().msg("获取数据成功").listData(internshipChangeHistoryBeans);
    }

    /**
     * 单位变更记录数据
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/record/company/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipChangeCompanyHistoryBean> changeCompanyDatas(@RequestParam("internshipReleaseId") String internshipReleaseId, @RequestParam("studentId") int studentId) {
        List<InternshipChangeCompanyHistoryBean> internshipChangeCompanyHistoryBeans = new ArrayList<>();
        Result<Record> records = internshipChangeCompanyHistoryService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
        if (records.isNotEmpty()) {
            internshipChangeCompanyHistoryBeans = records.into(InternshipChangeCompanyHistoryBean.class);
            internshipChangeCompanyHistoryBeans.forEach(i->i.setChangeTimeStr(DateTimeUtils.formatDate(i.getChangeTime())));
        }
        return new AjaxUtils<InternshipChangeCompanyHistoryBean>().success().msg("获取数据成功").listData(internshipChangeCompanyHistoryBeans);
    }

    /**
     * 未提交列表 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/unsubmitted/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipStatisticsBean> unsubmittedDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("student_name");
        headers.add("student_number");
        headers.add("science_name");
        headers.add("organize_name");
        headers.add("operator");
        DataTablesUtils<InternshipStatisticsBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        InternshipStatisticsBean internshipStatisticsBean = new InternshipStatisticsBean();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            internshipStatisticsBean.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
            Result<Record> records = internshipStatisticsService.unsubmittedFindAllByPage(dataTablesUtils, internshipStatisticsBean);
            List<InternshipStatisticsBean> internshipStatisticsBeens = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                internshipStatisticsBeens = records.into(InternshipStatisticsBean.class);
            }
            dataTablesUtils.setData(internshipStatisticsBeens);
            dataTablesUtils.setiTotalRecords(internshipStatisticsService.unsubmittedCountAll(internshipStatisticsBean));
            dataTablesUtils.setiTotalDisplayRecords(internshipStatisticsService.unsubmittedCountByCondition(dataTablesUtils, internshipStatisticsBean));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 数据列表 顶岗实习(留学院) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/college/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipCollege> collegeData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("student_name");
        headers.add("college_class");
        headers.add("student_sex");
        headers.add("student_number");
        headers.add("phone_number");
        headers.add("qq_mailbox");
        headers.add("parental_contact");
        headers.add("headmaster");
        headers.add("headmaster_contact");
        headers.add("internship_college_name");
        headers.add("internship_college_address");
        headers.add("internship_college_contacts");
        headers.add("internship_college_tel");
        headers.add("school_guidance_teacher");
        headers.add("school_guidance_teacher_tel");
        headers.add("start_time");
        headers.add("end_time");
        headers.add("commitment_book");
        headers.add("safety_responsibility_book");
        headers.add("practice_agreement");
        headers.add("internship_application");
        headers.add("practice_receiving");
        headers.add("security_education_agreement");
        headers.add("parental_consent");
        DataTablesUtils<InternshipCollege> dataTablesUtils = new DataTablesUtils<>(request, headers);
        InternshipCollege internshipCollege = new InternshipCollege();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            internshipCollege.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
            Result<Record> records = internshipCollegeService.findAllByPage(dataTablesUtils, internshipCollege);
            List<InternshipCollege> internshipColleges = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                internshipColleges = records.into(InternshipCollege.class);
            }
            dataTablesUtils.setData(internshipColleges);
            dataTablesUtils.setiTotalRecords(internshipCollegeService.countAll(internshipCollege));
            dataTablesUtils.setiTotalDisplayRecords(internshipCollegeService.countByCondition(dataTablesUtils, internshipCollege));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 数据列表 校外自主实习(去单位) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/company/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipCompany> companyData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("student_name");
        headers.add("college_class");
        headers.add("student_sex");
        headers.add("student_number");
        headers.add("phone_number");
        headers.add("qq_mailbox");
        headers.add("parental_contact");
        headers.add("headmaster");
        headers.add("headmaster_contact");
        headers.add("internship_company_name");
        headers.add("internship_company_address");
        headers.add("internship_company_contacts");
        headers.add("internship_company_tel");
        headers.add("school_guidance_teacher");
        headers.add("school_guidance_teacher_tel");
        headers.add("start_time");
        headers.add("end_time");
        headers.add("commitment_book");
        headers.add("safety_responsibility_book");
        headers.add("practice_agreement");
        headers.add("internship_application");
        headers.add("practice_receiving");
        headers.add("security_education_agreement");
        headers.add("parental_consent");
        DataTablesUtils<InternshipCompany> dataTablesUtils = new DataTablesUtils<>(request, headers);
        InternshipCompany internshipCompany = new InternshipCompany();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            internshipCompany.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
            Result<Record> records = internshipCompanyService.findAllByPage(dataTablesUtils, internshipCompany);
            List<InternshipCompany> internshipCompanies = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                internshipCompanies = records.into(InternshipCompany.class);
            }
            dataTablesUtils.setData(internshipCompanies);
            dataTablesUtils.setiTotalRecords(internshipCompanyService.countAll(internshipCompany));
            dataTablesUtils.setiTotalDisplayRecords(internshipCompanyService.countByCondition(dataTablesUtils, internshipCompany));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 数据列表 毕业实习(校外) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/graduation_practice_company/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationPracticeCompany> graduationPracticeCompanyData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("student_name");
        headers.add("college_class");
        headers.add("student_sex");
        headers.add("student_number");
        headers.add("phone_number");
        headers.add("qq_mailbox");
        headers.add("parental_contact");
        headers.add("headmaster");
        headers.add("headmaster_contact");
        headers.add("graduation_practice_company_name");
        headers.add("graduation_practice_company_address");
        headers.add("graduation_practice_company_contacts");
        headers.add("graduation_practice_company_tel");
        headers.add("school_guidance_teacher");
        headers.add("school_guidance_teacher_tel");
        headers.add("start_time");
        headers.add("end_time");
        headers.add("commitment_book");
        headers.add("safety_responsibility_book");
        headers.add("practice_agreement");
        headers.add("internship_application");
        headers.add("practice_receiving");
        headers.add("security_education_agreement");
        headers.add("parental_consent");
        DataTablesUtils<GraduationPracticeCompany> dataTablesUtils = new DataTablesUtils<>(request, headers);
        GraduationPracticeCompany graduationPracticeCompany = new GraduationPracticeCompany();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            graduationPracticeCompany.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
            Result<Record> records = graduationPracticeCompanyService.findAllByPage(dataTablesUtils, graduationPracticeCompany);
            List<GraduationPracticeCompany> graduationPracticeCompanies = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                graduationPracticeCompanies = records.into(GraduationPracticeCompany.class);
            }
            dataTablesUtils.setData(graduationPracticeCompanies);
            dataTablesUtils.setiTotalRecords(graduationPracticeCompanyService.countAll(graduationPracticeCompany));
            dataTablesUtils.setiTotalDisplayRecords(graduationPracticeCompanyService.countByCondition(dataTablesUtils, graduationPracticeCompany));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 数据列表 毕业实习(校内) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/graduation_practice_college/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationPracticeCollegeBean> graduationPracticeCollegeData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("student_name");
        headers.add("student_number");
        DataTablesUtils<GraduationPracticeCollegeBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        GraduationPracticeCollegeBean graduationPracticeCollegeBean = new GraduationPracticeCollegeBean();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            graduationPracticeCollegeBean.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
            Result<Record> records = graduationPracticeCollegeService.findAllByPage(dataTablesUtils, graduationPracticeCollegeBean);
            List<GraduationPracticeCollegeBean> graduationPracticeCollegeBeens = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                graduationPracticeCollegeBeens = records.into(GraduationPracticeCollegeBean.class);
            }
            dataTablesUtils.setData(graduationPracticeCollegeBeens);
            dataTablesUtils.setiTotalRecords(graduationPracticeCollegeService.countAll(graduationPracticeCollegeBean));
            dataTablesUtils.setiTotalDisplayRecords(graduationPracticeCollegeService.countByCondition(dataTablesUtils, graduationPracticeCollegeBean));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 数据列表 毕业实习(学校统一组织校外实习) 数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/statistical/graduation_practice_unify/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationPracticeUnifyBean> graduationPracticeUnifyData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("student_name");
        headers.add("student_number");
        DataTablesUtils<GraduationPracticeUnifyBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        GraduationPracticeUnifyBean graduationPracticeUnifyBean = new GraduationPracticeUnifyBean();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            graduationPracticeUnifyBean.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
            Result<Record> records = graduationPracticeUnifyService.findAllByPage(dataTablesUtils, graduationPracticeUnifyBean);
            List<GraduationPracticeUnifyBean> graduationPracticeUnifyBeens = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                graduationPracticeUnifyBeens = records.into(GraduationPracticeUnifyBean.class);
            }
            dataTablesUtils.setData(graduationPracticeUnifyBeens);
            dataTablesUtils.setiTotalRecords(graduationPracticeUnifyService.countAll(graduationPracticeUnifyBean));
            dataTablesUtils.setiTotalDisplayRecords(graduationPracticeUnifyService.countByCondition(dataTablesUtils, graduationPracticeUnifyBean));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }
}
