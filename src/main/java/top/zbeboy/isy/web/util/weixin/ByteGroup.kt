package top.zbeboy.isy.web.util.weixin

import java.util.ArrayList

/**
 * Created by zbeboy 2017-11-20 .
 **/
class ByteGroup {
    private val byteContainer = ArrayList<Byte>()

    fun toBytes(): ByteArray {
        val bytes = ByteArray(byteContainer.size)
        for (i in byteContainer.indices) {
            bytes[i] = byteContainer[i]
        }
        return bytes
    }

    fun addBytes(bytes: ByteArray): ByteGroup {
        for (b in bytes) {
            byteContainer.add(b)
        }
        return this
    }

    fun size(): Int {
        return byteContainer.size
    }
}