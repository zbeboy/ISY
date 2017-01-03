package top.zbeboy.isy.web.system.role;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.RandomUtils;
import top.zbeboy.isy.web.bean.platform.role.RoleBean;
import top.zbeboy.isy.web.bean.tree.TreeBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.ROLE;

/**
 * Created by lenovo on 2016-10-16.
 */
@Controller
public class SystemRoleController {

    private final Logger log = LoggerFactory.getLogger(SystemRoleController.class);

    @Resource
    private RoleService roleService;

    @Resource
    private UsersService usersService;

    @Resource
    private AuthoritiesService authoritiesService;

    @Resource
    private RoleApplicationService roleApplicationService;

    @Resource
    private CollegeRoleService collegeRoleService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

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
     * 角色数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/system/role/add", method = RequestMethod.GET)
    public String roleAdd() {
        return "web/system/role/system_role_add::#page-wrapper";
    }

    /**
     * 角色数据编辑
     *
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/system/role/edit", method = RequestMethod.GET)
    public String roleEdit(@RequestParam("id") int roleId, ModelMap modelMap) {
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
     * 保存时检验角色是否重复
     *
     * @param name 角色名
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/system/role/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("roleName") String name) {
        String roleName = StringUtils.trimWhitespace(name);
        if (StringUtils.hasLength(roleName)) {
            Result<Record> records = roleService.findByRoleNameAndRoleType(name, 1);
            if (records.isEmpty()) {
                return new AjaxUtils().success().msg("角色名不重复");
            } else {
                return new AjaxUtils().fail().msg("角色名重复");
            }
        }
        return new AjaxUtils().fail().msg("角色名不能为空");
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
    public AjaxUtils updateValid(@RequestParam("roleName") String name, @RequestParam("roleId") int roleId) {
        String roleName = StringUtils.trimWhitespace(name);
        if (StringUtils.hasLength(roleName)) {
            Result<Record> records = roleService.findByRoleNameAndRoleTypeNeRoleId(name, 1, roleId);
            if (records.isEmpty()) {
                return new AjaxUtils().success().msg("角色名不重复");
            } else {
                return new AjaxUtils().fail().msg("角色名重复");
            }
        }
        return new AjaxUtils().fail().msg("角色名不能为空");
    }

    /**
     * 保存角色
     *
     * @param roleName       角色名
     * @param applicationIds 应用ids
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/system/role/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils roleSave(@RequestParam("roleName") String roleName, String applicationIds) {
        Role role = new Role();
        role.setRoleName(roleName);
        role.setRoleEnName("ROLE_" + RandomUtils.generateRoleEnName().toUpperCase());
        role.setRoleType(1);
        int roleId = roleService.saveAndReturnId(role);
        if (roleId > 0) {
            saveOrUpdate(applicationIds, roleId);
            return new AjaxUtils().success().msg("保存成功");
        }
        return new AjaxUtils().fail().msg("保存失败");
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
    public AjaxUtils roleUpdate(@RequestParam("roleId") int roleId, @RequestParam("roleName") String roleName, String applicationIds) {
        Role role = roleService.findById(roleId);
        role.setRoleName(roleName);
        roleService.update(role);
        if (roleId > 0) {
            roleApplicationService.deleteByRoleId(roleId);
            saveOrUpdate(applicationIds, roleId);
            return new AjaxUtils().success().msg("更新成功");
        }
        return new AjaxUtils().fail().msg("更新失败");
    }

    /**
     * 保存或更新与角色相关的表
     *
     * @param applicationIds 应用ids
     * @param roleId         角色id
     */
    private void saveOrUpdate(String applicationIds, int roleId) {
        if (StringUtils.hasLength(applicationIds) && SmallPropsUtils.StringIdsIsNumber(applicationIds)) {
            List<Integer> ids = SmallPropsUtils.StringIdsToList(applicationIds);
            ids.forEach(id -> {
                RoleApplication roleApplication = new RoleApplication(roleId, id);
                roleApplicationService.save(roleApplication);
            });
        }
    }

    /**
     * 删除角色
     *
     * @param roleId 角色id
     * @return true成功
     */
    @RequestMapping(value = "/web/system/role/delete", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils roleDelete(@RequestParam("roleId") int roleId) {
        Role role = roleService.findById(roleId);
        if (!ObjectUtils.isEmpty(role)) {
            roleApplicationService.deleteByRoleId(roleId);
            authoritiesService.deleteByAuthorities(role.getRoleEnName());
            roleService.deleteById(roleId);
        }
        return new AjaxUtils().success().msg("删除成功");
    }

    /**
     * 获取角色id 下的 应用id
     *
     * @param roleId 角色id
     * @return 应用
     */
    @RequestMapping(value = "/web/system/role/application/data", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<RoleApplication> roleApplicationData(@RequestParam("roleId") int roleId) {
        Result<RoleApplicationRecord> roleApplicationRecords = roleApplicationService.findByRoleId(roleId);
        List<RoleApplication> roleApplications = new ArrayList<>();
        if (roleApplicationRecords.isNotEmpty()) {
            roleApplications = roleApplicationRecords.into(RoleApplication.class);
        }
        return new AjaxUtils<RoleApplication>().success().listData(roleApplications);
    }

    /**
     * 数据json
     *
     * @return json
     */
    @RequestMapping(value = "/web/system/role/application/json", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<TreeBean> applicationJson() {
        List<TreeBean> treeBeens = applicationService.getApplicationJson(0);
        return new AjaxUtils<TreeBean>().success().listData(treeBeens);
    }
}
