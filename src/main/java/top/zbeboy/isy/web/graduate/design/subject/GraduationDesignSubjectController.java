package top.zbeboy.isy.web.graduate.design.subject;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.jooq.Record1;
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
import top.zbeboy.isy.domain.tables.records.GraduationDesignDeclareRecord;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.data.DepartmentService;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.export.GraduationDesignDeclareExport;
import top.zbeboy.isy.service.graduate.design.*;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.data.department.DepartmentBean;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.export.ExportBean;
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean;
import top.zbeboy.isy.web.bean.graduate.design.subject.GraduationDesignPresubjectBean;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.graduate.design.subject.GraduationDesignDeclareUpdateVo;
import top.zbeboy.isy.web.vo.graduate.design.subject.GraduationDesignPresubjectAddVo;
import top.zbeboy.isy.web.vo.graduate.design.subject.GraduationDesignPresubjectUpdateVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/6/5.
 */
@Slf4j
@Controller
public class GraduationDesignSubjectController {

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignPresubjectService graduationDesignPresubjectService;

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private GraduationDesignTutorService graduationDesignTutorService;

    @Resource
    private GraduationDesignDeclareDataService graduationDesignDeclareDataService;

    @Resource
    private GraduationDesignDeclareService graduationDesignDeclareService;

    @Resource
    private GraduationDesignSubjectTypeService graduationDesignSubjectTypeService;

    @Resource
    private GraduationDesignSubjectOriginTypeService graduationDesignSubjectOriginTypeService;

    @Resource
    private UsersService usersService;

    @Resource
    private StudentService studentService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private CacheManageService cacheManageService;

    @Resource
    private RoleService roleService;

    @Resource
    private StaffService staffService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private UploadService uploadService;

    /**
     * 毕业设计题目
     *
     * @return 毕业设计题目页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/subject", method = RequestMethod.GET)
    public String subject() {
        return "web/graduate/design/subject/design_subject::#page-wrapper";
    }

    /**
     * 列表
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/list", method = RequestMethod.GET)
    public String list(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            Users users = usersService.getUserFromSession();
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Student student = studentService.findByUsername(users.getUsername());
                if (!ObjectUtils.isEmpty(student)) {
                    modelMap.addAttribute("studentId", student.getStudentId());
                }
            }
            UsersType usersType = cacheManageService.findByUsersTypeId(users.getUsersTypeId());
            modelMap.addAttribute("usersTypeName", usersType.getUsersTypeName());
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
                modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
            }
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("endTime", DateTimeUtils.formatDate(graduationDesignRelease.getEndTime()));
            page = "web/graduate/design/subject/design_subject_list::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/subject/list/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignPresubjectBean> listData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("presubject_title");
        headers.add("student_name");
        headers.add("student_number");
        headers.add("organize_name");
        headers.add("update_time");
        headers.add("operator");
        DataTablesUtils<GraduationDesignPresubjectBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        GraduationDesignPresubjectBean otherCondition = new GraduationDesignPresubjectBean();
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
            Result<Record> records = graduationDesignPresubjectService.findAllByPage(dataTablesUtils, otherCondition);
            List<GraduationDesignPresubjectBean> graduationDesignPresubjectBeens = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                graduationDesignPresubjectBeens = records.into(GraduationDesignPresubjectBean.class);
                graduationDesignPresubjectBeens.forEach(i -> {
                            i.setUpdateTimeStr(DateTimeUtils.formatDate(i.getUpdateTime()));
                            i.setPresubjectPlan("");
                        }
                );
            }
            dataTablesUtils.setData(graduationDesignPresubjectBeens);
            dataTablesUtils.setITotalRecords(graduationDesignPresubjectService.countAll(otherCondition));
            dataTablesUtils.setITotalDisplayRecords(graduationDesignPresubjectService.countByCondition(dataTablesUtils, otherCondition));
        }
        return dataTablesUtils;
    }

    /**
     * 申报数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignDeclareBean> declareData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    // 前台数据标题 注：要和前台标题顺序一致，获取order用
                    List<String> headers = new ArrayList<>();
                    headers.add("select");
                    headers.add("presubject_title");
                    headers.add("subject_type_name");
                    headers.add("origin_type_name");
                    headers.add("is_new_subject");
                    headers.add("is_new_teacher_make");
                    headers.add("is_new_subject_make");
                    headers.add("is_old_subject_change");
                    headers.add("old_subject_uses_times");
                    headers.add("plan_period");
                    headers.add("guide_teacher");
                    headers.add("academic_title_name");
                    headers.add("assistant_teacher");
                    headers.add("assistant_teacher_academic");
                    headers.add("guide_times");
                    headers.add("guide_peoples");
                    headers.add("student_number");
                    headers.add("student_name");
                    headers.add("organize_name");
                    headers.add("is_ok_apply");
                    headers.add("operator");
                    dataTablesUtils = new DataTablesUtils<>(request, headers);
                    GraduationDesignDeclareBean otherCondition = new GraduationDesignDeclareBean();
                    int staffId = NumberUtils.toInt(request.getParameter("staffId"));
                    otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
                    otherCondition.setStaffId(staffId);
                    List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = graduationDesignDeclareService.findAllByPage(dataTablesUtils, otherCondition);
                    dataTablesUtils.setData(graduationDesignDeclareBeens);
                    dataTablesUtils.setITotalRecords(graduationDesignDeclareService.countAll(otherCondition));
                    dataTablesUtils.setITotalDisplayRecords(graduationDesignDeclareService.countByCondition(dataTablesUtils, otherCondition));
                }
            }
        }
        return dataTablesUtils;
    }

    /**
     * 导出 申报 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/data/export", method = RequestMethod.GET)
    public void declareDataExport(HttpServletRequest request, HttpServletResponse response) {
        try {
            String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
            int staffId = NumberUtils.toInt(request.getParameter("staffId"));
            if (!ObjectUtils.isEmpty(graduationDesignReleaseId) && staffId > 0) {
                Optional<Record> staffRecord = staffService.findByIdRelation(staffId);
                ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
                if (!errorBean.isHasError() && staffRecord.isPresent()) {
                    Users users = staffRecord.get().into(Users.class);
                    GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                    // 是否已确认调整
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                        GraduationDesignDeclareData graduationDesignDeclareData = graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
                        if (!ObjectUtils.isEmpty(graduationDesignDeclareData)) {
                            String year = graduationDesignDeclareData.getGraduationDate().substring(0, graduationDesignDeclareData.getGraduationDate().indexOf("年"));
                            String fileName = users.getRealName() + "_ " + year + "届毕业设计（论文）题目申报表";
                            String ext = Workbook.XLSX_FILE;
                            ExportBean exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean.class);

                            String extraSearchParam = request.getParameter("extra_search");
                            DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils = DataTablesUtils.of();
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(extraSearchParam)) {
                                dataTablesUtils.setSearch(JSON.parseObject(extraSearchParam));
                            }
                            GraduationDesignDeclareBean otherCondition = new GraduationDesignDeclareBean();
                            otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
                            otherCondition.setStaffId(staffId);
                            List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = graduationDesignDeclareService.exportData(dataTablesUtils, otherCondition);
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.getFileName())) {
                                fileName = exportBean.getFileName();
                            }
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.getExt())) {
                                ext = exportBean.getExt();
                            }
                            if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
                                int peoples = graduationDesignTutorService.countByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staffId);
                                Optional<Record> record = departmentService.findByIdRelation(graduationDesignRelease.getDepartmentId());
                                if (record.isPresent()) {
                                    DepartmentBean departmentBean = record.get().into(DepartmentBean.class);
                                    GraduationDesignDeclareExport export = new GraduationDesignDeclareExport(graduationDesignDeclareBeens, graduationDesignDeclareData, peoples, year);
                                    String schoolInfoPath = departmentBean.getSchoolName() + "/" + departmentBean.getCollegeName() + "/" + departmentBean.getDepartmentName() + "/";
                                    String path = Workbook.graduateDesignPath(schoolInfoPath) + fileName + "." + ext;
                                    export.exportExcel(RequestUtils.getRealPath(request) + Workbook.graduateDesignPath(schoolInfoPath), fileName, ext);
                                    uploadService.download(fileName, "/" + path, response, request);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Export file error, error is {}", e);
        }
    }

    /**
     * 查看
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/list/look", method = RequestMethod.GET)
    public String look(@RequestParam("id") String graduationDesignReleaseId,
                       @RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            boolean canLook = false;
            Optional<Record> graduationDesignPresubjectRecord = graduationDesignPresubjectService.findByIdRelation(graduationDesignPresubjectId);
            if (graduationDesignPresubjectRecord.isPresent()) {
                GraduationDesignPresubjectBean graduationDesignPresubject = graduationDesignPresubjectRecord.get().into(GraduationDesignPresubjectBean.class);
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                Users users = usersService.getUserFromSession();
                if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                    canLook = true;
                } else {
                    if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                        Student student = studentService.findByUsername(users.getUsername());
                        if (Objects.equals(graduationDesignPresubject.getStudentId(), student.getStudentId())) {
                            canLook = true;
                        }
                    }
                    if (!canLook) {
                        // 仅允许教职工查看
                        if (graduationDesignPresubject.getPublicLevel() == 1) {
                            UsersType usersType = cacheManageService.findByUsersTypeId(users.getUsersTypeId());
                            if (usersType.getUsersTypeName().equals(Workbook.STAFF_USERS_TYPE)) {
                                canLook = true;
                            }
                        } else if (graduationDesignPresubject.getPublicLevel() == 2) {
                            // 毕业设计结束后允许任何人查看，结束前仅允许教职工及管理员查看(半公开)
                            if (DateTimeUtils.timestampAfterDecide(graduationDesignRelease.getEndTime())) {
                                canLook = true;
                            }
                        } else if (graduationDesignPresubject.getPublicLevel() == 3) {
                            canLook = true;
                        }
                    }
                }

                if (canLook) {
                    graduationDesignPresubject.setUpdateTimeStr(DateTimeUtils.formatDate(graduationDesignPresubject.getUpdateTime()));
                    modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject);
                    modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                    page = "web/graduate/design/subject/design_subject_look::#page-wrapper";
                } else {
                    page = commonControllerMethodService.showTip(modelMap, "您不符合查看条件");
                }
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未查询到相关毕业设计题目信息");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 编辑
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/list/edit", method = RequestMethod.GET)
    public String edit(@RequestParam("id") String graduationDesignReleaseId,
                       @RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                GraduationDesignPresubject graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectId);
                if (listAndMyCanEditCondition(graduationDesignPresubject)) {
                    modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                    modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject);
                    page = "web/graduate/design/subject/design_subject_my_edit::#page-wrapper";
                } else {
                    page = commonControllerMethodService.showTip(modelMap, "您不符合编辑条件");
                }
            } else {
                page = commonControllerMethodService.showTip(modelMap, "不在毕业时间范围，无法操作");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 我的题目
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/my", method = RequestMethod.GET)
    public String my(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            Users users = usersService.getUserFromSession();
            Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
            if (studentRecord.isPresent()) {
                Student student = studentRecord.get().into(Student.class);
                GraduationDesignPresubjectRecord record = graduationDesignPresubjectService.findByStudentIdAndGraduationDesignReleaseId(student.getStudentId(), graduationDesignReleaseId);
                GraduationDesignPresubjectBean graduationDesignPresubject = new GraduationDesignPresubjectBean();
                if (!ObjectUtils.isEmpty(record)) {
                    graduationDesignPresubject = record.into(GraduationDesignPresubjectBean.class);
                    graduationDesignPresubject.setUpdateTimeStr(DateTimeUtils.formatDate(graduationDesignPresubject.getUpdateTime()));
                }
                modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject);
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                page = "web/graduate/design/subject/design_subject_my::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "您可能不是学生用户或不符合进入条件");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 题目申报
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare", method = RequestMethod.GET)
    public String declare(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                Users users = usersService.getUserFromSession();
                boolean hasValue = false;
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                    if (studentRecord.isPresent()) {
                        Student student = studentRecord.get().into(Student.class);
                        if (!ObjectUtils.isEmpty(student)) {
                            Optional<Record> staffRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.getStudentId(), graduationDesignReleaseId);
                            if (staffRecord.isPresent()) {
                                GraduationDesignTeacher graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher.class);
                                modelMap.addAttribute("studentId", student.getStudentId());
                                modelMap.addAttribute("staffId", graduationDesignTeacher.getStaffId());
                                hasValue = true;
                            }
                        }
                    }
                } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    Staff staff = staffService.findByUsername(users.getUsername());
                    if (!ObjectUtils.isEmpty(staff)) {
                        Optional<Record> staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                        if (staffRecord.isPresent()) {
                            modelMap.addAttribute("studentId", 0);
                            modelMap.addAttribute("staffId", staff.getStaffId());
                            hasValue = true;
                        }
                    }
                }

                if (!hasValue) {
                    modelMap.addAttribute("studentId", 0);
                    modelMap.addAttribute("staffId", 0);
                }

                UsersType usersType = cacheManageService.findByUsersTypeId(users.getUsersTypeId());
                modelMap.addAttribute("usersTypeName", usersType.getUsersTypeName());
                if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
                    modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
                } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                    modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
                }
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                modelMap.addAttribute("endTime", DateTimeUtils.formatDate(graduationDesignRelease.getEndTime()));
                page = "web/graduate/design/subject/design_subject_declare::#page-wrapper";

            } else {
                page = commonControllerMethodService.showTip(modelMap, "请等待确认指导教师调整后查看");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 修改题目
     *
     * @param graduationDesignReleaseId    发布id
     * @param graduationDesignPresubjectId 题目id
     * @param staffId                      教职工id
     * @param modelMap                     页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/edit/title", method = RequestMethod.GET)
    public String declareEditTtile(@RequestParam("id") String graduationDesignReleaseId,
                                   @RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId,
                                   @RequestParam("staffId") int staffId, ModelMap modelMap) {
        String page;
        GraduationDesignPresubject graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectId);
        if (declareUpdateTitleCondition(graduationDesignReleaseId, graduationDesignPresubject, staffId)) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject);
            modelMap.addAttribute("staffId", staffId);
            page = "web/graduate/design/subject/design_subject_declare_title::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 编辑申报项
     *
     * @param graduationDesignReleaseId    发布id
     * @param graduationDesignPresubjectId 题目id
     * @param modelMap                     页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/edit/apply", method = RequestMethod.GET)
    public String declareEditApply(@RequestParam("id") String graduationDesignReleaseId,
                                   @RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId, ModelMap modelMap) {
        String page;
        Optional<Record> graduationDesignPresubjectRecord = graduationDesignPresubjectService.findByIdRelation(graduationDesignPresubjectId);
        if (graduationDesignPresubjectRecord.isPresent()) {
            GraduationDesignPresubjectBean graduationDesignPresubject = graduationDesignPresubjectRecord.get().into(GraduationDesignPresubjectBean.class);
            Optional<Record> graduationDesignTutorRecord =
                    graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(graduationDesignPresubject.getStudentId(), graduationDesignReleaseId);
            if (graduationDesignTutorRecord.isPresent()) {
                GraduationDesignTutorBean graduationDesignTutorBean = graduationDesignTutorRecord.get().into(GraduationDesignTutorBean.class);
                int staffId = graduationDesignTutorBean.getStaffId();
                graduationDesignPresubject.setGraduationDesignPresubjectId(graduationDesignPresubjectId);
                if (updateApplyCondition(graduationDesignReleaseId, graduationDesignPresubject, staffId)) {
                    modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                    modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject);
                    modelMap.addAttribute("staffId", staffId);
                    page = "web/graduate/design/subject/design_subject_declare_apply::#page-wrapper";
                } else {
                    page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
                }
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未查询到指导教师信息");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, "未查询到相关题目信息");
        }
        return page;
    }

    /**
     * 教师数据
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    @RequestMapping(value = "/anyone/graduate/design/subject/teachers", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignTeacherBean> subjectTeachers(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils<GraduationDesignTeacherBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                List<GraduationDesignTeacherBean> graduationDesignTeacherBeens = new ArrayList<>();
                GraduationDesignTeacherBean graduationDesignTeacherBean = new GraduationDesignTeacherBean();
                graduationDesignTeacherBean.setStaffId(0);
                graduationDesignTeacherBean.setRealName("全部");
                graduationDesignTeacherBean.setStaffMobile("");
                graduationDesignTeacherBeens.add(graduationDesignTeacherBean);
                graduationDesignTeacherBeens.addAll(graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId));
                ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens);
            } else {
                ajaxUtils.fail().msg("请等待确认毕业设计指导教师调整后查看");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 申报基础信息
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/basic", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils declareBasic(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                GraduationDesignDeclareData graduationDesignDeclareData = graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
                ajaxUtils.success().msg("获取数据成功").obj(graduationDesignDeclareData);
            } else {
                ajaxUtils.fail().msg("请等待确认毕业设计指导教师调整后查看");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 获取指导人数
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/basic/peoples", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils declareBasicPeoples(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("staffId") int staffId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                int peoples = 0;
                if (staffId > 0) {
                    peoples = graduationDesignTutorService.countByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staffId);
                }
                ajaxUtils.success().msg("获取数据成功").obj(peoples);
            } else {
                ajaxUtils.fail().msg("请等待确认毕业设计指导教师调整后查看");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 获取题目类型
     *
     * @return 数据
     */
    @RequestMapping(value = "/user/graduate/design/subject/types", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignSubjectType> subjectTypes() {
        AjaxUtils<GraduationDesignSubjectType> ajaxUtils = AjaxUtils.of();
        List<GraduationDesignSubjectType> graduationDesignSubjectTypes = new ArrayList<>();
        GraduationDesignSubjectType graduationDesignSubjectType = new GraduationDesignSubjectType(0, "题目类型");
        graduationDesignSubjectTypes.add(graduationDesignSubjectType);
        graduationDesignSubjectTypes.addAll(graduationDesignSubjectTypeService.findAll());
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignSubjectTypes);
    }

    /**
     * 获取课题来源
     *
     * @return 数据
     */
    @RequestMapping(value = "/user/graduate/design/subject/origin_types", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignSubjectOriginType> subjectOriginTypes() {
        AjaxUtils<GraduationDesignSubjectOriginType> ajaxUtils = AjaxUtils.of();
        List<GraduationDesignSubjectOriginType> graduationDesignSubjectOriginTypes = new ArrayList<>();
        GraduationDesignSubjectOriginType graduationDesignSubjectOriginType = new GraduationDesignSubjectOriginType(0, "课题来源");
        graduationDesignSubjectOriginTypes.add(graduationDesignSubjectOriginType);
        graduationDesignSubjectOriginTypes.addAll(graduationDesignSubjectOriginTypeService.findAll());
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignSubjectOriginTypes);
    }

    /**
     * 编辑我的题目
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/edit", method = RequestMethod.GET)
    public String myEdit(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                Users users = usersService.getUserFromSession();
                Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                if (studentRecord.isPresent()) {
                    Student student = studentRecord.get().into(Student.class);
                    GraduationDesignPresubjectRecord record = graduationDesignPresubjectService.findByStudentIdAndGraduationDesignReleaseId(student.getStudentId(), graduationDesignReleaseId);
                    GraduationDesignPresubject graduationDesignPresubject;
                    if (!ObjectUtils.isEmpty(record)) {
                        graduationDesignPresubject = record.into(GraduationDesignPresubject.class);
                        GraduationDesignDeclareRecord graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignPresubject.getGraduationDesignPresubjectId());
                        if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.getIsOkApply()) || graduationDesignDeclare.getIsOkApply() != 1) {
                            page = "web/graduate/design/subject/design_subject_my_edit::#page-wrapper";
                        } else {
                            page = commonControllerMethodService.showTip(modelMap, "您的题目已确认申报，无法更改");
                        }
                    } else {
                        graduationDesignPresubject = new GraduationDesignPresubject();
                        page = "web/graduate/design/subject/design_subject_my_add::#page-wrapper";
                    }
                    modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                    modelMap.addAttribute("graduationDesignPresubject", graduationDesignPresubject);
                } else {
                    page = commonControllerMethodService.showTip(modelMap, "您可能不是学生用户或不符合进入条件");
                }
            } else {
                page = commonControllerMethodService.showTip(modelMap, "不在毕业时间范围，无法操作");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 保存时检验毕业设计题目
     *
     * @param presubjectTitle 题目
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/save/valid/title", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidTitle(@RequestParam("presubjectTitle") String presubjectTitle) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        List<GraduationDesignPresubject> graduationDesignPresubjects = graduationDesignPresubjectService.findByPresubjectTitle(StringUtils.trimAllWhitespace(presubjectTitle));
        if (!ObjectUtils.isEmpty(graduationDesignPresubjects) && graduationDesignPresubjects.size() > 0) {
            ajaxUtils.fail().msg("该题目已被使用");
        } else {
            ajaxUtils.success().msg("该题目未被使用");
        }
        return ajaxUtils;
    }

    /**
     * 编辑时检验毕业设计题目
     *
     * @param presubjectTitle 题目
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/update/valid/title", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidTitle(@RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId,
                                      @RequestParam("presubjectTitle") String presubjectTitle) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        Result<GraduationDesignPresubjectRecord> graduationDesignPresubjects = graduationDesignPresubjectService.findByPresubjectTitleNeId(StringUtils.trimAllWhitespace(presubjectTitle), graduationDesignPresubjectId);
        if (!ObjectUtils.isEmpty(graduationDesignPresubjects) && graduationDesignPresubjects.size() > 0) {
            ajaxUtils.fail().msg("该题目已被使用");
        } else {
            ajaxUtils.success().msg("该题目未被使用");
        }
        return ajaxUtils;
    }

    /**
     * 保存
     *
     * @param graduationDesignPresubjectAddVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils mySave(@Valid GraduationDesignPresubjectAddVo graduationDesignPresubjectAddVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignPresubjectAddVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    Users users = usersService.getUserFromSession();
                    Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                    if (studentRecord.isPresent()) {
                        Student student = studentRecord.get().into(Student.class);
                        GraduationDesignPresubject graduationDesignPresubject = new GraduationDesignPresubject();
                        graduationDesignPresubject.setGraduationDesignPresubjectId(UUIDUtils.getUUID());
                        graduationDesignPresubject.setStudentId(student.getStudentId());
                        graduationDesignPresubject.setGraduationDesignReleaseId(graduationDesignPresubjectAddVo.getGraduationDesignReleaseId());
                        graduationDesignPresubject.setPresubjectTitle(graduationDesignPresubjectAddVo.getPresubjectTitle());
                        graduationDesignPresubject.setPresubjectPlan(graduationDesignPresubjectAddVo.getPresubjectPlan());
                        graduationDesignPresubject.setPublicLevel(graduationDesignPresubjectAddVo.getPublicLevel());
                        graduationDesignPresubject.setUpdateTime(DateTimeUtils.getNow());
                        graduationDesignPresubjectService.save(graduationDesignPresubject);
                        ajaxUtils.success().msg("保存成功");
                    } else {
                        ajaxUtils.fail().msg("您可能不是学生用户或不符合进入条件");
                    }
                } else {
                    ajaxUtils.fail().msg("不在毕业时间范围，无法操作");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 更新
     *
     * @param graduationDesignPresubjectUpdateVo 数据
     * @param bindingResult                      检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myUpdate(@Valid GraduationDesignPresubjectUpdateVo graduationDesignPresubjectUpdateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignPresubjectUpdateVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    GraduationDesignPresubject graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectUpdateVo.getGraduationDesignPresubjectId());
                    if (!ObjectUtils.isEmpty(graduationDesignPresubject) && listAndMyCanEditCondition(graduationDesignPresubject)) {
                        updatePresubject(graduationDesignPresubjectUpdateVo, graduationDesignPresubject);
                        ajaxUtils.success().msg("保存成功");
                    } else {
                        ajaxUtils.fail().msg("您不允许编辑该题目");
                    }
                } else {
                    ajaxUtils.fail().msg("不在毕业时间范围，无法操作");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 修改题目
     *
     * @param graduationDesignPresubjectUpdateVo 数据
     * @param bindingResult                      检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/edit/title/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils declareUpdateTitle(@Valid GraduationDesignPresubjectUpdateVo graduationDesignPresubjectUpdateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            GraduationDesignPresubject graduationDesignPresubject = graduationDesignPresubjectService.findById(graduationDesignPresubjectUpdateVo.getGraduationDesignPresubjectId());
            if (!ObjectUtils.isEmpty(graduationDesignPresubject) && declareUpdateTitleCondition(graduationDesignPresubjectUpdateVo.getGraduationDesignReleaseId(), graduationDesignPresubject, graduationDesignPresubjectUpdateVo.getStaffId())) {
                updatePresubject(graduationDesignPresubjectUpdateVo, graduationDesignPresubject);
                ajaxUtils.success().msg("保存成功");
            } else {
                ajaxUtils.fail().msg("不符合编辑条件");
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 修改申报项
     *
     * @param graduationDesignDeclareUpdateVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/edit/apply/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateApply(@Valid GraduationDesignDeclareUpdateVo graduationDesignDeclareUpdateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignDeclareUpdateVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    // 是否已确认调整
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                        Users users = usersService.getUserFromSession();
                        GraduationDesignDeclareRecord graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignDeclareUpdateVo.getGraduationDesignPresubjectId());
                        // 未确认申报
                        if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.getIsOkApply()) || graduationDesignDeclare.getIsOkApply() != 1) {
                            // 如果是管理员或系统
                            boolean canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isCurrentStaff(users, graduationDesignDeclareUpdateVo.getGraduationDesignReleaseId(), graduationDesignDeclareUpdateVo.getStaffId());
                            if (canEdit) {
                                GraduationDesignDeclare tempGraduationDesignDeclare = new GraduationDesignDeclare();
                                tempGraduationDesignDeclare.setSubjectTypeId(graduationDesignDeclareUpdateVo.getSubjectTypeId());
                                tempGraduationDesignDeclare.setOriginTypeId(graduationDesignDeclareUpdateVo.getOriginTypeId());
                                tempGraduationDesignDeclare.setIsNewSubject(graduationDesignDeclareUpdateVo.getIsNewSubject());
                                tempGraduationDesignDeclare.setIsNewTeacherMake(graduationDesignDeclareUpdateVo.getIsNewTeacherMake());
                                tempGraduationDesignDeclare.setIsNewSubjectMake(graduationDesignDeclareUpdateVo.getIsNewSubjectMake());
                                tempGraduationDesignDeclare.setIsOldSubjectChange(graduationDesignDeclareUpdateVo.getIsOldSubjectChange());
                                tempGraduationDesignDeclare.setOldSubjectUsesTimes(graduationDesignDeclareUpdateVo.getOldSubjectUsesTimes());
                                tempGraduationDesignDeclare.setPlanPeriod(graduationDesignDeclareUpdateVo.getPlanPeriod());
                                tempGraduationDesignDeclare.setAssistantTeacher(graduationDesignDeclareUpdateVo.getAssistantTeacher());
                                tempGraduationDesignDeclare.setAssistantTeacherAcademic(graduationDesignDeclareUpdateVo.getAssistantTeacherAcademic());
                                tempGraduationDesignDeclare.setAssistantTeacherNumber(graduationDesignDeclareUpdateVo.getAssistantTeacherNumber());
                                tempGraduationDesignDeclare.setGuideTimes(graduationDesignDeclareUpdateVo.getGuideTimes());
                                tempGraduationDesignDeclare.setGuidePeoples(graduationDesignDeclareUpdateVo.getGuidePeoples());
                                tempGraduationDesignDeclare.setIsOkApply(graduationDesignDeclareUpdateVo.getIsOkApply());
                                tempGraduationDesignDeclare.setGraduationDesignPresubjectId(graduationDesignDeclareUpdateVo.getGraduationDesignPresubjectId());
                                graduationDesignDeclareService.saveOrUpdate(tempGraduationDesignDeclare);
                                ajaxUtils.success().msg("保存成功");
                            } else {
                                ajaxUtils.fail().msg("不符合条件，无法编辑");
                            }
                        } else {
                            ajaxUtils.fail().msg("已确认申报，无法编辑");
                        }
                    } else {
                        ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                    }
                } else {
                    ajaxUtils.fail().msg("不在毕业时间范围，无法操作");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }

        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 确认申报
     *
     * @param graduationDesignReleaseId     发布id
     * @param staffId                       教职工id
     * @param graduationDesignPresubjectIds 题目ids
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/apply/ok", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils okApply(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("staffId") int staffId,
                             String graduationDesignPresubjectIds) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    if (StringUtils.hasLength(graduationDesignPresubjectIds)) {
                        List<String> ids = SmallPropsUtils.StringIdsToStringList(graduationDesignPresubjectIds);
                        GraduationDesignDeclare tempGraduationDesignDeclare = new GraduationDesignDeclare();
                        Byte b = 1;
                        Users users = usersService.getUserFromSession();
                        // 如果是管理员或系统
                        boolean canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isCurrentStaff(users, graduationDesignReleaseId, staffId);
                        for (String id : ids) {
                            GraduationDesignDeclareRecord graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(id);
                            // 未确认申报
                            if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.getIsOkApply()) || graduationDesignDeclare.getIsOkApply() != 1) {
                                if (canEdit) {
                                    tempGraduationDesignDeclare.setIsOkApply(b);
                                    tempGraduationDesignDeclare.setGraduationDesignPresubjectId(id);
                                    graduationDesignDeclareService.saveOrUpdateState(tempGraduationDesignDeclare);
                                }
                            }
                        }
                    }
                    ajaxUtils.success().msg("确认成功");
                } else {
                    ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                }
            } else {
                ajaxUtils.fail().msg("不在毕业时间范围，无法操作");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }

        return ajaxUtils;
    }

    /**
     * 统一设置
     *
     * @param graduationDesignReleaseId 发布id
     * @param staffId                   教职工id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/edit/all", method = RequestMethod.GET)
    public String editAll(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("staffId") int staffId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    Result<Record1<String>> records = graduationDesignDeclareService.findByStaffIdRelationNeIsOkApply(staffId, graduationDesignReleaseId);
                    if (records.isNotEmpty()) {
                        Users users = usersService.getUserFromSession();
                        // 如果是管理员或系统
                        boolean canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isCurrentStaff(users, graduationDesignReleaseId, staffId);
                        if (canEdit) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (Record r : records) {
                                stringBuilder.append(r.get(0).toString()).append(",");
                            }
                            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                            modelMap.addAttribute("staffId", staffId);
                            modelMap.addAttribute("graduationDesignPresubjectIds", stringBuilder.substring(0, stringBuilder.lastIndexOf(",")));
                            page = "web/graduate/design/subject/design_subject_declare_all::#page-wrapper";
                        } else {
                            page = commonControllerMethodService.showTip(modelMap, "您不满足条件，无法进入");
                        }
                    } else {
                        page = commonControllerMethodService.showTip(modelMap, "未发现未申报的数据");
                    }
                } else {
                    page = commonControllerMethodService.showTip(modelMap, "未确认毕业设计指导教师调整");
                }
            } else {
                page = commonControllerMethodService.showTip(modelMap, "不在毕业时间范围，无法操作");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }

        return page;
    }

    /**
     * 统一设置更新
     *
     * @param graduationDesignDeclareUpdateVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/all/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateAll(@Valid GraduationDesignDeclareUpdateVo graduationDesignDeclareUpdateVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignDeclareUpdateVo.getGraduationDesignReleaseId());
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    // 是否已确认调整
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                        Users users = usersService.getUserFromSession();
                        // 如果是管理员或系统
                        boolean canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isCurrentStaff(users, graduationDesignDeclareUpdateVo.getGraduationDesignReleaseId(), graduationDesignDeclareUpdateVo.getStaffId());
                        if (canEdit) {
                            List<String> ids = SmallPropsUtils.StringIdsToStringList(graduationDesignDeclareUpdateVo.getGraduationDesignPresubjectId());
                            for (String id : ids) {
                                GraduationDesignDeclare tempGraduationDesignDeclare = new GraduationDesignDeclare();
                                tempGraduationDesignDeclare.setSubjectTypeId(graduationDesignDeclareUpdateVo.getSubjectTypeId());
                                tempGraduationDesignDeclare.setOriginTypeId(graduationDesignDeclareUpdateVo.getOriginTypeId());
                                tempGraduationDesignDeclare.setIsNewSubject(graduationDesignDeclareUpdateVo.getIsNewSubject());
                                tempGraduationDesignDeclare.setIsNewTeacherMake(graduationDesignDeclareUpdateVo.getIsNewTeacherMake());
                                tempGraduationDesignDeclare.setIsNewSubjectMake(graduationDesignDeclareUpdateVo.getIsNewSubjectMake());
                                tempGraduationDesignDeclare.setIsOldSubjectChange(graduationDesignDeclareUpdateVo.getIsOldSubjectChange());
                                tempGraduationDesignDeclare.setOldSubjectUsesTimes(graduationDesignDeclareUpdateVo.getOldSubjectUsesTimes());
                                tempGraduationDesignDeclare.setPlanPeriod(graduationDesignDeclareUpdateVo.getPlanPeriod());
                                tempGraduationDesignDeclare.setAssistantTeacher(graduationDesignDeclareUpdateVo.getAssistantTeacher());
                                tempGraduationDesignDeclare.setAssistantTeacherAcademic(graduationDesignDeclareUpdateVo.getAssistantTeacherAcademic());
                                tempGraduationDesignDeclare.setAssistantTeacherNumber(graduationDesignDeclareUpdateVo.getAssistantTeacherNumber());
                                tempGraduationDesignDeclare.setGuideTimes(graduationDesignDeclareUpdateVo.getGuideTimes());
                                tempGraduationDesignDeclare.setGuidePeoples(graduationDesignDeclareUpdateVo.getGuidePeoples());
                                tempGraduationDesignDeclare.setIsOkApply(graduationDesignDeclareUpdateVo.getIsOkApply());
                                tempGraduationDesignDeclare.setGraduationDesignPresubjectId(id);
                                graduationDesignDeclareService.saveOrUpdate(tempGraduationDesignDeclare);
                            }
                            ajaxUtils.success().msg("保存成功");
                        } else {
                            ajaxUtils.fail().msg("不符合条件，无法编辑");
                        }
                    } else {
                        ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                    }
                } else {
                    ajaxUtils.fail().msg("不在毕业时间范围，无法操作");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }

        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 更新题目
     *
     * @param graduationDesignPresubjectUpdateVo 数据
     * @param graduationDesignPresubject         原题
     */
    private void updatePresubject(GraduationDesignPresubjectUpdateVo graduationDesignPresubjectUpdateVo, GraduationDesignPresubject graduationDesignPresubject) {
        graduationDesignPresubject.setPresubjectTitle(graduationDesignPresubjectUpdateVo.getPresubjectTitle());
        graduationDesignPresubject.setPresubjectPlan(graduationDesignPresubjectUpdateVo.getPresubjectPlan());
        graduationDesignPresubject.setPublicLevel(graduationDesignPresubjectUpdateVo.getPublicLevel());
        graduationDesignPresubject.setUpdateTime(DateTimeUtils.getNow());
        graduationDesignPresubjectService.update(graduationDesignPresubject);
    }

    /**
     * 编辑条件
     *
     * @param graduationDesignPresubject 数据
     * @return true or false
     */
    private boolean listAndMyCanEditCondition(GraduationDesignPresubject graduationDesignPresubject) {
        boolean canEdit = false;
        Users users = usersService.getUserFromSession();
        GraduationDesignDeclareRecord graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignPresubject.getGraduationDesignPresubjectId());
        if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.getIsOkApply()) || graduationDesignDeclare.getIsOkApply() != 1) {
            // 教职工可查看，因题目可在分配教师前写，不存在指导教师，本人指导教师则无法编辑，因此仅允许查看，在列表没有编辑
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                canEdit = true;
            } else {
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    Student student = studentService.findByUsername(users.getUsername());
                    if (Objects.equals(graduationDesignPresubject.getStudentId(), student.getStudentId())) {
                        canEdit = true;
                    }
                }
            }
        }
        return canEdit;
    }

    /**
     * 修改题目条件
     *
     * @param graduationDesignReleaseId  发布id
     * @param graduationDesignPresubject 题目
     * @param staffId                    教职工id
     * @return true or false
     */
    private boolean declareUpdateTitleCondition(String graduationDesignReleaseId, GraduationDesignPresubject graduationDesignPresubject, int staffId) {
        boolean canEdit = false;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    Users users = usersService.getUserFromSession();
                    GraduationDesignDeclareRecord graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignPresubject.getGraduationDesignPresubjectId());
                    // 未确认申报
                    if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.getIsOkApply()) || graduationDesignDeclare.getIsOkApply() != 1) {
                        // 如果是管理员或系统
                        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                            canEdit = true;
                        } else {
                            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                                Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                                if (studentRecord.isPresent()) {
                                    Student student = studentRecord.get().into(Student.class);
                                    canEdit = Objects.equals(student.getStudentId(), graduationDesignPresubject.getStudentId());
                                }
                            } else {
                                canEdit = isCurrentStaff(users, graduationDesignReleaseId, staffId);
                            }
                        }
                    }
                }
            }
        }
        return canEdit;
    }

    /**
     * 编辑申报条件
     *
     * @param graduationDesignReleaseId  发布id
     * @param graduationDesignPresubject 题目
     * @param staffId                    教职工id
     * @return true or false
     */
    private boolean updateApplyCondition(String graduationDesignReleaseId, GraduationDesignPresubject graduationDesignPresubject, int staffId) {
        boolean canEdit = false;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    Users users = usersService.getUserFromSession();
                    GraduationDesignDeclareRecord graduationDesignDeclare = graduationDesignDeclareService.findByGraduationDesignPresubjectId(graduationDesignPresubject.getGraduationDesignPresubjectId());
                    // 未确认申报
                    if (ObjectUtils.isEmpty(graduationDesignDeclare) || ObjectUtils.isEmpty(graduationDesignDeclare.getIsOkApply()) || graduationDesignDeclare.getIsOkApply() != 1) {
                        // 如果是管理员或系统或教职工
                        canEdit = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) || isCurrentStaff(users, graduationDesignReleaseId, staffId);
                    }
                }
            }
        }
        return canEdit;
    }

    /**
     * 判断当前操作用户是否为教职工
     *
     * @param users                     当前用户信息
     * @param graduationDesignReleaseId 发布Id
     * @param staffId                   教职工id
     * @return true or false
     */
    private boolean isCurrentStaff(Users users, String graduationDesignReleaseId, int staffId) {
        boolean canEdit = false;
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Staff staff = staffService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(staff)) {
                Optional<Record> staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                if (staffRecord.isPresent()) {
                    Staff tempStaff = staffRecord.get().into(Staff.class);
                    canEdit = tempStaff.getStaffId() == staffId;
                }
            }
        }
        return canEdit;
    }

    /**
     * 进入题目申报判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils declareCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg("请等待确认毕业设计指导教师调整后查看");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 题目申报操作条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/declare/operator/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils declareOperatorCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    ajaxUtils.success().msg("在条件范围，允许使用");
                } else {
                    ajaxUtils.fail().msg("请等待确认毕业设计指导教师调整后查看");
                }
            } else {
                ajaxUtils.fail().msg("不在毕业时间范围，无法操作");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入我的题目页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            Users users = usersService.getUserFromSession();
            Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
            if (studentRecord.isPresent()) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg("您可能不是学生用户或不符合进入条件");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 我的题目操作条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/my/operator/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myOperatorCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                Users users = usersService.getUserFromSession();
                Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                if (studentRecord.isPresent()) {
                    ajaxUtils.success().msg("在条件范围，允许使用");
                } else {
                    ajaxUtils.fail().msg("您可能不是学生用户或不符合进入条件");
                }
            } else {
                ajaxUtils.fail().msg("不在毕业时间范围，无法操作");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/subject/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canUse(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }
}
