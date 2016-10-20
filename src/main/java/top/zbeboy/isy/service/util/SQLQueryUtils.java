package top.zbeboy.isy.service.util;

/**
 * Created by lenovo on 2016-09-15.
 */
public class SQLQueryUtils {

    /**
     * 组装likeAll全匹配参数
     * @param param 参数
     * @return
     */
    public static String likeAllParam(String param){
        return "%"+param+"%";
    }
}
