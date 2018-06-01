package top.zbeboy.isy.service.util

/**
 * Created by zbeboy 2017-11-30 .
 **/
class SQLQueryUtils {
    companion object {
        /**
         * 组装likeAll全匹配参数
         *
         * @param param 参数
         * @return like '%{param}%'
         */
        @JvmStatic
        fun likeAllParam(param: String): String {
            return "%$param%"
        }

        /**
         * 组装likeAll全匹配参数
         *
         * @param param 参数
         * @return like '%{param}%'
         */
        @JvmStatic
        fun elasticLikeAllParam(param: String): String {
            return "*$param*"
        }

        /**
         * 右 like
         *
         * @param param 参数
         * @return like '{param}%'
         */
        @JvmStatic
        fun rightLikeParam(param: String): String {
            return param + "%"
        }

        /**
         * 精确匹配系统用户账号处理
         *
         * @param param 参数
         * @return 处理结果
         */
        fun phraseQueryingUsername(param: String): String {
            return param.substring(0, param.lastIndexOf('@'))
        }
    }
}