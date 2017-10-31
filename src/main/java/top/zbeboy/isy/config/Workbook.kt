package top.zbeboy.isy.config

import org.joda.time.DateTime
import top.zbeboy.isy.domain.tables.pojos.Users

/**
 * Application constants.
 * 开发环境配置常量
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
open class Workbook {
    companion object {
        /*
        开发环境
        */
        const val SPRING_PROFILE_DEVELOPMENT = "dev"

        /*
        生产环境
        */
        const val SPRING_PROFILE_PRODUCTION = "prod"

        /*
        测试环境
        */
        const val SPRING_PROFILE_TEST = "test"

        /*
        注册类型为学生
        */
        @JvmField
        val STUDENT_REGIST = "student"

        /*
        注册类型为教职工
        */
        @JvmField
        val STAFF_REGIST = "staff"

        /*
        学生类型
        */
        @JvmField
        val STUDENT_USERS_TYPE = "学生"

        /*
        教职工类型
        */
        @JvmField
        val STAFF_USERS_TYPE = "教职工"

        /*
        系统类型
        */
        @JvmField
        val SYSTEM_USERS_TYPE = "系统"

        /*
        实习类型
        */
        @JvmField
        val INTERNSHIP_COLLEGE_TYPE = "顶岗实习(留学院)"
        @JvmField
        val INTERNSHIP_COMPANY_TYPE = "校外自主实习(去单位)"
        @JvmField
        val GRADUATION_PRACTICE_COLLEGE_TYPE = "毕业实习(校内)"
        @JvmField
        val GRADUATION_PRACTICE_UNIFY_TYPE = "毕业实习(学校统一组织校外实习)"
        @JvmField
        val GRADUATION_PRACTICE_COMPANY_TYPE = "毕业实习(学生校外自主实习)"

        /*
        文件类型
        */
        @JvmField
        val DOC_FILE = "doc"
        @JvmField
        val DOCX_FILE = "docx"
        @JvmField
        val XLS_FILE = "xls"
        @JvmField
        val XLSX_FILE = "xlsx"

        /*
        邮件验证有效期 单位天
        */
        @JvmField
        val MAILBOX_VERIFY_VALID = 5

        /*
        忘记密码邮件有效期 单位天
        */
        @JvmField
        val MAILBOX_FORGET_PASSWORD_VALID = 2

        /*
        手机验证有效期 单位分钟
        */
        @JvmField
        val MOBILE_VERIFY_VALID = 30

        /*
        系统角色
        */
        @JvmField
        val SYSTEM_ROLE_NAME = "系统"

        /*
        系统权限
        */
        @JvmField
        val SYSTEM_AUTHORITIES = "ROLE_SYSTEM"

        /*
        运维角色
        */
        @JvmField
        val OPS_ROLE_NAME = "运维"

        /*
        运维权限
        */
        @JvmField
        val OPS_AUTHORITIES = "ROLE_ACTUATOR"

        /*
        管理员角色
        */
        @JvmField
        val ADMIN_ROLE_NAME = "管理员"

        /*
        管理员权限
        */
        @JvmField
        val ADMIN_AUTHORITIES = "ROLE_ADMIN"

        /*
        系统setting目录
        */
        @JvmField
        val SETTINGS_PATH = "./settings/"

        /*
        实习日志模板
        */
        @JvmField
        val INTERNSHIP_JOURNAL_FILE_PATH = SETTINGS_PATH + "internshipJournalTemplate.docx"

        /*
        用户默认头像
        */
        @JvmField
        val USERS_AVATAR = "images/avatar.jpg"

        /*
        用户档案袋路径
        */
        @JvmField
        val USERS_PORTFOLIOS = "portfolios/"

        /*
        文件档案路径
        */
        @JvmField
        val FILES_PORTFOLIOS = "files/"

        /*
        临时文件档案路径
        */
        @JvmField
        val TEMP_FILES_PORTFOLIOS = "files/temp"

        /*
        提示页面
        */
        @JvmField
        val TIP_PAGE = "tip::#page-wrapper"

        /*
        提醒类型
        */
        @JvmField
        val ALERT_MESSAGE_TYPE = "消息"

        /**
         * 头像路径
         *
         * @param users 用户
         * @return 路径
         */
        @JvmStatic
        fun avatarPath(users: Users): String {
            return Workbook.USERS_PORTFOLIOS + users.username + "/" + "avatar/"
        }

        /**
         * 保存实习资料路径
         *
         * @param users 用户
         * @return 路径
         */
        @JvmStatic
        fun internshipApplyPath(users: Users): String {
            return Workbook.USERS_PORTFOLIOS + users.username + "/" + "internship/apply/"
        }

        /**
         * 保存实习日志路径
         *
         * @param users 用户
         * @return 路径
         */
        @JvmStatic
        fun internshipJournalPath(users: Users): String {
            return Workbook.USERS_PORTFOLIOS + users.username + "/" + "internship/journal/"
        }

        /**
         * 实习文件路径
         *
         * @param schoolInfoPath 学校路径
         * @return 路径
         */
        @JvmStatic
        fun internshipPath(schoolInfoPath: String): String {
            return Workbook.FILES_PORTFOLIOS + schoolInfoPath + "internship/"
        }

        /**
         * 毕业设计文件路径
         *
         * @param schoolInfoPath 学校路径
         * @return 路径
         */
        @JvmStatic
        fun graduateDesignPath(schoolInfoPath: String): String {
            return Workbook.FILES_PORTFOLIOS + schoolInfoPath + "graduate/design/"
        }

        /**
         * 保存毕业设计规划文件路径
         *
         * @param users 用户
         * @return 路径
         */
        @JvmStatic
        fun graduationDesignPlanPath(users: Users): String {
            return Workbook.USERS_PORTFOLIOS + users.username + "/" + "graduate/design/project/"
        }

        /**
         * 保存毕业设计资料路径
         *
         * @param users 用户
         * @return 路径
         */
        @JvmStatic
        fun graduationDesignProposalPath(users: Users): String {
            return Workbook.USERS_PORTFOLIOS + users.username + "/" + "graduate/design/proposal/"
        }

        /**
         * 保存毕业设计清单路径
         *
         * @param users 用户
         * @return 路径
         */
        @JvmStatic
        fun graduationDesignManifestPath(users: Users): String {
            return Workbook.USERS_PORTFOLIOS + users.username + "/" + "graduate/design/manifest/"
        }

        /**
         * 毕业时间
         *
         * @return 时间
         */
        @JvmStatic
        fun graduationDate(): String {
            return DateTime.now().year.toString() + "年07月"
        }
    }
}