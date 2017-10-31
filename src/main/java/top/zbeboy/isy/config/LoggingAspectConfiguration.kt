package top.zbeboy.isy.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Profile
import top.zbeboy.isy.aop.logging.LoggingAspect
import top.zbeboy.isy.aop.logging.LoggingRecordAspect

/**
 * 日志切面环境配置.
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableAspectJAutoProxy
open class LoggingAspectConfiguration {

    /**
     * 仅在开发环境打印所有日志
     *
     * @return
     */
    @Bean
    @Profile(Workbook.SPRING_PROFILE_DEVELOPMENT)
    open fun loggingAspect(): LoggingAspect {
        return LoggingAspect()
    }

    /**
     * 保存日志
     *
     * @return
     */
    @Bean
    @Profile(Workbook.SPRING_PROFILE_DEVELOPMENT, Workbook.SPRING_PROFILE_PRODUCTION, Workbook.SPRING_PROFILE_TEST)
    open fun loggingRecordAspect(): LoggingRecordAspect {
        return LoggingRecordAspect()
    }
}