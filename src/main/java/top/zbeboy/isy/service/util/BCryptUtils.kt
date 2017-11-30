package top.zbeboy.isy.service.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Created by zbeboy 2017-11-30 .
 **/
class BCryptUtils {
    companion object {
        /**
         * BCryptPassword 方式生成密码
         *
         * @param password 需要加密的密码
         * @return 加密后的密码
         */
        @JvmStatic
        fun bCryptPassword(password: String): String {
            val bCryptPasswordEncoder = BCryptPasswordEncoder()
            return bCryptPasswordEncoder.encode(password)
        }

        /**
         * BCryptPassword 密码比对
         *
         * @param password         未加密的密码
         * @param databasePassword 加密码后的密码或数据库中保存的密码
         * @return 比较结果
         */
        @JvmStatic
        fun bCryptPasswordMatches(password: String, databasePassword: String): Boolean {
            val bCryptPasswordEncoder = BCryptPasswordEncoder()
            return bCryptPasswordEncoder.matches(password, databasePassword)
        }

        /*$2a$10$HKXHRhnhlC1aZQ4hukD0S.zYep/T5A7FULBo7S2UrJsqQCThUxdo2  123456*/
    }
}