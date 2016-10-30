package top.zbeboy.isy.web.data.nation;

import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.Nation;
import top.zbeboy.isy.service.NationService;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
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
        List<Nation> nations = new ArrayList<>();
        Nation nation = new Nation(0, "请选择民族");
        nations.add(nation);
        nations.addAll(nationService.findAll());
        return new AjaxUtils<Nation>().success().msg("获取民族数据成功！").listData(nations);
    }
}
