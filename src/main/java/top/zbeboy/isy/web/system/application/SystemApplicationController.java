package top.zbeboy.isy.web.system.application;

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
import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.records.ApplicationRecord;
import top.zbeboy.isy.service.system.ApplicationService;
import top.zbeboy.isy.service.data.CollegeApplicationService;
import top.zbeboy.isy.service.platform.RoleApplicationService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.web.bean.system.application.ApplicationBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.system.application.ApplicationVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016-09-29.
 * 系统应用模块
 */
@Controller
public class SystemApplicationController {

    private final Logger log = LoggerFactory.getLogger(SystemApplicationController.class);

    @Resource
    private ApplicationService applicationService;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleApplicationService roleApplicationService;

    @Resource
    private CollegeApplicationService collegeApplicationService;

    /**
     * 系统应用
     *
     * @return 系统应用页面
     */
    @RequestMapping(value = "/web/menu/system/application", method = RequestMethod.GET)
    public String systemLog() {
        return "web/system/application/system_application::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/system/application/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<ApplicationBean> applicationDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("application_name");
        headers.add("application_en_name");
        headers.add("application_pid");
        headers.add("application_url");
        headers.add("icon");
        headers.add("application_sort");
        headers.add("application_code");
        headers.add("application_data_url_start_with");
        headers.add("operator");
        DataTablesUtils<ApplicationBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = applicationService.findAllByPage(dataTablesUtils);
        List<ApplicationBean> applicationBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            applicationBeen = records.into(ApplicationBean.class);
            applicationBeen.forEach(a -> {
                if (a.getApplicationPid() == 0) {
                    a.setApplicationPidName("无");
                } else {
                    Application application = applicationService.findById(a.getApplicationPid());
                    a.setApplicationPidName(application.getApplicationName());
                }
            });
        }
        dataTablesUtils.setData(applicationBeen);
        dataTablesUtils.setiTotalRecords(applicationService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(applicationService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 应用添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/system/application/add", method = RequestMethod.GET)
    public String applicationAdd() {
        return "web/system/application/system_application_add::#page-wrapper";
    }

    /**
     * 应用更新
     *
     * @return 更新页面
     */
    @RequestMapping(value = "/web/system/application/edit", method = RequestMethod.GET)
    public String applicationEdit(@RequestParam("id") int id, ModelMap modelMap) {
        Application application = applicationService.findById(id);
        modelMap.addAttribute("sys_application", application);
        return "web/system/application/system_application_edit::#page-wrapper";
    }

    /**
     * 初始化添加页面数据
     *
     * @return 页面数据
     */
    @RequestMapping(value = "/web/system/application/init", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils init() {
        // 一级与二级菜单
        List<Application> applicationPids = new ArrayList<>();
        Application application = new Application();
        application.setApplicationId(0);
        application.setApplicationName("无");
        applicationPids.add(application);
        applicationPids.addAll(applicationService.findByPid(0));
        List<Integer> pids = new ArrayList<>();
        applicationPids.forEach(p -> {
            pids.add(p.getApplicationId());
        });
        Result<ApplicationRecord> applicationRecords = applicationService.findInPids(pids);
        if (applicationRecords.isNotEmpty()) {
            List<Application> secondLevelIds = applicationRecords.into(Application.class);
            applicationPids.addAll(secondLevelIds);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("applicationPids", applicationPids);
        return AjaxUtils.of().success().mapData(data);
    }

    /**
     * 检验保存时应用名是否重复
     *
     * @param applicationName 应用名
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/save/valid/name", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidName(@RequestParam("applicationName") String applicationName) {
        if (StringUtils.hasLength(applicationName)) {
            List<Application> applications = applicationService.findByApplicationName(applicationName);
            if (ObjectUtils.isEmpty(applications) && applications.isEmpty()) {
                return AjaxUtils.of().success().msg("应用名不存在");
            } else {
                return AjaxUtils.of().fail().msg("应用名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("应用名不能为空");
    }

    /**
     * 检验更新时应用名是否重复
     *
     * @param applicationName 应用名
     * @param applicationId   应用id
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/update/valid/name", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidName(@RequestParam("applicationName") String applicationName, @RequestParam("applicationId") int applicationId) {
        if (StringUtils.hasLength(applicationName)) {
            Result<ApplicationRecord> applications = applicationService.findByApplicationNameNeApplicationId(applicationName, applicationId);
            if (applications.isEmpty()) {
                return AjaxUtils.of().success().msg("应用名不存在");
            } else {
                return AjaxUtils.of().fail().msg("应用名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("应用名不能为空");
    }

    /**
     * 检验保存时应用英文名是否重复
     *
     * @param applicationEnName 应用英文名
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/save/valid/en_name", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidEnName(@RequestParam("applicationEnName") String applicationEnName) {
        if (StringUtils.hasLength(applicationEnName)) {
            List<Application> applications = applicationService.findByApplicationEnName(applicationEnName);
            if (ObjectUtils.isEmpty(applications) && applications.isEmpty()) {
                return AjaxUtils.of().success().msg("应用英文名不存在");
            } else {
                return AjaxUtils.of().fail().msg("应用英文名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("应用英文名不能为空");
    }

    /**
     * 检验更新时应用英文名是否重复
     *
     * @param applicationEnName 应用英文名
     * @param applicationId     应用id
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/update/valid/en_name", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidEnName(@RequestParam("applicationEnName") String applicationEnName, @RequestParam("applicationId") int applicationId) {
        if (StringUtils.hasLength(applicationEnName)) {
            Result<ApplicationRecord> applications = applicationService.findByApplicationEnNameNeApplicationId(applicationEnName, applicationId);
            if (applications.isEmpty()) {
                return AjaxUtils.of().success().msg("应用英文名不存在");
            } else {
                return AjaxUtils.of().fail().msg("应用英文名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("应用英文名不能为空");
    }

    /**
     * 检验保存时应用链接是否重复
     *
     * @param applicationUrl 应用链接
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/save/valid/url", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidUrl(@RequestParam("applicationUrl") String applicationUrl) {
        if (StringUtils.hasLength(applicationUrl)) {
            List<Application> applications = applicationService.findByApplicationUrl(applicationUrl);
            if (ObjectUtils.isEmpty(applications) && applications.isEmpty()) {
                return AjaxUtils.of().success().msg("应用链接不存在");
            } else {
                return AjaxUtils.of().fail().msg("应用链接已存在");
            }
        }
        return AjaxUtils.of().fail().msg("应用链接不能为空");
    }

    /**
     * 检验更新时应用链接是否重复
     *
     * @param applicationUrl 应用链接
     * @param applicationId  应用id
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/update/valid/url", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidUrl(@RequestParam("applicationUrl") String applicationUrl, @RequestParam("applicationId") int applicationId) {
        if (StringUtils.hasLength(applicationUrl)) {
            Result<ApplicationRecord> applications = applicationService.findByApplicationUrlNeApplicationId(applicationUrl, applicationId);
            if (applications.isEmpty()) {
                return AjaxUtils.of().success().msg("应用链接不存在");
            } else {
                return AjaxUtils.of().fail().msg("应用链接已存在");
            }
        }
        return AjaxUtils.of().fail().msg("应用链接不能为空");
    }

    /**
     * 检验保存时应用识别码是否重复
     *
     * @param applicationCode 应用识别码
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/save/valid/code", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidCode(@RequestParam("applicationCode") String applicationCode) {
        if (StringUtils.hasLength(applicationCode)) {
            List<Application> applications = applicationService.findByApplicationCode(applicationCode);
            if (ObjectUtils.isEmpty(applications) && applications.isEmpty()) {
                return AjaxUtils.of().success().msg("应用识别码不存在");
            } else {
                return AjaxUtils.of().fail().msg("应用识别码已存在");
            }
        }
        return AjaxUtils.of().fail().msg("应用识别码不能为空");
    }

    /**
     * 检验更新时应用识别码是否重复
     *
     * @param applicationCode 应用识别码
     * @param applicationId   应用id
     * @return true 不重复 false重复
     */
    @RequestMapping(value = "/web/system/application/update/valid/code", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidCode(@RequestParam("applicationCode") String applicationCode, @RequestParam("applicationId") int applicationId) {
        if (StringUtils.hasLength(applicationCode)) {
            Result<ApplicationRecord> applications = applicationService.findByApplicationCodeNeApplicationId(applicationCode, applicationId);
            if (applications.isEmpty()) {
                return AjaxUtils.of().success().msg("应用识别码不存在");
            } else {
                return AjaxUtils.of().fail().msg("应用识别码已存在");
            }
        }
        return AjaxUtils.of().fail().msg("应用识别码不能为空");
    }

    /**
     * 保存应用信息
     *
     * @param applicationVo 应用
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/system/application/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils applicationSave(@Valid ApplicationVo applicationVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Application application = new Application();
            application.setApplicationName(applicationVo.getApplicationName());
            application.setApplicationSort(applicationVo.getApplicationSort());
            application.setApplicationPid(applicationVo.getApplicationPid());
            application.setApplicationUrl(applicationVo.getApplicationUrl());
            application.setApplicationCode(applicationVo.getApplicationCode());
            application.setApplicationEnName(applicationVo.getApplicationEnName());
            application.setIcon(applicationVo.getIcon());
            application.setApplicationDataUrlStartWith(applicationVo.getApplicationDataUrlStartWith());
            int applicationId = applicationService.saveAndReturnId(application);
            Role role = roleService.findByRoleEnName(Workbook.SYSTEM_AUTHORITIES);
            RoleApplication roleApplication = new RoleApplication(role.getRoleId(), applicationId);
            roleApplicationService.save(roleApplication);
            return AjaxUtils.of().success().msg("保存成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 更新应用信息
     *
     * @param applicationVo 应用
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/system/application/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils applicationUpdate(@Valid ApplicationVo applicationVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(applicationVo.getApplicationId())) {
            Application application = applicationService.findById(applicationVo.getApplicationId());
            application.setApplicationName(applicationVo.getApplicationName());
            application.setApplicationSort(applicationVo.getApplicationSort());
            application.setApplicationPid(applicationVo.getApplicationPid());
            application.setApplicationUrl(applicationVo.getApplicationUrl());
            application.setApplicationCode(applicationVo.getApplicationCode());
            application.setApplicationEnName(applicationVo.getApplicationEnName());
            application.setIcon(applicationVo.getIcon());
            application.setApplicationDataUrlStartWith(applicationVo.getApplicationDataUrlStartWith());
            applicationService.update(application);
            return AjaxUtils.of().success().msg("更新成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 批量删除应用
     *
     * @param applicationIds 应用ids
     * @return true删除成功
     */
    @RequestMapping(value = "/web/system/application/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils applicationUpdateDel(String applicationIds) {
        if (StringUtils.hasLength(applicationIds) && SmallPropsUtils.StringIdsIsNumber(applicationIds)) {
            List<Integer> ids = SmallPropsUtils.StringIdsToList(applicationIds);
            ids.forEach(id -> {
                roleApplicationService.deleteByApplicationId(id);
                collegeApplicationService.deleteByApplicationId(id);
            });
            applicationService.deletes(ids);
            return AjaxUtils.of().success().msg("删除应用成功");
        }
        return AjaxUtils.of().fail().msg("删除应用失败");
    }
}
