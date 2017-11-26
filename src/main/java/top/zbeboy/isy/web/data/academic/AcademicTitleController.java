package top.zbeboy.isy.web.data.academic;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.AcademicTitle;
import top.zbeboy.isy.domain.tables.records.AcademicTitleRecord;
import top.zbeboy.isy.service.data.AcademicTitleService;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.data.academic.AcademicTitleVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbeboy on 2017/4/21.
 */
@Slf4j
@Controller
public class AcademicTitleController {

    @Resource
    private AcademicTitleService academicTitleService;

    /**
     * 获取全部职称
     *
     * @return 全部职称
     */
    @RequestMapping(value = "/user/academic", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<AcademicTitle> academic() {
        AjaxUtils<AcademicTitle> ajaxUtils = AjaxUtils.of();
        List<AcademicTitle> academicTitles = new ArrayList<>();
        AcademicTitle academicTitle = new AcademicTitle(0, "请选择职称");
        academicTitles.add(academicTitle);
        academicTitles.addAll(academicTitleService.findAll());
        return ajaxUtils.success().msg("获取职称数据成功！").listData(academicTitles);
    }

    /**
     * 职称数据
     *
     * @return 职称数据页面
     */
    @RequestMapping(value = "/web/menu/data/academic", method = RequestMethod.GET)
    public String academicData() {
        return "web/data/academic/academic_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/academic/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<AcademicTitle> academicDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("academic_title_id");
        headers.add("academic_title_name");
        headers.add("operator");
        DataTablesUtils<AcademicTitle> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = academicTitleService.findAllByPage(dataTablesUtils);
        List<AcademicTitle> academicTitles = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            academicTitles = records.into(AcademicTitle.class);
        }
        dataTablesUtils.setData(academicTitles);
        dataTablesUtils.setITotalRecords(academicTitleService.countAll());
        dataTablesUtils.setITotalDisplayRecords(academicTitleService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 保存时检验职称是否重复
     *
     * @param academicTitleName 职称
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/academic/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("academicTitleName") String academicTitleName) {
        if (StringUtils.hasLength(academicTitleName)) {
            List<AcademicTitle> academicTitles = academicTitleService.findByAcademicTitleName(academicTitleName);
            if (ObjectUtils.isEmpty(academicTitles)) {
                return AjaxUtils.of().success().msg("职称不存在");
            } else {
                return AjaxUtils.of().fail().msg("职称已存在");
            }
        }
        return AjaxUtils.of().fail().msg("职称不能为空");
    }

    /**
     * 保存职称信息
     *
     * @param academicTitleVo 职称
     * @param bindingResult   检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/academic/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils academicSave(@Valid AcademicTitleVo academicTitleVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            AcademicTitle academicTitle = new AcademicTitle();
            academicTitle.setAcademicTitleName(academicTitleVo.getAcademicTitleName());
            academicTitleService.save(academicTitle);
            return AjaxUtils.of().success().msg("保存成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 检验编辑时职称重复
     *
     * @param id                职称id
     * @param academicTitleName 职称
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/academic/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("academicTitleId") int id, @RequestParam("academicTitleName") String academicTitleName) {
        Result<AcademicTitleRecord> academicTitleRecords = academicTitleService.findByAcademicTitleNameNeAcademicTitleId(academicTitleName, id);
        if (academicTitleRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("职称不重复");
        }

        return AjaxUtils.of().fail().msg("职称重复");
    }


    /**
     * 保存更改
     *
     * @param academicTitleVo 职称
     * @param bindingResult   检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/academic/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils academicUpdate(@Valid AcademicTitleVo academicTitleVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(academicTitleVo.getAcademicTitleId())) {
            AcademicTitle academicTitle = academicTitleService.findById(academicTitleVo.getAcademicTitleId());
            if (!ObjectUtils.isEmpty(academicTitle)) {
                academicTitle.setAcademicTitleName(academicTitleVo.getAcademicTitleName());
                academicTitleService.update(academicTitle);
                return AjaxUtils.of().success().msg("更改成功");
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }
}
