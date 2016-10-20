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
        String pwd = md5.encodePassword(password, null);
        return pwd;
    }

    public static String sha_256(String password) throws NoSuchAlgorithmException {
        ShaPasswordEncoder sha = new ShaPasswordEncoder(256);
        sha.setEncodeHashAsBase64(true);
        String pwd = sha.encodePassword(password, null);
        return pwd;
    }


    public static String sha_SHA_256(String password) {
        ShaPasswordEncoder sha = new ShaPasswordEncoder();
        sha.setEncodeHashAsBase64(false);
        String pwd = sha.encodePassword(password, null);
        return pwd;
    }


    public static String md5_SystemWideSaltSource(String password) {
        Md5PasswordEncoder md5 = new Md5PasswordEncoder();
        md5.setEncodeHashAsBase64(false);

        // 使用动态加密盐的只需要在注册用户的时候将第二个参数换成用户名即可
        String pwd = md5.encodePassword(password, "acegisalt");
        return pwd;
    }

  /*  public static void main(String[] args) throws NoSuchAlgorithmException {
        md5("1234"); // 使用简单的MD5加密方式

        sha_256("1234"); // 使用256的哈希算法(SHA)加密

        sha_SHA_256("1234"); // 使用SHA-256的哈希算法(SHA)加密

        md5_SystemWideSaltSource("1234"); // 使用MD5再加全局加密盐加密的方式加密

        bCryptPassword("1234"); // 使用BCrypt加密方式
    }*/
}
