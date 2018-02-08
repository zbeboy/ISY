package top.zbeboy.isy.web.graduate.design.archives;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignArchives;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.records.GraduationDesignArchivesRecord;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.export.GraduationDesignArchivesExport;
import top.zbeboy.isy.service.graduate.design.GraduationDesignArchivesService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignDeclareDataService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignPresubjectService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.export.ExportBean;
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.common.MethodControllerCommon;
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

/**
 * Created by lenovo on 2017-08-05.
 */
@Slf4j
@Controller
public class GraduationDesignArchivesController {

    @Resource
    private GraduationDesignArchivesService graduationDesignArchivesService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignDeclareDataService graduationDesignDeclareDataService;

    @Resource
    private GraduationDesignPresubjectService graduationDesignPresubjectService;

    @Resource
    private UploadService uploadService;

    @Resource
    private CacheManageService cacheManageService;

    @Resource
    private MethodControllerCommon methodControllerCommon;

    @Resource
    private GraduationDesignMethodControllerCommon graduationDesignMethodControllerCommon;

    @Resource
    private GraduationDesignConditionCommon graduationDesignConditionCommon;

    /**
     * 毕业设计归档
     *
     * @return 毕业设计归档页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/archives", method = RequestMethod.GET)
    public String manifest() {
        return "web/graduate/design/archives/design_archives::#page-wrapper";
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/archives/design/data", method = RequestMethod.GET)
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
    @RequestMapping(value = "/web/graduate/design/archives/list", method = RequestMethod.GET)
    public String list(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
        if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
            modelMap.addAttribute("grade", graduationDesignRelease.getAllowGrade());
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            modelMap.addAttribute("curYear", DateTime.now().getYear());
            modelMap.addAttribute("upYear", DateTime.now().getYear() - 1);
            page = "web/graduate/design/archives/design_archives_list::#page-wrapper";
        } else {
            page = methodControllerCommon.showTip(modelMap, "未查询到毕业设计相关信息");
        }
        return page;
    }

    /**
     * 归档数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/archives/list/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignArchivesBean> listData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            // 前台数据标题 注：要和前台标题顺序一致，获取order用
            List<String> headers = new ArrayList<>();
            headers.add("college_name");
            headers.add("college_code");
            headers.add("science_name");
            headers.add("science_code");
            headers.add("graduation_date");
            headers.add("staff_name");
            headers.add("staff_number");
            headers.add("academic_title_name");
            headers.add("assistant_teacher");
            headers.add("assistant_teacher_number");
            headers.add("assistant_teacher_academic");
            headers.add("presubject_title");
            headers.add("subject_type_name");
            headers.add("origin_type_name");
            headers.add("student_number");
            headers.add("student_name");
            headers.add("score_type_name");
            headers.add("is_excellent");
            headers.add("archive_number");
            headers.add("note");
            headers.add("operator");
            dataTablesUtils = new DataTablesUtils<>(request, headers);
            GraduationDesignArchivesBean otherCondition = new GraduationDesignArchivesBean();
            otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
            List<GraduationDesignArchivesBean> graduationDesignArchivesBeans = graduationDesignArchivesService.findAllByPage(dataTablesUtils, otherCondition);
            dataTablesUtils.setData(graduationDesignArchivesBeans);
            dataTablesUtils.setiTotalRecords(graduationDesignArchivesService.countAll(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignArchivesService.countByCondition(dataTablesUtils, otherCondition));
        }
        return dataTablesUtils;
    }

    /**
     * 导出 归档 数据
     *
     * @param request 请求
     */
    @RequestMapping(value = "/web/graduate/design/archives/list/data/export", method = RequestMethod.GET)
    public void dataExport(HttpServletRequest request, HttpServletResponse response) {
        try {
            String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
            if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
                GraduationDesignDeclareData graduationDesignDeclareData = graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
                if (!ObjectUtils.isEmpty(graduationDesignDeclareData)) {
                    String year = graduationDesignDeclareData.getGraduationDate().substring(0, graduationDesignDeclareData.getGraduationDate().indexOf("年"));
                    String fileName = graduationDesignDeclareData.getScienceName() + " " + year + "届毕业设计（论文）汇总清单";
                    String ext = Workbook.XLSX_FILE;
                    ExportBean exportBean = JSON.parseObject(request.getParameter("exportFile"), ExportBean.class);

                    String extraSearchParam = request.getParameter("extra_search");
                    DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils = DataTablesUtils.of();
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(extraSearchParam)) {
                        dataTablesUtils.setSearch(JSON.parseObject(extraSearchParam));
                    }
                    GraduationDesignArchivesBean otherCondition = new GraduationDesignArchivesBean();
                    otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
                    List<GraduationDesignArchivesBean> graduationDesignArchivesBeans = graduationDesignArchivesService.exportData(dataTablesUtils, otherCondition);
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.getFileName())) {
                        fileName = exportBean.getFileName();
                    }
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(exportBean.getExt())) {
                        ext = exportBean.getExt();
                    }
                    GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
                    if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
                        GraduationDesignArchivesExport export = new GraduationDesignArchivesExport(graduationDesignArchivesBeans);
                        String schoolInfoPath = cacheManageService.schoolInfoPath(graduationDesignRelease.getDepartmentId());
                        String path = Workbook.graduateDesignPath(schoolInfoPath) + fileName + "." + ext;
                        export.exportExcel(RequestUtils.getRealPath(request) + Workbook.graduateDesignPath(schoolInfoPath), fileName, ext);
                        uploadService.download(fileName, path, response, request);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Export file error, error is {}", e);
        }
    }

    /**
     * 生成档案号
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param archivesAffix             前缀
     * @param archivesStart             开始序号
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/archives/generate", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils archivesGenerate(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId,
                                      @RequestParam("archivesAffix") String archivesAffix,
                                      @RequestParam("archivesStart") int archivesStart) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Byte b = 0;
            Result<GraduationDesignPresubjectRecord> records = graduationDesignPresubjectService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
            for (GraduationDesignPresubjectRecord r : records) {
                GraduationDesignArchives graduationDesignArchives = new GraduationDesignArchives();
                graduationDesignArchives.setGraduationDesignPresubjectId(r.getGraduationDesignPresubjectId());
                graduationDesignArchives.setArchiveNumber(archivesAffix + polishArchiveNumber(archivesStart));
                graduationDesignArchives.setIsExcellent(b);
                graduationDesignArchivesService.saveAndIgnore(graduationDesignArchives);
                archivesStart++;
            }
            ajaxUtils.success().msg("生成档案号成功");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 补0操作
     *
     * @param sort 序号
     * @return 档案编号
     */
    private String polishArchiveNumber(int sort) {
        String num = "";
        if (sort < 10) {
            num = "00" + sort;
        } else if (sort < 100) {
            num = "0" + sort;
        } else {
            num = sort + "";
        }
        return num;
    }

    /**
     * 设置百优
     *
     * @param graduationDesignReleaseId    毕业设计发布id
     * @param graduationDesignPresubjectId 毕业设计题目id
     * @param excellent                    百优
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/archives/excellent", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils archivesExcellent(@RequestParam("id") String graduationDesignReleaseId,
                                       @RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId,
                                       @RequestParam("excellent") Byte excellent) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignArchivesRecord record = graduationDesignArchivesService.findByGraduationDesignPresubjectId(graduationDesignPresubjectId);
            if (!ObjectUtils.isEmpty(record)) {
                GraduationDesignArchives graduationDesignArchives = record.into(GraduationDesignArchives.class);
                graduationDesignArchives.setIsExcellent(excellent);
                graduationDesignArchivesService.update(graduationDesignArchives);
                ajaxUtils.success().msg("更新成功");
            } else {
                ajaxUtils.fail().msg("未查询到相关档案信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 获取档案信息
     *
     * @param graduationDesignPresubjectId 毕业设计题目id
     * @return 信息
     */
    @RequestMapping(value = "/web/graduate/design/archives/info", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils archivesInfo(@RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        GraduationDesignArchivesRecord record = graduationDesignArchivesService.findByGraduationDesignPresubjectId(graduationDesignPresubjectId);
        if (!ObjectUtils.isEmpty(record)) {
            GraduationDesignArchives graduationDesignArchives = record.into(GraduationDesignArchives.class);
            ajaxUtils.success().msg("获取档案信息成功").obj(graduationDesignArchives);
        } else {
            ajaxUtils.fail().msg("未查询到相关档案信息");
        }
        return ajaxUtils;
    }

    /**
     * 检验档案号
     *
     * @param archiveNumber 档案号
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/archives/valid/number", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils archivesValidNumber(@RequestParam("archiveNumber") String archiveNumber) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        GraduationDesignArchivesRecord record = graduationDesignArchivesService.findByArchiveNumber(StringUtils.trimWhitespace(archiveNumber));
        if (!ObjectUtils.isEmpty(record)) {
            ajaxUtils.fail().msg("该档案号已被使用");
        } else {
            ajaxUtils.success().msg("允许使用");
        }
        return ajaxUtils;
    }

    /**
     * 更改档案号
     *
     * @param archiveNumber                档案号
     * @param graduationDesignReleaseId    毕业设计发布id
     * @param graduationDesignPresubjectId 毕业设计题目id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/archives/number", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils archivesNumber(@RequestParam("archiveNumber") String archiveNumber,
                                    @RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId,
                                    @RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            String tempArchiveNumber = StringUtils.trimWhitespace(archiveNumber);
            GraduationDesignArchivesRecord record = graduationDesignArchivesService.findByArchiveNumber(tempArchiveNumber);
            if (ObjectUtils.isEmpty(record)) {
                GraduationDesignArchivesRecord realRecord = graduationDesignArchivesService.findByGraduationDesignPresubjectId(graduationDesignPresubjectId);
                GraduationDesignArchives graduationDesignArchives;
                if (!ObjectUtils.isEmpty(realRecord)) {
                    graduationDesignArchives = realRecord.into(GraduationDesignArchives.class);
                    graduationDesignArchives.setArchiveNumber(archiveNumber);
                    graduationDesignArchivesService.update(graduationDesignArchives);
                } else {
                    Byte b = 0;
                    graduationDesignArchives = new GraduationDesignArchives();
                    graduationDesignArchives.setGraduationDesignPresubjectId(graduationDesignPresubjectId);
                    graduationDesignArchives.setIsExcellent(b);
                    graduationDesignArchives.setArchiveNumber(archiveNumber);
                    graduationDesignArchivesService.save(graduationDesignArchives);
                }

                ajaxUtils.success().msg("更改档案号成功");
            } else {
                ajaxUtils.fail().msg("该档案号已被使用");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 更新备注
     *
     * @param graduationDesignReleaseId    毕业设计发布id
     * @param graduationDesignPresubjectId 毕业题目id
     * @param note                         备注
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/archives/note", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils archivesNote(@RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId,
                                  @RequestParam("graduationDesignPresubjectId") String graduationDesignPresubjectId,
                                  @RequestParam("note") String note) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.isRangeGraduationDateCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignArchivesRecord record = graduationDesignArchivesService.findByGraduationDesignPresubjectId(graduationDesignPresubjectId);
            if (!ObjectUtils.isEmpty(record)) {
                GraduationDesignArchives graduationDesignArchives = record.into(GraduationDesignArchives.class);
                graduationDesignArchives.setNote(note);
                graduationDesignArchivesService.update(graduationDesignArchives);
                ajaxUtils.success().msg("更新成功");
            } else {
                ajaxUtils.fail().msg("未查询到相关档案信息");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }
}
