package top.zbeboy.isy.web.graduate.wishes

import org.springframework.stereotype.Controller
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.elastic.pojo.GraduationWishesElastic
import top.zbeboy.isy.service.graduate.wishes.GraduationWishesService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.util.AjaxUtils
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-05-23 .
 **/
@Controller
open class GraduationWishesController {

    @Resource
    open lateinit var graduationWishesService: GraduationWishesService

    /**
     * 获取数据
     *
     * @return 数据
     */
    @RequestMapping(value = ["/user/graduate/wishes/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun data(): AjaxUtils<GraduationWishesElastic> {
        val ajaxUtils = AjaxUtils.of<GraduationWishesElastic>()
        val data = graduationWishesService.findAll()
        val datas: ArrayList<GraduationWishesElastic> = ArrayList()
        if (!ObjectUtils.isEmpty(data)) {
            data!!.forEach { i ->
                val graduationWishesElastic = GraduationWishesElastic()
                graduationWishesElastic.id = i.id
                graduationWishesElastic.schoolName = i.schoolName
                graduationWishesElastic.username = i.username
                graduationWishesElastic.content = i.content
                graduationWishesElastic.writeDate = i.writeDate
                datas.add(graduationWishesElastic)
            }
        }
        return ajaxUtils.success().msg("获取数据成功").listData(datas)
    }

    /**
     * 保存
     *
     * @param graduationWishesElastic 页面数据
     * @return true or false
     */
    @RequestMapping(value = ["/user/graduate/wishes/save"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun save(graduationWishesElastic: GraduationWishesElastic): AjaxUtils<*> {
        graduationWishesElastic.id = UUIDUtils.getUUID()
        graduationWishesElastic.writeDate = DateTimeUtils.getNow()
        graduationWishesService.save(graduationWishesElastic)
        return AjaxUtils.of<Any>().success().msg("保存成功")
    }
}