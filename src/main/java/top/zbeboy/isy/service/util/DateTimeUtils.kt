package top.zbeboy.isy.service.util

import org.apache.commons.lang.StringUtils
import org.springframework.util.ObjectUtils
import java.sql.Timestamp
import java.text.ParseException
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Created by zbeboy 2017-11-30 .
 **/
class DateTimeUtils {
    companion object {
        /**
         * timestamp
         *
         * @param timestamp java.sql.timestamp
         * @return java.util.date
         */
        @JvmStatic
        fun timestampToDate(timestamp: java.sql.Timestamp): java.util.Date {
            return java.util.Date(timestamp.time)
        }

        /**
         * timestamp to string
         *
         * @param timestamp sql
         * @param format    格式
         * @return string
         */
        @JvmStatic
        fun timestampToString(timestamp: java.sql.Timestamp, format: String): String {
            return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern(format))
        }

        /**
         * util date to sql date
         *
         * @param date util date
         * @return sql date
         */
        @JvmStatic
        fun utilDateToSqlDate(date: java.util.Date): java.sql.Date {
            return java.sql.Date(date.time)
        }

        /**
         * 格式化date
         *
         * @param date   日期
         * @param format 格式
         * @return 格式化后的时间
         */
        @JvmStatic
        fun formatDate(date: java.util.Date, format: String): String {
            return date.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(format))
        }

        /**
         * 格式化date
         *
         * @param date   日期
         * @param format 格式
         * @return 格式化后的时间
         */
        @JvmStatic
        fun formatDate(date: java.sql.Date, format: String): String {
            return date.toLocalDate().format(DateTimeFormatter.ofPattern(format))
        }

        /**
         * 格式化date
         *
         * @param date 日期
         * @return 格式化后的时间
         */
        @JvmStatic
        fun formatDate(date: java.util.Date): String {
            return date.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

        /**
         * 格式化Timestamp
         *
         * @param timestamp 日期
         * @return 格式化后的时间
         */
        @JvmStatic
        fun formatDate(timestamp: java.sql.Timestamp): String {
            return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        }

        /**
         * 格式化成sql date
         *
         * @param date 日期
         * @return sql date
         * @throws ParseException
         */
        @JvmStatic
        @Throws(ParseException::class)
        fun formatDate(date: String): java.sql.Date {
            return java.sql.Date(java.sql.Date.from(LocalDate.parse(StringUtils.trim(date), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(ZoneId.systemDefault()).toInstant()).time)
        }

        /**
         * 格式化成sql date
         *
         * @param date   date 日期
         * @param format 格式
         * @return sql date
         * @throws ParseException
         */
        @JvmStatic
        @Throws(ParseException::class)
        fun formatDate(date: String, format: String): java.sql.Date {
            return java.sql.Date(java.sql.Date.from(LocalDate.parse(StringUtils.trim(date), DateTimeFormatter.ofPattern(format)).atStartOfDay(ZoneId.systemDefault()).toInstant()).time)
        }

        /**
         * 格式化成util date
         *
         * @param date   date 日期
         * @param format 格式
         * @return util date
         * @throws ParseException
         */
        @JvmStatic
        @Throws(ParseException::class)
        fun formatDateTime(date: String, format: String): java.sql.Date {
            return java.sql.Date(java.sql.Date.from(LocalDateTime.parse(StringUtils.trim(date), DateTimeFormatter.ofPattern(format)).atZone(ZoneId.systemDefault()).toInstant()).time)
        }

        /**
         * 格式化成sql date
         *
         * @param date   date 日期
         * @param format 格式
         * @return sql date
         * @throws ParseException
         */
        @JvmStatic
        @Throws(ParseException::class)
        fun formatDateToTimestamp(date: String, format: String): java.sql.Timestamp {
            return java.sql.Timestamp(java.sql.Timestamp.from(LocalDateTime.parse(StringUtils.trim(date), DateTimeFormatter.ofPattern(format)).atZone(ZoneId.systemDefault()).toInstant()).time)
        }

        /**
         * 当前时间是否在时间范围
         *
         * @param after  之前时间
         * @param before 之后时间
         * @return true or false
         */
        @JvmStatic
        fun timestampRangeDecide(after: java.sql.Timestamp, before: java.sql.Timestamp): Boolean {
            val now = Timestamp(Clock.systemDefaultZone().millis())
            return now.after(after) && now.before(before)
        }

        /**
         * 当前时间大于某一时间
         *
         * @param after 某一时间
         * @return true or false
         */
        @JvmStatic
        fun timestampAfterDecide(after: java.sql.Timestamp): Boolean {
            val now = Timestamp(Clock.systemDefaultZone().millis())
            return now.after(after)
        }

        /**
         * 当前时间小于某一时间
         *
         * @param before 某一时间
         * @return true or false
         */
        @JvmStatic
        fun timestampBeforeDecide(before: java.sql.Timestamp): Boolean {
            val now = Timestamp(Clock.systemDefaultZone().millis())
            return now.before(before)
        }

        /**
         * 得到当前时间
         *
         * @return 当前时间
         */
        @JvmStatic
        fun getNow(): Timestamp {
            return Timestamp(Clock.systemDefaultZone().millis())
        }

        /**
         * 得到当前时间
         *
         * @return 当前时间
         */
        @JvmStatic
        fun getLocalDateTime(format: String): String {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
        }

        /**
         * 拆分时间
         *
         * @param splitSymbol 分隔符
         * @param dateTime    需要拆分的时间
         * @return 拆分的数组时间
         */
        @JvmStatic
        @Throws(ParseException::class)
        fun splitDateTime(splitSymbol: String, dateTime: String): Array<String> {
            if (StringUtils.isNotBlank(dateTime)) {
                val arr = dateTime.split(splitSymbol.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return if (!ObjectUtils.isEmpty(arr) && arr.size >= 2) {
                    arr
                } else {
                    throw ParseException("时间长度不够", -1)
                }
            } else {
                throw ParseException("时间为空", -1)
            }
        }
    }
}