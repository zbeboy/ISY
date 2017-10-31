package top.zbeboy.isy.annotation.logging

import java.lang.annotation.*

/**
 * Created by zbeboy 2017-10-31 .
 **/
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RecordSystemLogging(
        /**
         * 功能模块
         *
         * @return 功能模块
         */
        val module: String = "",
        /**
         * 方法名
         *
         * @return 方法名
         */
        val methods: String = "",
        /**
         * 日志描述
         *
         * @return 日志描述
         */
        val description: String = "")