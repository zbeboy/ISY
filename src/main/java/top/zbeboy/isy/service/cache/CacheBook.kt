package top.zbeboy.isy.service.cache

/**
 * 缓存相关key 与 有效期配置
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
open class CacheBook {
    companion object {
        /*
        配置通用到期时间
        */
        @JvmField
        val EXPIRES_SECONDS: Long = 1800
        @JvmField
        val EXPIRES_MINUTES: Long = 30
        @JvmField
        val EXPIRES_HOURS: Long = 4
        @JvmField
        val EXPIRES_DAYS: Long = 1

        /*
        特殊到期时间
         */
        @JvmField
        val EXPIRES_APPLICATION_ID_DAYS: Long = 2
        @JvmField
        val EXPIRES_GRADUATION_DESIGN_TEACHER_STUDENT: Long = 30
        /*
        配置 KEY
         */
        @JvmField
        val QUERY_USER_TYPE_BY_NAME = "QUERY_USER_TYPE_BY_NAME"

        @JvmField
        val QUERY_USER_TYPE_BY_ID = "QUERY_USER_TYPE_BY_ID"

        @JvmField
        val MENU_HTML = "MENU_HTML_"

        @JvmField
        val USER_APPLICATION_ID = "USER_APPLICATION_ID_"

        @JvmField
        val URL_MAPPING = "URL_MAPPING_"

        @JvmField
        val USER_ROLE_ID = "USER_ROLE_ID_"

        @JvmField
        val USER_ROLE = "USER_ROLE_"

        @JvmField
        val GRADUATION_DESIGN_TEACHER_STUDENT_COUNT = "GRADUATION_DESIGN_TEACHER_STUDENT_COUNT_"

        @JvmField
        val GRADUATION_DESIGN_TEACHER_STUDENT = "GRADUATION_DESIGN_TEACHER_STUDENT_"

        @JvmField
        val GRADUATION_DESIGN_PHARMTECH_STUDENT = "GRADUATION_DESIGN_PHARMTECH_STUDENT_"

        @JvmField
        val GRADUATION_DESIGN_PHARMTECH_STUDENT_LIST = "GRADUATION_DESIGN_PHARMTECH_STUDENT_LIST_"

        @JvmField
        val GRADUATION_DESIGN_ADJUSTECH_SYNC_TIME = "GRADUATION_DESIGN_ADJUSTECH_SYNC_TIME_"
    }
}