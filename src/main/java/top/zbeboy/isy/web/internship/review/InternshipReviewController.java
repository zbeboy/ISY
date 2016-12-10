package top.zbeboy.isy.web.internship.review;

import org.apache.commons.lang3.BooleanUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.bean.internship.review.GraduationPracticeCollegeBean;
import top.zbeboy.isy.web.bean.internship.review.GraduationPracticeUnifyBean;
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2016/12/6.
 */
@Controller
public class InternshipReviewController {

    private final Logger log = LoggerFactory.getLogger(InternshipReviewController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipApplyService internshipApplyService;

    @Resource
    private InternshipReviewService internshipReviewService;

    @Resource
    private InternshipTypeService internshipTypeService;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

    @Resource
    private OrganizeService organizeService;

    @Resource
    private InternshipCollegeService internshipCollegeService;

    @Resource
    private InternshipCompanyService internshipCompanyService;

    @Resource
    private GraduationPracticeCollegeService graduationPracticeCollegeService;

    @Resource
    private GraduationPracticeCompanyService graduationPracticeCompanyService;

    @Resource
    private GraduationPracticeUnifyService graduationPracticeUnifyService;

    /**
     * 实习审核
     *
     * @return 实习审核页面
     */
    @RequestMapping(value = "/web/menu/internship/review", method = RequestMethod.GET)
    public String internshipReview() {
        return "/web/internship/review/internship_review::#page-wrapper";
    }

    /**
     * 获取实习审核数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/data", method = RequestMethod.GET)
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
     * 进入实习审核页面判断条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/review/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canUse(@RequestParam("id") String internshipReleaseId) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 实习审核页面
     *
     * @param internshipReleaseId 实习发布id
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/review/audit", method = RequestMethod.GET)
    public String reviewAudit(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page = "/web/internship/review/internship_review::#page-wrapper";
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "/web/internship/review/internship_audit::#page-wrapper";
        }
        return page;
    }

    /**
     * 实习已通过学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/review/pass", method = RequestMethod.GET)
    public String reviewPass(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page = "/web/internship/review/internship_pass::#page-wrapper";
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "/web/internship/review/internship_pass::#page-wrapper";
        }
        return page;
    }

    /**
     * 实习未通过学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/review/fail", method = RequestMethod.GET)
    public String reviewFail(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page = "/web/internship/review/internship_fail::#page-wrapper";
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "/web/internship/review/internship_fail::#page-wrapper";
        }
        return page;
    }

    /**
     * 实习申请基本信息修改学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/review/base_info_apply", method = RequestMethod.GET)
    public String reviewBaseInfoApply(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page = "/web/internship/review/internship_base_info_apply::#page-wrapper";
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "/web/internship/review/internship_base_info_apply::#page-wrapper";
        }
        return page;
    }

    /**
     * 实习基本信息修改填写中学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/review/base_info_fill", method = RequestMethod.GET)
    public String reviewBaseInfoFill(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page = "/web/internship/review/internship_base_info_fill::#page-wrapper";
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "/web/internship/review/internship_base_info_fill::#page-wrapper";
        }
        return page;
    }

    /**
     * 实习单位信息修改申请学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/review/company_apply", method = RequestMethod.GET)
    public String reviewCompanyApply(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page = "/web/internship/review/internship_company_apply::#page-wrapper";
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "/web/internship/review/internship_company_apply::#page-wrapper";
        }
        return page;
    }

    /**
     * 实习单位信息修改填写中学生页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/review/company_fill", method = RequestMethod.GET)
    public String reviewCompanyFill(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page = "/web/internship/review/internship_company_fill::#page-wrapper";
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "/web/internship/review/internship_company_fill::#page-wrapper";
        }
        return page;
    }

    /**
     * 查看详情页
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/review/audit/detail", method = RequestMethod.GET)
    public String auditDetail(@RequestParam("internshipReleaseId") String internshipReleaseId, @RequestParam("studentId") int studentId, ModelMap modelMap) {
        String page = "/web/internship/review/internship_review::#page-wrapper";
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        if (!ObjectUtils.isEmpty(internshipRelease)) {
            InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
            switch (internshipType.getInternshipTypeName()) {
                case Workbook.INTERNSHIP_COLLEGE_TYPE:
                    Optional<Record> internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (internshipCollegeRecord.isPresent()) {
                        InternshipCollege internshipCollege = internshipCollegeRecord.get().into(InternshipCollege.class);
                        modelMap.addAttribute("internshipData", internshipCollege);
                        page = "/web/internship/review/internship_college_detail::#page-wrapper";
                    }
                    break;
                case Workbook.INTERNSHIP_COMPANY_TYPE:
                    Optional<Record> internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (internshipCompanyRecord.isPresent()) {
                        InternshipCompany internshipCompany = internshipCompanyRecord.get().into(InternshipCompany.class);
                        modelMap.addAttribute("internshipData", internshipCompany);
                        page = "/web/internship/review/internship_company_detail::#page-wrapper";
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                    Optional<Record> graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeCollegeRecord.isPresent()) {
                        GraduationPracticeCollegeBean graduationPracticeCollegeBean = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollegeBean.class);
                        modelMap.addAttribute("internshipData", graduationPracticeCollegeBean);
                        page = "/web/internship/review/graduation_practice_college_detail::#page-wrapper";
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                    Optional<Record> graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeUnifyRecord.isPresent()) {
                        GraduationPracticeUnifyBean graduationPracticeUnifyBean = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnifyBean.class);
                        modelMap.addAttribute("internshipData", graduationPracticeUnifyBean);
                        page = "/web/internship/review/graduation_practice_unify_detail::#page-wrapper";
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                    Optional<Record> graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeCompanyRecord.isPresent()) {
                        GraduationPracticeCompany graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany.class);
                        modelMap.addAttribute("internshipData", graduationPracticeCompany);
                        page = "/web/internship/review/graduation_practice_company_detail::#page-wrapper";
                    }
                    break;
            }
        }
        return page;
    }

    /**
     * 查询申请中的学生数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/audit/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils auditDatas(PaginationUtils paginationUtils) {
        InternshipApplyBean internshipApplyBean = new InternshipApplyBean();
        internshipApplyBean.setInternshipApplyState(1);
        return internshipReviewData(paginationUtils, internshipApplyBean);
    }

    /**
     * 查询已通过的学生数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/pass/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils passDatas(PaginationUtils paginationUtils) {
        InternshipApplyBean internshipApplyBean = new InternshipApplyBean();
        internshipApplyBean.setInternshipApplyState(2);
        return internshipReviewData(paginationUtils, internshipApplyBean);
    }

    /**
     * 查询未通过的学生数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/fail/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils failDatas(PaginationUtils paginationUtils) {
        InternshipApplyBean internshipApplyBean = new InternshipApplyBean();
        internshipApplyBean.setInternshipApplyState(3);
        return internshipReviewData(paginationUtils, internshipApplyBean);
    }

    /**
     * 查询基本信息变更申请的学生数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/base_info_apply/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils baseInfoApplyDatas(PaginationUtils paginationUtils) {
        InternshipApplyBean internshipApplyBean = new InternshipApplyBean();
        internshipApplyBean.setInternshipApplyState(4);
        return internshipReviewData(paginationUtils, internshipApplyBean);
    }

    /**
     * 查询基本信息变更填写中的学生数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/base_info_fill/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils baseInfoFillDatas(PaginationUtils paginationUtils) {
        InternshipApplyBean internshipApplyBean = new InternshipApplyBean();
        internshipApplyBean.setInternshipApplyState(5);
        return internshipReviewData(paginationUtils, internshipApplyBean);
    }

    /**
     * 查询单位信息变更申请的学生数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/company_apply/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils companyApplyDatas(PaginationUtils paginationUtils) {
        InternshipApplyBean internshipApplyBean = new InternshipApplyBean();
        internshipApplyBean.setInternshipApplyState(6);
        return internshipReviewData(paginationUtils, internshipApplyBean);
    }

    /**
     * 查询单位信息变更填写中的学生数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/company_fill/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils companyFillDatas(PaginationUtils paginationUtils) {
        InternshipApplyBean internshipApplyBean = new InternshipApplyBean();
        internshipApplyBean.setInternshipApplyState(7);
        return internshipReviewData(paginationUtils, internshipApplyBean);
    }

    /**
     * 数据
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 申请条件
     * @return 数据
     */
    private AjaxUtils<InternshipReviewBean> internshipReviewData(PaginationUtils paginationUtils, InternshipApplyBean internshipApplyBean) {
        List<InternshipReviewBean> internshipReviewBeens = internshipReviewService.findAllByPage(paginationUtils, internshipApplyBean);
        if (!ObjectUtils.isEmpty(internshipReviewBeens)) {
            for (int i = 0; i < internshipReviewBeens.size(); i++) {
                InternshipReviewBean internshipReviewBean = internshipReviewBeens.get(i);
                internshipReviewBeens.set(i, fillInternshipReviewBean(internshipReviewBean));
            }
        }
        return new AjaxUtils<InternshipReviewBean>().success().msg("获取数据成功").listData(internshipReviewBeens).paginationUtils(paginationUtils);
    }

    /**
     * 实习审核 保存
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/review/audit/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils auditSave(InternshipReviewBean internshipReviewBean) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        if (!ObjectUtils.isEmpty(internshipReviewBean.getInternshipReleaseId()) && !ObjectUtils.isEmpty(internshipReviewBean.getStudentId())) {
            InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReviewBean.getInternshipReleaseId());
            if (!ObjectUtils.isEmpty(internshipRelease)) {
                InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
                switch (internshipType.getInternshipTypeName()) {
                    case Workbook.INTERNSHIP_COLLEGE_TYPE:
                        Optional<Record> internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                        if (internshipCollegeRecord.isPresent()) {
                            InternshipCollege internshipCollege = internshipCollegeRecord.get().into(InternshipCollege.class);
                            internshipCollege.setCommitmentBook(internshipReviewBean.getCommitmentBook());
                            internshipCollege.setSafetyResponsibilityBook(internshipReviewBean.getSafetyResponsibilityBook());
                            internshipCollege.setPracticeAgreement(internshipReviewBean.getPracticeAgreement());
                            internshipCollege.setInternshipApplication(internshipReviewBean.getInternshipApplication());
                            internshipCollege.setPracticeReceiving(internshipReviewBean.getPracticeReceiving());
                            internshipCollege.setSecurityEducationAgreement(internshipReviewBean.getSecurityEducationAgreement());
                            internshipCollege.setParentalConsent(internshipReviewBean.getParentalConsent());
                            internshipCollegeService.update(internshipCollege);
                        }
                        break;
                    case Workbook.INTERNSHIP_COMPANY_TYPE:
                        Optional<Record> internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                        if (internshipCompanyRecord.isPresent()) {
                            InternshipCompany internshipCompany = internshipCompanyRecord.get().into(InternshipCompany.class);
                            internshipCompany.setCommitmentBook(internshipReviewBean.getCommitmentBook());
                            internshipCompany.setSafetyResponsibilityBook(internshipReviewBean.getSafetyResponsibilityBook());
                            internshipCompany.setPracticeAgreement(internshipReviewBean.getPracticeAgreement());
                            internshipCompany.setInternshipApplication(internshipReviewBean.getInternshipApplication());
                            internshipCompany.setPracticeReceiving(internshipReviewBean.getPracticeReceiving());
                            internshipCompany.setSecurityEducationAgreement(internshipReviewBean.getSecurityEducationAgreement());
                            internshipCompany.setParentalConsent(internshipReviewBean.getParentalConsent());
                            internshipCompanyService.update(internshipCompany);
                        }
                        break;
                    case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                        break;
                    case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                        break;
                    case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                        Optional<Record> graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                        if (graduationPracticeCompanyRecord.isPresent()) {
                            GraduationPracticeCompany graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany.class);
                            graduationPracticeCompany.setCommitmentBook(internshipReviewBean.getCommitmentBook());
                            graduationPracticeCompany.setSafetyResponsibilityBook(internshipReviewBean.getSafetyResponsibilityBook());
                            graduationPracticeCompany.setPracticeAgreement(internshipReviewBean.getPracticeAgreement());
                            graduationPracticeCompany.setInternshipApplication(internshipReviewBean.getInternshipApplication());
                            graduationPracticeCompany.setPracticeReceiving(internshipReviewBean.getPracticeReceiving());
                            graduationPracticeCompany.setSecurityEducationAgreement(internshipReviewBean.getSecurityEducationAgreement());
                            graduationPracticeCompany.setParentalConsent(internshipReviewBean.getParentalConsent());
                            graduationPracticeCompanyService.update(graduationPracticeCompany);
                        }
                        break;
                }
                ajaxUtils.success().msg("保存成功");
            } else {
                ajaxUtils.fail().msg("未查询相关实习信息");
            }
        } else {
            ajaxUtils.fail().msg("缺失必要参数");
        }
        return ajaxUtils;
    }

    /**
     * 实习审核 通过
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/review/audit/pass", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils auditPass(InternshipReviewBean internshipReviewBean) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        if (!ObjectUtils.isEmpty(internshipReviewBean.getInternshipReleaseId()) && !ObjectUtils.isEmpty(internshipReviewBean.getStudentId())) {
            Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
            if (internshipApplyRecord.isPresent()) {
                InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
                internshipApply.setReason(internshipReviewBean.getReason());
                internshipApply.setInternshipApplyState(internshipReviewBean.getInternshipApplyState());
                internshipApplyService.update(internshipApply);
                InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReviewBean.getInternshipReleaseId());
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
                    Byte b = 1;
                    switch (internshipType.getInternshipTypeName()) {
                        case Workbook.INTERNSHIP_COLLEGE_TYPE:
                            Optional<Record> internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                            if (internshipCollegeRecord.isPresent()) {
                                InternshipCollege internshipCollege = internshipCollegeRecord.get().into(InternshipCollege.class);
                                internshipCollege.setCommitmentBook(b);
                                internshipCollege.setSafetyResponsibilityBook(b);
                                internshipCollege.setPracticeAgreement(b);
                                internshipCollege.setInternshipApplication(b);
                                internshipCollege.setPracticeReceiving(b);
                                internshipCollege.setSecurityEducationAgreement(b);
                                internshipCollege.setParentalConsent(b);
                                internshipCollegeService.update(internshipCollege);
                            }
                            break;
                        case Workbook.INTERNSHIP_COMPANY_TYPE:
                            Optional<Record> internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                            if (internshipCompanyRecord.isPresent()) {
                                InternshipCompany internshipCompany = internshipCompanyRecord.get().into(InternshipCompany.class);
                                internshipCompany.setCommitmentBook(b);
                                internshipCompany.setSafetyResponsibilityBook(b);
                                internshipCompany.setPracticeAgreement(b);
                                internshipCompany.setInternshipApplication(b);
                                internshipCompany.setPracticeReceiving(b);
                                internshipCompany.setSecurityEducationAgreement(b);
                                internshipCompany.setParentalConsent(b);
                                internshipCompanyService.update(internshipCompany);
                            }
                            break;
                        case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                            break;
                        case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                            break;
                        case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                            Optional<Record> graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                            if (graduationPracticeCompanyRecord.isPresent()) {
                                GraduationPracticeCompany graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany.class);
                                graduationPracticeCompany.setCommitmentBook(b);
                                graduationPracticeCompany.setSafetyResponsibilityBook(b);
                                graduationPracticeCompany.setPracticeAgreement(b);
                                graduationPracticeCompany.setInternshipApplication(b);
                                graduationPracticeCompany.setPracticeReceiving(b);
                                graduationPracticeCompany.setSecurityEducationAgreement(b);
                                graduationPracticeCompany.setParentalConsent(b);
                                graduationPracticeCompanyService.update(graduationPracticeCompany);
                            }
                            break;
                    }
                    ajaxUtils.success().msg("保存成功");
                } else {
                    ajaxUtils.fail().msg("未查询到相关实习信息");
                }
            } else {
                ajaxUtils.fail().msg("未查询到相关实习申请信息");
            }
        } else {
            ajaxUtils.fail().msg("缺失必要参数");
        }
        return ajaxUtils;
    }

    /**
     * 实习审核 同意  基本信息修改申请 单位信息修改申请
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/review/audit/agree", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils auditAgree(InternshipReviewBean internshipReviewBean) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        try {
            if (!ObjectUtils.isEmpty(internshipReviewBean.getInternshipReleaseId()) && !ObjectUtils.isEmpty(internshipReviewBean.getStudentId())) {
                Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                if (internshipApplyRecord.isPresent()) {
                    InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
                    internshipApply.setReason(internshipReviewBean.getReason());
                    internshipApply.setInternshipApplyState(internshipReviewBean.getInternshipApplyState());
                    String format = "yyyy-MM-dd HH:mm:ss";
                    if (StringUtils.hasLength(internshipReviewBean.getFillTime())) {
                        String[] timeArr = internshipReviewBean.getFillTime().split("至");
                        if (!ObjectUtils.isEmpty(timeArr) && timeArr.length >= 2) {
                            internshipApply.setChangeFillStartTime(DateTimeUtils.formatDateToTimestamp(timeArr[0], format));
                            internshipApply.setChangeFillEndTime(DateTimeUtils.formatDateToTimestamp(timeArr[1], format));
                        }
                    }
                    internshipApplyService.update(internshipApply);
                    ajaxUtils.success().msg("更新状态成功");
                } else {
                    ajaxUtils.fail().msg("未查询到相关实习申请信息");
                }
            } else {
                ajaxUtils.fail().msg("缺失必要参数");
            }
        } catch (ParseException e) {
            log.error(" format time is exception.", e);
            ajaxUtils.fail().msg("时间参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 实习审核 拒绝  基本信息修改申请 单位信息修改申请
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/review/audit/disagree", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils auditDisagree(InternshipReviewBean internshipReviewBean) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        if (!ObjectUtils.isEmpty(internshipReviewBean.getInternshipReleaseId()) && !ObjectUtils.isEmpty(internshipReviewBean.getStudentId())) {
            Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
            if (internshipApplyRecord.isPresent()) {
                InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
                internshipApply.setReason(internshipReviewBean.getReason());
                internshipApply.setInternshipApplyState(internshipReviewBean.getInternshipApplyState());
                internshipApplyService.update(internshipApply);
                ajaxUtils.success().msg("更新状态成功");
            } else {
                ajaxUtils.fail().msg("未查询到相关实习申请信息");
            }
        } else {
            ajaxUtils.fail().msg("缺失必要参数");
        }
        return ajaxUtils;
    }

    /**
     * 实习审核 不通过
     *
     * @param reason               原因
     * @param internshipApplyState 实习审核状态
     * @param internshipReleaseId  实习发布id
     * @param studentId            学生id
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/review/audit/fail", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils auditFail(@RequestParam("reason") String reason, @RequestParam("internshipApplyState") int internshipApplyState,
                               @RequestParam("internshipReleaseId") String internshipReleaseId, @RequestParam("studentId") int studentId) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
        if (internshipApplyRecord.isPresent()) {
            InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
            internshipApply.setInternshipApplyState(internshipApplyState);
            internshipApply.setReason(reason);
            internshipApplyService.update(internshipApply);
            ajaxUtils.success().msg("更改成功");
        } else {
            ajaxUtils.fail().msg("未查询到相关申请信息");
        }
        return ajaxUtils;
    }

    /**
     * 填充实习数据
     *
     * @param internshipReviewBean 学生申请数据
     * @return 学生申请数据
     */
    private InternshipReviewBean fillInternshipReviewBean(InternshipReviewBean internshipReviewBean) {
        int internshipTypeId = internshipReviewBean.getInternshipTypeId();
        int studentId = internshipReviewBean.getStudentId();
        String internshipReleaseId = internshipReviewBean.getInternshipReleaseId();
        InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipTypeId);
        switch (internshipType.getInternshipTypeName()) {
            case Workbook.INTERNSHIP_COLLEGE_TYPE:
                Optional<Record> internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                if (internshipCollegeRecord.isPresent()) {
                    InternshipCollege internshipCollege = internshipCollegeRecord.get().into(InternshipCollege.class);
                    internshipReviewBean.setHasController(true);
                    internshipReviewBean.setCommitmentBook(internshipCollege.getCommitmentBook());
                    internshipReviewBean.setSafetyResponsibilityBook(internshipCollege.getSafetyResponsibilityBook());
                    internshipReviewBean.setPracticeAgreement(internshipCollege.getPracticeAgreement());
                    internshipReviewBean.setInternshipApplication(internshipCollege.getInternshipApplication());
                    internshipReviewBean.setPracticeReceiving(internshipCollege.getPracticeReceiving());
                    internshipReviewBean.setSecurityEducationAgreement(internshipCollege.getSecurityEducationAgreement());
                    internshipReviewBean.setParentalConsent(internshipCollege.getParentalConsent());
                }
                break;
            case Workbook.INTERNSHIP_COMPANY_TYPE:
                Optional<Record> internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                if (internshipCompanyRecord.isPresent()) {
                    InternshipCompany internshipCompany = internshipCompanyRecord.get().into(InternshipCompany.class);
                    internshipReviewBean.setHasController(true);
                    internshipReviewBean.setCommitmentBook(internshipCompany.getCommitmentBook());
                    internshipReviewBean.setSafetyResponsibilityBook(internshipCompany.getSafetyResponsibilityBook());
                    internshipReviewBean.setPracticeAgreement(internshipCompany.getPracticeAgreement());
                    internshipReviewBean.setInternshipApplication(internshipCompany.getInternshipApplication());
                    internshipReviewBean.setPracticeReceiving(internshipCompany.getPracticeReceiving());
                    internshipReviewBean.setSecurityEducationAgreement(internshipCompany.getSecurityEducationAgreement());
                    internshipReviewBean.setParentalConsent(internshipCompany.getParentalConsent());
                }
                break;
            case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                internshipReviewBean.setHasController(false);
                break;
            case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                internshipReviewBean.setHasController(false);
                break;
            case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                Optional<Record> graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                if (graduationPracticeCompanyRecord.isPresent()) {
                    GraduationPracticeCompany graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany.class);
                    internshipReviewBean.setHasController(true);
                    internshipReviewBean.setCommitmentBook(graduationPracticeCompany.getCommitmentBook());
                    internshipReviewBean.setSafetyResponsibilityBook(graduationPracticeCompany.getSafetyResponsibilityBook());
                    internshipReviewBean.setPracticeAgreement(graduationPracticeCompany.getPracticeAgreement());
                    internshipReviewBean.setInternshipApplication(graduationPracticeCompany.getInternshipApplication());
                    internshipReviewBean.setPracticeReceiving(graduationPracticeCompany.getPracticeReceiving());
                    internshipReviewBean.setSecurityEducationAgreement(graduationPracticeCompany.getSecurityEducationAgreement());
                    internshipReviewBean.setParentalConsent(graduationPracticeCompany.getParentalConsent());
                }
                break;
        }
        return internshipReviewBean;
    }

    /**
     * 获取专业数据
     *
     * @param internshipReleaseId 实习发布id
     * @return 专业数据
     */
    @RequestMapping(value = "/web/internship/review/audit/sciences", method = RequestMethod.POST)
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
    @RequestMapping(value = "/web/internship/review/audit/organizes", method = RequestMethod.POST)
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
     * 进入实习审核入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private ErrorBean<InternshipRelease> accessCondition(String internshipReleaseId) {
        ErrorBean<InternshipRelease> errorBean = new ErrorBean<>();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        if (!ObjectUtils.isEmpty(internshipRelease)) {
            errorBean.setData(internshipRelease);
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询相关实习信息");
        }
        return errorBean;
    }
}
