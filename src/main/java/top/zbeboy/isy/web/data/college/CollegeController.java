package top.zbeboy.isy.web.data.college;

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
import top.zbeboy.isy.domain.tables.pojos.College;
import top.zbeboy.isy.domain.tables.pojos.CollegeApplication;
import top.zbeboy.isy.domain.tables.records.CollegeApplicationRecord;
import top.zbeboy.isy.domain.tables.records.CollegeRecord;
import top.zbeboy.isy.service.CollegeApplicationService;
import top.zbeboy.isy.service.CollegeService;
import top.zbeboy.isy.web.bean.data.college.CollegeBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.data.college.CollegeVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-08-21.
 */
@Controller
public class CollegeController {

    private final Logger log = LoggerFactory.getLogger(CollegeController.class);

    @Resource
    private CollegeService collegeService;

    @Resource
    private CollegeApplicationService collegeApplicationService;

    /**
     * 通过学校id获取全部院
     *
     * @param schoolId 学校id
     * @return 学校下全部院
     */
    @RequestMapping(value = "/user/colleges", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<College> colleges(@RequestParam("schoolId") int schoolId) {
        List<College> colleges = new ArrayList<>();
        Byte isDel = 0;
        College college = new College(0, "请选择院", isDel, 0);
        colleges.add(college);
        Result<CollegeRecord> collegeRecords = collegeService.findBySchoolId(schoolId);
        for (CollegeRecord r : collegeRecords) {
            College tempCollege = new College(r.getCollegeId(), r.getCollegeName(), r.getCollegeIsDel(), r.getSchoolId());
            colleges.add(tempCollege);
        }
        return new AjaxUtils<College>().success().msg("获取院数据成功！").listData(colleges);
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
    @RequestMapping(value = "/web/data/college/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("collegeName") String collegeName, @RequestParam("schoolId") int schoolId) {
        if (StringUtils.hasLength(collegeName)) {
            Result<CollegeRecord> collegeRecords = collegeService.findByCollegeNameAndSchoolId(collegeName, schoolId);
            if (collegeRecords.isEmpty()) {
                return new AjaxUtils().success().msg("院名不存在");
            } else {
                return new AjaxUtils().fail().msg("院名已存在");
            }
        }
        return new AjaxUtils().fail().msg("院名不能为空");
    }

    /**
     * 检验编辑时院名重复
     *
     * @param id          院id
     * @param collegeName 院名
     * @param schoolId    学校id
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/college/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("collegeId") int id, @RequestParam("collegeName") String collegeName, @RequestParam("schoolId") int schoolId) {
        Result<CollegeRecord> collegeRecords = collegeService.findByCollegeNameAndSchoolIdNeCollegeId(collegeName, id, schoolId);
        if (collegeRecords.isEmpty()) {
            return new AjaxUtils().success().msg("院名不重复");
        }

        return new AjaxUtils().fail().msg("院名重复");
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
                college.setSchoolId(collegeVo.getSchoolId());
                collegeService.update(college);
                return new AjaxUtils().success().msg("更改成功");
            }
        }
        return new AjaxUtils().fail().msg("更改失败");
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
            college.setSchoolId(collegeVo.getSchoolId());
            collegeService.save(college);
            return new AjaxUtils().success().msg("保存成功");
        }
        return new AjaxUtils().fail().msg("填写信息错误，请检查");
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
            return new AjaxUtils().success().msg("更改院状态成功");
        }
        return new AjaxUtils().fail().msg("更改院状态失败");
    }

    /**
     * 应用挂载
     *
     * @return 应用挂载页面
     */
    @RequestMapping(value = "/web/data/college/mount", method = RequestMethod.GET)
    public String collegeMount(@RequestParam("id") int collegeId, ModelMap modelMap) {
        College college = collegeService.findById(collegeId);
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
        Result<CollegeApplicationRecord> collegeApplicationRecords = collegeApplicationService.findByCollegeId(collegeId);
        List<CollegeApplication> collegeApplications = new ArrayList<>();
        if (collegeApplicationRecords.isNotEmpty()) {
            collegeApplications = collegeApplicationRecords.into(CollegeApplication.class);
        }
        return new AjaxUtils<CollegeApplication>().success().listData(collegeApplications);
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
            if (StringUtils.hasLength(applicationIds) && SmallPropsUtils.StringIdsIsNumber(applicationIds)) {
                List<Integer> ids = SmallPropsUtils.StringIdsToList(applicationIds);
                ids.forEach(id -> {
                    CollegeApplication collegeApplication = new CollegeApplication(id, collegeId);
                    collegeApplicationService.save(collegeApplication);
                });
            }
        }
        return new AjaxUtils().success().msg("更新成功");
    }
}
