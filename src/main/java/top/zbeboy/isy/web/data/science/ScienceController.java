package top.zbeboy.isy.web.data.science;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Record2;
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
import top.zbeboy.isy.domain.tables.pojos.Science;
import top.zbeboy.isy.domain.tables.records.ScienceRecord;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.ScienceService;
import top.zbeboy.isy.web.bean.data.science.ScienceBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.data.science.ScienceVo;

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
public class ScienceController {

    @Resource
    private ScienceService scienceService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    /**
     * 通过系id获取全部专业
     *
     * @param departmentId 系id
     * @return 系下全部专业
     */
    @RequestMapping(value = "/user/sciences", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Science> sciences(@RequestParam("departmentId") int departmentId) {
        AjaxUtils<Science> ajaxUtils = AjaxUtils.of();
        List<Science> sciences = new ArrayList<>();
        Byte isDel = 0;
        Science science = new Science(0, "请选择专业", null, isDel, 0);
        sciences.add(science);
        Result<ScienceRecord> scienceRecords = scienceService.findByDepartmentIdAndIsDel(departmentId, isDel);
        for (ScienceRecord r : scienceRecords) {
            Science tempScience = new Science(r.getScienceId(), r.getScienceName(), r.getScienceCode(), r.getScienceIsDel(), r.getDepartmentId());
            sciences.add(tempScience);
        }
        return ajaxUtils.success().msg("获取专业数据成功！").listData(sciences);
    }

    /**
     * 通过年级与系id获取全部专业
     *
     * @param grade 年级
     * @return 年级下全部专业
     */
    @RequestMapping(value = "/user/grade/sciences", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Science> gradeSciences(@RequestParam("grade") String grade, @RequestParam("departmentId") int departmentId) {
        AjaxUtils<Science> ajaxUtils = AjaxUtils.of();
        Result<Record2<String, Integer>> scienceRecords = scienceService.findByGradeAndDepartmentId(grade, departmentId);
        List<Science> sciences = scienceRecords.into(Science.class);
        return ajaxUtils.success().msg("获取专业数据成功！").listData(sciences);
    }

    /**
     * 专业数据
     *
     * @return 专业数据页面
     */
    @RequestMapping(value = "/web/menu/data/science", method = RequestMethod.GET)
    public String scienceData() {
        return "web/data/science/science_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/science/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<ScienceBean> scienceDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("science_id");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("science_name");
        headers.add("science_code");
        headers.add("science_is_del");
        headers.add("operator");
        DataTablesUtils<ScienceBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = scienceService.findAllByPage(dataTablesUtils);
        List<ScienceBean> scienceBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            scienceBeen = records.into(ScienceBean.class);
        }
        dataTablesUtils.setData(scienceBeen);
        dataTablesUtils.setiTotalRecords(scienceService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(scienceService.countByCondition(dataTablesUtils));

        return dataTablesUtils;
    }

    /**
     * 专业数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/data/science/add", method = RequestMethod.GET)
    public String scienceAdd(ModelMap modelMap) {
        commonControllerMethodService.currentUserRoleNameAndCollegeIdPageParam(modelMap);
        return "web/data/science/science_add::#page-wrapper";
    }

    /**
     * 专业数据编辑
     *
     * @param id       专业id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/data/science/edit", method = RequestMethod.GET)
    public String scienceEdit(@RequestParam("id") int id, ModelMap modelMap) {
        Optional<Record> record = scienceService.findByIdRelation(id);
        ScienceBean scienceBean;
        if (record.isPresent()) {
            scienceBean = record.get().into(ScienceBean.class);
        } else {
            scienceBean = new ScienceBean();
        }
        modelMap.addAttribute("science", scienceBean);
        commonControllerMethodService.currentUserRoleNameAndCollegeIdPageParam(modelMap);
        return "web/data/science/science_edit::#page-wrapper";
    }

    /**
     * 保存时检验专业名是否重复
     *
     * @param scienceName  专业名
     * @param departmentId 系id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/science/save/valid/name", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidName(@RequestParam("scienceName") String scienceName, @RequestParam("departmentId") int departmentId) {
        if (StringUtils.hasLength(scienceName)) {
            Result<ScienceRecord> scienceRecords = scienceService.findByScienceNameAndDepartmentId(scienceName, departmentId);
            if (ObjectUtils.isEmpty(scienceRecords)) {
                return AjaxUtils.of().success().msg("专业名不存在");
            } else {
                return AjaxUtils.of().fail().msg("专业名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("专业名不能为空");
    }

    /**
     * 保存时检验专业代码是否重复
     *
     * @param scienceCode 专业代码
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/science/save/valid/code", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidCode(@RequestParam("scienceCode") String scienceCode) {
        if (StringUtils.hasLength(scienceCode)) {
            Result<ScienceRecord> scienceRecords = scienceService.findByScienceCode(scienceCode);
            if (ObjectUtils.isEmpty(scienceRecords)) {
                return AjaxUtils.of().success().msg("专业代码不存在");
            } else {
                return AjaxUtils.of().fail().msg("专业代码已存在");
            }
        }
        return AjaxUtils.of().fail().msg("专业代码不能为空");
    }

    /**
     * 检验编辑时专业名重复
     *
     * @param id           专业id
     * @param scienceName  专业名
     * @param departmentId 系id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/science/update/valid/name", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidName(@RequestParam("scienceId") int id, @RequestParam("scienceName") String scienceName, @RequestParam("departmentId") int departmentId) {
        Result<ScienceRecord> scienceRecords = scienceService.findByScienceNameAndDepartmentIdNeScienceId(scienceName, id, departmentId);
        if (scienceRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("专业名不重复");
        }

        return AjaxUtils.of().fail().msg("专业名重复");
    }

    /**
     * 检验编辑时专业代码重复
     *
     * @param id          专业id
     * @param scienceCode 专业代码
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/science/update/valid/code", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidCode(@RequestParam("scienceId") int id, @RequestParam("scienceCode") String scienceCode) {
        Result<ScienceRecord> scienceRecords = scienceService.findByScienceCodeNeScienceId(scienceCode, id);
        if (scienceRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("专业代码不重复");
        }

        return AjaxUtils.of().fail().msg("专业代码重复");
    }

    /**
     * 保存专业信息
     *
     * @param scienceVo     专业
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/science/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils scienceSave(@Valid ScienceVo scienceVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Science science = new Science();
            Byte isDel = 0;
            if (null != scienceVo.getScienceIsDel() && scienceVo.getScienceIsDel() == 1) {
                isDel = 1;
            }
            science.setScienceIsDel(isDel);
            science.setScienceName(scienceVo.getScienceName());
            science.setScienceCode(scienceVo.getScienceCode());
            science.setDepartmentId(scienceVo.getDepartmentId());
            scienceService.save(science);
            return AjaxUtils.of().success().msg("保存成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 保存专业更改
     *
     * @param scienceVo     专业
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/science/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils scienceUpdate(@Valid ScienceVo scienceVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(scienceVo.getScienceId())) {
            Science science = scienceService.findById(scienceVo.getScienceId());
            if (!ObjectUtils.isEmpty(science)) {
                Byte isDel = 0;
                if (!ObjectUtils.isEmpty(scienceVo.getScienceIsDel()) && scienceVo.getScienceIsDel() == 1) {
                    isDel = 1;
                }
                science.setScienceIsDel(isDel);
                science.setScienceName(scienceVo.getScienceName());
                science.setScienceCode(scienceVo.getScienceCode());
                science.setDepartmentId(scienceVo.getDepartmentId());
                scienceService.update(science);
                return AjaxUtils.of().success().msg("更改成功");
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }

    /**
     * 批量更改专业状态
     *
     * @param scienceIds 专业ids
     * @param isDel      is_del
     * @return true注销成功
     */
    @RequestMapping(value = "/web/data/science/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils scienceUpdateDel(String scienceIds, Byte isDel) {
        if (StringUtils.hasLength(scienceIds) && SmallPropsUtils.StringIdsIsNumber(scienceIds)) {
            scienceService.updateIsDel(SmallPropsUtils.StringIdsToList(scienceIds), isDel);
            return AjaxUtils.of().success().msg("更改专业状态成功");
        }
        return AjaxUtils.of().fail().msg("更改专业状态失败");
    }
}
