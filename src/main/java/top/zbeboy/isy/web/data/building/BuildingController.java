package top.zbeboy.isy.web.data.building;

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
import top.zbeboy.isy.domain.tables.pojos.Building;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.BuildingRecord;
import top.zbeboy.isy.service.data.BuildingService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.web.bean.data.building.BuildingBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.data.building.BuildingVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/27.
 */
@Slf4j
@Controller
public class BuildingController {

    @Resource
    private BuildingService buildingService;

    @Resource
    private RoleService roleService;

    @Resource
    private UsersService usersService;

    /**
     * 楼数据
     *
     * @return 楼数据页面
     */
    @RequestMapping(value = "/web/menu/data/building", method = RequestMethod.GET)
    public String buildingData() {
        return "web/data/building/building_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/building/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<BuildingBean> buildingDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("building_id");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("building_name");
        headers.add("building_is_del");
        headers.add("operator");
        DataTablesUtils<BuildingBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = buildingService.findAllByPage(dataTablesUtils);
        List<BuildingBean> buildingBeens = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            buildingBeens = records.into(BuildingBean.class);
        }
        dataTablesUtils.setData(buildingBeens);
        dataTablesUtils.setiTotalRecords(buildingService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(buildingService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 楼数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/data/building/add", method = RequestMethod.GET)
    public String buildingAdd(ModelMap modelMap) {
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
        }
        return "web/data/building/building_add::#page-wrapper";
    }

    /**
     * 楼数据编辑
     *
     * @param id       楼id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/data/building/edit", method = RequestMethod.GET)
    public String buildingEdit(@RequestParam("id") int id, ModelMap modelMap) {
        Optional<Record> record = buildingService.findByIdRelation(id);
        BuildingBean buildingBean;
        if (record.isPresent()) {
            buildingBean = record.get().into(BuildingBean.class);
        } else {
            buildingBean = new BuildingBean();
        }
        modelMap.addAttribute("building", buildingBean);

        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
        }
        return "web/data/building/building_edit::#page-wrapper";
    }

    /**
     * 保存时检验楼名是否重复
     *
     * @param name      楼名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/building/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("buildingName") String name, @RequestParam(value = "collegeId", defaultValue = "0") int collegeId) {
        String buildingName = StringUtils.trimWhitespace(name);
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            collegeId = roleService.getRoleCollegeId(record);
        }
        if (StringUtils.hasLength(buildingName) && collegeId > 0) {
            Result<BuildingRecord> buildingRecords = buildingService.findByBuildingNameAndCollegeId(buildingName, collegeId);
            if (ObjectUtils.isEmpty(buildingRecords)) {
                return AjaxUtils.of().success().msg("楼名不存在");
            } else {
                return AjaxUtils.of().fail().msg("楼名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("缺失必要参数");
    }

    /**
     * 检验编辑时楼名重复
     *
     * @param id        楼id
     * @param name      楼名
     * @param collegeId 院id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/building/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("buildingId") int id, @RequestParam("buildingName") String name, @RequestParam(value = "collegeId", defaultValue = "0") int collegeId) {
        String buildingName = StringUtils.trimWhitespace(name);
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            collegeId = roleService.getRoleCollegeId(record);
        }
        if (StringUtils.hasLength(buildingName) && collegeId > 0) {
            Result<BuildingRecord> buildingRecords = buildingService.findByBuildingNameAndCollegeIdNeBuildingId(buildingName, id, collegeId);
            if (buildingRecords.isEmpty()) {
                return AjaxUtils.of().success().msg("楼名不重复");
            } else {
                return AjaxUtils.of().fail().msg("楼名重复");
            }
        }
        return AjaxUtils.of().fail().msg("缺失必要参数");
    }

    /**
     * 保存楼信息
     *
     * @param buildingVo    楼
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/building/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils buildingSave(@Valid BuildingVo buildingVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Building building = new Building();
            Byte isDel = 0;
            if (!ObjectUtils.isEmpty(buildingVo.getBuildingIsDel()) && buildingVo.getBuildingIsDel() == 1) {
                isDel = 1;
            }
            building.setBuildingIsDel(isDel);
            building.setBuildingName(StringUtils.trimWhitespace(buildingVo.getBuildingName()));
            int collegeId = getSaveOrUpdateCollegeId(buildingVo);
            if (collegeId > 0) {
                building.setCollegeId(collegeId);
                buildingService.save(building);
                return AjaxUtils.of().success().msg("保存成功");
            } else {
                return AjaxUtils.of().fail().msg("保存失败，缺失必要参数");
            }
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 保存楼更改
     *
     * @param buildingVo  楼
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/building/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils departmentUpdate(@Valid BuildingVo buildingVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(buildingVo.getBuildingId())) {
            Building building = buildingService.findById(buildingVo.getBuildingId());
            if (!ObjectUtils.isEmpty(building)) {
                Byte isDel = 0;
                if (!ObjectUtils.isEmpty(buildingVo.getBuildingIsDel()) && buildingVo.getBuildingIsDel() == 1) {
                    isDel = 1;
                }
                building.setBuildingIsDel(isDel);
                building.setBuildingName(buildingVo.getBuildingName());
                int collegeId = getSaveOrUpdateCollegeId(buildingVo);
                if (collegeId > 0) {
                    building.setCollegeId(collegeId);
                    buildingService.update(building);
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
     * @param buildingVo 楼
     * @return 院id
     */
    private int getSaveOrUpdateCollegeId(BuildingVo buildingVo) {
        int collegeId = 0;
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            collegeId = buildingVo.getCollegeId();
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            collegeId = roleService.getRoleCollegeId(record);
        }
        return collegeId;
    }

    /**
     * 批量更改楼状态
     *
     * @param buildingIds 楼ids
     * @param isDel         is_del
     * @return true注销成功
     */
    @RequestMapping(value = "/web/data/building/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils departmentUpdateDel(String buildingIds, Byte isDel) {
        if (StringUtils.hasLength(buildingIds) && SmallPropsUtils.StringIdsIsNumber(buildingIds)) {
            buildingService.updateIsDel(SmallPropsUtils.StringIdsToList(buildingIds), isDel);
            return AjaxUtils.of().success().msg("更改楼状态成功");
        }
        return AjaxUtils.of().fail().msg("更改楼状态失败");
    }
}
