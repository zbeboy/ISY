package top.zbeboy.isy.elastic.config

/**
 * Created by zbeboy 2017-11-19 .
 **/
open class ElasticBook {

    companion object {
        /*
        无权限
        */
        @JvmField
        val NO_AUTHORITIES = 99999
        /*
        有权限
        */
        @JvmField
        val HAS_AUTHORITIES = 0
        /*
        系统
        */
        @JvmField
        val SYSTEM_AUTHORITIES = 1
        /*
        管理员
        */
        @JvmField
        val ADMIN_AUTHORITIES = 2
        /*
        运维
        */
        @JvmField
        val OPS_AUTHORITIES = 3
    }
}