package top.zbeboy.isy.web.internship.review;

import org.apache.commons.lang3.BooleanUtils;
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
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
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
    private InternshipReviewService internshipReviewService;

    @Resource
    private InternshipTypeService internshipTypeService;

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

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
     * 查询申请中的学生数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/review/audit/data", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils auditDatas(PaginationUtils paginationUtils) {
        InternshipApplyBean internshipApplyBean = new InternshipApplyBean();
        internshipApplyBean.setInternshipApplyState(1);
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
                }
                break;
        }
        return internshipReviewBean;
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
