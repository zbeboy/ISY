package top.zbeboy.isy.web.data.school;

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
import top.zbeboy.isy.domain.tables.pojos.School;
import top.zbeboy.isy.domain.tables.records.SchoolRecord;
import top.zbeboy.isy.service.data.SchoolService;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.data.school.SchoolVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-08-21.
 */
@Controller
public class SchoolController {

    private final Logger log = LoggerFactory.getLogger(SchoolController.class);

    @Resource
    private SchoolService schoolService;

    /**
     * 获取全部学校
     *
     * @return 全部学校 json
     */
    @RequestMapping(value = "/user/schools", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<School> schools() {
        AjaxUtils<School> ajaxUtils = AjaxUtils.of();
        List<School> schools = new ArrayList<>();
        Byte isDel = 0;
        School school = new School(0, "请选择学校", isDel);
        schools.add(school);
        Result<SchoolRecord> schoolRecords = schoolService.findByIsDel(isDel);
        for (SchoolRecord r : schoolRecords) {
            School tempSchool = new School(r.getSchoolId(), r.getSchoolName(), r.getSchoolIsDel());
            schools.add(tempSchool);
        }
        return ajaxUtils.success().msg("获取学校数据成功！").listData(schools);
    }

    /**
     * 学校数据
     *
     * @return 学校数据页面
     */
    @RequestMapping(value = "/web/menu/data/school", method = RequestMethod.GET)
    public String schoolData() {
        return "web/data/school/school_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/school/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<School> schoolDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("school_id");
        headers.add("school_name");
        headers.add("school_is_del");
        headers.add("operator");
        DataTablesUtils<School> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = schoolService.findAllByPage(dataTablesUtils);
        List<School> schools = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            schools = records.into(School.class);
        }
        dataTablesUtils.setData(schools);
        dataTablesUtils.setiTotalRecords(schoolService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(schoolService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 学校数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/data/school/add", method = RequestMethod.GET)
    public String schoolAdd() {
        return "web/data/school/school_add::#page-wrapper";
    }

    /**
     * 学校数据编辑
     *
     * @param id       学校id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/data/school/edit", method = RequestMethod.GET)
    public String schoolEdit(@RequestParam("id") int id, ModelMap modelMap) {
        School school = schoolService.findById(id);
        modelMap.addAttribute("school", school);
        return "web/data/school/school_edit::#page-wrapper";
    }

    /**
     * 保存时检验学校名是否重复
     *
     * @param schoolName 学校名
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/school/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("schoolName") String schoolName) {
        if (StringUtils.hasLength(schoolName)) {
            List<School> schools = schoolService.findBySchoolName(schoolName);
            if (ObjectUtils.isEmpty(schools)) {
                return AjaxUtils.of().success().msg("学校名不存在");
            } else {
                return AjaxUtils.of().fail().msg("学校名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("学校名不能为空");
    }

    /**
     * 保存学校信息
     *
     * @param schoolVo      学校
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/school/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils schoolSave(@Valid SchoolVo schoolVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            School school = new School();
            Byte isDel = 0;
            if (null != schoolVo.getSchoolIsDel() && schoolVo.getSchoolIsDel() == 1) {
                isDel = 1;
            }
            school.setSchoolIsDel(isDel);
            school.setSchoolName(schoolVo.getSchoolName());
            schoolService.save(school);
            return AjaxUtils.of().success().msg("保存成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 检验编辑时学校名重复
     *
     * @param id         学校id
     * @param schoolName 学校名
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/school/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("schoolId") int id, @RequestParam("schoolName") String schoolName) {
        Result<SchoolRecord> schoolRecords = schoolService.findBySchoolNameNeSchoolId(schoolName, id);
        if (schoolRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("学校名不重复");
        }

        return AjaxUtils.of().fail().msg("学校名重复");
    }

    /**
     * 保存学校更改
     *
     * @param schoolVo      学校
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/school/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils schoolUpdate(@Valid SchoolVo schoolVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(schoolVo.getSchoolId())) {
            School school = schoolService.findById(schoolVo.getSchoolId());
            if (!ObjectUtils.isEmpty(school)) {
                Byte isDel = 0;
                if (!ObjectUtils.isEmpty(schoolVo.getSchoolIsDel()) && schoolVo.getSchoolIsDel() == 1) {
                    isDel = 1;
                }
                school.setSchoolIsDel(isDel);
                school.setSchoolName(schoolVo.getSchoolName());
                schoolService.update(school);
                return AjaxUtils.of().success().msg("更改成功");
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }

    /**
     * 批量更改学校状态
     *
     * @param schoolIds 学校ids
     * @param isDel     is_del
     * @return true注销成功
     */
    @RequestMapping(value = "/web/data/school/update/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils schoolUpdateDel(String schoolIds, Byte isDel) {
        if (StringUtils.hasLength(schoolIds) && SmallPropsUtils.StringIdsIsNumber(schoolIds)) {
            schoolService.updateIsDel(SmallPropsUtils.StringIdsToList(schoolIds), isDel);
            return AjaxUtils.of().success().msg("更改学校状态成功");
        }
        return AjaxUtils.of().fail().msg("更改学校状态失败");
    }
}
