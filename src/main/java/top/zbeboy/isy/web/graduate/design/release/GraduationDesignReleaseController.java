package top.zbeboy.isy.web.graduate.design.release;

import com.alibaba.fastjson.JSON;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.domain.tables.records.GraduationDesignReleaseRecord;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.common.FilesService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.data.CollegeService;
import top.zbeboy.isy.service.data.DepartmentService;
import top.zbeboy.isy.service.data.SchoolService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseFileService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.FilesUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.file.FileBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;
import top.zbeboy.isy.web.vo.graduate.design.release.GraduationDesignReleaseAddVo;
import top.zbeboy.isy.web.vo.graduate.design.release.GraduationDesignReleaseUpdateVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/5.
 */
@Controller
public class GraduationDesignReleaseController {

    private final Logger log = LoggerFactory.getLogger(GraduationDesignReleaseController.class);

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignReleaseFileService graduationDesignReleaseFileService;

    @Resource
    private UsersService usersService;

    @Resource
    private UploadService uploadService;

    @Resource
    private SchoolService schoolService;

    @Resource
    private CollegeService collegeService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private FilesService filesService;

    @Resource
    private RoleService roleService;

    /**
     * 毕业设计发布
     *
     * @return 毕业设计发布页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/release", method = RequestMethod.GET)
    public String releaseData() {
        return "web/graduate/design/release/design_release::#page-wrapper";
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/release/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> releaseDatas(PaginationUtils paginationUtils) {
        GraduationDesignReleaseBean graduationDesignReleaseBean = new GraduationDesignReleaseBean();
        Map<String, Integer> commonData = commonControllerMethodService.accessRoleCondition();
        graduationDesignReleaseBean.setDepartmentId(StringUtils.isEmpty(commonData.get("departmentId")) ? -1 : commonData.get("departmentId"));
        graduationDesignReleaseBean.setCollegeId(StringUtils.isEmpty(commonData.get("collegeId")) ? -1 : commonData.get("collegeId"));
        Result<Record> records = graduationDesignReleaseService.findAllByPage(paginationUtils, graduationDesignReleaseBean);
        List<GraduationDesignReleaseBean> graduationDesignReleaseBeans = graduationDesignReleaseService.dealData(paginationUtils, records, graduationDesignReleaseBean);
        return new AjaxUtils<GraduationDesignReleaseBean>().success().msg("获取数据成功").listData(graduationDesignReleaseBeans).paginationUtils(paginationUtils);
    }

    /**
     * 获取毕业设计发布数据 用于通用列表数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/anyone/graduate/design/release/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> commonDatas(PaginationUtils paginationUtils) {
        Byte isDel = 0;
        GraduationDesignReleaseBean graduationDesignReleaseBean = new GraduationDesignReleaseBean();
        graduationDesignReleaseBean.setGraduationDesignIsDel(isDel);
        Map<String, Integer> commonData = commonControllerMethodService.accessRoleCondition();
        graduationDesignReleaseBean.setDepartmentId(StringUtils.isEmpty(commonData.get("departmentId")) ? -1 : commonData.get("departmentId"));
        graduationDesignReleaseBean.setCollegeId(StringUtils.isEmpty(commonData.get("collegeId")) ? -1 : commonData.get("collegeId"));
        Result<Record> records = graduationDesignReleaseService.findAllByPage(paginationUtils, graduationDesignReleaseBean);
        List<GraduationDesignReleaseBean> graduationDesignReleaseBeens = graduationDesignReleaseService.dealData(paginationUtils, records, graduationDesignReleaseBean);
        return new AjaxUtils<GraduationDesignReleaseBean>().success().msg("获取数据成功").listData(graduationDesignReleaseBeens).paginationUtils(paginationUtils);
    }

    /**
     * 毕业设计发布添加页面
     *
     * @param modelMap 页面对象
     * @return 毕业设计发布添加页面
     */
    @RequestMapping(value = "/web/graduate/design/release/add", method = RequestMethod.GET)
    public String releaseAdd(ModelMap modelMap) {
        Map<String, Integer> commonData = commonControllerMethodService.accessRoleCondition();
        modelMap.addAttribute("departmentId", StringUtils.isEmpty(commonData.get("departmentId")) ? -1 : commonData.get("departmentId"));
        modelMap.addAttribute("collegeId", StringUtils.isEmpty(commonData.get("collegeId")) ? -1 : commonData.get("collegeId"));
        return "web/graduate/design/release/design_release_add::#page-wrapper";
    }

    /**
     * 毕业设计发布编辑页面
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 毕业设计发布编辑页面
     */
    @RequestMapping(value = "/web/graduate/design/release/edit", method = RequestMethod.GET)
    public String releaseEdit(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        Optional<Record> records = graduationDesignReleaseService.findByIdRelation(graduationDesignReleaseId);
        GraduationDesignReleaseBean graduationDesignRelease = new GraduationDesignReleaseBean();
        if (records.isPresent()) {
            graduationDesignRelease = records.get().into(GraduationDesignReleaseBean.class);
            graduationDesignRelease.setStartTimeStr(DateTimeUtils.formatDate(graduationDesignRelease.getStartTime()));
            graduationDesignRelease.setEndTimeStr(DateTimeUtils.formatDate(graduationDesignRelease.getEndTime()));
        }
        modelMap.addAttribute("graduationDesignRelease", graduationDesignRelease);
        return "web/graduate/design/release/design_release_edit::#page-wrapper";
    }

    /**
     * 获取毕业设计发布附件数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 毕业设计发布附件数据
     */
    @RequestMapping(value = "/user/graduate/design/files", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<Files> internshipFiles(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId) {
        List<Files> files = new ArrayList<>();
        Result<Record> records = graduationDesignReleaseFileService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
        if (records.isNotEmpty()) {
            files = records.into(Files.class);
        }
        return new AjaxUtils<Files>().success().msg("获取毕业设计附件数据成功").listData(files);
    }

    /**
     * 保存时检验标题
     *
     * @param title 标题
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/release/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("graduationDesignTitle") String title) {
        String graduationDesignTitle = StringUtils.trimWhitespace(title);
        if (StringUtils.hasLength(graduationDesignTitle)) {
            List<GraduationDesignRelease> graduationDesignReleases = graduationDesignReleaseService.findByGraduationDesignTitle(graduationDesignTitle);
            if (ObjectUtils.isEmpty(graduationDesignReleases) && graduationDesignReleases.isEmpty()) {
                return new AjaxUtils().success().msg("标题不重复");
            }
        }
        return new AjaxUtils().fail().msg("标题重复");
    }

    /**
     * 更新时检验标题
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param title                     标题
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/release/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId, @RequestParam("graduationDesignTitle") String title) {
        String releaseTitle = StringUtils.trimWhitespace(title);
        if (StringUtils.hasLength(releaseTitle)) {
            Result<GraduationDesignReleaseRecord> graduationDesignReleaseRecords =
                    graduationDesignReleaseService.findByGraduationDesignTitleNeGraduationDesignReleaseId(releaseTitle, graduationDesignReleaseId);
            if (ObjectUtils.isEmpty(graduationDesignReleaseRecords) && graduationDesignReleaseRecords.isEmpty()) {
                return new AjaxUtils().success().msg("标题不重复");
            }
        }
        return new AjaxUtils().fail().msg("标题重复");
    }

    /**
     * 保存
     *
     * @param graduationDesignReleaseAddVo 毕业设计
     * @param bindingResult                检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/release/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils save(@Valid GraduationDesignReleaseAddVo graduationDesignReleaseAddVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            String graduationDesignReleaseId = UUIDUtils.getUUID();
            String fillTeacherTime = graduationDesignReleaseAddVo.getFillTeacherTime();
            String files = graduationDesignReleaseAddVo.getFiles();
            GraduationDesignRelease graduationDesignRelease = new GraduationDesignRelease();
            graduationDesignRelease.setGraduationDesignReleaseId(graduationDesignReleaseId);
            graduationDesignRelease.setGraduationDesignTitle(graduationDesignReleaseAddVo.getGraduationDesignTitle());
            graduationDesignRelease.setReleaseTime(new Timestamp(Clock.systemDefaultZone().millis()));
            Users users = usersService.getUserFromSession();
            graduationDesignRelease.setUsername(users.getUsername());
            saveOrUpdateTime(graduationDesignRelease, graduationDesignReleaseAddVo.getStartTime(), graduationDesignReleaseAddVo.getEndTime(), fillTeacherTime);
            graduationDesignRelease.setAllowGrade(graduationDesignReleaseAddVo.getGrade());
            graduationDesignRelease.setDepartmentId(graduationDesignReleaseAddVo.getDepartmentId());
            graduationDesignRelease.setScienceId(graduationDesignReleaseAddVo.getScienceId());
            graduationDesignRelease.setGraduationDesignIsDel(graduationDesignReleaseAddVo.getGraduationDesignIsDel());
            graduationDesignReleaseService.save(graduationDesignRelease);
            saveOrUpdateFiles(files, graduationDesignReleaseId);
            return new AjaxUtils().success().msg("保存成功");
        }
        return new AjaxUtils().fail().msg("保存失败");
    }

    /**
     * 更新
     *
     * @param graduationDesignReleaseUpdateVo 数据
     * @param bindingResult                   检验
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/release/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils update(@Valid GraduationDesignReleaseUpdateVo graduationDesignReleaseUpdateVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            String graduationDesignReleaseId = graduationDesignReleaseUpdateVo.getGraduationDesignReleaseId();
            String fillTeacherTime = graduationDesignReleaseUpdateVo.getFillTeacherTime();
            String files = graduationDesignReleaseUpdateVo.getFiles();
            GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
            graduationDesignRelease.setGraduationDesignTitle(graduationDesignReleaseUpdateVo.getGraduationDesignTitle());
            saveOrUpdateTime(graduationDesignRelease, graduationDesignReleaseUpdateVo.getStartTime(), graduationDesignReleaseUpdateVo.getEndTime(), fillTeacherTime);
            graduationDesignRelease.setGraduationDesignIsDel(graduationDesignReleaseUpdateVo.getGraduationDesignIsDel());
            graduationDesignReleaseService.update(graduationDesignRelease);
            Result<Record> records = graduationDesignReleaseFileService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            if (records.isNotEmpty()) {
                graduationDesignReleaseFileService.deleteByGraduationDesignReleaseId(graduationDesignReleaseId);
                List<GraduationDesignReleaseFile> graduationDesignReleaseFiles = records.into(GraduationDesignReleaseFile.class);
                graduationDesignReleaseFiles.forEach(f -> filesService.deleteById(f.getFileId()));
            }
            saveOrUpdateFiles(files, graduationDesignReleaseId);
            return new AjaxUtils().success().msg("保存成功");
        }
        return new AjaxUtils().fail().msg("保存失败");
    }

    /**
     * 更新毕业设计发布状态
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param isDel               注销参数
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/release/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateDel(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId, @RequestParam("isDel") Byte isDel) {
        GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
        graduationDesignRelease.setGraduationDesignIsDel(isDel);
        graduationDesignReleaseService.update(graduationDesignRelease);
        return new AjaxUtils().success().msg("更新状态成功");
    }

    /**
     * 更新或保存时间
     *
     * @param graduationDesignRelease 实习
     * @param startTime               开始时间
     * @param endTime                 结束时间
     * @param fillTeacherTime         学生申报教师时间
     */
    private void saveOrUpdateTime(GraduationDesignRelease graduationDesignRelease, String startTime, String endTime, String fillTeacherTime) {
        try {
            String format = "yyyy-MM-dd HH:mm:ss";
            String[] teacherDistributionArr = DateTimeUtils.splitDateTime("至", fillTeacherTime);
            graduationDesignRelease.setFillTeacherStartTime(DateTimeUtils.formatDateToTimestamp(teacherDistributionArr[0], format));
            graduationDesignRelease.setFillTeacherEndTime(DateTimeUtils.formatDateToTimestamp(teacherDistributionArr[1], format));
            graduationDesignRelease.setStartTime(DateTimeUtils.formatDateToTimestamp(startTime, format));
            graduationDesignRelease.setEndTime(DateTimeUtils.formatDateToTimestamp(endTime, format));
        } catch (ParseException e) {
            log.error(" format time is exception.", e);
        }
    }

    /**
     * 更新或保存文件
     *
     * @param files                     文件json
     * @param graduationDesignReleaseId 毕业设计id
     */
    private void saveOrUpdateFiles(String files, String graduationDesignReleaseId) {
        if (StringUtils.hasLength(files)) {
            List<Files> filesList = JSON.parseArray(files, Files.class);
            for (Files f : filesList) {
                String fileId = UUIDUtils.getUUID();
                f.setFileId(fileId);
                filesService.save(f);
                GraduationDesignReleaseFile graduationDesignReleaseFile = new GraduationDesignReleaseFile(graduationDesignReleaseId, fileId);
                graduationDesignReleaseFileService.save(graduationDesignReleaseFile);
            }
        }
    }

    /**
     * 上传毕业设计附件
     *
     * @param schoolId                    学校id
     * @param collegeId                   院id
     * @param departmentId                系id
     * @param multipartHttpServletRequest 文件请求
     * @return 文件信息
     */
    @RequestMapping("/anyone/users/upload/graduate/design")
    @ResponseBody
    public AjaxUtils<FileBean> upload(int schoolId, int collegeId, @RequestParam("departmentId") int departmentId,
                                      MultipartHttpServletRequest multipartHttpServletRequest) {
        AjaxUtils<FileBean> data = new AjaxUtils<>();
        try {
            School school = null;
            College college = null;
            Department department = null;
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
                school = schoolService.findById(schoolId);
                college = collegeService.findById(collegeId);
                department = departmentService.findById(departmentId);
            } else {
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                if (record.isPresent()) {
                    school = record.get().into(School.class);
                    college = record.get().into(College.class);
                    department = record.get().into(Department.class);
                }
            }
            if (!ObjectUtils.isEmpty(school)) {
                if (!ObjectUtils.isEmpty(college)) {
                    if (!ObjectUtils.isEmpty(department)) {
                        String path = Workbook.graduateDesignPath(school.getSchoolName(), college.getCollegeName(), department.getDepartmentName());
                        List<FileBean> fileBeen = uploadService.upload(multipartHttpServletRequest,
                                RequestUtils.getRealPath(multipartHttpServletRequest) + path, multipartHttpServletRequest.getRemoteAddr());
                        data.success().listData(fileBeen).obj(path);
                    } else {
                        data.fail().msg("上传失败，未查询到系信息");
                    }
                } else {
                    data.fail().msg("上传失败，未查询到院信息");
                }
            } else {
                data.fail().msg("上传失败，未查询到学校信息");
            }
        } catch (Exception e) {
            log.error("Upload file exception,is {}", e);
        }
        return data;
    }

    /**
     * 删除毕业设计附件
     *
     * @param filePath                  文件路径
     * @param fileId                    文件id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param request                   请求
     * @return true or false
     */
    @RequestMapping("/anyone/users/delete/file/graduate/design")
    @ResponseBody
    public AjaxUtils deleteFileInternship(@RequestParam("filePath") String filePath, @RequestParam("fileId") String fileId,
                                          @RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId, HttpServletRequest request) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        try {
            if (FilesUtils.deleteFile(RequestUtils.getRealPath(request) + filePath)) {
                graduationDesignReleaseFileService.deleteByFileIdAndGraduationDesignReleaseId(fileId, graduationDesignReleaseId);
                filesService.deleteById(fileId);
                ajaxUtils.success().msg("删除文件成功");
            } else {
                ajaxUtils.fail().msg("删除文件失败");
            }
        } catch (Exception e) {
            log.error("Delete file exception, is {}", e);
        }
        return ajaxUtils;
    }
}
