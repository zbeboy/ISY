package top.zbeboy.isy.web.graduate.design.manifest;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.export.GraduationDesignManifestExport;
import top.zbeboy.isy.service.graduate.design.DefenseOrderService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignDeclareDataService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignManifestService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.export.ExportBean;
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon;
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/8/1.
 */
@Slf4j
@Controller
public class GraduationDesignManifestController {

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private GraduationDesignManifestService graduationDesignManifestService;

    @Resource
    private GraduationDesignDeclareDataService graduationDesignDeclareDataService;

    @Resource
    private UsersService usersService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private CacheManageService cacheManageService;

    @Resource
    private RoleService roleService;

    @Resource
    private StaffService staffService;

    @Resource
    private DefenseOrderService defenseOrderService;

    @Resource
    private UploadService uploadService;

    @Resource
    private GraduationDesignMethodControllerCommon graduationDesignMethodControllerCommon;

    @Resource
    private GraduationDesignConditionCommon graduationDesignConditionCommon;

    /**
     * 毕业设计清单
     *
     * @return 毕业设计清单页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/manifest", method = RequestMethod.GET)
    public String manifest() {
        return "web/graduate/design/manifest/design_manifest::#page-wrapper";
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/manifest/design/data")
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> designDatas(PaginationUtils paginationUtils) {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils);
    }

    /**
     * 列表
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/manifest/list", method = RequestMethod.GET)
    public String list(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        Users users = usersService.getUserFromSession();
        boolean hasValue = false;
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            Staff staff = staffService.findByUsername(users.getUsername());
            if (!ObjectUtils.isEmpty(staff)) {
                Optional<Record> staffRecord = graduationDesignTeacherService.findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId, staff.getStaffId());
                if (staffRecord.isPresent()) {
                    modelMap.addAttribute("staffId", staff.getStaffId());
                    hasValue = true;
                }
            }
        }
        if (!hasValue) {
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
        page = "web/graduate/design/manifest/design_manifest_list::#page-wrapper";
        return page;
    }

    /**
     * 清单数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/manifest/list/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignDeclareBean> listData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            // 前台数据标题 注：要和前台标题顺序一致，获取order用
            List<String> headers = new ArrayList<>();
            headers.add("presubject_title");
            headers.add("subject_type_name");
            headers.add("origin_type_name");
            headers.add("guide_teacher");
            headers.add("academic_title_name");
            headers.add("guide_peoples");
            headers.add("student_number");
            headers.add("student_name");
            headers.add("score_type_name");
            headers.add("operator");
            dataTablesUtils = new DataTablesUtils<>(request, headers);
            GraduationDesignDeclareBean otherCondition = new GraduationDesignDeclareBean();
            int staffId = NumberUtils.toInt(request.getParameter("staffId"));
            otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
            otherCondition.setStaffId(staffId);
            List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = graduationDesignManifestService.findAllManifestByPage(dataTablesUtils, otherCondition);
            dataTablesUtils.setData(graduationDesignDeclareBeens);
            dataTablesUtils.setiTotalRecords(graduationDesignManifestService.countAllManifest(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignManifestService.countManifestByCondition(dataTablesUtils, otherCondition));
        }
        return dataTablesUtils;
    }

    /**
     * 导出 清单 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = "/web/graduate/design/manifest/list/data/export", method = RequestMethod.GET)
    public void listDataExport(HttpServletRequest request, HttpServletResponse response) {
        try {
            String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
            int staffId = NumberUtils.toInt(request.getParameter("staffId"));
            if (!ObjectUtils.isEmpty(graduationDesignReleaseId) && staffId > 0) {
                Optional<Record> staffRecord = staffService.findByIdRelation(staffId);
                if (staffRecord.isPresent()) {
                    Users users = staffRecord.get().into(Users.class);
                    GraduationDesignDeclareData graduationDesignDeclareData = graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
                    if (!ObjectUtils.isEmpty(graduationDesignDeclareData)) {
                        String year = graduationDesignDeclareData.getGraduationDate().substring(0, graduationDesignDeclareData.getGraduationDate().indexOf("年"));
                        String fileName = users.getRealName() + "_ " + year + "届" + graduationDesignDeclareData.getDepartmentName() + "毕业设计归档清单";
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
                        List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = graduationDesignManifestService.exportManifestData(dataTablesUtils, otherCondition);
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.getFileName())) {
                            fileName = exportBean.getFileName();
                        }
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.getExt())) {
                            ext = exportBean.getExt();
                        }
                        GraduationDesignManifestExport export = new GraduationDesignManifestExport(graduationDesignDeclareBeens);
                        String path = Workbook.graduationDesignManifestPath(users) + fileName + "." + ext;
                        export.exportExcel(RequestUtils.getRealPath(request) + Workbook.graduationDesignManifestPath(users), fileName, ext);
                        uploadService.download(fileName, path, response, request);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Export file error, error is {}", e);
        }
    }

    /**
     * 成绩信息
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseOrderId            毕业设计答辩顺序id
     * @return 成绩
     */
    @RequestMapping(value = "/web/graduate/design/manifest/mark/info", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils markInfo(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId,
                              @RequestParam("defenseOrderId") String defenseOrderId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderId);
            if (!ObjectUtils.isEmpty(defenseOrder)) {
                ajaxUtils.success().msg("获取数据成功！").obj(defenseOrder);
            } else {
                ajaxUtils.fail().msg("未获取到相关顺序");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 修改成绩
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param defenseOrderId            毕业设计答辩顺序id
     * @param scoreTypeId               成绩类型id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/manifest/mark", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils mark(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId,
                          @RequestParam("defenseOrderId") String defenseOrderId,
                          @RequestParam("scoreTypeId") int scoreTypeId, @RequestParam("staffId") int staffId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            boolean canUse = false;
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) || roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                canUse = true;
            } else {
                if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
                    Users users = usersService.getUserFromSession();
                    Staff staff = staffService.findByUsername(users.getUsername());
                    canUse = !ObjectUtils.isEmpty(staff) && staff.getStaffId() == staffId;
                }
            }
            if (canUse) {
                DefenseOrder defenseOrder = defenseOrderService.findById(defenseOrderId);
                if (!ObjectUtils.isEmpty(defenseOrder)) {
                    defenseOrder.setScoreTypeId(scoreTypeId);
                    defenseOrderService.update(defenseOrder);
                    ajaxUtils.success().msg("修改成绩成功");
                } else {
                    ajaxUtils.fail().msg("未获取到相关顺序");
                }
            } else {
                ajaxUtils.fail().msg("您不符合编辑条件");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> accessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                errorBean.setHasError(false);
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("不在毕业时间范围，无法操作");
            }
        }
        return errorBean;
    }
}
