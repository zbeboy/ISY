package top.zbeboy.isy.web.data.academic

import org.springframework.stereotype.Controller
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.AcademicTitle
import top.zbeboy.isy.service.data.AcademicTitleService
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.data.academic.AcademicTitleVo
import java.util.ArrayList
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Controller
open class AcademicTitleController {

    @Resource
    open lateinit var academicTitleService: AcademicTitleService


    /**
     * 获取全部职称
     *
     * @return 全部职称
     */
    @RequestMapping(value = ["/user/academic"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun academic(): AjaxUtils<AcademicTitle> {
        val ajaxUtils = AjaxUtils.of<AcademicTitle>()
        val academicTitles = ArrayList<AcademicTitle>()
        val academicTitle = AcademicTitle(0, "请选择职称")
        academicTitles.add(academicTitle)
        academicTitles.addAll(academicTitleService.findAll())
        return ajaxUtils.success().msg("获取职称数据成功！").listData(academicTitles)
    }

    /**
     * 职称数据
     *
     * @return 职称数据页面
     */
    @RequestMapping(value = ["/web/menu/data/academic"], method = [(RequestMethod.GET)])
    fun academicData(): String {
        return "web/data/academic/academic_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/academic/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun academicDatas(request: HttpServletRequest): DataTablesUtils<AcademicTitle> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("academic_title_id")
        headers.add("academic_title_name")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<AcademicTitle>(request, headers)
        val records = academicTitleService.findAllByPage(dataTablesUtils)
        var academicTitles: List<AcademicTitle> = ArrayList()
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty) {
            academicTitles = records.into(AcademicTitle::class.java)
        }
        dataTablesUtils.data = academicTitles
        dataTablesUtils.setiTotalRecords(academicTitleService.countAll().toLong())
        dataTablesUtils.setiTotalDisplayRecords(academicTitleService.countByCondition(dataTablesUtils).toLong())
        return dataTablesUtils
    }

    /**
     * 保存时检验职称是否重复
     *
     * @param name   职称
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/academic/save/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun saveValid(@RequestParam("academicTitleName") name: String): AjaxUtils<*> {
        val academicTitleName = StringUtils.trimWhitespace(name)
        if (StringUtils.hasLength(academicTitleName)) {
            val academicTitles = academicTitleService.findByAcademicTitleName(academicTitleName)
            return if (ObjectUtils.isEmpty(academicTitles)) {
                AjaxUtils.of<Any>().success().msg("职称不存在")
            } else {
                AjaxUtils.of<Any>().fail().msg("职称已存在")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("职称不能为空")
    }

    /**
     * 保存职称信息
     *
     * @param academicTitleVo 职称
     * @param bindingResult   检验
     * @return true 保存成功 false 保存失败
     */
    @RequestMapping(value = ["/web/data/academic/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun academicSave(@Valid academicTitleVo: AcademicTitleVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val academicTitle = AcademicTitle()
            academicTitle.academicTitleName = StringUtils.trimWhitespace(academicTitleVo.academicTitleName!!)
            academicTitleService.save(academicTitle)
            return AjaxUtils.of<Any>().success().msg("保存成功")
        }
        return AjaxUtils.of<Any>().fail().msg("填写信息错误，请检查")
    }

    /**
     * 检验编辑时职称重复
     *
     * @param id                职称id
     * @param name              职称
     * @return true 合格 false 不合格
     */
    @RequestMapping(value = ["/web/data/academic/update/valid"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateValid(@RequestParam("academicTitleId") id: Int, @RequestParam("academicTitleName") name: String): AjaxUtils<*> {
        val academicTitleName = StringUtils.trimWhitespace(name)
        val academicTitleRecords = academicTitleService.findByAcademicTitleNameNeAcademicTitleId(academicTitleName, id)
        return if (academicTitleRecords.isEmpty()) {
            AjaxUtils.of<Any>().success().msg("职称不重复")
        } else AjaxUtils.of<Any>().fail().msg("职称重复")

    }


    /**
     * 保存更改
     *
     * @param academicTitleVo 职称
     * @param bindingResult   检验
     * @return true 更改成功 false 更改失败
     */
    @RequestMapping(value = ["/web/data/academic/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun academicUpdate(@Valid academicTitleVo: AcademicTitleVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors() && !ObjectUtils.isEmpty(academicTitleVo.academicTitleId)) {
            val academicTitle = academicTitleService.findById(academicTitleVo.academicTitleId!!)
            if (!ObjectUtils.isEmpty(academicTitle)) {
                academicTitle.academicTitleName = StringUtils.trimWhitespace(academicTitleVo.academicTitleName!!)
                academicTitleService.update(academicTitle)
                return AjaxUtils.of<Any>().success().msg("更改成功")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("更改失败")
    }
}