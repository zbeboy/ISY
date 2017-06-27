package top.zbeboy.isy.web.graduate.design.proposal;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.common.FilesService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignDatumService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignDatumTypeService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.FilesUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.file.FileBean;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.graduate.design.proposal.GraduationDesignProposalAddVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * Created by zbeboy on 2017/6/22.
 */
@Slf4j
@Controller
public class GraduationDesignProposalController {

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignDatumService graduationDesignDatumService;

    @Resource
    private GraduationDesignDatumTypeService graduationDesignDatumTypeService;

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
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
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
                modelMap.addAttribute("studentId", student.getStudentId());
                page = "web/graduate/design/proposal/design_proposal_my::#page-wrapper";
            } else {
                page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, "目前仅提供学生使用");
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
                    headers.add("select");
                    headers.add("original_file_name");
                    headers.add("graduation_design_datum_type_name");
                    headers.add("version");
                    headers.add("update_time");
                    headers.add("operator");
                    GraduationDesignDatumBean otherCondition = new GraduationDesignDatumBean();
                    dataTablesUtils = new DataTablesUtils<>(request, headers);
                    List<GraduationDesignDatumBean> graduationDesignDatumBeens = new ArrayList<>();
                    otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
                    otherCondition.setGraduationDesignTutorId(graduationDesignTutor.getGraduationDesignTutorId());
                    Result<Record> records = graduationDesignDatumService.findAllByPage(dataTablesUtils, otherCondition);
                    if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                        graduationDesignDatumBeens = records.into(GraduationDesignDatumBean.class);
                        graduationDesignDatumBeens.forEach(i -> i.setUpdateTimeStr(DateTimeUtils.formatDate(i.getUpdateTime())));
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
                                    GraduationDesignTutor graduationDesignTutor = record.get().into(GraduationDesignTutor.class);
                                    // 是否更新
                                    Optional<Record> recordDatum =
                                            graduationDesignDatumService.findByGraduationDesignTutorIdAndGraduationDesignDatumTypeId(graduationDesignTutor.getGraduationDesignTutorId(), graduationDesignProposalAddVo.getGraduationDesignDatumTypeId());
                                    GraduationDesignDatum graduationDesignDatum;
                                    boolean isUpdate = false;
                                    if (recordDatum.isPresent()) {
                                        graduationDesignDatum = recordDatum.get().into(GraduationDesignDatum.class);
                                        isUpdate = true;
                                    } else {
                                        graduationDesignDatum = new GraduationDesignDatum();
                                    }
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
                                    ajaxUtils.fail().msg("您的账号不符合该毕业设计条件");
                                }
                            } else {
                                ajaxUtils.fail().msg("未确认指导教师调整");
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
     * 我的资料页面删除文件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param graduationDesignDatumId   毕业资料id
     * @param request                   请求
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/proposal/my/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myDelete(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("graduationDesignDatumId") String graduationDesignDatumId, HttpServletRequest request) {
        AjaxUtils<FileBean> ajaxUtils = AjaxUtils.of();
        try {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Users users = usersService.getUserFromSession();
                Student student = studentService.findByUsername(users.getUsername());
                ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
                if (!errorBean.isHasError()) {
                    GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                    // 毕业时间范围
                    if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                        // 是否已确认调整
                        if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                            // 是否符合该毕业设计条件
                            Optional<Record> record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.getStudentId(), graduationDesignRelease.getGraduationDesignReleaseId());
                            if (record.isPresent()) {
                                GraduationDesignDatum graduationDesignDatum = graduationDesignDatumService.findById(graduationDesignDatumId);
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
                                    ajaxUtils.fail().msg("未查询到该资料信息");
                                }
                            } else {
                                ajaxUtils.fail().msg("您的账号不符合该毕业设计条件");
                            }
                        } else {
                            ajaxUtils.fail().msg("未确认指导教师调整");
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
        } catch (IOException e) {
            log.error("Delete graduation design proposal error, error is {}", e);
            ajaxUtils.fail().msg("保存文件异常");
        }
        return ajaxUtils;
    }

    /**
     * 我的资料页面下载文件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param graduationDesignDatumId   毕业资料id
     * @param request                   请求
     */
    @RequestMapping(value = "/web/graduate/design/proposal/my/download", method = RequestMethod.GET)
    public void myDownload(@RequestParam("id") String graduationDesignReleaseId, @RequestParam("graduationDesignDatumId") String graduationDesignDatumId, HttpServletResponse response, HttpServletRequest request) {
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Users users = usersService.getUserFromSession();
            Student student = studentService.findByUsername(users.getUsername());
            ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
            if (!errorBean.isHasError()) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    // 是否已确认调整
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                        // 是否符合该毕业设计条件
                        Optional<Record> record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(student.getStudentId(), graduationDesignRelease.getGraduationDesignReleaseId());
                        if (record.isPresent()) {
                            GraduationDesignDatum graduationDesignDatum = graduationDesignDatumService.findById(graduationDesignDatumId);
                            if (!ObjectUtils.isEmpty(graduationDesignDatum)) {
                                Files files = filesService.findById(graduationDesignDatum.getFileId());
                                if (!ObjectUtils.isEmpty(files)) {
                                    uploadService.download(files.getOriginalFileName() + "V" + graduationDesignDatum.getVersion(), "/" + files.getRelativePath(), response, request);
                                }
                            }
                        }
                    }
                }
            }
        }
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
                errorBean.setErrorMsg("请等待确认调整后查看");
            }
            errorBean.setMapData(dataMap);
        }

        return errorBean;
    }
}
