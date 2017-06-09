package top.zbeboy.isy.web.internship.regulate;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.data.DepartmentService;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.export.InternshipRegulateExport;
import top.zbeboy.isy.service.internship.InternshipRegulateService;
import top.zbeboy.isy.service.internship.InternshipReleaseService;
import top.zbeboy.isy.service.internship.InternshipTeacherDistributionService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.data.department.DepartmentBean;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.export.ExportBean;
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.internship.regulate.InternshipRegulateVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.*;

/**
 * Created by zbeboy on 2016/12/21.
 */
@Slf4j
@Controller
public class InternshipRegulateController {

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

    @Resource
    private UsersService usersService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private RoleService roleService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private InternshipRegulateService internshipRegulateService;

    @Resource
    private StaffService staffService;

    @Resource
    private StudentService studentService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private UploadService uploadService;

    /**
     * 实习监管
     *
     * @return 实习监管页面
     */
    @RequestMapping(value = "/web/menu/internship/regulate", method = RequestMethod.GET)
    public String internshipRegulate() {
        return "web/internship/regulate/internship_regulate::#page-wrapper";
    }

    /**
     * 实习监管列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/regulate/list", method = RequestMethod.GET)
    public String regulateList(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        Users users = usersService.getUserFromSession();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Staff staff = staffService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(staff)) {
                modelMap.addAttribute("staffId", staff.getStaffId());
            } else {
                modelMap.addAttribute("staffId", null);
            }
        }
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
        }
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "web/internship/regulate/internship_regulate_list::#page-wrapper";
    }

    /**
     * 我的监管记录列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/regulate/my/list", method = RequestMethod.GET)
    public String myRegulateList(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page;
        boolean canUse = false;
        Users users = usersService.getUserFromSession();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Staff staff = staffService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(staff)) {
                ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, staff.getStaffId());
                canUse = !errorBean.isHasError();
                modelMap.addAttribute("staffId", staff.getStaffId());
                modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            }
        }
        if (canUse) {
            page = "web/internship/regulate/internship_regulate_my::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 监管列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/regulate/list/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipRegulateBean> listData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("student_name");
        headers.add("student_number");
        headers.add("student_tel");
        headers.add("school_guidance_teacher");
        headers.add("create_date");
        headers.add("operator");
        DataTablesUtils<InternshipRegulateBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        InternshipRegulateBean otherCondition = new InternshipRegulateBean();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            otherCondition.setInternshipReleaseId(internshipReleaseId);
            String staffId = request.getParameter("staffId");
            if (StringUtils.hasLength(staffId)) {
                if (NumberUtils.isDigits(staffId)) {
                    otherCondition.setStaffId(NumberUtils.toInt(staffId));
                }
            }
            Result<Record> records = internshipRegulateService.findAllByPage(dataTablesUtils, otherCondition);
            List<InternshipRegulateBean> internshipRegulateBeans = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                internshipRegulateBeans = records.into(InternshipRegulateBean.class);
                internshipRegulateBeans.forEach(i -> i.setCreateDateStr(DateTimeUtils.formatDate(i.getCreateDate())));
            }
            dataTablesUtils.setData(internshipRegulateBeans);
            dataTablesUtils.setiTotalRecords(internshipRegulateService.countAll(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(internshipRegulateService.countByCondition(dataTablesUtils, otherCondition));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 导出 监管列表 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = "/web/internship/regulate/list/data/export", method = RequestMethod.GET)
    public void dataExport(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = "实习指导教师监管记录表";
            String ext = Workbook.XLSX_FILE;
            ExportBean exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean.class);

            String extraSearchParam = request.getParameter("extra_search");
            DataTablesUtils<InternshipRegulateBean> dataTablesUtils = DataTablesUtils.of();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(extraSearchParam)) {
                dataTablesUtils.setSearch(JSON.parseObject(extraSearchParam));
            }
            InternshipRegulateBean otherCondition = new InternshipRegulateBean();
            String internshipReleaseId = request.getParameter("internshipReleaseId");
            if (!ObjectUtils.isEmpty(internshipReleaseId)) {
                otherCondition.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
                Result<Record> records = internshipRegulateService.exportData(dataTablesUtils, otherCondition);
                List<InternshipRegulateBean> internshipRegulateBeens = new ArrayList<>();
                if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                    internshipRegulateBeens = records.into(InternshipRegulateBean.class);
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.getFileName())) {
                    fileName = exportBean.getFileName();
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.getExt())) {
                    ext = exportBean.getExt();
                }
                InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
                if (!ObjectUtils.isEmpty(internshipRelease)) {
                    Optional<Record> record = departmentService.findByIdRelation(internshipRelease.getDepartmentId());
                    if (record.isPresent()) {
                        DepartmentBean departmentBean = record.get().into(DepartmentBean.class);
                        InternshipRegulateExport export = new InternshipRegulateExport(internshipRegulateBeens);
                        String path = Workbook.internshipPath(departmentBean.getSchoolName(), departmentBean.getCollegeName(), departmentBean.getDepartmentName()) + fileName + "." + ext;
                        export.exportExcel(RequestUtils.getRealPath(request) + Workbook.internshipPath(departmentBean.getSchoolName(), departmentBean.getCollegeName(), departmentBean.getDepartmentName()), fileName, ext);
                        uploadService.download(fileName, "/" + path, response, request);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Export file error, error is {}", e);
        }
    }

    /**
     * 实习监管添加
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教职工id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/regulate/list/add", method = RequestMethod.GET)
    public String regulateListAdd(@RequestParam("id") String internshipReleaseId, @RequestParam("staffId") int staffId, ModelMap modelMap) {
        String page;
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, staffId);
        if (!errorBean.isHasError()) {
            InternshipRegulate internshipRegulate = new InternshipRegulate();
            internshipRegulate.setInternshipReleaseId(internshipReleaseId);
            internshipRegulate.setStaffId(staffId);
            modelMap.addAttribute("internshipRegulate", internshipRegulate);
            page = "web/internship/regulate/internship_regulate_add::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 编辑监管记录
     *
     * @param internshipRegulateId 监管id
     * @param modelMap             页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/regulate/list/edit", method = RequestMethod.GET)
    public String regulateListEdit(@RequestParam("id") String internshipRegulateId, ModelMap modelMap) {
        String page;
        InternshipRegulate internshipRegulate = internshipRegulateService.findById(internshipRegulateId);
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipRegulate.getInternshipReleaseId(), internshipRegulate.getStaffId());
        if (!errorBean.isHasError()) {
            if (!ObjectUtils.isEmpty(internshipRegulate)) {
                modelMap.addAttribute("internshipRegulate", internshipRegulate);
                page = "web/internship/regulate/internship_regulate_edit::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未查询到相关监管信息");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 查看实习监管页面
     *
     * @param id       实习监管id
     * @param modelMap 页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/regulate/list/look", method = RequestMethod.GET)
    public String regulateListLook(@RequestParam("id") String id, ModelMap modelMap) {
        String page;
        InternshipRegulate internshipRegulate = internshipRegulateService.findById(id);
        if (!ObjectUtils.isEmpty(internshipRegulate)) {
            modelMap.addAttribute("internshipRegulateDate", DateTimeUtils.formatDate(internshipRegulate.getReportDate(), "yyyy年MM月dd日"));
            modelMap.addAttribute("internshipRegulate", internshipRegulate);
            page = "web/internship/regulate/internship_regulate_look::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "未查询到相关监管信息");
        }
        return page;
    }

    /**
     * 批量删除监管记录
     *
     * @param regulateIds ids
     * @param staffId     教职工id
     * @return true 删除成功
     */
    @RequestMapping(value = "/web/internship/regulate/list/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils regulateListDel(String regulateIds, @RequestParam("staffId") int staffId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        // 强制身份判断
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                Users users = usersService.getUserFromSession();
                Staff staff = staffService.findByUsername(users.getUsername());
                if (!ObjectUtils.isEmpty(staff) && staff.getStaffId() != staffId) {
                    ajaxUtils.fail().msg("您的个人信息可能有误");
                    return ajaxUtils;
                }
            }

            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                ajaxUtils.fail().msg("您的注册类型不能删除此记录");
                return ajaxUtils;
            }
        }
        if (StringUtils.hasLength(regulateIds)) {
            List<String> ids = SmallPropsUtils.StringIdsToStringList(regulateIds);
            internshipRegulateService.batchDelete(ids);
            return ajaxUtils.success().msg("删除监管记录成功");
        }
        return ajaxUtils.fail().msg("删除监管记录失败");
    }

    /**
     * 获取该指导教师下的学生
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教师id
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/regulate/students", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<StudentBean> regulateStudents(@RequestParam("internshipReleaseId") String internshipReleaseId, @RequestParam("staffId") int staffId) {
        AjaxUtils<StudentBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, staffId);
        if (!errorBean.isHasError()) {
            List<StudentBean> studentBeens = new ArrayList<>();
            Result<Record> records = internshipTeacherDistributionService.findByInternshipReleaseIdAndStaffIdForStudent(internshipReleaseId, staffId);
            if (records.isNotEmpty()) {
                studentBeens = records.into(StudentBean.class);
            }
            ajaxUtils.success().msg("获取学生数据成功").listData(studentBeens);
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }

        return ajaxUtils;
    }

    /**
     * 获取学生信息
     *
     * @param studentId 学生id
     * @return 学生信息
     */
    @RequestMapping(value = "/web/internship/regulate/student/info", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<StudentBean> studentInfo(@RequestParam("studentId") int studentId) {
        AjaxUtils<StudentBean> ajaxUtils = AjaxUtils.of();
        Optional<Record> record = studentService.findByIdRelationForUsers(studentId);
        if (record.isPresent()) {
            StudentBean studentBean = record.get().into(StudentBean.class);
            studentBean.setPassword("");
            studentBean.setMailboxVerifyCode("");
            studentBean.setPasswordResetKey("");
            ajaxUtils.success().msg("获取学生信息成功").obj(studentBean);
        } else {
            ajaxUtils.fail().msg("未查询到该用户信息");
        }
        return ajaxUtils;
    }

    /**
     * 保存实习监管
     *
     * @param internshipRegulateVo 数据
     * @param bindingResult        检验
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/regulate/my/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils regulateSave(@Valid InternshipRegulateVo internshipRegulateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<InternshipRelease> errorBean = accessCondition(internshipRegulateVo.getInternshipReleaseId(), internshipRegulateVo.getStaffId());
            if (!errorBean.isHasError()) {
                Optional<Record> studentRecord = studentService.findByIdRelationForUsers(internshipRegulateVo.getStudentId());
                Optional<Record> staffRecord = staffService.findByIdRelationForUsers(internshipRegulateVo.getStaffId());
                if (studentRecord.isPresent() && staffRecord.isPresent()) {
                    StudentBean studentBean = studentRecord.get().into(StudentBean.class);
                    StaffBean staffBean = staffRecord.get().into(StaffBean.class);
                    InternshipRegulate internshipRegulate = new InternshipRegulate();
                    internshipRegulate.setInternshipRegulateId(UUIDUtils.getUUID());
                    internshipRegulate.setStudentName(studentBean.getRealName());
                    internshipRegulate.setStudentNumber(studentBean.getStudentNumber());
                    internshipRegulate.setStudentTel(studentBean.getMobile());
                    internshipRegulate.setInternshipContent(internshipRegulateVo.getInternshipContent());
                    internshipRegulate.setInternshipProgress(internshipRegulateVo.getInternshipProgress());
                    internshipRegulate.setReportWay(internshipRegulateVo.getReportWay());
                    internshipRegulate.setReportDate(internshipRegulateVo.getReportDate());
                    internshipRegulate.setSchoolGuidanceTeacher(staffBean.getRealName());
                    internshipRegulate.setTliy(internshipRegulateVo.getTliy());
                    internshipRegulate.setCreateDate(new Timestamp(Clock.systemDefaultZone().millis()));
                    internshipRegulate.setStudentId(internshipRegulateVo.getStudentId());
                    internshipRegulate.setInternshipReleaseId(internshipRegulateVo.getInternshipReleaseId());
                    internshipRegulate.setStaffId(internshipRegulateVo.getStaffId());

                    internshipRegulateService.save(internshipRegulate);
                    ajaxUtils.success().msg("保存成功");
                } else {
                    ajaxUtils.fail().msg(errorBean.getErrorMsg());
                }
            } else {
                ajaxUtils.fail().msg("未查询到相关用户信息");
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 更新监管记录
     *
     * @param internshipRegulateVo 数据
     * @param bindingResult        检验
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/regulate/my/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils regulateUpdate(@Valid InternshipRegulateVo internshipRegulateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(internshipRegulateVo.getInternshipRegulateId())) {
            ErrorBean<InternshipRelease> errorBean = accessCondition(internshipRegulateVo.getInternshipReleaseId(), internshipRegulateVo.getStaffId());
            if (!errorBean.isHasError()) {
                InternshipRegulate internshipRegulate = internshipRegulateService.findById(internshipRegulateVo.getInternshipRegulateId());
                internshipRegulate.setInternshipContent(internshipRegulateVo.getInternshipContent());
                internshipRegulate.setInternshipProgress(internshipRegulateVo.getInternshipProgress());
                internshipRegulate.setReportWay(internshipRegulateVo.getReportWay());
                internshipRegulate.setReportDate(internshipRegulateVo.getReportDate());
                internshipRegulate.setTliy(internshipRegulateVo.getTliy());
                internshipRegulateService.update(internshipRegulate);
                ajaxUtils.success().msg("更新成功");
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 检验教职工
     *
     * @param info                教职工信息
     * @param internshipReleaseId 实习发布id
     * @param type                检验类型
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/regulate/valid/staff", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validStaff(@RequestParam("staff") String info, @RequestParam("internshipReleaseId") String internshipReleaseId, int type) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        Staff staff = null;
        if (type == 0) {
            staff = staffService.findByUsername(info);
        } else if (type == 1) {
            staff = staffService.findByStaffNumber(info);
        }
        if (!ObjectUtils.isEmpty(staff)) {
            ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, staff.getStaffId());
            if (!errorBean.isHasError()) {
                ajaxUtils.success().msg("查询教职工数据成功").obj(staff.getStaffId());
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }

        } else {
            ajaxUtils.fail().msg("查询教职工数据失败");
        }
        return ajaxUtils;
    }

    /**
     * 我的监管 进入条件
     *
     * @param internshipReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/regulate/my/list/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myRegulateListCondition(@RequestParam("id") String internshipReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        Users users = usersService.getUserFromSession();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Staff staff = staffService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(staff)) {
                ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, staff.getStaffId());
                if (!errorBean.isHasError()) {
                    ajaxUtils.success().msg("在条件范围，允许进入");
                } else {
                    ajaxUtils.fail().msg(errorBean.getErrorMsg());
                }
            } else {
                ajaxUtils.fail().msg("未查询到相关教职工信息");
            }
        } else {
            ajaxUtils.fail().msg("您的注册类型不符合进入条件");
        }
        return ajaxUtils;
    }

    /**
     * 进入实习监管入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private ErrorBean<InternshipRelease> accessCondition(String internshipReleaseId, int staffId) {
        ErrorBean<InternshipRelease> errorBean = ErrorBean.of();
        // 强制身份判断
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                Users users = usersService.getUserFromSession();
                Staff staff = staffService.findByUsername(users.getUsername());
                if (!ObjectUtils.isEmpty(staff) && staff.getStaffId() != staffId) {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("您的个人信息有误");
                    return errorBean;
                }
            }

            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("您的注册类型不适用于监管");
                return errorBean;
            }
        }
        Map<String, Object> mapData = new HashMap<>();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        if (ObjectUtils.isEmpty(internshipRelease)) {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关实习信息");
            return errorBean;
        }
        errorBean.setData(internshipRelease);
        if (internshipRelease.getInternshipReleaseIsDel() == 1) {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("该实习已被注销");
            return errorBean;
        }
        Result<InternshipTeacherDistributionRecord> internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStaffId(internshipReleaseId, staffId);
        if (internshipTeacherDistributionRecord.isNotEmpty()) {
            errorBean.setHasError(false);
            mapData.put("internshipTeacherDistribution", internshipTeacherDistributionRecord.into(InternshipTeacherDistribution.class));
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("您不是该实习指导教师");
        }
        errorBean.setMapData(mapData);
        return errorBean;
    }
}
