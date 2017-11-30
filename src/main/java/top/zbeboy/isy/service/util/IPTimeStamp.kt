package top.zbeboy.isy.service.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by zbeboy 2017-11-30 .
 **/
class IPTimeStamp(ip: String) {

    private var ip: String? = ip

    fun getIPTimeRand(): String {
        val buf = StringBuilder()
        if (this.ip != null) {
            val s = this.ip!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (value in s) {
                buf.append(this.addZero(value, 3))
            }
        }
        buf.append(this.getTimeStamp())
        val r = Random()
        for (i in 0..2) {
            buf.append(r.nextInt(10))
        }
        return buf.toString()
    }

    private fun addZero(str: String, len: Int): String {
        val s = StringBuilder()
        s.append(str)
        while (s.length < len) {
            s.insert(0, "0")
        }
        return s.toString()
    }

    private fun getTimeStamp(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
    }
}