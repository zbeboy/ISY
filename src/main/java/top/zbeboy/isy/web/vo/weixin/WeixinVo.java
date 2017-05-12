package top.zbeboy.isy.web.vo.weixin;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by lenovo on 2016-11-15.
 */
@Data
public class WeixinVo {
    /*
    微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数
     */
    private String signature;
    /*
    时间戳
     */
    private String timestamp;
    /*
    随机数
     */
    private String nonce;
    /*
    随机字符串
     */
    private String echostr;
    /*
    加密类型，为aes
     */
    private String encrypt_type;
    /*
    消息体签名，用于验证消息体的正确性
     */
    private String msg_signature;
}
