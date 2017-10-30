package top.zbeboy.isy.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import top.zbeboy.isy.async.ExceptionHandlingAsyncTaskExecutor
import java.util.concurrent.Executor

/**
 * 异步配置.
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
@Configuration
@EnableAsync
@EnableScheduling
open class AsyncConfiguration : AsyncConfigurer {

    private val log = LoggerFactory.getLogger(AsyncConfiguration::class.java)

    @Autowired
    private val isyProperties: ISYProperties? = null

    @Override
    @Bean(name = arrayOf("taskExecutor"))
    override fun getAsyncExecutor(): Executor {
        log.debug("Creating Async Task Executor")
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = this.isyProperties?.getAsync()!!.corePoolSize
        executor.maxPoolSize = this.isyProperties.getAsync()!!.maxPoolSize
        executor.setQueueCapacity(this.isyProperties.getAsync()!!.queueCapacity)
        executor.threadNamePrefix = "isy-app-Executor-"
        return ExceptionHandlingAsyncTaskExecutor(executor)
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return SimpleAsyncUncaughtExceptionHandler()
    }
}