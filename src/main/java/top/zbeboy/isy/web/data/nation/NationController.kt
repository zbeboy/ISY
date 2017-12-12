package top.zbeboy.isy.web.data.nation

import org.springframework.stereotype.Controller
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.Nation
import top.zbeboy.isy.service.data.NationService
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.data.nation.NationVo
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Controller
open class NationController {

    @Resource
    open lateinit var nationService: NationService

    /**
     * 获取全部民族
     *
     * @return 全部民族
     */
    @RequestMapping(value = ["/user/nations"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun nations(): AjaxUtils<Nation> {
        val ajaxUtils = AjaxUtils.of<Nation>()
        val nations = ArrayList<Nation>()
        val nation = Nation(0, "请选择民族")
        nations.add(nation)
        nations.addAll(nationService.findAll())
        return ajaxUtils.success().msg("获取民族数据成功！").listData(nations)
    }

    /**
     * 民族数据
     *
     * @return 民族数据页面
     */
    @RequestMapping(value = ["/web/menu/data/nation"], method = [(RequestMethod.GET)])
    fun nationData(): String {
        return "web/data/nation/nation_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/nation/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun nationDatas(request: HttpServletRequest): DataTablesUtils<Nation> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("nation_id")
        headers.add("nation_name")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<Nation>(request, headers)
        val records = nationService.findAllByPage(dataTablesUtils)
        var nations: List<Nation> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            nations = records.into(Nation::class.java)
        }
        dataTablesUtils.data = nations
        dataTablesUtils.setiTotalRecords(nationService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(nationService.countByCondition(dataTablesUtils).toLong())
        return dataTablesUtils
    }

    /**
     * 保存时检验民族是否重复
     *
     * @param name  民族
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/nation/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("nationName") name: String): AjaxUtils<*> {
        val nationName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(nationName)) {
            val nations = nationService.findByNationName(nationName)
            return if (ObjectUtils.isEmpty(nations)) {
                AjaxUtils.of<Any>().success().msg("民族名不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("民族名已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("民族名不能为空")
    }

    /**
     * 保存民族信息
     *
     * @param nationVo      民族
     * @param bindingResult 检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/nation/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun nationSave(@Valid nationVo: NationVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val nation = Nation()
            nation.nationName = StringUtils.trimWhitespace(nationVo.nationName)
            nationService.save(nation)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 检验编辑时民族名重复
     *
     * @param id         民族id
     * @param name  民族名
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/nation/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("nationId") id: Int, @RequestParam("nationName") name: String): AjaxUtils<*> {
        val nationName = StringUtils.trimWhitespace(name)
        val nationRecords = nationService.findByNationNameNeNationId(nationName, id)
        return if (nationRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("民族名不重复")
        } else AjaxUtils.of<Any>().fail().msg("民族名重复")

    }


    /**
     * 保存更改
     *
     * @param nationVo      民族
     * @param bindingResult 检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/nation/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun nationUpdate(@Valid nationVo: NationVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(nationVo.nationId)) {
            val nation = nationService.findById(nationVo.nationId!!)
            if (!ObjectUtils.isEmpty(nation)) {
                nation.nationName = StringUtils.trimWhitespace(nationVo.nationName)
                nationService.update(nation)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }

}