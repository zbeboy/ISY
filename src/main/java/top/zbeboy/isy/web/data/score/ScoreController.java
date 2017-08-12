package top.zbeboy.isy.web.data.score;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.ScoreType;
import top.zbeboy.isy.service.data.ScoreTypeService;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017-07-23.
 */
@Slf4j
@Controller
public class ScoreController {

    @Resource
    private ScoreTypeService scoreTypeService;

    /**
     * 获取全部类型
     *
     * @return 全部类型 json
     */
    @RequestMapping(value = "/user/scores", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<ScoreType> scores() {
        AjaxUtils<ScoreType> ajaxUtils = AjaxUtils.of();
        List<ScoreType> scoreTypes = new ArrayList<>();
        ScoreType scoreType = new ScoreType(0, "请选择成绩");
        scoreTypes.add(scoreType);
        scoreTypes.addAll(scoreTypeService.findAll());
        return ajaxUtils.success().msg("获取成绩数据成功！").listData(scoreTypes);
    }
}
