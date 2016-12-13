package top.zbeboy.isy.test;

import junit.framework.TestCase;
import top.zbeboy.isy.service.util.BCryptUtils;
import top.zbeboy.isy.service.util.MD5Utils;

import java.security.NoSuchAlgorithmException;

import static top.zbeboy.isy.service.util.UUIDUtils.getUUID;

/**
 * Created by lenovo on 2016-11-06.
 */
public class TestUtil extends TestCase{

    public void testBCryptUtils(){
        BCryptUtils.bCryptPassword("123456");
    }

    public void testMD5Utils() throws NoSuchAlgorithmException {
        MD5Utils.md5("1234"); // 使用简单的MD5加密方式

        MD5Utils.sha_256("1234"); // 使用256的哈希算法(SHA)加密

        MD5Utils.sha_SHA_256("1234"); // 使用SHA-256的哈希算法(SHA)加密

        MD5Utils.md5_SystemWideSaltSource("1234"); // 使用MD5再加全局加密盐加密的方式加密
    }

    public void testUUIDUtils(){
        System.out.println("::" + getUUID());
        String[] ss = getUUID(10);
        for (String s : ss) {
            System.out.println(s.length());
        }
    }
}
