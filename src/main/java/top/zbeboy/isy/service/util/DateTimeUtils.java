package top.zbeboy.isy.service.util;


import java.text.SimpleDateFormat;

/**
 * Created by lenovo on 2016-09-15.
 */
public class DateTimeUtils {

    /**
     * timestamp
     *
     * @param timestamp
     * @return java.util.date
     */
    public static java.util.Date timestampToDate(java.sql.Timestamp timestamp) {
        java.util.Date date = new java.util.Date(timestamp.getTime());
        return date;
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
}
