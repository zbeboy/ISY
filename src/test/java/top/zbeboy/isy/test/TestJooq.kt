package top.zbeboy.isy.test

import org.jooq.DSLContext
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import top.zbeboy.isy.Application
import top.zbeboy.isy.domain.Tables.*

/**
 * Created by zbeboy 2018-03-12 .
 **/
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["es.set.netty.runtime.available.processors=false"])
open class TestJooq {

    @Autowired
    lateinit var create: DSLContext

    @Before
    fun testBefore(){
        System.setProperty("es.set.netty.runtime.available.processors", "false")
    }

    @Test
    @Ignore
    fun testUsers() {
        val result = create.selectFrom(USERS).where(USERS.USERNAME.eq("863052317@qq.com")).fetchResultSet()
        println(result)
    }
}