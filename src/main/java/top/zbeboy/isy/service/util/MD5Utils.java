package top.zbeboy.isy.service.util;

/**
 * Created by lenovo on 2015/8/31.
 */


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密工具类
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class MD5Utils {

    /**
     * md5加密
     *
     * @param password 密码
     * @return 加密后
     */
    public static String md5(String password) {
        Md5PasswordEncoder md5 = new Md5PasswordEncoder();
        // false 表示：生成32位的Hex版, 这也是encodeHashAsBase64的, Acegi 默认配置; true  表示：生成24位的Base64版
        md5.setEncodeHashAsBase64(false);
        return md5.encodePassword(password, null);
    }

    /**
     * sha 256 encode
     *
     * @param password 密码
     * @return 加密后
     * @throws NoSuchAlgorithmException
     */
    public static String sha_256(String password) throws NoSuchAlgorithmException {
        ShaPasswordEncoder sha = new ShaPasswordEncoder(256);
        sha.setEncodeHashAsBase64(true);
        return sha.encodePassword(password, null);
    }

    /**
     * sha 256
     *
     * @param password 密码
     * @return 加密后
     */
    public static String sha_SHA_256(String password) {
        ShaPasswordEncoder sha = new ShaPasswordEncoder();
        sha.setEncodeHashAsBase64(false);
        return sha.encodePassword(password, null);
    }

    /**
     * 加盐加密
     *
     * @param password 密码
     * @return 加密后
     */
    public static String md5_SystemWideSaltSource(String password) {
        Md5PasswordEncoder md5 = new Md5PasswordEncoder();
        md5.setEncodeHashAsBase64(false);

        // 使用动态加密盐的只需要在注册用户的时候将第二个参数换成用户名即可
        return md5.encodePassword(password, "acegisalt");
    }

    /**
     * sha 1 加密
     *
     * @param decript 字符串
     * @return 加密后
     */
    public static String sha_1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 sha1 error : {}", e);
        }
        return "";
    }
}
