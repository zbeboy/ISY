package top.zbeboy.isy.web.data.politics;

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
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape;
import top.zbeboy.isy.domain.tables.records.PoliticalLandscapeRecord;
import top.zbeboy.isy.service.data.PoliticalLandscapeService;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.data.politics.PoliticsVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-10-30.
 */
@Controller
public class PoliticalLandscapeController {

    private final Logger log = LoggerFactory.getLogger(PoliticalLandscapeController.class);

    @Resource
    private PoliticalLandscapeService politicalLandscapeService;

    /**
     * 获取全部政治面貌
     *
     * @return 全部政治面貌
     */
    @RequestMapping(value = "/user/political_landscapes", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<PoliticalLandscape> politicalLandscapes() {
        AjaxUtils<PoliticalLandscape> ajaxUtils = AjaxUtils.of();
        List<PoliticalLandscape> politicalLandscapes = new ArrayList<>();
        PoliticalLandscape politicalLandscape = new PoliticalLandscape(0, "请选择政治面貌");
        politicalLandscapes.add(politicalLandscape);
        politicalLandscapes.addAll(politicalLandscapeService.findAll());
        return ajaxUtils.success().msg("获取政治面貌数据成功！").listData(politicalLandscapes);
    }

    /**
     * 政治面貌数据
     *
     * @return 政治面貌数据页面
     */
    @RequestMapping(value = "/web/menu/data/politics", method = RequestMethod.GET)
    public String politicsData() {
        return "web/data/politics/politics_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/politics/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<PoliticalLandscape> politicsDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("political_landscape_id");
        headers.add("political_landscape_name");
        headers.add("operator");
        DataTablesUtils<PoliticalLandscape> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = politicalLandscapeService.findAllByPage(dataTablesUtils);
        List<PoliticalLandscape> politics = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            politics = records.into(PoliticalLandscape.class);
        }
        dataTablesUtils.setData(politics);
        dataTablesUtils.setiTotalRecords(politicalLandscapeService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(politicalLandscapeService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 保存时检验政治面貌是否重复
     *
     * @param politicalLandscapeName 政治面貌
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/politics/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("politicalLandscapeName") String politicalLandscapeName) {
        if (StringUtils.hasLength(politicalLandscapeName)) {
            List<PoliticalLandscape> nations = politicalLandscapeService.findByPoliticalLandscapeName(politicalLandscapeName);
            if (ObjectUtils.isEmpty(nations)) {
                return AjaxUtils.of().success().msg("政治面貌不存在");
            } else {
                return AjaxUtils.of().fail().msg("政治面貌已存在");
            }
        }
        return AjaxUtils.of().fail().msg("政治面貌不能为空");
    }

    /**
     * 保存政治面貌信息
     *
     * @param politicsVo    政治面貌
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = "/web/data/politics/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils politicsSave(@Valid PoliticsVo politicsVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            PoliticalLandscape politicalLandscape = new PoliticalLandscape();
            politicalLandscape.setPoliticalLandscapeName(politicsVo.getPoliticalLandscapeName());
            politicalLandscapeService.save(politicalLandscape);
            return AjaxUtils.of().success().msg("保存成功");
        }
        return AjaxUtils.of().fail().msg("填写信息错误，请检查");
    }

    /**
     * 检验编辑时政治面貌重复
     *
     * @param id                     政治面貌id
     * @param politicalLandscapeName 政治面貌
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = "/web/data/politics/update/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils updateValid(@RequestParam("politicalLandscapeId") int id, @RequestParam("politicalLandscapeName") String politicalLandscapeName) {
        Result<PoliticalLandscapeRecord> politicalLandscapeRecords = politicalLandscapeService.findByNationNameNePoliticalLandscapeId(politicalLandscapeName, id);
        if (politicalLandscapeRecords.isEmpty()) {
            return AjaxUtils.of().success().msg("政治面貌不重复");
        }
        return AjaxUtils.of().fail().msg("政治面貌重复");
    }

    /**
     * 保存更改
     *
     * @param politicsVo    政治面貌
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = "/web/data/politics/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils politicsUpdate(@Valid PoliticsVo politicsVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(politicsVo.getPoliticalLandscapeId())) {
            PoliticalLandscape politicalLandscape = politicalLandscapeService.findById(politicsVo.getPoliticalLandscapeId());
            if (!ObjectUtils.isEmpty(politicalLandscape)) {
                politicalLandscape.setPoliticalLandscapeName(politicsVo.getPoliticalLandscapeName());
                politicalLandscapeService.update(politicalLandscape);
                return AjaxUtils.of().success().msg("更改成功");
            }
        }
        return AjaxUtils.of().fail().msg("更改失败");
    }
}
