package top.zbeboy.isy.service.util;

/**
 * Created by lenovo on 2015/8/31.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import java.security.NoSuchAlgorithmException;

/**
 * md5加密工具类
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */

public class MD5Utils {

    private final Logger log = LoggerFactory.getLogger(MD5Utils.class);

    public static String md5(String password) {
        Md5PasswordEncoder md5 = new Md5PasswordEncoder();
        // false 表示：生成32位的Hex版, 这也是encodeHashAsBase64的, Acegi 默认配置; true  表示：生成24位的Base64版
        md5.setEncodeHashAsBase64(false);
        return md5.encodePassword(password, null);
    }

    public static String sha_256(String password) throws NoSuchAlgorithmException {
        ShaPasswordEncoder sha = new ShaPasswordEncoder(256);
        sha.setEncodeHashAsBase64(true);
        return sha.encodePassword(password, null);
    }


    public static String sha_SHA_256(String password) {
        ShaPasswordEncoder sha = new ShaPasswordEncoder();
        sha.setEncodeHashAsBase64(false);
        return sha.encodePassword(password, null);
    }


    public static String md5_SystemWideSaltSource(String password) {
        Md5PasswordEncoder md5 = new Md5PasswordEncoder();
        md5.setEncodeHashAsBase64(false);

        // 使用动态加密盐的只需要在注册用户的时候将第二个参数换成用户名即可
        return md5.encodePassword(password, "acegisalt");
    }
}
