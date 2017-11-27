package top.zbeboy.isy.web.data.college;

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
import top.zbeboy.isy.domain.tables.pojos.College;
import top.zbeboy.isy.domain.tables.pojos.CollegeApplication;
import top.zbeboy.isy.domain.tables.records.CollegeApplicationRecord;
import top.zbeboy.isy.domain.tables.records.CollegeRecord;
import top.zbeboy.isy.service.data.CollegeApplicationService;
import top.zbeboy.isy.service.data.CollegeService;
import top.zbeboy.isy.service.system.ApplicationService;
import top.zbeboy.isy.web.bean.data.college.CollegeBean;
import top.zbeboy.isy.web.bean.tree.TreeBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.data.college.CollegeVo;

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
public class CollegeController {

    @Resource
    private CollegeService collegeService;

    @Resource
    private CollegeApplicationService collegeApplicationService;

    @Resource
    private ApplicationService applicationService;

    /**
     * 通过学校id获取全部院
     *
     * @param schoolId 学校id
     * @return 学校下全部院
     */
    @RequestMapping(value = "/user/colleges", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<College> colleges(@RequestParam("schoolId") int schoolId) {
        AjaxUtils<College> ajaxUtils = AjaxUtils.of();
        List<College> colleges = new ArrayList<>();
        Byte isDel = 0;
        College college = new College(0, "请选择院", null, null, isDel, 0);
        colleges.add(college);
        Result<CollegeRecord> collegeRecords = collegeService.findBySchoolIdAndIsDel(schoolId, isDel);
        for (CollegeRecord r : collegeRecords) {
            College tempCollege = new College(r.getCollegeId(), r.getCollegeName(), r.getCollegeAddress(), r.getCollegeCode(), r.getCollegeIsDel(), r.getSchoolId());
            colleges.add(tempCollege);
        }
        return ajaxUtils.success().msg("获取院数据成功！").listData(colleges);
    }

    /**
     * 院数据
     *
     * @return 院数据页面
     */
    @RequestMapping(value = "/web/menu/data/college", method = RequestMethod.GET)
    public String collegeData() {
        return "web/data/college/college_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/college/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<CollegeBean> collegeDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("college_id");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("college_code");
        headers.add("college_address");
        headers.add("college_is_del");
        headers.add("operator");
        DataTablesUtils<CollegeBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = collegeService.findAllByPage(dataTablesUtils);
        List<CollegeBean> colleges = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            colleges = records.into(CollegeBean.class);
        }
        dataTablesUtils.setData(colleges);
        dataTablesUtils.setiTotalRecords(collegeService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(collegeService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 院数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/data/college/add", method = RequestMethod.GET)
    public String collegeAdd() {
        return "web/data/college/college_add::#page-wrapper";
    }

    /**
     * 院数据编辑
     *
     * @param id       院id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/data/college/edit", method = RequestMethod.GET)
    public String collegeEdit(@RequestParam("id") int id, ModelMap modelMap) {
        College college = collegeService.findById(id);
        modelMap.addAttribute("college", college);
        return "web/data/college/college_edit::#page-wrapper";
    }

    /**
     * 保存时检验院名是否重复
     *
     * @param collegeName 院名
     * @param schoolId    学校id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/college/save/valid/name", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidName(@RequestParam("collegeName") String collegeName, @RequestParam("schoolId") int schoolId) {
        if (StringUtils.hasLength(collegeName)) {
            Result<CollegeRecord> collegeRecords = collegeService.findByCollegeNameAndSchoolId(collegeName, schoolId);
            if (collegeRecords.isEmpty()) {
                return AjaxUtils.of().success().msg("院名不存在");
            } else {
                return AjaxUtils.of().fail().msg("院名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("院名不能为空");
    }

    /**
     * 检验院代码是否重复
     *
     * @param collegeCode 院代码
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/college/save/valid/code", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValidCode(@RequestParam("collegeCode") String collegeCode) {
        if (StringUtils.hasLength(collegeCode)) {
            Result<CollegeRecord> collegeRecords = collegeService.findByCollegeCode(collegeCode);
            if (collegeRecords.isEmpty()) {
                return AjaxUtils.of().success().msg("院代码不存在");
            } else {
                return AjaxUtils.of().fail().msg("院代码已存在");
            }
        }
        return AjaxUtils.of().fail().msg("院代码不能为空");
    }

    /**
     * 检验编辑时院名重复
     *
     * @param id          院id
     * @param collegeName 院名
     * @param schoolId    学校id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/college/update/valid/name", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidName(@RequestParam("collegeId") int id, @RequestParam("collegeName") String collegeName, @RequestParam("schoolId") int schoolId) {
        Result<CollegeRecord> collegeRecords = collegeService.findByCollegeNameAndSchoolIdNeCollegeId(collegeName, id, schoolId);
        if (collegeRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("院名不重复");
        }

        return AjaxUtils.of().fail().msg("院名重复");
    }

    /**
     * 检验编辑时院代码重复
     *
     * @param id          院id
     * @param collegeCode 院代码
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/college/update/valid/code", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValidName(@RequestParam("collegeId") int id, @RequestParam("collegeCode") String collegeCode) {
        Result<CollegeRecord> collegeRecords = collegeService.findByCollegeCodeNeCollegeId(collegeCode, id);
        if (collegeRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("院代码不重复");
        }

        return AjaxUtils.of().fail().msg("院代码重复");
    }

    /**
     * 保存院更改
     *
     * @param collegeVo     院
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/college/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils collegeUpdate(@Valid CollegeVo collegeVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(collegeVo.getCollegeId())) {
            College college = collegeService.findById(collegeVo.getCollegeId());
            if (!ObjectUtils.isEmpty(college)) {
                Byte isDel = 0;
                if (!ObjectUtils.isEmpty(collegeVo.getCollegeIsDel()) && collegeVo.getCollegeIsDel() == 1) {
                    isDel = 1;
                }
                college.setCollegeIsDel(isDel);
                college.setCollegeName(collegeVo.getCollegeName());
                college.setCollegeCode(collegeVo.getCollegeCode());
                college.setCollegeAddress(collegeVo.getCollegeAddress());
                college.setSchoolId(collegeVo.getSchoolId());
                collegeService.update(college);
                return AjaxUtils.of().success().msg("更改成功");
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }

    /**
     * 保存院信息
     *
     * @param collegeVo     院
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/college/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils collegeSave(@Valid CollegeVo collegeVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            College college = new College();
            Byte isDel = 0;
            if (null != collegeVo.getCollegeIsDel() && collegeVo.getCollegeIsDel() == 1) {
                isDel = 1;
            }
            college.setCollegeIsDel(isDel);
            college.setCollegeName(collegeVo.getCollegeName());
            college.setCollegeCode(collegeVo.getCollegeCode());
            college.setCollegeAddress(collegeVo.getCollegeAddress());
            college.setSchoolId(collegeVo.getSchoolId());
            collegeService.save(college);
            return AjaxUtils.of().success().msg("保存成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 批量更改院状态
     *
     * @param collegeIds 院ids
     * @param isDel      is_del
     * @return true注销成功
     */
    @RequestMapping(value = "/web/data/college/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils collegeUpdateDel(String collegeIds, Byte isDel) {
        if (StringUtils.hasLength(collegeIds) && SmallPropsUtils.StringIdsIsNumber(collegeIds)) {
            collegeService.updateIsDel(SmallPropsUtils.StringIdsToList(collegeIds), isDel);
            return AjaxUtils.of().success().msg("更改院状态成功");
        }
        return AjaxUtils.of().fail().msg("更改院状态失败");
    }

    /**
     * 应用挂载
     *
     * @return 应用挂载页面
     */
    @RequestMapping(value = "/web/data/college/mount", method = RequestMethod.GET)
    public String collegeMount(@RequestParam("id") int collegeId, ModelMap modelMap) {
        Optional<Record> record = collegeService.findByIdRelation(collegeId);
        College college = new College();
        if (record.isPresent()) {
            college = record.get().into(CollegeBean.class);
        }
        modelMap.addAttribute("college", college);
        return "web/data/college/college_mount::#page-wrapper";
    }

    /**
     * 院与应用数据
     *
     * @param collegeId 院id
     * @return 数据
     */
    @RequestMapping(value = "/web/data/college/application/data", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<CollegeApplication> collegeApplicationData(@RequestParam("collegeId") int collegeId) {
        AjaxUtils<CollegeApplication> ajaxUtils = AjaxUtils.of();
        Result<CollegeApplicationRecord> collegeApplicationRecords = collegeApplicationService.findByCollegeId(collegeId);
        List<CollegeApplication> collegeApplications = new ArrayList<>();
        if (collegeApplicationRecords.isNotEmpty()) {
            collegeApplications = collegeApplicationRecords.into(CollegeApplication.class);
        }
        return ajaxUtils.success().listData(collegeApplications);
    }

    /**
     * 更新应用挂载
     *
     * @param collegeId      院id
     * @param applicationIds 应用ids
     * @return true 更新成功
     */
    @RequestMapping(value = "/web/data/college/update/mount", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateMount(@RequestParam("collegeId") int collegeId, String applicationIds) {
        if (collegeId > 0) {
            collegeApplicationService.deleteByCollegeId(collegeId);
            if (StringUtils.hasLength(applicationIds)) {
                List<String> ids = SmallPropsUtils.StringIdsToStringList(applicationIds);
                ids.forEach(id -> {
                    CollegeApplication collegeApplication = new CollegeApplication(id, collegeId);
                    collegeApplicationService.save(collegeApplication);
                });
            }
        }
        return AjaxUtils.of().success().msg("更新成功");
    }

    /**
     * 数据json
     *
     * @return json
     */
    @RequestMapping(value = "/web/data/college/application/json", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<TreeBean> applicationJson() {
        AjaxUtils<TreeBean> ajaxUtils = AjaxUtils.of();
        List<TreeBean> treeBeens = applicationService.getApplicationJson("0");
        return ajaxUtils.success().listData(treeBeens);
    }
}
