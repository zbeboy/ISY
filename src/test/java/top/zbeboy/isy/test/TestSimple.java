package top.zbeboy.isy.test;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import top.zbeboy.isy.service.util.MD5Utils;

import java.util.Arrays;

/**
 * Created by lenovo on 2016-11-15.
 */
public class TestSimple {

    @Test
    public void testWeixinCheck(){
        String[] arr = new String[]{"23","1","34"};
        Arrays.sort(arr);
        System.out.println(arr[0] + arr[1] + arr[2]);
        String newArr = MD5Utils.sha_1(arr[0] + arr[1] + arr[2]);
        System.out.println(newArr);
        System.out.println(RandomStringUtils.randomAlphabetic(10));
    }
}
