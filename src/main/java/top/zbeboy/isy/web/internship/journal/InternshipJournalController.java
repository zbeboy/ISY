package top.zbeboy.isy.web.internship.journal;

import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import top.zbeboy.isy.domain.tables.records.InternshipJournalRecord;
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.common.FilesService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.internship.*;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.FilesUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.internship.journal.InternshipJournalVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.*;

/**
 * Created by zbeboy on 2016/12/14.
 */
@Controller
public class InternshipJournalController {

    private final Logger log = LoggerFactory.getLogger(InternshipJournalController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipApplyService internshipApplyService;

    @Resource
    private InternshipJournalService internshipJournalService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

    @Resource
    private InternshipTypeService internshipTypeService;

    @Resource
    private UploadService uploadService;

    @Resource
    private UsersService usersService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private CacheManageService cacheManageService;

    @Resource
    private StudentService studentService;

    @Resource
    private StaffService staffService;

    @Resource
    private InternshipCollegeService internshipCollegeService;

    @Resource
    private InternshipCompanyService internshipCompanyService;

    @Resource
    private GraduationPracticeCompanyService graduationPracticeCompanyService;

    @Resource
    private FilesService filesService;

    @Resource
    private RoleService roleService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    /**
     * 实习日志
     *
     * @return 实习日志页面
     */
    @RequestMapping(value = "/web/menu/internship/journal", method = RequestMethod.GET)
    public String internshipJournal() {
        return "web/internship/journal/internship_journal::#page-wrapper";
    }

    /**
     * 实习日志列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/journal/list", method = RequestMethod.GET)
    public String journalList(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        Users users = usersService.getUserFromSession();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Student student = studentService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(student)) {
                modelMap.addAttribute("studentId", student.getStudentId());
            } else {
                modelMap.addAttribute("studentId", null);
            }
        }
        UsersType usersType = cacheManageService.findByUsersTypeId(users.getUsersTypeId());
        modelMap.addAttribute("usersTypeName", usersType.getUsersTypeName());
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
        }
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "web/internship/journal/internship_journal_list::#page-wrapper";
    }

    /**
     * 我的日志列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/journal/my/list", method = RequestMethod.GET)
    public String myJournalList(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page;
        boolean canUse = false;
        Users users = usersService.getUserFromSession();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Student student = studentService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(student)) {
                ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, student.getStudentId());
                canUse = !errorBean.isHasError();
                modelMap.addAttribute("studentId", student.getStudentId());
                modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
            }
        }
        if (canUse) {
            page = "web/internship/journal/internship_my_journal::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 小组日志列表页面
     *
     * @param internshipReleaseId 实习发布id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/journal/team/list", method = RequestMethod.GET)
    public String teamJournalList(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        String page;
        boolean canUse = false;
        Users users = usersService.getUserFromSession();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Student student = studentService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(student)) {
                Optional<Record> record = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, student.getStudentId());
                if (record.isPresent()) {
                    InternshipTeacherDistribution internshipTeacherDistribution = record.get().into(InternshipTeacherDistribution.class);
                    canUse = true;
                    modelMap.addAttribute("studentId", student.getStudentId());
                    modelMap.addAttribute("staffId", internshipTeacherDistribution.getStaffId());
                    modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
                    modelMap.addAttribute("usersTypeName", Workbook.STUDENT_USERS_TYPE);
                } else {
                    canUse = false;
                }
            }
        } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Staff staff = staffService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(staff)) {
                Result<InternshipTeacherDistributionRecord> records = internshipTeacherDistributionService.findByInternshipReleaseIdAndStaffId(internshipReleaseId, staff.getStaffId());
                canUse = records.isNotEmpty();
                modelMap.addAttribute("staffId", staff.getStaffId());
                modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
                modelMap.addAttribute("usersTypeName", Workbook.STAFF_USERS_TYPE);
            }
        }

        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
        }

        if (canUse) {
            page = "web/internship/journal/internship_team_journal::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 日志列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/journal/list/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipJournalBean> listData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("student_name");
        headers.add("student_number");
        headers.add("organize");
        headers.add("school_guidance_teacher");
        headers.add("create_date");
        headers.add("operator");
        DataTablesUtils<InternshipJournalBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        InternshipJournalBean otherCondition = new InternshipJournalBean();
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        if (!ObjectUtils.isEmpty(internshipReleaseId)) {
            otherCondition.setInternshipReleaseId(internshipReleaseId);
            String studentId = request.getParameter("studentId");
            String staffId = request.getParameter("staffId");
            if (StringUtils.hasLength(studentId)) {
                if (NumberUtils.isDigits(studentId)) {
                    otherCondition.setStudentId(NumberUtils.toInt(studentId));
                }
            }
            if (StringUtils.hasLength(staffId)) {
                if (NumberUtils.isDigits(staffId)) {
                    otherCondition.setStaffId(NumberUtils.toInt(staffId));
                }
            }
            Result<Record> records = internshipJournalService.findAllByPage(dataTablesUtils, otherCondition);
            List<InternshipJournalBean> internshipJournalBeans = new ArrayList<>();
            if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                internshipJournalBeans = records.into(InternshipJournalBean.class);
                internshipJournalBeans.forEach(i -> i.setCreateDateStr(DateTimeUtils.formatDate(i.getCreateDate())));
            }
            dataTablesUtils.setData(internshipJournalBeans);
            dataTablesUtils.setiTotalRecords(internshipJournalService.countAll(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(internshipJournalService.countByCondition(dataTablesUtils, otherCondition));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 实习日志添加
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/journal/list/add", method = RequestMethod.GET)
    public String journalListAdd(@RequestParam("id") String internshipReleaseId, @RequestParam("studentId") int studentId, ModelMap modelMap) {
        String page;
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, studentId);
        if (!errorBean.isHasError()) {
            InternshipJournal internshipJournal = new InternshipJournal();
            Optional<Record> studentRecord = studentService.findByIdRelation(studentId);
            if (studentRecord.isPresent()) {
                StudentBean studentBean = studentRecord.get().into(StudentBean.class);
                internshipJournal.setStudentId(studentId);
                internshipJournal.setInternshipReleaseId(internshipReleaseId);
                internshipJournal.setStudentName(studentBean.getRealName());
                internshipJournal.setOrganize(studentBean.getOrganizeName());
                internshipJournal.setStudentNumber(studentBean.getStudentNumber());
            }
            Optional<Record> internshipTeacherDistributionRecord = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentIdForStaff(internshipReleaseId, studentId);
            if (internshipTeacherDistributionRecord.isPresent()) {
                InternshipTeacherDistributionBean internshipTeacherDistributionBean = internshipTeacherDistributionRecord.get().into(InternshipTeacherDistributionBean.class);
                internshipJournal.setSchoolGuidanceTeacher(internshipTeacherDistributionBean.getRealName());
                internshipJournal.setStaffId(internshipTeacherDistributionBean.getStaffId());
            }
            InternshipRelease internshipRelease = errorBean.getData();
            InternshipType internshipType = internshipTypeService.findByInternshipTypeId(internshipRelease.getInternshipTypeId());
            switch (internshipType.getInternshipTypeName()) {
                case Workbook.INTERNSHIP_COLLEGE_TYPE:
                    Optional<Record> internshipCollegeRecord = internshipCollegeService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (internshipCollegeRecord.isPresent()) {
                        InternshipCollege internshipCollege = internshipCollegeRecord.get().into(InternshipCollege.class);
                        internshipJournal.setGraduationPracticeCompanyName(internshipCollege.getInternshipCollegeName());
                    }
                    break;
                case Workbook.INTERNSHIP_COMPANY_TYPE:
                    Optional<Record> internshipCompanyRecord = internshipCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (internshipCompanyRecord.isPresent()) {
                        InternshipCompany internshipCompany = internshipCompanyRecord.get().into(InternshipCompany.class);
                        internshipJournal.setGraduationPracticeCompanyName(internshipCompany.getInternshipCompanyName());
                    }
                    break;
                case Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE:
                    break;
                case Workbook.GRADUATION_PRACTICE_UNIFY_TYPE:
                    break;
                case Workbook.GRADUATION_PRACTICE_COMPANY_TYPE:
                    Optional<Record> graduationPracticeCompanyRecord = graduationPracticeCompanyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                    if (graduationPracticeCompanyRecord.isPresent()) {
                        GraduationPracticeCompany graduationPracticeCompany = graduationPracticeCompanyRecord.get().into(GraduationPracticeCompany.class);
                        internshipJournal.setGraduationPracticeCompanyName(graduationPracticeCompany.getGraduationPracticeCompanyName());
                    }
                    break;
            }
            modelMap.addAttribute("internshipJournal", internshipJournal);
            page = "web/internship/journal/internship_journal_add::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 编辑实习日志页面
     *
     * @param id       实习日志id
     * @param modelMap 页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/journal/list/edit", method = RequestMethod.GET)
    public String journalListEdit(@RequestParam("id") String id, ModelMap modelMap) {
        String page;
        InternshipJournal internshipJournal = internshipJournalService.findById(id);
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipJournal.getInternshipReleaseId(), internshipJournal.getStudentId());
        if (!errorBean.isHasError()) {
            if (!ObjectUtils.isEmpty(internshipJournal)) {
                modelMap.addAttribute("internshipJournal", internshipJournal);
                page = "web/internship/journal/internship_journal_edit::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, "您不符合进入条件");
        }
        return page;
    }

    /**
     * 查看实习日志页面
     *
     * @param id       实习日志id
     * @param modelMap 页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/journal/list/look", method = RequestMethod.GET)
    public String journalListLook(@RequestParam("id") String id, ModelMap modelMap) {
        String page;
        InternshipJournal internshipJournal = internshipJournalService.findById(id);
        if (!ObjectUtils.isEmpty(internshipJournal)) {
            if (isSeeStaff(internshipJournal)) {
                modelMap.addAttribute("internshipJournalDate", DateTimeUtils.formatDate(internshipJournal.getInternshipJournalDate(), "yyyy年MM月dd日"));
                modelMap.addAttribute("internshipJournal", internshipJournal);
                page = "web/internship/journal/internship_journal_look::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, "该日志已限制查阅");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
        }
        return page;
    }

    /**
     * 下载实习日志
     *
     * @param id       实习日志id
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping(value = "/web/internship/journal/list/download", method = RequestMethod.GET)
    public void journalListDownload(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response) {
        InternshipJournal internshipJournal = internshipJournalService.findById(id);
        if (!ObjectUtils.isEmpty(internshipJournal)) {
            if (isSeeStaff(internshipJournal)) {
                uploadService.download(internshipJournal.getStudentName() + " " + internshipJournal.getStudentNumber(), "/" + internshipJournal.getInternshipJournalWord(), response, request);
            }
        }
    }

    /**
     * 判断是否允许查看和下载
     *
     * @param internshipJournal 实习日志
     * @return true or false
     */
    private boolean isSeeStaff(InternshipJournal internshipJournal) {
        Student student = studentService.findById(internshipJournal.getStudentId());
        Users users = usersService.getUserFromSession();
        return users.getUsername().equals(student.getUsername()) ||
                roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) ||
                roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) ||
                internshipJournal.getIsSeeStaff() != 1 || usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE);
    }

    /**
     * 下载全部实习日志
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param request             请求
     * @param response            响应
     */
    @RequestMapping(value = "/web/internship/journal/list/my/downloads", method = RequestMethod.GET)
    public void journalListMyDownloads(@RequestParam("id") String internshipReleaseId, @RequestParam("studentId") int studentId, HttpServletRequest request, HttpServletResponse response) {
        try {
            ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, studentId);
            if (!errorBean.isHasError()) {
                Result<InternshipJournalRecord> records = internshipJournalService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
                if (records.isNotEmpty()) {
                    List<String> fileName = new ArrayList<>();
                    List<String> filePath = new ArrayList<>();
                    records.forEach(r -> {
                        filePath.add(RequestUtils.getRealPath(request) + r.getInternshipJournalWord());
                        fileName.add(r.getInternshipJournalWord().substring(r.getInternshipJournalWord().lastIndexOf('/') + 1));
                    });
                    Optional<Record> studentRecord = studentService.findByIdRelation(studentId);
                    if (studentRecord.isPresent()) {
                        Users users = studentRecord.get().into(Users.class);
                        String downloadFileName = StringUtils.hasLength(users.getRealName()) ? users.getRealName() : "实习日志";
                        String zipName = downloadFileName + ".zip";
                        String downloadFilePath = Workbook.internshipJournalPath(users) + zipName;
                        String zipPath = RequestUtils.getRealPath(request) + downloadFilePath;
                        FilesUtils.compressZipMulti(fileName, zipPath, filePath);
                        uploadService.download(downloadFileName, "/" + downloadFilePath, response, request);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Compress zip error , error is {}", e);
        }
    }

    /**
     * 下载小组全部实习日志
     *
     * @param internshipReleaseId 实习发布id
     * @param request             请求
     * @param response            响应
     */
    @RequestMapping(value = "/web/internship/journal/list/team/downloads", method = RequestMethod.GET)
    public void journalListTeamDownloads(@RequestParam("id") String internshipReleaseId, @RequestParam("staffId") int staffId, HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean canDown = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) ||
                    roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) ||
                    usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE);
            if (canDown) {
                Result<InternshipJournalRecord> records = internshipJournalService.findByInternshipReleaseIdAndStaffId(internshipReleaseId, staffId);
                if (records.isNotEmpty()) {
                    List<String> fileName = new ArrayList<>();
                    List<String> filePath = new ArrayList<>();
                    records.forEach(r -> {
                        filePath.add(RequestUtils.getRealPath(request) + r.getInternshipJournalWord());
                        fileName.add(r.getInternshipJournalWord().substring(r.getInternshipJournalWord().lastIndexOf('/') + 1));
                    });
                    Users users = usersService.getUserFromSession();
                    String downloadFileName = users.getRealName() + "小组实习日志";
                    String zipName = downloadFileName + ".zip";
                    String downloadFilePath = Workbook.TEMP_FILES_PORTFOLIOS + File.separator + zipName;
                    String zipPath = RequestUtils.getRealPath(request) + downloadFilePath;
                    FilesUtils.compressZipMulti(fileName, zipPath, filePath);
                    uploadService.download(downloadFileName, "/" + downloadFilePath, response, request);
                }
            }
        } catch (Exception e) {
            log.error("Compress zip error , error is {}", e);
        }
    }

    /**
     * 批量删除日志
     *
     * @param journalIds ids
     * @param request    请求
     * @return true 删除成功
     */
    @RequestMapping(value = "/web/internship/journal/list/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils journalListDel(String journalIds, HttpServletRequest request) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        try {
            boolean canDel = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES);
            Users users = usersService.getUserFromSession();
            Student student = null;
            if (!canDel && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                student = studentService.findByUsername(users.getUsername());
            }
            if (StringUtils.hasLength(journalIds)) {
                List<String> ids = SmallPropsUtils.StringIdsToStringList(journalIds);
                for (String id : ids) {
                    InternshipJournal internshipJournal = internshipJournalService.findById(id);
                    if (!ObjectUtils.isEmpty(internshipJournal)) {
                        if (!canDel) {
                            // 学生本人操作
                            if (!ObjectUtils.isEmpty(student) && Objects.equals(student.getStudentId(), internshipJournal.getStudentId())) {
                                FilesUtils.deleteFile(RequestUtils.getRealPath(request) + internshipJournal.getInternshipJournalWord());
                                internshipJournalService.deleteById(id);
                            }
                        } else { // 系统或管理员操作
                            FilesUtils.deleteFile(RequestUtils.getRealPath(request) + internshipJournal.getInternshipJournalWord());
                            internshipJournalService.deleteById(id);
                        }
                    }
                }
                ajaxUtils.success().msg("删除日志成功");
            } else {
                ajaxUtils.fail().msg("未查询到相关信息");
            }
        } catch (IOException e) {
            log.error("Not found journal file , error is {}", e);
            ajaxUtils.fail().msg("删除日志失败");
        }

        return ajaxUtils;
    }

    /**
     * 保存实习日志
     *
     * @param internshipJournalVo 实习日志
     * @param bindingResult       检验
     * @param request             请求
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/journal/my/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils journalSave(@Valid InternshipJournalVo internshipJournalVo, BindingResult bindingResult, HttpServletRequest request) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        if (!bindingResult.hasErrors()) {
            ErrorBean<InternshipRelease> errorBean = accessCondition(internshipJournalVo.getInternshipReleaseId(), internshipJournalVo.getStudentId());
            if (!errorBean.isHasError()) {
                InternshipJournal internshipJournal = new InternshipJournal();
                internshipJournal.setInternshipJournalId(UUIDUtils.getUUID());
                internshipJournal.setStudentName(internshipJournalVo.getStudentName());
                internshipJournal.setStudentNumber(internshipJournalVo.getStudentNumber());
                internshipJournal.setOrganize(internshipJournalVo.getOrganize());
                internshipJournal.setSchoolGuidanceTeacher(internshipJournalVo.getSchoolGuidanceTeacher());
                internshipJournal.setGraduationPracticeCompanyName(internshipJournalVo.getGraduationPracticeCompanyName());
                internshipJournal.setInternshipJournalContent(internshipJournalVo.getInternshipJournalContent());
                internshipJournal.setInternshipJournalHtml(internshipJournalVo.getInternshipJournalHtml());
                internshipJournal.setInternshipJournalDate(internshipJournalVo.getInternshipJournalDate());
                internshipJournal.setCreateDate(new Timestamp(Clock.systemDefaultZone().millis()));
                internshipJournal.setStudentId(internshipJournalVo.getStudentId());
                internshipJournal.setStaffId(internshipJournalVo.getStaffId());
                internshipJournal.setInternshipReleaseId(internshipJournalVo.getInternshipReleaseId());
                internshipJournal.setIsSeeStaff(internshipJournalVo.getIsSeeStaff());

                Optional<Record> studentRecord = studentService.findByIdRelation(internshipJournalVo.getStudentId());
                if (studentRecord.isPresent()) {
                    Users users = studentRecord.get().into(Users.class);
                    String outputPath = filesService.saveInternshipJournal(internshipJournal, users, request);
                    if (StringUtils.hasLength(outputPath)) {
                        internshipJournal.setInternshipJournalWord(outputPath);
                        internshipJournalService.save(internshipJournal);
                        ajaxUtils.success().msg("保存成功");
                    } else {
                        ajaxUtils.fail().msg("保存文件失败");
                    }
                } else {
                    ajaxUtils.fail().msg("未查询到相关学生信息");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }

        } else {
            ajaxUtils.fail().msg("保存失败，参数错误");
        }
        return ajaxUtils;
    }

    /**
     * 实习日志编辑
     *
     * @param internshipJournalVo 实习日志
     * @param bindingResult       检验
     * @param request             请求
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/journal/my/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils journalUpdate(@Valid InternshipJournalVo internshipJournalVo, BindingResult bindingResult, HttpServletRequest request) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        try {
            if (!bindingResult.hasErrors() && StringUtils.hasLength(internshipJournalVo.getInternshipJournalId())) {
                ErrorBean<InternshipRelease> errorBean = accessCondition(internshipJournalVo.getInternshipReleaseId(), internshipJournalVo.getStudentId());
                if (!errorBean.isHasError()) {
                    InternshipJournal internshipJournal = internshipJournalService.findById(internshipJournalVo.getInternshipJournalId());
                    internshipJournal.setStudentName(internshipJournalVo.getStudentName());
                    internshipJournal.setInternshipJournalContent(internshipJournalVo.getInternshipJournalContent());
                    internshipJournal.setInternshipJournalHtml(internshipJournalVo.getInternshipJournalHtml());
                    internshipJournal.setInternshipJournalDate(internshipJournalVo.getInternshipJournalDate());
                    internshipJournal.setIsSeeStaff(internshipJournalVo.getIsSeeStaff() == null ? 0 : internshipJournalVo.getIsSeeStaff());
                    FilesUtils.deleteFile(RequestUtils.getRealPath(request) + internshipJournal.getInternshipJournalWord());
                    Optional<Record> studentRecord = studentService.findByIdRelation(internshipJournalVo.getStudentId());
                    if (studentRecord.isPresent()) {
                        Users users = studentRecord.get().into(Users.class);
                        String outputPath = filesService.saveInternshipJournal(internshipJournal, users, request);
                        if (StringUtils.hasLength(outputPath)) {
                            internshipJournal.setInternshipJournalWord(outputPath);
                            internshipJournalService.update(internshipJournal);
                            ajaxUtils.success().msg("更新成功");
                        } else {
                            ajaxUtils.fail().msg("保存文件失败");
                        }
                    } else {
                        ajaxUtils.fail().msg("未查询到相关学生信息");
                    }

                } else {
                    ajaxUtils.fail().msg(errorBean.getErrorMsg());
                }
            } else {
                ajaxUtils.fail().msg("更新失败，参数错误");
            }
        } catch (IOException e) {
            log.error("Delete dist journal error , error is {} ", e);
        }

        return ajaxUtils;
    }

    /**
     * 检验学生
     *
     * @param info                学生信息
     * @param internshipReleaseId 实习发布id
     * @param type                检验类型
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/journal/valid/student", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validStudent(@RequestParam("student") String info, @RequestParam("internshipReleaseId") String internshipReleaseId, int type) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        Student student = null;
        if (type == 0) {
            student = studentService.findByUsername(info);
        } else if (type == 1) {
            student = studentService.findByStudentNumber(info);
        }
        if (!ObjectUtils.isEmpty(student)) {
            // 当前用户角色
            boolean canGo = roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES);
            // step 1.
            if (!canGo) {
                // 是否为该生指导教师
                Optional<Record> staffInfo = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentIdForStaff(internshipReleaseId, student.getStudentId());
                if (staffInfo.isPresent()) {
                    Staff staff = staffInfo.get().into(Staff.class);
                    Users users = usersService.getUserFromSession();
                    canGo = staff.getUsername().equals(users.getUsername());
                } else {
                    canGo = false;
                }
            }
            // step 2.
            if (canGo) {
                ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, student.getStudentId());
                if (!errorBean.isHasError()) {
                    ajaxUtils.success().msg("查询学生数据成功").obj(student.getStudentId());
                } else {
                    ajaxUtils.fail().msg(errorBean.getErrorMsg());
                }
            } else {
                ajaxUtils.fail().msg("您权限不足或未参与该实习");
            }
        } else {
            ajaxUtils.fail().msg("查询学生数据失败");
        }
        return ajaxUtils;
    }

    /**
     * 进入实习日志入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private ErrorBean<InternshipRelease> accessCondition(String internshipReleaseId, int studentId) {
        ErrorBean<InternshipRelease> errorBean = new ErrorBean<>();
        if (!commonControllerMethodService.limitCurrentStudent(studentId)) {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("您的个人信息有误");
            return errorBean;
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
        Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
        if (internshipApplyRecord.isPresent()) {
            InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
            mapData.put("internshipApply", internshipApply);
            // 状态为 2：已通过；4：基本信息变更申请中；5：基本信息变更填写中；6：单位信息变更申请中；7：单位信息变更填写中 允许进行填写
            if (internshipApply.getInternshipApplyState() == 2 || internshipApply.getInternshipApplyState() == 4 ||
                    internshipApply.getInternshipApplyState() == 5 || internshipApply.getInternshipApplyState() == 6 || internshipApply.getInternshipApplyState() == 7) {
                errorBean.setHasError(false);
                errorBean.setErrorMsg("允许填写");
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("检测到您未通过申请，不允许填写");
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("检测到您未申请该实习");
        }
        errorBean.setMapData(mapData);
        return errorBean;
    }
}
