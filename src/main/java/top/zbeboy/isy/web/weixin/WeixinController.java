package top.zbeboy.isy.web.weixin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.service.weixin.WeixinService;
import top.zbeboy.isy.web.vo.weixin.WeixinVo;

import javax.annotation.Resource;

/**
 * Created by lenovo on 2016-11-15.
 */
@Slf4j
@Controller
public class WeixinController {

    @Resource
    private WeixinService weixinService;

    /**
     * 微信接入检验
     *
     * @param weixinVo 参数
     * @return 接入值
     */
    @RequestMapping(value = "/weixin", method = RequestMethod.GET)
    @ResponseBody
    public String weixinValid(WeixinVo weixinVo) {
        if (weixinService.checkSignature(weixinVo)) {
            return weixinVo.getEchostr();
        }
        return "error";
    }
}
