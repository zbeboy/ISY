package top.zbeboy.isy.aop.logging

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import top.zbeboy.isy.annotation.logging.RecordSystemLogging
import top.zbeboy.isy.elastic.pojo.SystemLogElastic
import top.zbeboy.isy.glue.system.SystemLogGlue
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.service.util.UUIDUtils
import java.sql.Timestamp
import java.time.Clock
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-10-31 .
 * 使用该切面，方法必须 open .
 **/
@Aspect
class LoggingRecordAspect {

    private val log = LoggerFactory.getLogger(LoggingRecordAspect::class.java)

    @Resource
    private lateinit var usersService: UsersService

    @Resource
    private lateinit var systemLogGlue: SystemLogGlue

    /**
     * 日志记录切面
     */
    @Pointcut("@annotation(top.zbeboy.isy.annotation.logging.RecordSystemLogging)")
    fun loggingRecordPointcut() {
    }

    /**
     * 日志记录
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("loggingRecordPointcut()")
    @Throws(Throwable::class)
    fun doRecord(point: ProceedingJoinPoint): Any? {
        val targetName = point.target.javaClass.name
        val methodName = point.signature.name
        val arguments = point.args
        val targetClass = Class.forName(targetName)
        val methods = targetClass.methods
        for (method in methods) {
            if (method.name == methodName) {
                val clazzs = method.parameterTypes
                if (clazzs.size == arguments.size) {
                    for (o in arguments) {
                        if (o is HttpServletRequest) {
                            val users = usersService.userFromSession
                            val systemLog = SystemLogElastic(UUIDUtils.getUUID(), method.getAnnotation(RecordSystemLogging::class.java).description, Timestamp(Clock.systemDefaultZone().millis()), users.username, RequestUtils.getIpAddress(o))
                            systemLogGlue.save(systemLog)
                            log.info(" Record operator logging to database , the module is {} , the method is {} ", method.getAnnotation(RecordSystemLogging::class.java).module, method.getAnnotation(RecordSystemLogging::class.java).methods)
                            break
                        }
                    }
                    break
                }
            }
        }
        return point.proceed()
    }
}