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
        @JvmField
        val EXPIRES_YEAR: Long = 365

        /*
        特殊到期时间
         */
        @JvmField
        val EXPIRES_APPLICATION_ID_DAYS: Long = 2
        @JvmField
        val EXPIRES_GRADUATION_DESIGN_TEACHER_STUDENT_DAYS: Long = 30
        @JvmField
        val EXPIRES_SCHOOL_INFO_DAYS: Long = 300
        /*
        配置 KEY
         */
        const val QUERY_USER_TYPE_BY_NAME = "QUERY_USER_TYPE_BY_NAME"

        const val QUERY_USER_TYPE_BY_ID = "QUERY_USER_TYPE_BY_ID"

        const val QUERY_SYSTEM_ALERT_TYPE_BY_NAME = "QUERY_SYSTEM_ALERT_TYPE_BY_NAME"

        const val MENU_HTML = "MENU_HTML_"

        const val SCHOOL_INFO_PATH = "SCHOOL_INFO_PATH_"

        const val USER_APPLICATION_ID = "USER_APPLICATION_ID_"

        const val URL_MAPPING = "URL_MAPPING_"

        const val USER_ROLE_ID = "USER_ROLE_ID_"

        const val USER_ROLE = "USER_ROLE_"

        const val USER_KEY = "USER_KEY_"

        const val USER_COLLEGE_ID = "USER_COLLEGE_ID_"

        const val USER_DEPARTMENT_ID = "USER_DEPARTMENT_ID_"

        const val INTERNSHIP_BASE_CONDITION = "INTERNSHIP_BASE_CONDITION_"

        const val GRADUATION_DESIGN_BASE_CONDITION = "GRADUATION_DESIGN_BASE_CONDITION_"

        const val GRADUATION_DESIGN_TEACHER_STUDENT_COUNT = "GRADUATION_DESIGN_TEACHER_STUDENT_COUNT_"

        const val GRADUATION_DESIGN_TEACHER_STUDENT = "GRADUATION_DESIGN_TEACHER_STUDENT_"

        const val GRADUATION_DESIGN_PHARMTECH_STUDENT = "GRADUATION_DESIGN_PHARMTECH_STUDENT_"

        const val GRADUATION_DESIGN_PHARMTECH_STUDENT_LIST = "GRADUATION_DESIGN_PHARMTECH_STUDENT_LIST_"

        const val GRADUATION_DESIGN_ADJUSTECH_SYNC_TIME = "GRADUATION_DESIGN_ADJUSTECH_SYNC_TIME_"
    }
}