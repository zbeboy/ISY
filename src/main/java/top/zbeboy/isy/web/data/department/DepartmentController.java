package top.zbeboy.isy.web.data.department;

import lombok.extern.slf4j.Slf4j;
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
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Department;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.DepartmentRecord;
import top.zbeboy.isy.service.data.DepartmentService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.web.bean.data.department.DepartmentBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.data.department.DepartmentVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-21.
 */
@Slf4j
@Controller
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

    /**
     * 根据院id获取全部系
     *
     * @param collegeId 院id
     * @return 院下全部系
     */
    @RequestMapping(value = "/user/departments", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Department> departments(@RequestParam("collegeId") int collegeId) {
        AjaxUtils<Department> ajaxUtils = AjaxUtils.of();
        List<Department> departments = new ArrayList<>();
        Byte isDel = 0;
        Department department = new Department(0, "请选择系", isDel, 0);
        departments.add(department);
        Result<DepartmentRecord> departmentRecords = departmentService.findByCollegeIdAndIsDel(collegeId, isDel);
        for (DepartmentRecord r : departmentRecords) {
            Department tempDepartment = new Department(r.getDepartmentId(), r.getDepartmentName(), r.getDepartmentIsDel(), r.getCollegeId());
            departments.add(tempDepartment);
        }
        return ajaxUtils.success().msg("获取系数据成功！").listData(departments);
    }

    /**
     * 系数据
     *
     * @return 系数据页面
     */
    @RequestMapping(value = "/web/menu/data/department", method = RequestMethod.GET)
    public String departmentData() {
        return "web/data/department/department_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/department/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<DepartmentBean> departmentDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("department_id");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("department_is_del");
        headers.add("operator");
        DataTablesUtils<DepartmentBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = departmentService.findAllByPage(dataTablesUtils);
        List<DepartmentBean> departmentBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            departmentBeen = records.into(DepartmentBean.class);
        }
        dataTablesUtils.setData(departmentBeen);
        dataTablesUtils.setiTotalRecords(departmentService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(departmentService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 系数据添加
     *
     * @param modelMap 页面对象
     * @return 添加页面
     */
    @RequestMapping(value = "/web/data/department/add", method = RequestMethod.GET)
    public String departmentAdd(ModelMap modelMap) {
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
        }
        return "web/data/department/department_add::#page-wrapper";
    }

    /**
     * 系数据编辑
     *
     * @param id       系id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/data/department/edit", method = RequestMethod.GET)
    public String departmentEdit(@RequestParam("id") int id, ModelMap modelMap) {
        Optional<Record> record = departmentService.findByIdRelation(id);
        DepartmentBean departmentBean;
        if (record.isPresent()) {
            departmentBean = record.get().into(DepartmentBean.class);
        } else {
            departmentBean = new DepartmentBean();
        }
        modelMap.addAttribute("department", departmentBean);

        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
        }
        return "web/data/department/department_edit::#page-wrapper";
    }

    /**
     * 保存时检验系名是否重复
     *
     * @param name      系名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/department/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("departmentName") String name, @RequestParam(value = "collegeId", defaultValue = "0") int collegeId) {
        String departmentName = StringUtils.trimWhitespace(name);
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            collegeId = roleService.getRoleCollegeId(record);
        }
        if (StringUtils.hasLength(departmentName) && collegeId > 0) {
            Result<DepartmentRecord> departmentRecords = departmentService.findByDepartmentNameAndCollegeId(departmentName, collegeId);
            if (ObjectUtils.isEmpty(departmentRecords)) {
                return AjaxUtils.of().success().msg("系名不存在");
            } else {
                return AjaxUtils.of().fail().msg("系名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("缺失必要参数");
    }

    /**
     * 检验编辑时系名重复
     *
     * @param id        系id
     * @param name      系名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/department/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("departmentId") int id, @RequestParam("departmentName") String name, @RequestParam(value = "collegeId", defaultValue = "0") int collegeId) {
        String departmentName = StringUtils.trimWhitespace(name);
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            collegeId = roleService.getRoleCollegeId(record);
        }
        if (StringUtils.hasLength(departmentName) && collegeId > 0) {
            Result<DepartmentRecord> departmentRecords = departmentService.findByDepartmentNameAndCollegeIdNeDepartmentId(departmentName, id, collegeId);
            if (departmentRecords.isEmpty()) {
                return AjaxUtils.of().success().msg("系名不重复");
            } else {
                return AjaxUtils.of().fail().msg("系名重复");
            }
        }
        return AjaxUtils.of().fail().msg("缺失必要参数");
    }

    /**
     * 保存系信息
     *
     * @param departmentVo  系
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/department/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils departmentSave(@Valid DepartmentVo departmentVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Department department = new Department();
            Byte isDel = 0;
            if (!ObjectUtils.isEmpty(departmentVo.getDepartmentIsDel()) && departmentVo.getDepartmentIsDel() == 1) {
                isDel = 1;
            }
            department.setDepartmentIsDel(isDel);
            department.setDepartmentName(StringUtils.trimWhitespace(departmentVo.getDepartmentName()));
            int collegeId = getSaveOrUpdateCollegeId(departmentVo);
            if (collegeId > 0) {
                department.setCollegeId(collegeId);
                departmentService.save(department);
                return AjaxUtils.of().success().msg("保存成功");
            } else {
                return AjaxUtils.of().fail().msg("保存失败，缺失必要参数");
            }
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 保存系更改
     *
     * @param departmentVo  系
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/department/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils departmentUpdate(@Valid DepartmentVo departmentVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(departmentVo.getDepartmentId())) {
            Department department = departmentService.findById(departmentVo.getDepartmentId());
            if (!ObjectUtils.isEmpty(department)) {
                Byte isDel = 0;
                if (!ObjectUtils.isEmpty(departmentVo.getDepartmentIsDel()) && departmentVo.getDepartmentIsDel() == 1) {
                    isDel = 1;
                }
                department.setDepartmentIsDel(isDel);
                department.setDepartmentName(departmentVo.getDepartmentName());
                int collegeId = getSaveOrUpdateCollegeId(departmentVo);
                if (collegeId > 0) {
                    department.setCollegeId(collegeId);
                    departmentService.update(department);
                    return AjaxUtils.of().success().msg("更改成功");
                } else {
                    return AjaxUtils.of().fail().msg("更新失败，缺失必要参数");
                }
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }

    /**
     * 保存或更新时获取院id
     *
     * @param departmentVo 系
     * @return 院id
     */
    private int getSaveOrUpdateCollegeId(DepartmentVo departmentVo) {
        int collegeId = 0;
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            collegeId = departmentVo.getCollegeId();
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            collegeId = roleService.getRoleCollegeId(record);
        }
        return collegeId;
    }

    /**
     * 批量更改系状态
     *
     * @param departmentIds 系ids
     * @param isDel         is_del
     * @return true注销成功
     */
    @RequestMapping(value = "/web/data/department/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils departmentUpdateDel(String departmentIds, Byte isDel) {
        if (StringUtils.hasLength(departmentIds) && SmallPropsUtils.StringIdsIsNumber(departmentIds)) {
            departmentService.updateIsDel(SmallPropsUtils.StringIdsToList(departmentIds), isDel);
            return AjaxUtils.of().success().msg("更改系状态成功");
        }
        return AjaxUtils.of().fail().msg("更改系状态失败");
    }
}
