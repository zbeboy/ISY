package top.zbeboy.isy.web.data.organize;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Record1;
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
import top.zbeboy.isy.domain.tables.pojos.Organize;
import top.zbeboy.isy.domain.tables.records.OrganizeRecord;
import top.zbeboy.isy.elastic.pojo.OrganizeElastic;
import top.zbeboy.isy.glue.data.OrganizeGlue;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.DepartmentService;
import top.zbeboy.isy.service.data.OrganizeService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SelectUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.data.organize.OrganizeVo;

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
public class OrganizeController {

    @Resource
    private OrganizeService organizeService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private OrganizeGlue organizeGlue;

    @Resource
    private RoleService roleService;

    @Resource
    private DepartmentService departmentService;

    /**
     * 通过专业id获取全部年级
     *
     * @param scienceId 专业id
     * @return 专业下全部年级
     */
    @RequestMapping(value = "/user/grades", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<SelectUtils> grades(@RequestParam("scienceId") int scienceId) {
        AjaxUtils<SelectUtils> ajaxUtils = AjaxUtils.of();
        List<SelectUtils> grades = new ArrayList<>();
        SelectUtils selectUtils = new SelectUtils(0, "0", "请选择年级", true);
        grades.add(selectUtils);
        Byte isDel = 0;
        Result<Record1<String>> gradeRecord = organizeService.findByScienceIdAndDistinctGradeAndIsDel(scienceId, isDel);
        for (Record r : gradeRecord) {
            SelectUtils tempGrade = new SelectUtils(0, r.getValue("grade").toString(), r.getValue("grade").toString(), false);
            grades.add(tempGrade);
        }
        return ajaxUtils.success().msg("获取年级数据成功！").listData(grades);
    }

    /**
     * 通过系id获取全部年级
     *
     * @param departmentId 系id
     * @return 系下全部年级
     */
    @RequestMapping(value = "/user/department/grades", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<SelectUtils> departmentGrades(@RequestParam("departmentId") int departmentId) {
        AjaxUtils<SelectUtils> ajaxUtils = AjaxUtils.of();
        List<SelectUtils> grades = new ArrayList<>();
        SelectUtils selectUtils = new SelectUtils(0, "0", "请选择年级", true);
        grades.add(selectUtils);
        Result<Record1<String>> gradeRecord = organizeService.findByDepartmentIdAndDistinctGrade(departmentId);
        for (Record r : gradeRecord) {
            SelectUtils tempGrade = new SelectUtils(0, r.getValue("grade").toString(), r.getValue("grade").toString(), false);
            grades.add(tempGrade);
        }
        return ajaxUtils.success().msg("获取年级数据成功！").listData(grades);
    }

    /**
     * 通过年级和专业id获取全部班级
     *
     * @param grade     年级
     * @param scienceId 专业id
     * @return 年级下全部班级
     */
    @RequestMapping(value = "/user/organizes", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Organize> organizes(@RequestParam("grade") String grade, @RequestParam("scienceId") int scienceId) {
        AjaxUtils<Organize> ajaxUtils = AjaxUtils.of();
        List<Organize> organizes = new ArrayList<>();
        Byte isDel = 0;
        Organize organize = new Organize(0, "请选择班级", isDel, 0, "");
        organizes.add(organize);
        Result<OrganizeRecord> organizeRecords = organizeService.findByGradeAndScienceId(StringUtils.trimWhitespace(grade), scienceId);
        for (OrganizeRecord r : organizeRecords) {
            Organize tempOrganize = new Organize(r.getOrganizeId(), r.getOrganizeName(), r.getOrganizeIsDel(), r.getScienceId(), r.getGrade());
            organizes.add(tempOrganize);
        }
        return ajaxUtils.success().msg("获取班级数据成功！").listData(organizes);
    }

    /**
     * 班级数据
     *
     * @return 班级数据页面
     */
    @RequestMapping(value = "/web/menu/data/organize", method = RequestMethod.GET)
    public String organizeData() {
        return "web/data/organize/organize_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/organize/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<OrganizeBean> organizeDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("organize_id");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("science_name");
        headers.add("grade");
        headers.add("organize_name");
        headers.add("organize_is_del");
        headers.add("operator");
        DataTablesUtils<OrganizeBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        ResultUtils<List<OrganizeBean>> resultUtils = organizeGlue.findAllByPage(dataTablesUtils);
        dataTablesUtils.setData(resultUtils.getData());
        dataTablesUtils.setITotalRecords(organizeGlue.countAll());
        dataTablesUtils.setITotalDisplayRecords(resultUtils.getTotalElements());
        return dataTablesUtils;
    }

    /**
     * 班级数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/data/organize/add", method = RequestMethod.GET)
    public String organizeAdd(ModelMap modelMap) {
        commonControllerMethodService.currentUserRoleNameAndCollegeIdPageParam(modelMap);
        return "web/data/organize/organize_add::#page-wrapper";
    }

    /**
     * 班级数据编辑
     *
     * @param id       班级id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/data/organize/edit", method = RequestMethod.GET)
    public String organizeEdit(@RequestParam("id") int id, ModelMap modelMap) {
        Optional<Record> record = organizeService.findByIdRelation(id);
        OrganizeBean organizeBean;
        if (record.isPresent()) {
            organizeBean = record.get().into(OrganizeBean.class);
        } else {
            organizeBean = new OrganizeBean();
        }
        modelMap.addAttribute("organize", organizeBean);
        commonControllerMethodService.currentUserRoleNameAndCollegeIdPageParam(modelMap);
        return "web/data/organize/organize_edit::#page-wrapper";
    }

    /**
     * 保存时检验班级名是否重复
     *
     * @param organizeName 班级名
     * @param scienceId    专业id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/organize/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("organizeName") String organizeName, @RequestParam("scienceId") int scienceId) {
        if (StringUtils.hasLength(organizeName)) {
            Result<OrganizeRecord> scienceRecords = organizeService.findByOrganizeNameAndScienceId(organizeName, scienceId);
            if (ObjectUtils.isEmpty(scienceRecords)) {
                return AjaxUtils.of().success().msg("班级名不存在");
            } else {
                return AjaxUtils.of().fail().msg("班级名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("班级名不能为空");
    }

    /**
     * 检验编辑时班级名重复
     *
     * @param id           班级id
     * @param organizeName 班级名
     * @param scienceId    专业id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/organize/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("organizeId") int id, @RequestParam("organizeName") String organizeName, @RequestParam("scienceId") int scienceId) {
        Result<OrganizeRecord> organizeRecords = organizeService.findByOrganizeNameAndScienceIdNeOrganizeId(organizeName, id, scienceId);
        if (organizeRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("班级名不重复");
        }

        return AjaxUtils.of().fail().msg("班级名重复");
    }

    /**
     * 保存班级信息
     *
     * @param organizeVo    班级
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/organize/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils organizeSave(@Valid OrganizeVo organizeVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (!bindingResult.hasErrors()) {
            OrganizeElastic organizeElastic = new OrganizeElastic();
            Byte isDel = 0;
            if (!ObjectUtils.isEmpty(organizeVo.getOrganizeIsDel()) && organizeVo.getOrganizeIsDel() == 1) {
                isDel = 1;
            }
            organizeElastic.setOrganizeName(organizeVo.getOrganizeName());
            organizeElastic.setOrganizeIsDel(isDel);
            organizeElastic.setScienceId(organizeVo.getScienceId());
            organizeElastic.setGrade(organizeVo.getGrade());
            organizeElastic.setDepartmentId(organizeVo.getDepartmentId());
            organizeElastic.setDepartmentName(organizeVo.getDepartmentName());
            organizeElastic.setScienceName(organizeVo.getScienceName());

            // 管理员
            if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
                Optional<Record> departmentRecord = departmentService.findByIdRelation(organizeVo.getDepartmentId());
                if (departmentRecord.isPresent()) {
                    OrganizeBean organizeBean = departmentRecord.get().into(OrganizeBean.class);
                    organizeElastic.setSchoolId(organizeBean.getSchoolId());
                    organizeElastic.setSchoolName(organizeBean.getSchoolName());
                    organizeElastic.setCollegeId(organizeBean.getCollegeId());
                    organizeElastic.setCollegeName(organizeBean.getCollegeName());
                    organizeService.save(organizeElastic);
                    ajaxUtils.success().msg("保存成功");
                } else {
                    ajaxUtils.fail().msg("未查询到相关系信息");
                }
            } else {
                organizeElastic.setSchoolId(organizeVo.getSchoolId());
                organizeElastic.setSchoolName(organizeVo.getSchoolName());
                organizeElastic.setCollegeId(organizeVo.getCollegeId());
                organizeElastic.setCollegeName(organizeVo.getCollegeName());
                organizeService.save(organizeElastic);
                ajaxUtils.success().msg("保存成功");
            }
        } else {
            ajaxUtils.fail().msg("填写信息错误，请检查");
        }
        return ajaxUtils;
    }

    /**
     * 保存班级更改
     *
     * @param organizeVo    班级
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/organize/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils organizeUpdate(@Valid OrganizeVo organizeVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(organizeVo.getOrganizeId())) {
            Organize organize = organizeService.findById(organizeVo.getOrganizeId());
            if (!ObjectUtils.isEmpty(organize)) {
                Byte isDel = 0;
                if (!ObjectUtils.isEmpty(organizeVo.getOrganizeIsDel()) && organizeVo.getOrganizeIsDel() == 1) {
                    isDel = 1;
                }
                organize.setOrganizeIsDel(isDel);
                organize.setOrganizeName(organizeVo.getOrganizeName());
                organize.setScienceId(organizeVo.getScienceId());
                organize.setGrade(organizeVo.getGrade());
                organizeService.update(organize);
                return AjaxUtils.of().success().msg("更改成功");
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }

    /**
     * 批量更改班级状态
     *
     * @param organizeIds 班级ids
     * @param isDel       is_del
     * @return true注销成功
     */
    @RequestMapping(value = "/web/data/organize/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils organizeUpdateDel(String organizeIds, Byte isDel) {
        if (StringUtils.hasLength(organizeIds) && SmallPropsUtils.StringIdsIsNumber(organizeIds)) {
            log.debug(" ids : {}", organizeIds);
            organizeService.updateIsDel(SmallPropsUtils.StringIdsToList(organizeIds), isDel);
            return AjaxUtils.of().success().msg("更改班级状态成功");
        }
        return AjaxUtils.of().fail().msg("更改班级状态失败");
    }
}
