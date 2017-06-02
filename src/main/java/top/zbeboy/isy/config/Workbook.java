package top.zbeboy.isy.config;

import top.zbeboy.isy.domain.tables.pojos.Users;

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
    实习类型
     */
    public static final String INTERNSHIP_COLLEGE_TYPE = "顶岗实习(留学院)";
    public static final String INTERNSHIP_COMPANY_TYPE = "校外自主实习(去单位)";
    public static final String GRADUATION_PRACTICE_COLLEGE_TYPE = "毕业实习(校内)";
    public static final String GRADUATION_PRACTICE_UNIFY_TYPE = "毕业实习(学校统一组织校外实习)";
    public static final String GRADUATION_PRACTICE_COMPANY_TYPE = "毕业实习(学生校外自主实习)";

    /*
    文件类型
     */
    public static final String DOC_FILE = "doc";
    public static final String DOCX_FILE = "docx";
    public static final String XLS_FILE = "xls";
    public static final String XLSX_FILE = "xlsx";

    /*
    邮件验证有效期 单位天
     */
    public static final int MAILBOX_VERIFY_VALID = 5;

    /*
    忘记密码邮件有效期 单位天
     */
    public static final int MAILBOX_FORGET_PASSWORD_VALID = 2;

    /*
    手机验证有效期 单位分钟
     */
    public static final int MOBILE_VERIFY_VALID = 30;

    /*
    系统角色
     */
    public static final String SYSTEM_ROLE_NAME = "系统";

    /*
    系统权限
     */
    public static final String SYSTEM_AUTHORITIES = "ROLE_SYSTEM";

    /*
   运维角色
    */
    public static final String OPS_ROLE_NAME = "运维";

    /*
    运维权限
     */
    public static final String OPS_AUTHORITIES = "ROLE_ACTUATOR";

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
   实习日志模板
    */
    public static final String INTERNSHIP_JOURNAL_FILE_PATH = SETTINGS_PATH + "internshipJournalTemplate.docx";

    /*
    用户默认头像
     */
    public static final String USERS_AVATAR = "images/avatar.jpg";

    /*
    用户档案袋路径
     */
    public static final String USERS_PORTFOLIOS = "portfolios/";

    /*
    文件档案路径
   */
    public static final String FILES_PORTFOLIOS = "files/";

    /*
    临时文件档案路径
    */
    public static final String TEMP_FILES_PORTFOLIOS = "files/temp";

    /*
    提示页面
     */
    public static final String TIP_PAGE = "tip::#page-wrapper";

    /*
    提醒类型
     */
    public static final String ALERT_MESSAGE_TYPE = "消息";

    private Workbook() {
    }

    /**
     * 头像路径
     *
     * @param users 用户
     * @return 路径
     */
    public static String avatarPath(Users users) {
        return Workbook.USERS_PORTFOLIOS + users.getUsername() + "/" + "avatar/";
    }

    /**
     * 保存实习资料路径
     *
     * @param users 用户
     * @return 路径
     */
    public static String internshipApplyPath(Users users) {
        return Workbook.USERS_PORTFOLIOS + users.getUsername() + "/" + "internship/apply/";
    }

    /**
     * 保存实习日志路径
     *
     * @param users 用户
     * @return 路径
     */
    public static String internshipJournalPath(Users users) {
        return Workbook.USERS_PORTFOLIOS + users.getUsername() + "/" + "internship/journal/";
    }

    /**
     * 实习文件路径
     *
     * @param schoolName     学校名
     * @param collegeName    院名
     * @param departmentName 系名
     * @return 路径
     */
    public static String internshipPath(String schoolName, String collegeName, String departmentName) {
        return Workbook.FILES_PORTFOLIOS + schoolName + "/" + collegeName + "/" + departmentName + "/" + "internship/";
    }

    /**
     * 毕业设计文件路径
     *
     * @param schoolName     学校名
     * @param collegeName    院名
     * @param departmentName 系名
     * @return 路径
     */
    public static String graduateDesignPath(String schoolName, String collegeName, String departmentName) {
        return Workbook.FILES_PORTFOLIOS + schoolName + "/" + collegeName + "/" + departmentName + "/" + "graduate/design/";
    }

    /**
     * 保存毕业设计规划文件路径
     *
     * @param users 用户
     * @return 路径
     */
    public static String graduationDesignPlanPath(Users users) {
        return Workbook.USERS_PORTFOLIOS + users.getUsername() + "/" + "graduate/design/project/";
    }
}
