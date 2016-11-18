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

    /**
     * 加密消息
     *
     * @param msg      消息
     * @param weixinVo 微信参数
     * @return 加密后的内容
     */
    String encryptMsg(String msg, WeixinVo weixinVo);

    /**
     * 解密消息
     *
     * @param msg      消息
     * @param weixinVo 微信参数
     * @return 加密后的内容
     */
    String decryptMsg(String msg, WeixinVo weixinVo);
}
