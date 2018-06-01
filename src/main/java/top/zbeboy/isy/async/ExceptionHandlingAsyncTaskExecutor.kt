package top.zbeboy.isy.async

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.task.AsyncTaskExecutor
import java.util.concurrent.Callable
import java.util.concurrent.Future

/**
 * 异步异常处理.
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
class ExceptionHandlingAsyncTaskExecutor(private val executor: AsyncTaskExecutor) : AsyncTaskExecutor,
        InitializingBean, DisposableBean {

    private val log = LoggerFactory.getLogger(ExceptionHandlingAsyncTaskExecutor::class.java)

    override fun submit(p0: Runnable?): Future<*> {
        return executor.submit(createWrappedRunnable(p0!!))
    }

    override fun <T : Any?> submit(p0: Callable<T>?): Future<T> {
        return executor.submit(createCallable(p0!!))
    }

    override fun execute(p0: Runnable?, p1: Long) {
        executor.execute(createWrappedRunnable(p0!!), p1)
    }

    override fun execute(p0: Runnable?) {
        executor.execute(createWrappedRunnable(p0!!))
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        if (executor is InitializingBean) {
            val bean = executor as InitializingBean
            bean.afterPropertiesSet()
        }
    }

    @Throws(Exception::class)
    override fun destroy() {
        if (executor is DisposableBean) {
            val bean = executor as DisposableBean
            bean.destroy()
        }
    }

    private fun <T> createCallable(task: Callable<T>): () -> T {
        return {
            try {
                task.call()
            } catch (e: Exception) {
                handle(e)
                throw e
            }
        }
    }

    private fun createWrappedRunnable(task: Runnable): () -> Unit {
        return {
            try {
                task.run()
            } catch (e: Exception) {
                handle(e)
            }
        }
    }

    protected fun handle(e: Exception) {
        log.error("Caught async exception", e)
    }
}