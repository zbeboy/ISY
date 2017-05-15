package top.zbeboy.isy.config;

/**
 * 缓存相关key 与 有效期配置
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
public final class CacheBook {
    /*
    配置通用到期时间
     */
    public static final long EXPIRES_SECONDS = 1800;
    public static final long EXPIRES_MINUTES = 30;
    public static final long EXPIRES_HOURS = 4;
    public static final long EXPIRES_DAYS = 1;


    /*
    特殊到期时间
     */
    public static final long EXPIRES_APPLICATION_ID_DAYS = 2;
    public static final long EXPIRES_GRADUATION_DESIGN_TEACHER_STUDENT_COUNT = 30;
    /*
    配置 KEY
     */
    public static final String QUERY_USER_TYPE_BY_NAME = "QUERY_USER_TYPE_BY_NAME";

    public static final String QUERY_USER_TYPE_BY_ID = "QUERY_USER_TYPE_BY_ID";

    public static final String MENU_HTML = "MENU_HTML_";

    public static final String USER_APPLICATION_ID = "USER_APPLICATION_ID_";

    public static final String URL_MAPPING = "URL_MAPPING_";

    public static final String USER_ROLE_ID = "USER_ROLE_ID_";

    public static final String USER_ROLE = "USER_ROLE_";

    public static final String GRADUATION_DESIGN_TEACHER_STUDENT_COUNT = "GRADUATION_DESIGN_TEACHER_STUDENT_COUNT_";
}
