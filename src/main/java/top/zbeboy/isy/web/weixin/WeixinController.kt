package top.zbeboy.isy.web.weixin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.service.weixin.WeixinService
import top.zbeboy.isy.web.vo.weixin.WeixinVo
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-20 .
 **/
@Controller
open class WeixinController {

    @Resource
    open lateinit var weixinService: WeixinService

    @Autowired
    open lateinit var isyProperties: ISYProperties

    /**
     * 微信接入检验
     *
     * @param weixinVo 参数
     * @return 接入值
     */
    @RequestMapping(value = ["/weixin"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun weixinValid(weixinVo: WeixinVo): String {
        weixinVo.token = isyProperties.getWeixin().token
        return if (weixinService.checkSignature(weixinVo)) {
            weixinVo.echostr!!
        } else "error"
    }
}