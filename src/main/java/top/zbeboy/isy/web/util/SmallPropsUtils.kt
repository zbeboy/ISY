package top.zbeboy.isy.web.util

import org.apache.commons.lang3.math.NumberUtils
import java.util.*

/**
 * Created by zbeboy 2017-11-26 .
 **/
open class SmallPropsUtils {
    companion object {
        /**
         * 参数ids ',' 分隔转list<Integer>
         *
         * @param ids string ids
         * @return list<Integer>
        <*/
        @JvmStatic
        fun StringIdsToList(ids: String): List<Int> {
            val idArr = ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return idArr.map { NumberUtils.toInt(it) }
        }

        /**
         * 参数ids ',' 分隔转list<String>
         *
         * @param ids string ids
         * @return list<String>
         */
        @JvmStatic
        fun StringIdsToStringList(ids: String): List<String> {
            val idArr = ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val newIds = ArrayList<String>()
            Collections.addAll(newIds, *idArr)
            return newIds
        }

        /**
         * 参数ids ',' 是否为Integer类型
         *
         * @param ids string ids
         * @return true or false
         */
        @JvmStatic
        fun StringIdsIsNumber(ids: String): Boolean {
            val idArr = ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return idArr.any { NumberUtils.isNumber(it) }
        }
    }
}