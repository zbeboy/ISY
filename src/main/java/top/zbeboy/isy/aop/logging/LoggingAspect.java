package top.zbeboy.isy.aop.logging;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import top.zbeboy.isy.annotation.logging.RecordSystemLogging;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.SystemLog;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.system.SystemLogService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.Arrays;

/**
 * Aspect for logging execution of service and repository Spring components.
 * 运用切面输出日志信息.
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Aspect
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private Environment env;

    @Resource
    private UsersService usersService;

    @Resource
    private SystemLogService systemLogService;

    @Pointcut("within(top.zbeboy.isy.service..*) || within(top.zbeboy.isy.web..*)")
    public void loggingPointcut() {
    }

    @Pointcut("@annotation(top.zbeboy.isy.annotation.logging.RecordSystemLogging)")
    public void recordLoggingAspect() {
    }

    @AfterThrowing(pointcut = "loggingPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        if (env.acceptsProfiles(Workbook.SPRING_PROFILE_DEVELOPMENT)) {
            log.error("Exception in {}.{}() with cause = {} and exception {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), e.getCause(), e);
        } else {
            log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), e.getCause());
        }
    }

    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }
    }

    @Around("recordLoggingAspect()")
    public Object doRecord(ProceedingJoinPoint point) throws Throwable {
        String targetName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] arguments = point.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    for (Object o : arguments) {
                        if (o instanceof HttpServletRequest) {
                            HttpServletRequest request = (HttpServletRequest) o;
                            Users users = usersService.getUserFromSession();
                            SystemLog systemLog = new SystemLog(UUIDUtils.getUUID(), String.valueOf(method.getAnnotation(RecordSystemLogging.class).description()), new Timestamp(Clock.systemDefaultZone().millis()), users.getUsername(), RequestUtils.getIpAddress(request));
                            systemLogService.save(systemLog);
                            log.info(" Record operator logging to database , the module is {} , the method is {} ", method.getAnnotation(RecordSystemLogging.class).module(), method.getAnnotation(RecordSystemLogging.class).methods());
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return point.proceed();
    }
}
