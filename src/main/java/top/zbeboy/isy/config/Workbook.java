package top.zbeboy.isy.config;

/**
 * Application constants.
 * 开发环境配置常量
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
public final class Workbook {

    /*
    开发环境
     */
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    /*
    生产环境
     */
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    /*
   注册类型为学生
    */
    public static final String STUDENT_REGIST = "student";

    /*
    注册类型为教职工
     */
    public static final String STAFF_REGIST = "staff";

    /*
    学生类型
     */
    public static final String STUDENT_USERS_TYPE = "学生";

    /*
    教职工类型
     */
    public static final String STAFF_USERS_TYPE = "教职工";

    /*
    系统类型
     */
    public static final String SYSTEM_USERS_TYPE = "系统";

    /*
    系统角色
     */
    public static final String SYSTEM_ROLE_NAME = "系统";

    /*
    系统权限
     */
    public static final String SYSTEM_AUTHORITIES = "ROLE_SYSTEM";

    /*
    管理员角色
     */
    public static final String ADMIN_ROLE_NAME = "管理员";

    /*
    管理员权限
     */
    public static final String ADMIN_AUTHORITIES = "ROLE_ADMIN";

    /*
    系统setting目录
     */
    public static final String SETTINGS_PATH = "./settings/";

    /*
    url mapping
     */
    public static final String URL_MAPPING_FILE_PATH = SETTINGS_PATH + "url-mapping.txt";

    /*
    用户默认头像
     */
    public static final String USERS_AVATAR = "/images/avatar.jpg";

    /*
    用户档案袋路径
     */
    public static final String USERS_PORTFOLIOS = "portfolios/";

    private Workbook() {
    }
}
