package top.zbeboy.isy.web.data.score

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.ScoreType
import top.zbeboy.isy.service.data.ScoreTypeService
import top.zbeboy.isy.web.util.AjaxUtils
import java.util.ArrayList
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Controller
open class ScoreController {

    @Resource
    open lateinit var scoreTypeService: ScoreTypeService


    /**
     * 获取全部类型
     *
     * @return 全部类型 json
     */
    @RequestMapping(value = ["/user/scores"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun scores(): AjaxUtils<ScoreType> {
        val ajaxUtils = AjaxUtils.of<ScoreType>()
        val scoreTypes = ArrayList<ScoreType>()
        val scoreType = ScoreType(0, "请选择成绩")
        scoreTypes.add(scoreType)
        scoreTypes.addAll(scoreTypeService.findAll())
        return ajaxUtils.success().msg("获取成绩数据成功！").listData(scoreTypes)
    }
}