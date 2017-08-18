package top.zbeboy.isy.web.system.role;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;
import top.zbeboy.isy.service.data.ElasticSyncService;
import top.zbeboy.isy.service.platform.RoleApplicationService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.system.ApplicationService;
import top.zbeboy.isy.web.bean.platform.role.RoleBean;
import top.zbeboy.isy.web.bean.tree.TreeBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.ROLE;

/**
 * Created by lenovo on 2016-10-16.
 */
@Slf4j
@Controller
public class SystemRoleController {

    @Resource
    private RoleService roleService;

    @Resource
    private RoleApplicationService roleApplicationService;

    @Resource
    private ElasticSyncService elasticSyncService;

    @Resource
    private ApplicationService applicationService;

    /**
     * 系统角色页面
     *
     * @return 页面
     */
    @RequestMapping(value = "/web/menu/system/role", method = RequestMethod.GET)
    public String platformRole() {
        return "web/system/role/system_role::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/system/role/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<RoleBean> roleDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        ArrayList<String> headers = new ArrayList<>();
        headers.add("role_name");
        headers.add("role_en_name");
        headers.add("operator");
        RoleBean otherCondition = new RoleBean();
        otherCondition.setRoleType(1);
        DataTablesUtils<RoleBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = roleService.findAllByPage(dataTablesUtils, otherCondition);
        List<RoleBean> roleBeens = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            for (Record record : records) {
                RoleBean roleBean = new RoleBean();
                roleBean.setRoleId(record.getValue(ROLE.ROLE_ID));
                roleBean.setRoleName(record.getValue(ROLE.ROLE_NAME));
                roleBean.setRoleEnName(record.getValue(ROLE.ROLE_EN_NAME));
                roleBeens.add(roleBean);
            }
        }
        dataTablesUtils.setData(roleBeens);
        dataTablesUtils.setiTotalRecords(roleService.countAll(otherCondition));
        dataTablesUtils.setiTotalDisplayRecords(roleService.countByCondition(dataTablesUtils, otherCondition));
        return dataTablesUtils;
    }

    /**
     * 角色数据编辑
     *
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/system/role/edit", method = RequestMethod.GET)
    public String roleEdit(@RequestParam("id") String roleId, ModelMap modelMap) {
        Optional<Record> record = roleService.findByRoleIdRelation(roleId);
        RoleBean roleBean = new RoleBean();
        if (record.isPresent()) {
            Record temp = record.get();
            roleBean.setRoleId(temp.getValue(ROLE.ROLE_ID));
            roleBean.setRoleName(temp.getValue(ROLE.ROLE_NAME));
            roleBean.setRoleEnName(temp.getValue(ROLE.ROLE_EN_NAME));
        }
        modelMap.addAttribute("role", roleBean);
        return "web/system/role/system_role_edit::#page-wrapper";
    }

    /**
     * 更新时检验角色名
     *
     * @param name   角色名
     * @param roleId 角色id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/system/role/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("roleName") String name, @RequestParam("roleId") String roleId) {
        String roleName = StringUtils.trimWhitespace(name);
        if (StringUtils.hasLength(roleName)) {
            Result<Record> records = roleService.findByRoleNameAndRoleTypeNeRoleId(name, 1, roleId);
            if (records.isEmpty()) {
                return AjaxUtils.of().success().msg("角色名不重复");
            } else {
                return AjaxUtils.of().fail().msg("角色名重复");
            }
        }
        return AjaxUtils.of().fail().msg("角色名不能为空");
    }

    /**
     * 更新角色
     *
     * @param roleId         角色id
     * @param roleName       角色名
     * @param applicationIds 应用ids
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/system/role/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils roleUpdate(@RequestParam("roleId") String roleId, @RequestParam("roleName") String roleName, String applicationIds) {
        Role role = roleService.findById(roleId);
        String oldRoleName = role.getRoleName();
        role.setRoleName(roleName);
        roleService.update(role);
        roleApplicationService.deleteByRoleId(roleId);
        roleApplicationService.batchSaveRoleApplication(applicationIds, roleId);
        elasticSyncService.systemRoleNameUpdate(oldRoleName);
        return AjaxUtils.of().success().msg("更新成功");
    }

    /**
     * 获取角色id 下的 应用id
     *
     * @param roleId 角色id
     * @return 应用
     */
    @RequestMapping(value = "/web/system/role/application/data", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<RoleApplication> roleApplicationData(@RequestParam("roleId") String roleId) {
        AjaxUtils<RoleApplication> ajaxUtils = AjaxUtils.of();
        Result<RoleApplicationRecord> roleApplicationRecords = roleApplicationService.findByRoleId(roleId);
        List<RoleApplication> roleApplications = new ArrayList<>();
        if (roleApplicationRecords.isNotEmpty()) {
            roleApplications = roleApplicationRecords.into(RoleApplication.class);
        }
        return ajaxUtils.success().listData(roleApplications);
    }

    /**
     * 数据json
     *
     * @return json
     */
    @RequestMapping(value = "/web/system/role/application/json", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<TreeBean> applicationJson() {
        AjaxUtils<TreeBean> ajaxUtils = AjaxUtils.of();
        List<TreeBean> treeBeens = applicationService.getApplicationJson("0");
        return ajaxUtils.success().listData(treeBeens);
    }
}
