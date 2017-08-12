package top.zbeboy.isy.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import top.zbeboy.isy.annotation.logging.RecordSystemLogging;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.elastic.pojo.SystemLogElastic;
import top.zbeboy.isy.glue.system.SystemLogGlue;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Clock;

/**
 * Created by lenovo on 2017-08-12.
 */
@Slf4j
@Aspect
public class LoggingRecordAspect {

    @Resource
    private UsersService usersService;

    @Resource
    private SystemLogGlue systemLogGlue;

    /**
     * 日志记录切面
     */
    @Pointcut("@annotation(top.zbeboy.isy.annotation.logging.RecordSystemLogging)")
    public void loggingRecordPointcut() {
    }

    /**
     * 日志记录
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("loggingRecordPointcut()")
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
                            SystemLogElastic systemLog = new SystemLogElastic(UUIDUtils.getUUID(), String.valueOf(method.getAnnotation(RecordSystemLogging.class).description()), new Timestamp(Clock.systemDefaultZone().millis()), users.getUsername(), RequestUtils.getIpAddress(request));
                            systemLogGlue.save(systemLog);
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
