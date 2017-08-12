package top.zbeboy.isy.web.data.schoolroom;

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
import top.zbeboy.isy.domain.tables.pojos.Schoolroom;
import top.zbeboy.isy.domain.tables.records.SchoolroomRecord;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.SchoolroomService;
import top.zbeboy.isy.web.bean.data.schoolroom.SchoolroomBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.data.schoolroom.SchoolroomVo;

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
public class SchoolroomController {

    @Resource
    private SchoolroomService schoolroomService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    /**
     * 通过楼id获取全部教室
     *
     * @param buildingId 楼id
     * @return 楼下全部教室
     */
    @RequestMapping(value = "/user/schoolrooms", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Schoolroom> schoolrooms(@RequestParam("buildingId") int buildingId) {
        AjaxUtils<Schoolroom> ajaxUtils = AjaxUtils.of();
        List<Schoolroom> schoolrooms = new ArrayList<>();
        Byte isDel = 0;
        Schoolroom schoolroom = new Schoolroom(0, 0, "请选择教室", isDel);
        schoolrooms.add(schoolroom);
        Result<SchoolroomRecord> schoolroomRecords = schoolroomService.findByBuildingIdAndIsDel(buildingId, isDel);
        for (SchoolroomRecord r : schoolroomRecords) {
            Schoolroom tempSchoolroom = new Schoolroom(r.getSchoolroomId(), r.getBuildingId(), r.getBuildingCode(), r.getSchoolroomIsDel());
            schoolrooms.add(tempSchoolroom);
        }
        return ajaxUtils.success().msg("获取楼数据成功！").listData(schoolrooms);
    }

    /**
     * 教室数据
     *
     * @return 教室数据页面
     */
    @RequestMapping(value = "/web/menu/data/schoolroom", method = RequestMethod.GET)
    public String scienceData() {
        return "web/data/schoolroom/schoolroom_data::#page-wrapper";
    }

    /**
     * 教室数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/data/schoolroom/add", method = RequestMethod.GET)
    public String schoolroomAdd(ModelMap modelMap) {
        commonControllerMethodService.currentUserRoleNameAndCollegeIdPageParam(modelMap);
        return "web/data/schoolroom/schoolroom_add::#page-wrapper";
    }

    /**
     * 教室数据编辑
     *
     * @param id       专业id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/data/schoolroom/edit", method = RequestMethod.GET)
    public String schoolroomEdit(@RequestParam("id") int id, ModelMap modelMap) {
        Optional<Record> record = schoolroomService.findByIdRelation(id);
        SchoolroomBean schoolroomBean;
        if (record.isPresent()) {
            schoolroomBean = record.get().into(SchoolroomBean.class);
        } else {
            schoolroomBean = new SchoolroomBean();
        }
        modelMap.addAttribute("schoolroom", schoolroomBean);
        commonControllerMethodService.currentUserRoleNameAndCollegeIdPageParam(modelMap);
        return "web/data/schoolroom/schoolroom_edit::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/schoolroom/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<SchoolroomBean> schoolroomDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("schoolroom_id");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("building_name");
        headers.add("building_code");
        headers.add("schoolroom_is_del");
        headers.add("operator");
        DataTablesUtils<SchoolroomBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = schoolroomService.findAllByPage(dataTablesUtils);
        List<SchoolroomBean> schoolroomBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            schoolroomBeen = records.into(SchoolroomBean.class);
        }
        dataTablesUtils.setData(schoolroomBeen);
        dataTablesUtils.setiTotalRecords(schoolroomService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(schoolroomService.countByCondition(dataTablesUtils));

        return dataTablesUtils;
    }

    /**
     * 保存时检验教室是否重复
     *
     * @param buildingCode 教室名
     * @param buildingId   楼id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/schoolroom/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("buildingCode") String buildingCode, @RequestParam("buildingId") int buildingId) {
        if (StringUtils.hasLength(buildingCode)) {
            Result<SchoolroomRecord> schoolroomRecords = schoolroomService.findByBuildingCodeAndBuildingId(buildingCode, buildingId);
            if (ObjectUtils.isEmpty(schoolroomRecords)) {
                return AjaxUtils.of().success().msg("教室不存在");
            } else {
                return AjaxUtils.of().fail().msg("教室已存在");
            }
        }
        return AjaxUtils.of().fail().msg("教室不能为空");
    }

    /**
     * 检验编辑时教室重复
     *
     * @param id           教室id
     * @param buildingCode 教室
     * @param buildingId   楼id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/schoolroom/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("schoolroomId") int id, @RequestParam("buildingCode") String buildingCode, @RequestParam("buildingId") int buildingId) {
        Result<SchoolroomRecord> schoolroomRecords = schoolroomService.findByBuildingCodeAndBuildingIdNeSchoolroomId(buildingCode, id, buildingId);
        if (schoolroomRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("教室不重复");
        }

        return AjaxUtils.of().fail().msg("教室重复");
    }

    /**
     * 保存教室信息
     *
     * @param schoolroomVo  教室
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/schoolroom/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils scienceSave(@Valid SchoolroomVo schoolroomVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Schoolroom schoolroom = new Schoolroom();
            Byte isDel = 0;
            if (null != schoolroomVo.getSchoolroomIsDel() && schoolroomVo.getSchoolroomIsDel() == 1) {
                isDel = 1;
            }
            schoolroom.setSchoolroomIsDel(isDel);
            schoolroom.setBuildingCode(schoolroomVo.getBuildingCode());
            schoolroom.setBuildingId(schoolroomVo.getBuildingId());
            schoolroomService.save(schoolroom);
            return AjaxUtils.of().success().msg("保存成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 保存教室更改
     *
     * @param schoolroomVo  教室
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/schoolroom/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils scienceUpdate(@Valid SchoolroomVo schoolroomVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(schoolroomVo.getSchoolroomId())) {
            Schoolroom schoolroom = schoolroomService.findById(schoolroomVo.getSchoolroomId());
            if (!ObjectUtils.isEmpty(schoolroom)) {
                Byte isDel = 0;
                if (!ObjectUtils.isEmpty(schoolroomVo.getSchoolroomIsDel()) && schoolroomVo.getSchoolroomIsDel() == 1) {
                    isDel = 1;
                }
                schoolroom.setSchoolroomIsDel(isDel);
                schoolroom.setBuildingCode(schoolroomVo.getBuildingCode());
                schoolroom.setBuildingId(schoolroomVo.getBuildingId());
                schoolroomService.update(schoolroom);
                return AjaxUtils.of().success().msg("更改成功");
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }

    /**
     * 批量更改教室状态
     *
     * @param schoolroomIds 教室ids
     * @param isDel         is_del
     * @return true注销成功
     */
    @RequestMapping(value = "/web/data/schoolroom/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils scienceUpdateDel(String schoolroomIds, Byte isDel) {
        if (StringUtils.hasLength(schoolroomIds) && SmallPropsUtils.StringIdsIsNumber(schoolroomIds)) {
            schoolroomService.updateIsDel(SmallPropsUtils.StringIdsToList(schoolroomIds), isDel);
            return AjaxUtils.of().success().msg("更改教室状态成功");
        }
        return AjaxUtils.of().fail().msg("更改教室状态失败");
    }
}
