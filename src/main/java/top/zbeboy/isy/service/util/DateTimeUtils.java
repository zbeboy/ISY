package top.zbeboy.isy.service.util;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by lenovo on 2016-09-15.
 */
public class DateTimeUtils {

    /**
     * timestamp
     *
     * @param timestamp java.sql.timestamp
     * @return java.util.date
     */
    public static java.util.Date timestampToDate(java.sql.Timestamp timestamp) {
        return new java.util.Date(timestamp.getTime());
    }

    /**
     * timestamp to string
     *
     * @param timestamp sql
     * @param format    格式
     * @return string
     */
    public static String timestampToString(java.sql.Timestamp timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new java.util.Date(timestamp.getTime()));
    }

    /**
     * 格式化date
     *
     * @param date   日期
     * @param format 格式
     * @return 格式化后的时间
     */
    public static String formatDate(java.util.Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 格式化date
     *
     * @param date 日期
     * @return 格式化后的时间
     */
    public static String formatDate(java.util.Date date) {
        String format = "yyyy-MM-dd HH:mm:ss";
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 格式化Timestamp
     *
     * @param timestamp 日期
     * @return 格式化后的时间
     */
    public static String formatDate(java.sql.Timestamp timestamp) {
        String format = "yyyy-MM-dd HH:mm:ss";
        return new SimpleDateFormat(format).format(timestamp);
    }

    /**
     * 格式化成sql date
     *
     * @param date 日期
     * @return sql date
     * @throws ParseException
     */
    public static java.sql.Date formatDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = sdf.parse(date);
        return new java.sql.Date(d.getTime());
    }

    /**
     * 格式化成sql date
     *
     * @param date   date 日期
     * @param format 格式
     * @return sql date
     * @throws ParseException
     */
    public static java.sql.Date formatDate(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        java.util.Date d = sdf.parse(date);
        return new java.sql.Date(d.getTime());
    }

    /**
     * 格式化成sql date
     *
     * @param date   date 日期
     * @param format 格式
     * @return sql date
     * @throws ParseException
     */
    public static java.sql.Timestamp formatDateToTimestamp(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        java.util.Date d = sdf.parse(date);
        return new java.sql.Timestamp(d.getTime());
    }

    /**
     * 当前时间是否在时间范围
     *
     * @param after  之前时间
     * @param before 之后时间
     * @return true or false
     */
    public static boolean timestampRangeDecide(java.sql.Timestamp after, java.sql.Timestamp before) {
        java.sql.Timestamp now = new Timestamp(System.currentTimeMillis());
        if (now.after(after) && now.before(before)) {
            return true;
        }
        return false;
    }
}
