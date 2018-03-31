package top.zbeboy.isy.test

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 * Created by zbeboy 2018-03-31 .
 * 测试说明:本测试需要在 VM OPTIONS中设置:-Des.set.netty.runtime.available.processors=false 以解决elasticsearch netty问题
 * 目前已在Intellij idea 中针对该测试类 VM options 做了设置.
 **/
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestServer {

    @Test
    fun test() {
        println("test")
    }
}