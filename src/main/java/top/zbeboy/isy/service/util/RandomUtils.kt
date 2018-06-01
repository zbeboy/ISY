package top.zbeboy.isy.service.util

import org.apache.commons.lang3.RandomStringUtils

/**
 * Created by zbeboy 2017-11-30 .
 **/
class RandomUtils {
    companion object {
        private const val DEF_COUNT = 20

        private const val MOBILE_COUNT = 6

        private const val ROLE_EN_NAME_COUNT = 20

        /**
         * Generates a password.
         *
         * @return the generated password
         */
        @JvmStatic
        fun generatePassword(): String {
            return RandomStringUtils.randomAlphanumeric(DEF_COUNT)
        }

        /**
         * Generates an activation key.
         *
         * @return the generated activation key
         */
        @JvmStatic
        fun generateActivationKey(): String {
            return RandomStringUtils.randomNumeric(DEF_COUNT)
        }

        /**
         * Generates a reset key.
         *
         * @return the generated reset key
         */
        @JvmStatic
        fun generateResetKey(): String {
            return RandomStringUtils.randomNumeric(DEF_COUNT)
        }

        /**
         * Generates a email check key
         *
         * @return the email check key
         */
        @JvmStatic
        fun generateEmailCheckKey(): String {
            return RandomStringUtils.randomAlphanumeric(DEF_COUNT)
        }

        /**
         * Generates a mobile check key
         *
         * @return the mobile check key
         */
        @JvmStatic
        fun generateMobileKey(): String {
            return RandomStringUtils.randomNumeric(MOBILE_COUNT)
        }

        /**
         * Generates a role english name
         *
         * @return the role english name
         */
        @JvmStatic
        fun generateRoleEnName(): String {
            return RandomStringUtils.randomAlphabetic(ROLE_EN_NAME_COUNT)
        }
    }
}