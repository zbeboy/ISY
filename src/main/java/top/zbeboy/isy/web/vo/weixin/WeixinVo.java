package top.zbeboy.isy.web.vo.weixin;

import javax.validation.constraints.NotNull;

/**
 * Created by lenovo on 2016-11-15.
 */
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getEchostr() {
        return echostr;
    }

    public void setEchostr(String echostr) {
        this.echostr = echostr;
    }

    public String getEncrypt_type() {
        return encrypt_type;
    }

    public void setEncrypt_type(String encrypt_type) {
        this.encrypt_type = encrypt_type;
    }

    public String getMsg_signature() {
        return msg_signature;
    }

    public void setMsg_signature(String msg_signature) {
        this.msg_signature = msg_signature;
    }
}
