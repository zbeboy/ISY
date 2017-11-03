package top.zbeboy.isy.aop.logging

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import top.zbeboy.isy.config.Workbook
import java.util.*
import javax.inject.Inject

/**
 * Aspect for logging execution of service and repository Spring components.
 * 运用切面输出日志信息.
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
@Aspect
class LoggingAspect {

    private val log = LoggerFactory.getLogger(LoggingAspect::class.java)

    @Inject
    private lateinit var env: Environment

    /**
     * 日志切面
     */
    @Pointcut("within(top.zbeboy.isy.service..*) || within(top.zbeboy.isy.web..*)")
    fun loggingPointcut() {
    }

    /**
     * 日志打印报错信息
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "loggingPointcut()", throwing = "e")
    fun logAfterThrowing(joinPoint: JoinPoint, e: Throwable) {
        if (env.acceptsProfiles(Workbook.SPRING_PROFILE_DEVELOPMENT)) {
            log.error("Exception in {}.{}() with cause = {} and exception {}", joinPoint.signature.declaringTypeName,
                    joinPoint.signature.name, e.cause, e)
        } else {
            log.error("Exception in {}.{}() with cause = {}", joinPoint.signature.declaringTypeName,
                    joinPoint.signature.name, e.cause)
        }
    }

    /**
     * 日志切面环绕
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("loggingPointcut()")
    @Throws(Throwable::class)
    fun logAround(joinPoint: ProceedingJoinPoint): Any {
        if (log.isDebugEnabled) {
            log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.signature.declaringTypeName,
                    joinPoint.signature.name, Arrays.toString(joinPoint.args))
        }
        try {
            val result = joinPoint.proceed()
            if (log.isDebugEnabled) {
                log.debug("Exit: {}.{}() with result = {}", joinPoint.signature.declaringTypeName,
                        joinPoint.signature.name, result)
            }
            return result
        } catch (e: IllegalArgumentException) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.args),
                    joinPoint.signature.declaringTypeName, joinPoint.signature.name)

            throw e
        }

    }
}