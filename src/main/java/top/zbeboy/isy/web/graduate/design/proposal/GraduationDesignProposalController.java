package top.zbeboy.isy.web.graduate.design.proposal;

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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.common.FilesService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.graduate.design.*;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.FilesUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.file.FileBean;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumGroupBean;
import top.zbeboy.isy.web.common.MethodControllerCommon;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.graduate.design.proposal.GraduationDesignProposalAddVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.*;

/**
 * Created by zbeboy on 2017/6/22.
 */
@Slf4j
@Controller
public class GraduationDesignProposalController {

    @Resource
    private MethodControllerCommon methodControllerCommon;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignDatumService graduationDesignDatumService;

    @Resource
    private GraduationDesignDatumTypeService graduationDesignDatumTypeService;

    @Resource
    private GraduationDesignDatumGroupService graduationDesignDatumGroupService;

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private GraduationDesignTutorService graduationDesignTutorService;

    @Resource
    private UsersService usersService;

    @Resource
    private StudentService studentService;

    @Resource
    private UploadService uploadService;

    @Resource
    private FilesService filesService;

    @Resource
    private CacheManageService cacheManageService;

    @Resource
    private RoleService roleService;

    @Resource
    private StaffService staffService;

    /**
     * 毕业设计资料
     *
     * @return 毕业设计资料页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/proposal", method = RequestMethod.GET)
    public String proposal() {
        return "web/graduate/design/proposal/design_proposal::#page-wrapper";
    }

    /**
     * 获取文件类型
     *
     * @return 数据
     */
    @RequestMapping(value = "/use/graduate/design/proposal/datums", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignDatumType> subjectTypes() {
        AjaxUtils<GraduationDesignDatumType> ajaxUtils = AjaxUtils.of();
        List<GraduationDesignDatumType> graduationDesignDatumTypes = new ArrayList<>();
        GraduationDesignDatumType graduationDesignDatumType = new GraduationDesignDatumType(0, "文件类型");
        graduationDesignDatumTypes.add(graduationDesignDatumType);
        graduationDesignDatumTypes.addAll(graduationDesignDatumTypeService.findAll());
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignDatumTypes);
    }

    /**
     * 附件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/proposal/affix", method = RequestMethod.GET)
    public String affix(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            page = "web/graduate/design/proposal/design_proposal_affix::#page-wrapper";
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 我的资料页面
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/proposal/my", method = RequestMethod.GET)
    public String my(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Users users = usersService.getUserFromSession();
            Student student = studentService.findByUsername(users.getUsername());
            ErrorBean<GraduationDesignRelease> errorBean = studentCondition(graduationDesignReleaseId, student.getStudentId());
            if (!errorBean.isHasError()) {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                page = "web/graduate/design/proposal/design_proposal_my::#page-wrapper";
            } else {
                page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, "目前仅提供学生使用");
        }
        return page;
    }

    /**
     * 小组资料
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/proposal/team", method = RequestMethod.GET)
    public String team(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
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
                page = "web/graduate/design/proposal/design_proposal_team::#page-wrapper";

            } else {
                page = methodControllerCommon.showTip(modelMap, "请等待确认毕业设计指导教师调整后查看");
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 组内资料
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/proposal/group", method = RequestMethod.GET)
    public String group(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                boolean canUse = false;
                Users users = usersService.getUserFromSession();
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                    if (studentRecord.isPresent()) {
                        Student student = studentRecord.get().into(Student.class);
                        Optional<Record> staffRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.getStudentId(), graduationDesignReleaseId);
                        if (staffRecord.isPresent()) {
                            GraduationDesignTeacher graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher.class);
                            modelMap.addAttribute("graduationDesignTeacherId", graduationDesignTeacher.getGraduationDesignTeacherId());
                            modelMap.addAttribute("currUseIsStaff", 0);
                            canUse = true;
                        }
                    }
                } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    Staff staff = staffService.findByUsername(users.getUsername());
                    if (!ObjectUtils.isEmpty(staff)) {
                        Optional<Record> staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                        if (staffRecord.isPresent()) {
                            GraduationDesignTeacher graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher.class);
                            modelMap.addAttribute("graduationDesignTeacherId", graduationDesignTeacher.getGraduationDesignTeacherId());
                            modelMap.addAttribute("currUseIsStaff", 1);
                            canUse = true;
                        }
                    }
                }

                if (canUse) {
                    modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                    page = "web/graduate/design/proposal/design_proposal_group::#page-wrapper";
                } else {
                    page = methodControllerCommon.showTip(modelMap, "您不符合进入条件");
                }
            } else {
                page = methodControllerCommon.showTip(modelMap, "请等待确认毕业设计指导教师调整后查看");
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 我的资料数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/proposal/my/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignDatumBean> myData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Users users = usersService.getUserFromSession();
                Student student = studentService.findByUsername(users.getUsername());
                ErrorBean<GraduationDesignRelease> errorBean = studentCondition(graduationDesignReleaseId, student.getStudentId());
                if (!errorBean.isHasError()) {
                    GraduationDesignTutor graduationDesignTutor = (GraduationDesignTutor) errorBean.getMapData().get("graduationDesignTutor");
                    // 前台数据标题 注：要和前台标题顺序一致，获取order用
                    List<String> headers = new ArrayList<>();
                    headers.add("original_file_name");
                    headers.add("graduation_design_datum_type_name");
                    headers.add("version");
                    headers.add("update_time");
                    headers.add("operator");
                    GraduationDesignDatumBean otherCondition = new GraduationDesignDatumBean();
                    dataTablesUtils = new DataTablesUtils<>(request, headers);
                    List<GraduationDesignDatumBean> graduationDesignDatumBeens = new ArrayList<>();
                    otherCondition.setGraduationDesignTutorId(graduationDesignTutor.getGraduationDesignTutorId());
                    Result<Record> records = graduationDesignDatumService.findAllByPage(dataTablesUtils, otherCondition);
                    if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                        graduationDesignDatumBeens = records.into(GraduationDesignDatumBean.class);
                        GraduationDesignDatumBeenFilter(graduationDesignDatumBeens);
                    }
                    dataTablesUtils.setData(graduationDesignDatumBeens);
                    dataTablesUtils.setiTotalRecords(graduationDesignDatumService.countAll(otherCondition));
                    dataTablesUtils.setiTotalDisplayRecords(graduationDesignDatumService.countByCondition(dataTablesUtils, otherCondition));
                }
            }
        }
        return dataTablesUtils;
    }

    /**
     * 小组数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/proposal/team/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignDatumBean> teamData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    // 前台数据标题 注：要和前台标题顺序一致，获取order用
                    List<String> headers = new ArrayList<>();
                    headers.add("real_name");
                    headers.add("student_number");
                    headers.add("organize_name");
                    headers.add("original_file_name");
                    headers.add("version");
                    headers.add("update_time");
                    headers.add("operator");
                    dataTablesUtils = new DataTablesUtils<>(request, headers);
                    GraduationDesignDatumBean otherCondition = new GraduationDesignDatumBean();
                    int staffId = NumberUtils.toInt(request.getParameter("staffId"));
                    otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
                    otherCondition.setStaffId(staffId);
                    List<GraduationDesignDatumBean> graduationDesignDatumBeens = new ArrayList<>();
                    Result<Record> records = graduationDesignDatumService.findTeamAllByPage(dataTablesUtils, otherCondition);
                    if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                        graduationDesignDatumBeens = records.into(GraduationDesignDatumBean.class);
                        GraduationDesignDatumBeenFilter(graduationDesignDatumBeens);
                    }
                    dataTablesUtils.setData(graduationDesignDatumBeens);
                    dataTablesUtils.setiTotalRecords(graduationDesignDatumService.countTeamAll(otherCondition));
                    dataTablesUtils.setiTotalDisplayRecords(graduationDesignDatumService.countTeamByCondition(dataTablesUtils, otherCondition));
                }
            }
        }
        return dataTablesUtils;
    }

    /**
     * 过滤数据，保护隐私
     *
     * @param graduationDesignDatumBeens 数据
     */
    private void GraduationDesignDatumBeenFilter(List<GraduationDesignDatumBean> graduationDesignDatumBeens) {
        graduationDesignDatumBeens.forEach(i -> {
            i.setUpdateTimeStr(DateTimeUtils.formatDate(i.getUpdateTime()));
            i.setRelativePath("");
        });
    }

    /**
     * 组内资料数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/proposal/group/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignDatumGroupBean> groupData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        String graduationDesignTeacherId = request.getParameter("graduationDesignTeacherId");
        DataTablesUtils<GraduationDesignDatumGroupBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    // 前台数据标题 注：要和前台标题顺序一致，获取order用
                    List<String> headers = new ArrayList<>();
                    headers.add("original_file_name");
                    headers.add("upload_time");
                    headers.add("operator");
                    dataTablesUtils = new DataTablesUtils<>(request, headers);
                    GraduationDesignDatumGroupBean otherCondition = new GraduationDesignDatumGroupBean();
                    otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
                    otherCondition.setGraduationDesignTeacherId(graduationDesignTeacherId);
                    List<GraduationDesignDatumGroupBean> graduationDesignDatumGroupBeens = new ArrayList<>();
                    Result<Record> records = graduationDesignDatumGroupService.findAllByPage(dataTablesUtils, otherCondition);
                    if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                        graduationDesignDatumGroupBeens = records.into(GraduationDesignDatumGroupBean.class);
                        graduationDesignDatumGroupBeens.forEach(i -> {
                            i.setUploadTimeStr(DateTimeUtils.formatDate(i.getUploadTime()));
                            i.setRelativePath("");
                        });
                    }
                    dataTablesUtils.setData(graduationDesignDatumGroupBeens);
                    dataTablesUtils.setiTotalRecords(graduationDesignDatumGroupService.countAll(otherCondition));
                    dataTablesUtils.setiTotalDisplayRecords(graduationDesignDatumGroupService.countByCondition(dataTablesUtils, otherCondition));
                }
            }
        }
        return dataTablesUtils;
    }

    /**
     * 保存或更新毕业设计资料
     *
     * @param graduationDesignProposalAddVo 数据
     * @param multipartHttpServletRequest   文件
     * @param request                       请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/proposal/my/save")
    @ResponseBody
    public AjaxUtils<FileBean> mySave(@Valid GraduationDesignProposalAddVo graduationDesignProposalAddVo, BindingResult bindingResult,
                                      MultipartHttpServletRequest multipartHttpServletRequest, HttpServletRequest request) {
        AjaxUtils<FileBean> ajaxUtils = AjaxUtils.of();
        try {
            if (!bindingResult.hasErrors()) {
                // 学生使用
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    Users users = usersService.getUserFromSession();
                    Student student = studentService.findByUsername(users.getUsername());
                    ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignProposalAddVo.getGraduationDesignReleaseId());
                    if (!errorBean.isHasError()) {
                        GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                        // 毕业时间范围
                        if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                            // 是否已确认调整
                            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                                // 是否符合该毕业设计条件
                                Optional<Record> record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.getStudentId(), graduationDesignRelease.getGraduationDesignReleaseId());
                                if (record.isPresent()) {
                                    boolean canUse = true;
                                    GraduationDesignTutor graduationDesignTutor = record.get().into(GraduationDesignTutor.class);
                                    // 是否更新
                                    Optional<Record> recordDatum =
                                            graduationDesignDatumService.findByGraduationDesignTutorIdAndGraduationDesignDatumTypeId(graduationDesignTutor.getGraduationDesignTutorId(), graduationDesignProposalAddVo.getGraduationDesignDatumTypeId());
                                    GraduationDesignDatum graduationDesignDatum = null;
                                    boolean isUpdate = false;
                                    if (recordDatum.isPresent()) {
                                        GraduationDesignDatumBean graduationDesignDatumBean = recordDatum.get().into(GraduationDesignDatumBean.class);
                                        if (student.getStudentId() == graduationDesignDatumBean.getStudentId()) {
                                            graduationDesignDatum = recordDatum.get().into(GraduationDesignDatum.class);
                                            isUpdate = true;
                                        } else {
                                            canUse = false;
                                        }
                                    } else {
                                        graduationDesignDatum = new GraduationDesignDatum();
                                    }
                                    if (canUse) {
                                        String path = Workbook.graduationDesignProposalPath(users);
                                        List<FileBean> fileBeen = uploadService.upload(multipartHttpServletRequest,
                                                RequestUtils.getRealPath(request) + path, request.getRemoteAddr());
                                        if (!ObjectUtils.isEmpty(fileBeen) && fileBeen.size() > 0) {
                                            String fileId = UUIDUtils.getUUID();
                                            FileBean tempFile = fileBeen.get(0);
                                            Files files = new Files();
                                            files.setFileId(fileId);
                                            files.setExt(tempFile.getExt());
                                            files.setNewName(tempFile.getNewName());
                                            files.setOriginalFileName(tempFile.getOriginalFileName());
                                            files.setSize(String.valueOf(tempFile.getSize()));
                                            files.setRelativePath(path + tempFile.getNewName());
                                            filesService.save(files);
                                            if (isUpdate) {
                                                // 删除旧文件
                                                Files oldFile = filesService.findById(graduationDesignDatum.getFileId());
                                                FilesUtils.deleteFile(RequestUtils.getRealPath(request) + oldFile.getRelativePath());
                                                graduationDesignDatum.setFileId(fileId);
                                                graduationDesignDatum.setUpdateTime(DateTimeUtils.getNow());
                                                graduationDesignDatum.setVersion(graduationDesignProposalAddVo.getVersion());
                                                graduationDesignDatumService.update(graduationDesignDatum);
                                                filesService.deleteById(oldFile.getFileId());
                                            } else {
                                                graduationDesignDatum.setGraduationDesignDatumId(UUIDUtils.getUUID());
                                                graduationDesignDatum.setFileId(fileId);
                                                graduationDesignDatum.setUpdateTime(DateTimeUtils.getNow());
                                                graduationDesignDatum.setGraduationDesignTutorId(graduationDesignTutor.getGraduationDesignTutorId());
                                                graduationDesignDatum.setVersion(graduationDesignProposalAddVo.getVersion());
                                                graduationDesignDatum.setGraduationDesignDatumTypeId(graduationDesignProposalAddVo.getGraduationDesignDatumTypeId());
                                                graduationDesignDatumService.save(graduationDesignDatum);
                                            }
                                            ajaxUtils.success().msg("保存成功");
                                        } else {
                                            ajaxUtils.fail().msg("未查询到文件信息");
                                        }
                                    } else {
                                        ajaxUtils.fail().msg("无法核实该文件属于您");
                                    }
                                } else {
                                    ajaxUtils.fail().msg("您的账号不符合该毕业设计条件");
                                }
                            } else {
                                ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                            }
                        } else {
                            ajaxUtils.fail().msg("不在时间范围，无法操作");
                        }
                    } else {
                        ajaxUtils.fail().msg(errorBean.getErrorMsg());
                    }
                } else {
                    ajaxUtils.fail().msg("目前仅提供学生使用");
                }
            } else {
                ajaxUtils.fail().msg("参数异常");
            }
        } catch (Exception e) {
            log.error("Upload graduation design proposal error, error is {}", e);
            ajaxUtils.fail().msg("保存文件异常");
        }
        return ajaxUtils;
    }

    /**
     * 删除文件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param graduationDesignDatumId   毕业资料id
     * @param request                   请求
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/proposal/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils proposalDelete(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("graduationDesignDatumId") String graduationDesignDatumId, HttpServletRequest request) {
        AjaxUtils<FileBean> ajaxUtils = AjaxUtils.of();
        try {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    // 是否已确认调整
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                        Users users = usersService.getUserFromSession();
                        GraduationDesignDatum graduationDesignDatum = canUseCondition(graduationDesignDatumId, users);
                        if (!ObjectUtils.isEmpty(graduationDesignDatum)) {
                            Files files = filesService.findById(graduationDesignDatum.getFileId());
                            if (!ObjectUtils.isEmpty(files)) {
                                FilesUtils.deleteFile(RequestUtils.getRealPath(request) + files.getRelativePath());
                                graduationDesignDatumService.deleteById(graduationDesignDatumId);
                                filesService.deleteById(files.getFileId());
                                ajaxUtils.success().msg("删除成功");
                            } else {
                                ajaxUtils.fail().msg("未查询到该文件信息");
                            }
                        } else {
                            ajaxUtils.fail().msg("不符合条件，删除失败");
                        }
                    } else {
                        ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                    }
                } else {
                    ajaxUtils.fail().msg("不在时间范围，无法操作");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } catch (IOException e) {
            log.error("Delete graduation design proposal error, error is {}", e);
            ajaxUtils.fail().msg("保存文件异常");
        }
        return ajaxUtils;
    }

    /**
     * 下载文件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param graduationDesignDatumId   毕业资料id
     * @param request                   请求
     */
    @RequestMapping(value = "/web/graduate/design/proposal/download", method = RequestMethod.GET)
    public void download(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("graduationDesignDatumId") String graduationDesignDatumId, HttpServletResponse response, HttpServletRequest request) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                boolean canUse = false;
                Users users = usersService.getUserFromSession();
                GraduationDesignDatum graduationDesignDatum = null;
                if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)
                        || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                    graduationDesignDatum = graduationDesignDatumService.findById(graduationDesignDatumId);
                    canUse = true;
                } else {
                    // 学生
                    if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                        Optional<Record> record = graduationDesignDatumService.findByIdRelation(graduationDesignDatumId);
                        if (record.isPresent()) {
                            Student student = studentService.findByUsername(users.getUsername());
                            GraduationDesignDatumBean graduationDesignDatumBean = record.get().into(GraduationDesignDatumBean.class);
                            if (student.getStudentId() == graduationDesignDatumBean.getStudentId()) {
                                graduationDesignDatum = record.get().into(GraduationDesignDatum.class);
                                canUse = true;
                            }
                        }
                    } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                        graduationDesignDatum = graduationDesignDatumService.findById(graduationDesignDatumId);
                        canUse = true;
                    }
                }

                if (canUse) {
                    Files files = filesService.findById(graduationDesignDatum.getFileId());
                    if (!ObjectUtils.isEmpty(files)) {
                        uploadService.download(files.getOriginalFileName() + "V" + graduationDesignDatum.getVersion(), "/" + files.getRelativePath(), response, request);
                    }
                }

            }
        }
    }

    /**
     * 获取毕业设计资料信息
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param graduationDesignDatumId   毕业设计资料id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/proposal/team/datum", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<GraduationDesignDatumBean> teamDatum(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("graduationDesignDatumId") String graduationDesignDatumId) {
        AjaxUtils<GraduationDesignDatumBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Optional<Record> record = graduationDesignDatumService.findByIdRelation(graduationDesignDatumId);
            if (record.isPresent()) {
                GraduationDesignDatumBean graduationDesignDatum = record.get().into(GraduationDesignDatumBean.class);
                ajaxUtils.success().msg("获取数据成功").obj(graduationDesignDatum);
            } else {
                ajaxUtils.fail().msg("未查询到相关毕业设计资料信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 更新毕业设计资料
     *
     * @param graduationDesignProposalAddVo 数据
     * @param multipartHttpServletRequest   文件
     * @param request                       请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/proposal/team/update")
    @ResponseBody
    public AjaxUtils<FileBean> teamUpdate(@Valid GraduationDesignProposalAddVo graduationDesignProposalAddVo, BindingResult bindingResult,
                                          MultipartHttpServletRequest multipartHttpServletRequest, HttpServletRequest request) {
        AjaxUtils<FileBean> ajaxUtils = AjaxUtils.of();
        try {
            if (!bindingResult.hasErrors() && StringUtils.hasLength(graduationDesignProposalAddVo.getGraduationDesignDatumId())) {
                ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignProposalAddVo.getGraduationDesignReleaseId());
                if (!errorBean.isHasError()) {
                    GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                    // 毕业时间范围
                    if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                        // 是否已确认调整
                        if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                            Users users = usersService.getUserFromSession();
                            GraduationDesignDatum graduationDesignDatum = canUseCondition(graduationDesignProposalAddVo.getGraduationDesignDatumId(), users);
                            if (!ObjectUtils.isEmpty(graduationDesignDatum)) {
                                String path = Workbook.graduationDesignProposalPath(users);
                                List<FileBean> fileBeen = uploadService.upload(multipartHttpServletRequest,
                                        RequestUtils.getRealPath(request) + path, request.getRemoteAddr());
                                if (!ObjectUtils.isEmpty(fileBeen) && fileBeen.size() > 0) {
                                    String fileId = UUIDUtils.getUUID();
                                    FileBean tempFile = fileBeen.get(0);
                                    Files files = new Files();
                                    files.setFileId(fileId);
                                    files.setExt(tempFile.getExt());
                                    files.setNewName(tempFile.getNewName());
                                    files.setOriginalFileName(tempFile.getOriginalFileName());
                                    files.setSize(String.valueOf(tempFile.getSize()));
                                    files.setRelativePath(path + tempFile.getNewName());
                                    filesService.save(files);
                                    Files oldFile = filesService.findById(graduationDesignDatum.getFileId());
                                    FilesUtils.deleteFile(RequestUtils.getRealPath(request) + oldFile.getRelativePath());
                                    graduationDesignDatum.setFileId(fileId);
                                    graduationDesignDatum.setUpdateTime(DateTimeUtils.getNow());
                                    graduationDesignDatum.setVersion(graduationDesignProposalAddVo.getVersion());
                                    graduationDesignDatumService.update(graduationDesignDatum);
                                    filesService.deleteById(oldFile.getFileId());
                                    ajaxUtils.success().msg("保存成功");
                                } else {
                                    ajaxUtils.fail().msg("未查询到文件信息");
                                }
                            } else {
                                ajaxUtils.fail().msg("不符合保存条件，保存失败");
                            }
                        } else {
                            ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                        }
                    } else {
                        ajaxUtils.fail().msg("不在时间范围，无法操作");
                    }
                } else {
                    ajaxUtils.fail().msg(errorBean.getErrorMsg());
                }
            } else {
                ajaxUtils.fail().msg("参数异常");
            }
        } catch (Exception e) {
            log.error("Upload graduation design proposal error, error is {}", e);
            ajaxUtils.fail().msg("保存文件异常");
        }
        return ajaxUtils;
    }

    /**
     * 保存组内资料
     *
     * @param graduationDesignReleaseId   毕业设计发布oid
     * @param multipartHttpServletRequest 文件
     * @param request                     请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/proposal/group/save")
    @ResponseBody
    public AjaxUtils<FileBean> groupSave(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId,
                                         MultipartHttpServletRequest multipartHttpServletRequest, HttpServletRequest request) {
        AjaxUtils<FileBean> ajaxUtils = AjaxUtils.of();
        try {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    // 是否已确认调整
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                        Users users = usersService.getUserFromSession();
                        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                            Staff staff = staffService.findByUsername(users.getUsername());
                            if (!ObjectUtils.isEmpty(staff)) {
                                Optional<Record> staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                                if (staffRecord.isPresent()) {
                                    GraduationDesignTeacher graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher.class);
                                    String path = Workbook.graduationDesignProposalPath(users);
                                    List<FileBean> fileBeen = uploadService.upload(multipartHttpServletRequest,
                                            RequestUtils.getRealPath(request) + path, request.getRemoteAddr());
                                    if (!ObjectUtils.isEmpty(fileBeen) && fileBeen.size() > 0) {
                                        String fileId = UUIDUtils.getUUID();
                                        FileBean tempFile = fileBeen.get(0);
                                        Files files = new Files();
                                        files.setFileId(fileId);
                                        files.setExt(tempFile.getExt());
                                        files.setNewName(tempFile.getNewName());
                                        files.setOriginalFileName(tempFile.getOriginalFileName());
                                        files.setSize(String.valueOf(tempFile.getSize()));
                                        files.setRelativePath(path + tempFile.getNewName());
                                        filesService.save(files);

                                        GraduationDesignDatumGroup graduationDesignDatumGroup = new GraduationDesignDatumGroup();
                                        graduationDesignDatumGroup.setGraduationDesignDatumGroupId(UUIDUtils.getUUID());
                                        graduationDesignDatumGroup.setFileId(fileId);
                                        graduationDesignDatumGroup.setGraduationDesignTeacherId(graduationDesignTeacher.getGraduationDesignTeacherId());
                                        graduationDesignDatumGroup.setUploadTime(new Timestamp(Clock.systemDefaultZone().millis()));
                                        graduationDesignDatumGroupService.save(graduationDesignDatumGroup);
                                        ajaxUtils.success().msg("保存成功");
                                    } else {
                                        ajaxUtils.fail().msg("保存文件失败");
                                    }
                                } else {
                                    ajaxUtils.fail().msg("您不是该毕业设计指导教师");
                                }
                            } else {
                                ajaxUtils.fail().msg("未查询到教职工信息");
                            }
                        } else {
                            ajaxUtils.fail().msg("您的注册类型不符合进入条件");
                        }
                    } else {
                        ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                    }
                } else {
                    ajaxUtils.fail().msg("不在时间范围，无法操作");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }

        } catch (Exception e) {
            log.error("Upload graduation design proposal error, error is {}", e);
            ajaxUtils.fail().msg("保存文件异常");
        }
        return ajaxUtils;
    }

    /**
     * 删除组内资料
     *
     * @param graduationDesignReleaseId    毕业设计发布oid
     * @param graduationDesignDatumGroupId 组内资料id
     * @param request                      请求
     * @return true or false
     */
    @RequestMapping("/web/graduate/design/proposal/group/del")
    @ResponseBody
    public AjaxUtils<FileBean> groupDel(@RequestParam("id") String graduationDesignReleaseId,
                                        @RequestParam("graduationDesignDatumGroupId") String graduationDesignDatumGroupId, HttpServletRequest request) {
        AjaxUtils<FileBean> ajaxUtils = AjaxUtils.of();
        try {
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    // 是否已确认调整
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                        Users users = usersService.getUserFromSession();
                        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                            Staff staff = staffService.findByUsername(users.getUsername());
                            if (!ObjectUtils.isEmpty(staff)) {
                                Optional<Record> staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                                if (staffRecord.isPresent()) {
                                    GraduationDesignTeacher graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher.class);
                                    GraduationDesignDatumGroup graduationDesignDatumGroup = graduationDesignDatumGroupService.findById(graduationDesignDatumGroupId);
                                    if (!ObjectUtils.isEmpty(graduationDesignDatumGroup)) {
                                        if (graduationDesignTeacher.getGraduationDesignTeacherId().equals(graduationDesignDatumGroup.getGraduationDesignTeacherId())) {
                                            Files files = filesService.findById(graduationDesignDatumGroup.getFileId());
                                            if (!ObjectUtils.isEmpty(files)) {
                                                FilesUtils.deleteFile(RequestUtils.getRealPath(request) + files.getRelativePath());
                                                graduationDesignDatumGroupService.delete(graduationDesignDatumGroup);
                                                filesService.deleteById(files.getFileId());
                                                ajaxUtils.success().msg("删除文件成功");
                                            } else {
                                                ajaxUtils.fail().msg("未查询到文件信息");
                                            }
                                        } else {
                                            ajaxUtils.fail().msg("该文件不属于您");
                                        }
                                    } else {
                                        ajaxUtils.fail().msg("未查询到组内文件信息");
                                    }
                                } else {
                                    ajaxUtils.fail().msg("您不是该毕业设计指导教师");
                                }
                            } else {
                                ajaxUtils.fail().msg("未查询到教职工信息");
                            }
                        } else {
                            ajaxUtils.fail().msg("您的注册类型不符合进入条件");
                        }
                    } else {
                        ajaxUtils.fail().msg("未确认毕业设计指导教师调整");
                    }
                } else {
                    ajaxUtils.fail().msg("不在时间范围，无法操作");
                }
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } catch (Exception e) {
            log.error("Delete graduation design proposal file error, error is {}", e);
            ajaxUtils.fail().msg("删除文件异常");
        }
        return ajaxUtils;
    }

    /**
     * 下载组内资料
     *
     * @param graduationDesignReleaseId    毕业设计发布oid
     * @param graduationDesignDatumGroupId 组内资料id
     * @param request                      请求
     * @param response                     响应
     */
    @RequestMapping("/web/graduate/design/proposal/group/download")
    @ResponseBody
    public void groupDownload(@RequestParam("id") String graduationDesignReleaseId,
                              @RequestParam("graduationDesignDatumGroupId") String graduationDesignDatumGroupId,
                              HttpServletRequest request, HttpServletResponse response) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                Users users = usersService.getUserFromSession();
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                    if (studentRecord.isPresent()) {
                        Student student = studentRecord.get().into(Student.class);
                        if (!ObjectUtils.isEmpty(student)) {
                            Optional<Record> staffRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.getStudentId(), graduationDesignReleaseId);
                            if (staffRecord.isPresent()) {
                                GraduationDesignTeacher graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher.class);
                                GraduationDesignDatumGroup graduationDesignDatumGroup = graduationDesignDatumGroupService.findById(graduationDesignDatumGroupId);
                                groupDownloadBuild(graduationDesignTeacher, graduationDesignDatumGroup, request, response);
                            }
                        }
                    }
                } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    Staff staff = staffService.findByUsername(users.getUsername());
                    if (!ObjectUtils.isEmpty(staff)) {
                        Optional<Record> staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                        if (staffRecord.isPresent()) {
                            GraduationDesignTeacher graduationDesignTeacher = staffRecord.get().into(GraduationDesignTeacher.class);
                            GraduationDesignDatumGroup graduationDesignDatumGroup = graduationDesignDatumGroupService.findById(graduationDesignDatumGroupId);
                            groupDownloadBuild(graduationDesignTeacher, graduationDesignDatumGroup, request, response);
                        }
                    }
                }
            }
        }
    }

    /**
     * 组内资料下载构建
     *
     * @param graduationDesignTeacher    指导教师信息
     * @param graduationDesignDatumGroup 组内资料信息
     * @param request                    请求
     * @param response                   响应
     */
    private void groupDownloadBuild(GraduationDesignTeacher graduationDesignTeacher, GraduationDesignDatumGroup graduationDesignDatumGroup,
                                    HttpServletRequest request, HttpServletResponse response) {
        if (!ObjectUtils.isEmpty(graduationDesignDatumGroup)) {
            if (graduationDesignTeacher.getGraduationDesignTeacherId().equals(graduationDesignDatumGroup.getGraduationDesignTeacherId())) {
                Files files = filesService.findById(graduationDesignDatumGroup.getFileId());
                if (!ObjectUtils.isEmpty(files)) {
                    uploadService.download(files.getOriginalFileName(), "/" + files.getRelativePath(), response, request);
                }
            }
        }
    }

    /**
     * 团队进入条件入口
     *
     * @param graduationDesignDatumId 毕业资料id
     * @param users                   用户信息
     * @return 毕业资料数据
     */
    private GraduationDesignDatum canUseCondition(String graduationDesignDatumId, Users users) {
        GraduationDesignDatum graduationDesignDatum = null;
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)
                || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            graduationDesignDatum = graduationDesignDatumService.findById(graduationDesignDatumId);
        } else {
            // 学生
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Optional<Record> record = graduationDesignDatumService.findByIdRelation(graduationDesignDatumId);
                if (record.isPresent()) {
                    Student student = studentService.findByUsername(users.getUsername());
                    GraduationDesignDatumBean graduationDesignDatumBean = record.get().into(GraduationDesignDatumBean.class);

                    if (student.getStudentId() == graduationDesignDatumBean.getStudentId()) {
                        graduationDesignDatum = record.get().into(GraduationDesignDatum.class);
                    }
                }
            } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                Optional<Record> staffRecord = graduationDesignDatumService.findByIdRelation(graduationDesignDatumId);
                if (staffRecord.isPresent()) {
                    Staff staff = staffService.findByUsername(users.getUsername());
                    GraduationDesignDatumBean graduationDesignDatumBean = staffRecord.get().into(GraduationDesignDatumBean.class);
                    if (staff.getStaffId() == graduationDesignDatumBean.getStaffId()) {
                        graduationDesignDatum = staffRecord.get().into(GraduationDesignDatum.class);
                    }
                }
            }
        }
        return graduationDesignDatum;
    }

    /**
     * 我的资料页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/proposal/my/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Users users = usersService.getUserFromSession();
            Student student = studentService.findByUsername(users.getUsername());
            ErrorBean<GraduationDesignRelease> errorBean = studentCondition(graduationDesignReleaseId, student.getStudentId());
            if (!errorBean.isHasError()) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg(errorBean.getErrorMsg());
            }
        } else {
            ajaxUtils.fail().msg("目前仅提供学生使用");
        }
        return ajaxUtils;
    }

    /**
     * 小组页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/proposal/team/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils teamCondition(@RequestParam("id") String graduationDesignReleaseId) {
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
     * 组内资料页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/proposal/group/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils groupCondition(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = groupAccessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
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
    @RequestMapping(value = "/web/graduate/design/proposal/condition", method = RequestMethod.POST)
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

    /**
     * 进入学生资料条件
     *
     * @param graduationDesignReleaseId 发布
     * @param studentId                 学生id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> studentCondition(String graduationDesignReleaseId, int studentId) {
        Map<String, Object> dataMap = new HashMap<>();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                Optional<Record> record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(studentId, graduationDesignReleaseId);
                if (record.isPresent()) {
                    GraduationDesignTutor graduationDesignTutor = record.get().into(GraduationDesignTutor.class);
                    dataMap.put("graduationDesignTutor", graduationDesignTutor);
                    errorBean.setHasError(false);
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("您不符合该毕业设计条件");
                }
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("请等待确认毕业设计指导教师调整后查看");
            }
            errorBean.setMapData(dataMap);
        }

        return errorBean;
    }

    /**
     * 组内资料页面判断条件Id
     *
     * @param graduationDesignReleaseId 毕业设计发布
     * @return 条件
     */
    private ErrorBean<GraduationDesignRelease> groupAccessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 是否已确认调整
            if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                boolean canUse = false;
                Users users = usersService.getUserFromSession();
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                    Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                    if (studentRecord.isPresent()) {
                        Student student = studentRecord.get().into(Student.class);
                        if (!ObjectUtils.isEmpty(student)) {
                            Optional<Record> staffRecord = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.getStudentId(), graduationDesignReleaseId);
                            if (staffRecord.isPresent()) {
                                canUse = true;
                            }
                        }
                    }
                } else if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    Staff staff = staffService.findByUsername(users.getUsername());
                    if (!ObjectUtils.isEmpty(staff)) {
                        Optional<Record> staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                        if (staffRecord.isPresent()) {
                            canUse = true;
                        }
                    }
                }

                if (canUse) {
                    errorBean.setHasError(false);
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("您不符合进入条件");
                }
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("请等待确认毕业设计指导教师调整后查看");
            }
        }
        return errorBean;
    }
}
