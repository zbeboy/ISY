package top.zbeboy.isy.web.weixin.small;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.service.weixin.WeixinService;
import top.zbeboy.isy.web.vo.weixin.WeixinVo;

import javax.annotation.Resource;

/**
 * Created by lenovo on 2017-08-21.
 * 微信小程序接入
 */
@Slf4j
@Controller
public class WeixinSmallController {

    @Resource
    private WeixinService weixinService;

    @Autowired
    private ISYProperties isyProperties;

    /**
     * 微信接入检验
     *
     * @param weixinVo 参数
     * @return 接入值
     */
    @RequestMapping(value = "/weixin/small/msg/words", method = RequestMethod.GET)
    @ResponseBody
    public String weixinValid(WeixinVo weixinVo) {
        weixinVo.setToken(isyProperties.getWeixin().getSmallToken());
        if (weixinService.checkSignature(weixinVo)) {
            return weixinVo.getEchostr();
        }
        return "error";
    }
}
