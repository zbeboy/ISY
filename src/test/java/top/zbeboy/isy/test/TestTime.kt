package top.zbeboy.isy.test

import junit.framework.TestCase
import org.joda.time.DateTime
import java.text.SimpleDateFormat

/**
 * Created by zbeboy 2017-10-29 .
 **/
class TestTime : TestCase() {

    fun testJoda(){
        val dateTime = DateTime.now()
        val s = dateTime.minusDays(120)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val d = sdf.format(s.toDate())
        println(d)
        println(dateTime.year)
    }
}