package top.zbeboy.isy.service.util

import java.util.*

/**
 * Created by zbeboy 2017-11-30 .
 **/
class UUIDUtils {
    companion object {
        /**
         * 获得一个UUID
         *
         * @return String UUID
         */
        @JvmStatic
        fun getUUID(): String {
            return UUID.randomUUID().toString().replace("-".toRegex(), "")
        }

        /**
         * 获得指定数目的UUID
         *
         * @param number int 需要获得的UUID数量
         * @return String[] UUID数组
         */
        @JvmStatic
        fun getUUID(number: Int): Array<String?>? {
            if (number < 1) {
                return null
            }
            val ss = arrayOfNulls<String>(number)
            for (i in 0..number - 1) {
                ss[i] = getUUID()
            }
            return ss
        }
    }
}