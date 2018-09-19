package top.zbeboy.isy.test

import org.junit.Test

open class TestCover{

    @Test
    fun testC(){
        val rand = (Math.random() * 100).toInt()
        if (rand % 2 == 0) {
            println("Hello, world! 0")
        } else
            println("Hello, world! 1")

        val result = if (rand % 2 == 0) rand + rand else rand * rand
        println(result)
    }
}