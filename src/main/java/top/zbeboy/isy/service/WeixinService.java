package top.zbeboy.isy.service;

import top.zbeboy.isy.web.vo.weixin.WeixinVo;

/**
 * Created by zbeboy on 2016/11/15.
 */
public interface WeixinService {
    /**
     * 检验签名
     *
     * @param weixinVo 参数
     * @return true or false
     */
    boolean checkSignature(WeixinVo weixinVo);
}
