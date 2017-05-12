package top.zbeboy.isy.web.data.nation;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.Nation;
import top.zbeboy.isy.domain.tables.records.NationRecord;
import top.zbeboy.isy.service.data.NationService;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.data.nation.NationVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-10-30.
 */
@Controller
public class NationController {

    private final Logger log = LoggerFactory.getLogger(NationController.class);

    @Resource
    private NationService nationService;

    /**
     * 获取全部民族
     *
     * @return 全部民族
     */
    @RequestMapping(value = "/user/nations", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<Nation> nations() {
        AjaxUtils<Nation> ajaxUtils = AjaxUtils.of();
        List<Nation> nations = new ArrayList<>();
        Nation nation = new Nation(0, "请选择民族");
        nations.add(nation);
        nations.addAll(nationService.findAll());
        return ajaxUtils.success().msg("获取民族数据成功！").listData(nations);
    }

    /**
     * 民族数据
     *
     * @return 民族数据页面
     */
    @RequestMapping(value = "/web/menu/data/nation", method = RequestMethod.GET)
    public String nationData() {
        return "web/data/nation/nation_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/nation/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<Nation> nationDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("nation_id");
        headers.add("nation_name");
        headers.add("operator");
        DataTablesUtils<Nation> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = nationService.findAllByPage(dataTablesUtils);
        List<Nation> nations = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            nations = records.into(Nation.class);
        }
        dataTablesUtils.setData(nations);
        dataTablesUtils.setiTotalRecords(nationService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(nationService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 保存时检验民族是否重复
     *
     * @param nationName 民族
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/nation/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("nationName") String nationName) {
        if (StringUtils.hasLength(nationName)) {
            List<Nation> nations = nationService.findByNationName(nationName);
            if (ObjectUtils.isEmpty(nations)) {
                return AjaxUtils.of().success().msg("民族名不存在");
            } else {
                return AjaxUtils.of().fail().msg("民族名已存在");
            }
        }
        return AjaxUtils.of().fail().msg("民族名不能为空");
    }

    /**
     * 保存民族信息
     *
     * @param nationVo      民族
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/nation/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils nationSave(@Valid NationVo nationVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Nation nation = new Nation();
            nation.setNationName(nationVo.getNationName());
            nationService.save(nation);
            return AjaxUtils.of().success().msg("保存成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 检验编辑时民族名重复
     *
     * @param id         民族id
     * @param nationName 民族名
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/nation/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("nationId") int id, @RequestParam("nationName") String nationName) {
        Result<NationRecord> nationRecords = nationService.findByNationNameNeNationId(nationName, id);
        if (nationRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("民族名不重复");
        }

        return AjaxUtils.of().fail().msg("民族名重复");
    }


    /**
     * 保存更改
     *
     * @param nationVo      民族
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/nation/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils nationUpdate(@Valid NationVo nationVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(nationVo.getNationId())) {
            Nation nation = nationService.findById(nationVo.getNationId());
            if (!ObjectUtils.isEmpty(nation)) {
                nation.setNationName(nationVo.getNationName());
                nationService.update(nation);
                return AjaxUtils.of().success().msg("更改成功");
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }
}
