package top.zbeboy.isy.web.data.politics

import org.springframework.stereotype.Controller
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape
import top.zbeboy.isy.service.data.PoliticalLandscapeService
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.data.politics.PoliticsVo
import java.util.ArrayList
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Controller
open class PoliticalLandscapeController {

    @Resource
    open lateinit var politicalLandscapeService: PoliticalLandscapeService


    /**
     * 获取全部政治面貌
     *
     * @return 全部政治面貌
     */
    @RequestMapping(value = ["/user/political_landscapes"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun politicalLandscapes(): AjaxUtils<PoliticalLandscape> {
        val ajaxUtils = AjaxUtils.of<PoliticalLandscape>()
        val politicalLandscapes = ArrayList<PoliticalLandscape>()
        val politicalLandscape = PoliticalLandscape(0, "请选择政治面貌")
        politicalLandscapes.add(politicalLandscape)
        politicalLandscapes.addAll(politicalLandscapeService.findAll())
        return ajaxUtils.success().msg("获取政治面貌数据成功！").listData(politicalLandscapes)
    }

    /**
     * 政治面貌数据
     *
     * @return 政治面貌数据页面
     */
    @RequestMapping(value = ["/web/menu/data/politics"], method = [(RequestMethod.GET)])
    fun politicsData(): String {
        return "web/data/politics/politics_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/politics/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun politicsDatas(request: HttpServletRequest): DataTablesUtils<PoliticalLandscape> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("political_landscape_id")
        headers.add("political_landscape_name")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<PoliticalLandscape>(request, headers)
        val records = politicalLandscapeService.findAllByPage(dataTablesUtils)
        var politics: List<PoliticalLandscape> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            politics = records.into(PoliticalLandscape::class.java)
        }
        dataTablesUtils.data = politics
        dataTablesUtils.setiTotalRecords(politicalLandscapeService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(politicalLandscapeService.countByCondition(dataTablesUtils).toLong())
        return dataTablesUtils
    }

    /**
     * 保存时检验政治面貌是否重复
     *
     * @param name  政治面貌
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/politics/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("politicalLandscapeName") name: String): AjaxUtils<*> {
        val politicalLandscapeName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(politicalLandscapeName)) {
            val nations = politicalLandscapeService.findByPoliticalLandscapeName(politicalLandscapeName)
            return if (ObjectUtils.isEmpty(nations)) {
                AjaxUtils.of<Any>().success().msg("政治面貌不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("政治面貌已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("政治面貌不能为空")
    }

    /**
     * 保存政治面貌信息
     *
     * @param politicsVo    政治面貌
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/politics/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun politicsSave(@Valid politicsVo: PoliticsVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val politicalLandscape = PoliticalLandscape()
            politicalLandscape.politicalLandscapeName = StringUtils.trimWhitespace(politicsVo.politicalLandscapeName!!)
            politicalLandscapeService.save(politicalLandscape)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 检验编辑时政治面貌重复
     *
     * @param id                     政治面貌id
     * @param name                   政治面貌
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/politics/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("politicalLandscapeId") id: Int, @RequestParam("politicalLandscapeName") name: String): AjaxUtils<*> {
        val politicalLandscapeName = StringUtils.trimWhitespace(name)
        val politicalLandscapeRecords = politicalLandscapeService.findByNationNameNePoliticalLandscapeId(politicalLandscapeName, id)
        return if (politicalLandscapeRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("政治面貌不重复")
        } else AjaxUtils.of<Any>().fail().msg("政治面貌重复")
    }

    /**
     * 保存更改
     *
     * @param politicsVo    政治面貌
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/politics/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun politicsUpdate(@Valid politicsVo: PoliticsVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(politicsVo.politicalLandscapeId)) {
            val politicalLandscape = politicalLandscapeService.findById(politicsVo.politicalLandscapeId!!)
            if (!ObjectUtils.isEmpty(politicalLandscape)) {
                politicalLandscape.politicalLandscapeName = StringUtils.trimWhitespace(politicsVo.politicalLandscapeName!!)
                politicalLandscapeService.update(politicalLandscape)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }
}