package top.zbeboy.isy.web.data.landscape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.Nation;
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape;
import top.zbeboy.isy.service.PoliticalLandscapeService;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
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
        List<PoliticalLandscape> politicalLandscapes = new ArrayList<>();
        PoliticalLandscape politicalLandscape = new PoliticalLandscape(0, "请选择政治面貌");
        politicalLandscapes.add(politicalLandscape);
        politicalLandscapes.addAll(politicalLandscapeService.findAll());
        return new AjaxUtils<PoliticalLandscape>().success().msg("获取政治面貌数据成功！").listData(politicalLandscapes);
    }
}
