package top.zbeboy.isy.web.internship.journal;

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
import top.zbeboy.isy.service.*;
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
import java.io.IOException;
import java.sql.Timestamp;
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
    private StudentService studentService;

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
        Optional<Record> record = usersService.findUserSchoolInfo(users);
        if (record.isPresent() && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Student student = record.get().into(Student.class);
            modelMap.addAttribute("studentId", student.getStudentId());
        }
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
        Optional<Record> record = usersService.findUserSchoolInfo(users);
        if (record.isPresent() && usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Student student = record.get().into(Student.class);
            ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, student.getStudentId());
            canUse = !errorBean.isHasError();
            modelMap.addAttribute("studentId", student.getStudentId());
            modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        }
        if (canUse) {
            page = "web/internship/journal/internship_my_journal::#page-wrapper";
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
            otherCondition.setInternshipReleaseId(request.getParameter("internshipReleaseId"));
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
        }
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
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
        return "web/internship/journal/internship_journal_add::#page-wrapper";
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
        if (!ObjectUtils.isEmpty(internshipJournal)) {
            modelMap.addAttribute("internshipJournal", internshipJournal);
            page = "web/internship/journal/internship_journal_edit::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
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
            modelMap.addAttribute("internshipJournalDate", DateTimeUtils.formatDate(internshipJournal.getInternshipJournalDate(), "yyyy年MM月dd日"));
            modelMap.addAttribute("internshipJournal", internshipJournal);
            page = "web/internship/journal/internship_journal_look::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, "未查询到相关实习信息");
        }
        return page;
    }

    /**
     * 下载实习日志
     *
     * @param id 实习日志id
     */
    @RequestMapping(value = "/web/internship/journal/list/download", method = RequestMethod.GET)
    public void journalListDownload(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response) {
        InternshipJournal internshipJournal = internshipJournalService.findById(id);
        if (!ObjectUtils.isEmpty(internshipJournal)) {
            uploadService.download(internshipJournal.getStudentName() + " " + internshipJournal.getStudentNumber(), "/" + internshipJournal.getInternshipJournalWord(), response, request);
        }
    }

    /**
     * 下载某位学生全部实习日志
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param request             请求
     * @param response            响应
     */
    @RequestMapping(value = "/web/internship/journal/list/downloads", method = RequestMethod.GET)
    public void journalListDownloads(@RequestParam("id") String internshipReleaseId, @RequestParam("studentId") int studentId, HttpServletRequest request, HttpServletResponse response) {
        try {
            Result<InternshipJournalRecord> records = internshipJournalService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
            if (records.isNotEmpty()) {
                List<String> fileName = new ArrayList<>();
                List<String> filePath = new ArrayList<>();
                records.forEach(r -> {
                    filePath.add(RequestUtils.getRealPath(request) + r.getInternshipJournalWord());
                    fileName.add(r.getInternshipJournalWord().substring(r.getInternshipJournalWord().lastIndexOf("/") + 1));
                });
                Optional<Record> studentRecord = studentService.findByIdRelation(studentId);
                if (studentRecord.isPresent()) {
                    Users users = studentRecord.get().into(Users.class);
                    String downloadFileName = StringUtils.hasLength(users.getRealName()) ? users.getRealName() : "实习日志";
                    String zipName = downloadFileName + ".zip";
                    String downloadFilePath = Workbook.internshipJournalPath(users) + zipName;
                    String zipPath = RequestUtils.getRealPath(request) + Workbook.internshipJournalPath(users) + zipName;
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
     * @param journalIds 系ids
     * @return true 删除成功
     */
    @RequestMapping(value = "/web/internship/journal/list/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils departmentUpdateDel(String journalIds, HttpServletRequest request) {
        if (StringUtils.hasLength(journalIds)) {
            List<String> ids = SmallPropsUtils.StringIdsToStringList(journalIds);
            ids.forEach(id -> {
                InternshipJournal internshipJournal = internshipJournalService.findById(id);
                try {
                    if (!ObjectUtils.isEmpty(internshipJournal)) {
                        FilesUtils.deleteFile(RequestUtils.getRealPath(request) + internshipJournal.getInternshipJournalWord());
                    }
                } catch (IOException e) {
                    log.error("Not found journal file , error is {}", e);
                }
            });
            internshipJournalService.batchDelete(ids);
            return new AjaxUtils().success().msg("删除日志成功");
        }

        return new AjaxUtils().fail().msg("删除日志失败");
    }

    /**
     * 实习日志进入条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/journal/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canUse(@RequestParam("id") String internshipReleaseId, int studentId) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, studentId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 保存实习日志
     *
     * @param internshipJournalVo 实习日志
     * @param bindingResult       检验
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/journal/my/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils journalSave(@Valid InternshipJournalVo internshipJournalVo, BindingResult bindingResult, HttpServletRequest request) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        if (!bindingResult.hasErrors()) {
            InternshipJournal internshipJournal = new InternshipJournal();
            internshipJournal.setInternshipJournalId(UUIDUtils.getUUID());
            internshipJournal.setStudentName(internshipJournalVo.getStudentName());
            internshipJournal.setStudentNumber(internshipJournalVo.getStudentNumber());
            internshipJournal.setOrganize(internshipJournalVo.getOrganize());
            internshipJournal.setSchoolGuidanceTeacher(internshipJournalVo.getSchoolGuidanceTeacher());
            internshipJournal.setGraduationPracticeCompanyName(internshipJournalVo.getGraduationPracticeCompanyName());
            internshipJournal.setInternshipJournalContent(internshipJournalVo.getInternshipJournalContent());
            internshipJournal.setInternshipJournalDate(internshipJournalVo.getInternshipJournalDate());
            internshipJournal.setCreateDate(new Timestamp(System.currentTimeMillis()));
            internshipJournal.setStudentId(internshipJournalVo.getStudentId());
            internshipJournal.setInternshipReleaseId(internshipJournalVo.getInternshipReleaseId());

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
                InternshipJournal internshipJournal = internshipJournalService.findById(internshipJournalVo.getInternshipJournalId());
                internshipJournal.setStudentName(internshipJournalVo.getStudentName());
                internshipJournal.setInternshipJournalContent(internshipJournalVo.getInternshipJournalContent());
                internshipJournal.setInternshipJournalDate(internshipJournalVo.getInternshipJournalDate());
                log.debug(RequestUtils.getRealPath(request) + internshipJournal.getInternshipJournalWord());
                if (FilesUtils.deleteFile(RequestUtils.getRealPath(request) + internshipJournal.getInternshipJournalWord())) {
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
                    ajaxUtils.fail().msg("未找到相关实习日志文件");
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
     * @param info 学生信息
     * @param type 检验类型
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
            ErrorBean<InternshipRelease> errorBean = accessCondition(internshipReleaseId, student.getStudentId());
            if (!errorBean.isHasError()) {
                ajaxUtils.success().msg("查询学生数据成功").obj(student.getStudentId());
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
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
        }
        errorBean.setMapData(mapData);
        return errorBean;
    }
}
