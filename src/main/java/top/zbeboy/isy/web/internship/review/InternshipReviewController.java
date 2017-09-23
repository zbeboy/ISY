package top.zbeboy.isy.web.internship.review;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
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
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.OrganizeService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.internship.*;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zbeboy on 2016/12/6.
 */
@Slf4j
@Controller
public class InternshipReviewController {

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

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private InternshipChangeHistoryService internshipChangeHistoryService;

    @Resource
    private InternshipChangeCompanyHistoryService internshipChangeCompanyHistoryService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

    @Resource
    private StudentService studentService;

    @Resource
    private UsersService usersService;

    /**
     * 实习审核
     *
     * @return 实习审核页面
     */
    @RequestMapping(value = "/web/menu/internship/review", method = RequestMethod.GET)
    public String internshipReview() {
        return "web/internship/review/internship_review::#page-wrapper";
    }

    /**
     * 获取实习审核数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipReleaseBean> internshipListDatas(PaginationUtils paginationUtils) {
        AjaxUtils<InternshipReleaseBean> ajaxUtils = AjaxUtils.of();
        Byte isDel = 0;
        InternshipReleaseBean internshipReleaseBean = new InternshipReleaseBean();
        internshipReleaseBean.setInternshipReleaseIsDel(isDel);
        Map<String, Integer> commonData = commonControllerMethodService.accessRoleCondition();
        internshipReleaseBean.setDepartmentId(StringUtils.isEmpty(commonData.get("departmentId")) ? -1 : commonData.get("departmentId"));
        internshipReleaseBean.setCollegeId(StringUtils.isEmpty(commonData.get("collegeId")) ? -1 : commonData.get("collegeId"));
        Result<Record> records = internshipReleaseService.findAllByPage(paginationUtils, internshipReleaseBean);
        List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipReleaseBean);
        internshipReleaseBeens.forEach(r -> {
            r.setWaitTotalData(internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.getInternshipReleaseId(), 1));
            r.setPassTotalData(internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.getInternshipReleaseId(), 2));
            r.setFailTotalData(internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.getInternshipReleaseId(), 3));
            r.setBasicApplyTotalData(internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.getInternshipReleaseId(), 4));
            r.setCompanyApplyTotalData(internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.getInternshipReleaseId(), 6));
            r.setBasicFillTotalData(internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.getInternshipReleaseId(), 5));
            r.setCompanyFillTotalData(internshipReviewService.countByInternshipReleaseIdAndInternshipApplyState(r.getInternshipReleaseId(), 7));
        });
        return ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils);
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
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
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
        String page;
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "web/internship/review/internship_audit::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
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
        String page;
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "web/internship/review/internship_pass::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
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
        String page;
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "web/internship/review/internship_fail::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
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
        String page;
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "web/internship/review/internship_base_info_apply::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
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
        String page;
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "web/internship/review/internship_base_info_fill::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
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
        String page;
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "web/internship/review/internship_company_apply::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
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
        String page;
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            page = "web/internship/review/internship_company_fill::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
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
        String page;
        ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReleaseId);
        if (!errorBean.isHasError()) {
            InternshipRelease internshipRelease = errorBean.getData();
            InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
            switch (internshipType.getInternshipTypeName()) {
                case Workbook.INTERNSHIP_COLLEGE_TYPE:
                    Optional<Record> internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (internshipCollegeRecord.isPresent()) {
                        InternshipCollege internshipCollege = internshipCollegeRecord.get().into(InternshipCollege.class);
                        modelMap.addAttribute("internshipData", internshipCollege);
                        page = "web/internship/review/internship_college_detail::#page-wrapper";
                    } else {
                        page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
                    }
                    break;
                case Workbook.INTERNSHIP_COMPANY_TYPE:
                    Optional<Record> internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (internshipCompanyRecord.isPresent()) {
                        InternshipCompany internshipCompany = internshipCompanyRecord.get().into(InternshipCompany.class);
                        modelMap.addAttribute("internshipData", internshipCompany);
                        page = "web/internship/review/internship_company_detail::#page-wrapper";
                    } else {
                        page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                    Optional<Record> graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeCollegeRecord.isPresent()) {
                        GraduationPracticeCollege graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege.class);
                        modelMap.addAttribute("internshipData", graduationPracticeCollege);
                        page = "web/internship/review/graduation_practice_college_detail::#page-wrapper";
                    } else {
                        page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                    Optional<Record> graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeUnifyRecord.isPresent()) {
                        GraduationPracticeUnify graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify.class);
                        modelMap.addAttribute("internshipData", graduationPracticeUnify);
                        page = "web/internship/review/graduation_practice_unify_detail::#page-wrapper";
                    } else {
                        page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                    Optional<Record> graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeCompanyRecord.isPresent()) {
                        GraduationPracticeCompany graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany.class);
                        modelMap.addAttribute("internshipData", graduationPracticeCompany);
                        page = "web/internship/review/graduation_practice_company_detail::#page-wrapper";
                    } else {
                        page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
                    }
                    break;
                default:
                    page = commonControllerMethodService.showTip(modelMap, "未找到相关实习类型页面");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
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
        AjaxUtils<InternshipReviewBean> ajaxUtils = AjaxUtils.of();
        List<InternshipReviewBean> internshipReviewBeens = internshipReviewService.findAllByPage(paginationUtils, internshipApplyBean);
        if (!ObjectUtils.isEmpty(internshipReviewBeens)) {
            for (int i = 0; i < internshipReviewBeens.size(); i++) {
                InternshipReviewBean internshipReviewBean = internshipReviewBeens.get(i);
                internshipReviewBeens.set(i, fillInternshipReviewBean(internshipReviewBean));
            }
        }
        return ajaxUtils.success().msg("获取数据成功").listData(internshipReviewBeens).paginationUtils(paginationUtils);
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
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!ObjectUtils.isEmpty(internshipReviewBean.getInternshipReleaseId()) && !ObjectUtils.isEmpty(internshipReviewBean.getStudentId())) {
            ErrorBean<InternshipRelease> errorBean = internshipReleaseService.basicCondition(internshipReviewBean.getInternshipReleaseId());
            if (!errorBean.isHasError()) {
                InternshipRelease internshipRelease = errorBean.getData();
                InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
                updateInternshipMaterialState(internshipType, internshipReviewBean);
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
    public AjaxUtils auditPass(InternshipReviewBean internshipReviewBean, HttpServletRequest request) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
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
                    updateInternshipMaterialState(internshipType, internshipReviewBean);
                    InternshipChangeHistory internshipChangeHistory = new InternshipChangeHistory();
                    internshipChangeHistory.setInternshipChangeHistoryId(UUIDUtils.getUUID());
                    internshipChangeHistory.setInternshipReleaseId(internshipReviewBean.getInternshipReleaseId());
                    internshipChangeHistory.setStudentId(internshipReviewBean.getStudentId());
                    internshipChangeHistory.setState(internshipReviewBean.getInternshipApplyState());
                    internshipChangeHistory.setApplyTime(new Timestamp(Clock.systemDefaultZone().millis()));
                    internshipChangeHistoryService.save(internshipChangeHistory);

                    Optional<Record> userRecord = studentService.findByIdRelation(internshipReviewBean.getStudentId());
                    if (userRecord.isPresent()) {
                        Users users = userRecord.get().into(Users.class);
                        Users curUsers = usersService.getUserFromSession();
                        String notify = "您的自主实习 " + internshipRelease.getInternshipTitle() + " 申请已通过。";
                        commonControllerMethodService.sendNotify(users, curUsers, internshipRelease.getInternshipTitle(), notify, request);
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
     * 更新实习材料状态
     */
    private void updateInternshipMaterialState(InternshipType internshipType, InternshipReviewBean internshipReviewBean) {
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
                Optional<Record> graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                if (graduationPracticeCollegeRecord.isPresent()) {
                    GraduationPracticeCollege graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege.class);
                    graduationPracticeCollege.setCommitmentBook(internshipReviewBean.getCommitmentBook());
                    graduationPracticeCollege.setSafetyResponsibilityBook(internshipReviewBean.getSafetyResponsibilityBook());
                    graduationPracticeCollege.setPracticeAgreement(internshipReviewBean.getPracticeAgreement());
                    graduationPracticeCollege.setInternshipApplication(internshipReviewBean.getInternshipApplication());
                    graduationPracticeCollege.setPracticeReceiving(internshipReviewBean.getPracticeReceiving());
                    graduationPracticeCollege.setSecurityEducationAgreement(internshipReviewBean.getSecurityEducationAgreement());
                    graduationPracticeCollege.setParentalConsent(internshipReviewBean.getParentalConsent());
                    graduationPracticeCollegeService.update(graduationPracticeCollege);
                }
                break;
            case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                Optional<Record> graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                if (graduationPracticeUnifyRecord.isPresent()) {
                    GraduationPracticeUnify graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify.class);
                    graduationPracticeUnify.setCommitmentBook(internshipReviewBean.getCommitmentBook());
                    graduationPracticeUnify.setSafetyResponsibilityBook(internshipReviewBean.getSafetyResponsibilityBook());
                    graduationPracticeUnify.setPracticeAgreement(internshipReviewBean.getPracticeAgreement());
                    graduationPracticeUnify.setInternshipApplication(internshipReviewBean.getInternshipApplication());
                    graduationPracticeUnify.setPracticeReceiving(internshipReviewBean.getPracticeReceiving());
                    graduationPracticeUnify.setSecurityEducationAgreement(internshipReviewBean.getSecurityEducationAgreement());
                    graduationPracticeUnify.setParentalConsent(internshipReviewBean.getParentalConsent());
                    graduationPracticeUnifyService.update(graduationPracticeUnify);
                }
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
    }

    /**
     * 更新实习材料状态
     *
     * @param internshipType 实习类型
     * @param b              状态
     */
    private void updateInternshipMaterialState(InternshipType internshipType, Byte b, String InternshipReleaseId, int studentId) {
        switch (internshipType.getInternshipTypeName()) {
            case Workbook.INTERNSHIP_COLLEGE_TYPE:
                Optional<Record> internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId);
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
                Optional<Record> internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId);
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
                Optional<Record> graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId);
                if (graduationPracticeCollegeRecord.isPresent()) {
                    GraduationPracticeCollege graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege.class);
                    graduationPracticeCollege.setCommitmentBook(b);
                    graduationPracticeCollege.setSafetyResponsibilityBook(b);
                    graduationPracticeCollege.setPracticeAgreement(b);
                    graduationPracticeCollege.setInternshipApplication(b);
                    graduationPracticeCollege.setPracticeReceiving(b);
                    graduationPracticeCollege.setSecurityEducationAgreement(b);
                    graduationPracticeCollege.setParentalConsent(b);
                    graduationPracticeCollegeService.update(graduationPracticeCollege);
                }
                break;
            case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                Optional<Record> graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId);
                if (graduationPracticeUnifyRecord.isPresent()) {
                    GraduationPracticeUnify graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify.class);
                    graduationPracticeUnify.setCommitmentBook(b);
                    graduationPracticeUnify.setSafetyResponsibilityBook(b);
                    graduationPracticeUnify.setPracticeAgreement(b);
                    graduationPracticeUnify.setInternshipApplication(b);
                    graduationPracticeUnify.setPracticeReceiving(b);
                    graduationPracticeUnify.setSecurityEducationAgreement(b);
                    graduationPracticeUnify.setParentalConsent(b);
                    graduationPracticeUnifyService.update(graduationPracticeUnify);
                }
                break;
            case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                Optional<Record> graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(InternshipReleaseId, studentId);
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
    }

    /**
     * 实习审核 同意  基本信息修改申请 单位信息修改申请
     *
     * @param internshipReviewBean 数据
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/review/audit/agree", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils auditAgree(InternshipReviewBean internshipReviewBean, HttpServletRequest request) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        try {
            if (!ObjectUtils.isEmpty(internshipReviewBean.getInternshipReleaseId()) && !ObjectUtils.isEmpty(internshipReviewBean.getStudentId())) {
                Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                if (internshipApplyRecord.isPresent()) {
                    InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
                    internshipApply.setReason(internshipReviewBean.getReason());
                    internshipApply.setInternshipApplyState(internshipReviewBean.getInternshipApplyState());
                    String format = "yyyy-MM-dd HH:mm:ss";
                    if (StringUtils.hasLength(internshipReviewBean.getFillTime())) {
                        String[] timeArr = DateTimeUtils.splitDateTime("至", internshipReviewBean.getFillTime());
                        internshipApply.setChangeFillStartTime(DateTimeUtils.formatDateToTimestamp(timeArr[0], format));
                        internshipApply.setChangeFillEndTime(DateTimeUtils.formatDateToTimestamp(timeArr[1], format));
                    }
                    internshipApplyService.update(internshipApply);

                    // 若同意进入 7：单位信息变更填写中 需要删除提交材料的状态
                    if (internshipReviewBean.getInternshipApplyState() == 7) {
                        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReviewBean.getInternshipReleaseId());
                        InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
                        Byte b = 0;
                        updateInternshipMaterialState(internshipType, b, internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
                    }

                    ajaxUtils.success().msg("更新状态成功");
                    InternshipChangeHistory internshipChangeHistory = new InternshipChangeHistory();
                    internshipChangeHistory.setInternshipChangeHistoryId(UUIDUtils.getUUID());
                    internshipChangeHistory.setInternshipReleaseId(internshipReviewBean.getInternshipReleaseId());
                    internshipChangeHistory.setStudentId(internshipReviewBean.getStudentId());
                    internshipChangeHistory.setState(internshipReviewBean.getInternshipApplyState());
                    internshipChangeHistory.setApplyTime(new Timestamp(Clock.systemDefaultZone().millis()));
                    internshipChangeHistory.setReason(internshipApply.getReason());
                    internshipChangeHistory.setChangeFillStartTime(internshipApply.getChangeFillStartTime());
                    internshipChangeHistory.setChangeFillEndTime(internshipApply.getChangeFillEndTime());
                    internshipChangeHistoryService.save(internshipChangeHistory);

                    Optional<Record> userRecord = studentService.findByIdRelation(internshipReviewBean.getStudentId());
                    if (userRecord.isPresent()) {
                        Users users = userRecord.get().into(Users.class);
                        Users curUsers = usersService.getUserFromSession();
                        String notify = "已同意您的实习变更申请，请尽快登录系统在填写时间范围变更您的内容。";
                        commonControllerMethodService.sendNotify(users, curUsers, "同意实习变更申请", notify, request);
                    }
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
    public AjaxUtils auditDisagree(InternshipReviewBean internshipReviewBean, HttpServletRequest request) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!ObjectUtils.isEmpty(internshipReviewBean.getInternshipReleaseId()) && !ObjectUtils.isEmpty(internshipReviewBean.getStudentId())) {
            Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReviewBean.getInternshipReleaseId(), internshipReviewBean.getStudentId());
            if (internshipApplyRecord.isPresent()) {
                InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
                internshipApply.setReason(internshipReviewBean.getReason());
                internshipApply.setInternshipApplyState(internshipReviewBean.getInternshipApplyState());
                internshipApplyService.update(internshipApply);
                ajaxUtils.success().msg("更新状态成功");
                InternshipChangeHistory internshipChangeHistory = new InternshipChangeHistory();
                internshipChangeHistory.setInternshipChangeHistoryId(UUIDUtils.getUUID());
                internshipChangeHistory.setInternshipReleaseId(internshipReviewBean.getInternshipReleaseId());
                internshipChangeHistory.setStudentId(internshipReviewBean.getStudentId());
                internshipChangeHistory.setState(internshipReviewBean.getInternshipApplyState());
                internshipChangeHistory.setApplyTime(new Timestamp(Clock.systemDefaultZone().millis()));
                internshipChangeHistory.setReason(internshipApply.getReason());
                internshipChangeHistoryService.save(internshipChangeHistory);

                Optional<Record> userRecord = studentService.findByIdRelation(internshipReviewBean.getStudentId());
                if (userRecord.isPresent()) {
                    Users users = userRecord.get().into(Users.class);
                    Users curUsers = usersService.getUserFromSession();
                    String notify = "已拒绝您的实习变更申请。";
                    commonControllerMethodService.sendNotify(users, curUsers, "拒绝实习变更申请", notify, request);
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
                               @RequestParam("internshipReleaseId") String internshipReleaseId, @RequestParam("studentId") int studentId, HttpServletRequest request) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
        if (internshipApplyRecord.isPresent()) {
            InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
            internshipApply.setInternshipApplyState(internshipApplyState);
            internshipApply.setReason(reason);
            internshipApplyService.update(internshipApply);
            ajaxUtils.success().msg("更改成功");
            InternshipChangeHistory internshipChangeHistory = new InternshipChangeHistory();
            internshipChangeHistory.setInternshipChangeHistoryId(UUIDUtils.getUUID());
            internshipChangeHistory.setInternshipReleaseId(internshipReleaseId);
            internshipChangeHistory.setStudentId(studentId);
            internshipChangeHistory.setState(internshipApplyState);
            internshipChangeHistory.setApplyTime(new Timestamp(Clock.systemDefaultZone().millis()));
            internshipChangeHistory.setReason(internshipApply.getReason());
            internshipChangeHistoryService.save(internshipChangeHistory);

            Optional<Record> userRecord = studentService.findByIdRelation(studentId);
            if (userRecord.isPresent()) {
                Users users = userRecord.get().into(Users.class);
                Users curUsers = usersService.getUserFromSession();
                String notify = "您的自主实习申请未通过，具体原因：" + reason;
                commonControllerMethodService.sendNotify(users, curUsers, "实习申请未通过", notify, request);
            }
        } else {
            ajaxUtils.fail().msg("未查询到相关申请信息");
        }
        return ajaxUtils;
    }

    /**
     * 删除申请记录
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/review/audit/delete", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils auditDelete(@RequestParam("internshipReleaseId") String internshipReleaseId, @RequestParam("studentId") int studentId, HttpServletRequest request) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        if (!ObjectUtils.isEmpty(internshipRelease)) {
            internshipApplyService.deleteInternshipApplyRecord(internshipRelease.getInternshipTypeId(), internshipReleaseId, studentId);
            internshipApplyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
            internshipChangeHistoryService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
            internshipChangeCompanyHistoryService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
            internshipTeacherDistributionService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);

            Optional<Record> userRecord = studentService.findByIdRelation(studentId);
            if (userRecord.isPresent()) {
                Users users = userRecord.get().into(Users.class);
                Users curUsers = usersService.getUserFromSession();
                String notify = "您的自主实习可能存在问题，已被管理员删除此次申请，若您有任何疑问，请联系管理员";
                commonControllerMethodService.sendNotify(users, curUsers, "实习申请被删除", notify, request);
            }
            ajaxUtils.success().msg("删除申请成功");
        } else {
            ajaxUtils.fail().msg("未查询到相关实习信息");
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
                Optional<Record> graduationPracticeCollegeRecord = graduationPracticeCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                if (graduationPracticeCollegeRecord.isPresent()) {
                    GraduationPracticeCollege graduationPracticeCollege = graduationPracticeCollegeRecord.get().into(GraduationPracticeCollege.class);
                    internshipReviewBean.setHasController(true);
                    internshipReviewBean.setCommitmentBook(graduationPracticeCollege.getCommitmentBook());
                    internshipReviewBean.setSafetyResponsibilityBook(graduationPracticeCollege.getSafetyResponsibilityBook());
                    internshipReviewBean.setPracticeAgreement(graduationPracticeCollege.getPracticeAgreement());
                    internshipReviewBean.setInternshipApplication(graduationPracticeCollege.getInternshipApplication());
                    internshipReviewBean.setPracticeReceiving(graduationPracticeCollege.getPracticeReceiving());
                    internshipReviewBean.setSecurityEducationAgreement(graduationPracticeCollege.getSecurityEducationAgreement());
                    internshipReviewBean.setParentalConsent(graduationPracticeCollege.getParentalConsent());
                }
                break;
            case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                Optional<Record> graduationPracticeUnifyRecord = graduationPracticeUnifyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                if (graduationPracticeUnifyRecord.isPresent()) {
                    GraduationPracticeUnify graduationPracticeUnify = graduationPracticeUnifyRecord.get().into(GraduationPracticeUnify.class);
                    internshipReviewBean.setHasController(true);
                    internshipReviewBean.setCommitmentBook(graduationPracticeUnify.getCommitmentBook());
                    internshipReviewBean.setSafetyResponsibilityBook(graduationPracticeUnify.getSafetyResponsibilityBook());
                    internshipReviewBean.setPracticeAgreement(graduationPracticeUnify.getPracticeAgreement());
                    internshipReviewBean.setInternshipApplication(graduationPracticeUnify.getInternshipApplication());
                    internshipReviewBean.setPracticeReceiving(graduationPracticeUnify.getPracticeReceiving());
                    internshipReviewBean.setSecurityEducationAgreement(graduationPracticeUnify.getSecurityEducationAgreement());
                    internshipReviewBean.setParentalConsent(graduationPracticeUnify.getParentalConsent());
                }
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
    @RequestMapping(value = "/anyone/internship/sciences", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Science> auditSciences(@RequestParam("internshipReleaseId") String internshipReleaseId) {
        AjaxUtils<Science> ajaxUtils = AjaxUtils.of();
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
    @RequestMapping(value = "/anyone/internship/organizes", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Organize> auditOrganizes(@RequestParam("scienceId") int scienceId) {
        AjaxUtils<Organize> ajaxUtils = AjaxUtils.of();
        List<Organize> organizes = new ArrayList<>();
        Organize organize = new Organize();
        organize.setOrganizeId(0);
        organize.setOrganizeName("请选择班级");
        organizes.add(organize);
        organizes.addAll(organizeService.findByScienceId(scienceId));
        return ajaxUtils.success().msg("获取班级数据成功").listData(organizes);
    }
}
