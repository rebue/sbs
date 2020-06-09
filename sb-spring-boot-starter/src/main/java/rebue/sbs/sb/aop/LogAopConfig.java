package rebue.sbs.sb.aop;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志拦截
 */
@Slf4j
@Aspect
@Configuration
@ConditionalOnExpression("${rebue.sb.log-aop.enabled:true}")
public class LogAopConfig {

    /**
     * 控制层日志
     */
    @Before("execution(public * *..ctrl.*Ctrl.*(..))")
    @ConditionalOnExpression("${rebue.sb.log-aop.ctrl.enabled:true}")
    public void logCtrl(final JoinPoint joinPoint) throws Throwable {

        log.info("============================ Controller调用详情 ============================");

        // 获取请求信息
        final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        final HttpServletRequest       request                  = servletRequestAttributes.getRequest();

        final String requestMethod = request.getMethod();
        final String requestURI    = request.getRequestURI();
        log.info("* {} : {}", "请求链接", StringUtils.rightPad(requestMethod + " " + requestURI, 100));

        final String clazzName  = joinPoint.getTarget().getClass().getSimpleName();
        final String methodName = joinPoint.getSignature().getName();
        log.info("* {} : {}", "调用方法", StringUtils.rightPad(clazzName + "." + methodName, 100));

        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String[]        parameterNames  = methodSignature.getParameterNames();
        final Object[]        parameterValues = joinPoint.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            log.info("* {} : {}", i == 0 ? "调用参数" : "　　　　", StringUtils.rightPad(
                    parameterNames[i] + "=" + (parameterValues[i] == null ? "" : parameterValues[i].toString()), 100));
        }

        log.info("===========================================================================");
    }

    /**
     * 服务层日志
     * 
     * @throws Throwable
     */
    @Around("execution(public * *..svc.*Svc.*(..))")
    public Object logSvc(final ProceedingJoinPoint joinPoint) throws Throwable {

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {

            log.info("============================ Service调用详情 ============================");

            final String clazzName  = joinPoint.getTarget().getClass().getSimpleName();
            final String methodName = joinPoint.getSignature().getName();
            log.info("* {} : {}", "调用方法", StringUtils.rightPad(clazzName + "." + methodName, 100));

            final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            final String[]        parameterNames  = methodSignature.getParameterNames();
            final Object[]        parameterValues = joinPoint.getArgs();
            for (int i = 0; i < parameterNames.length; i++) {
                log.info("* {} : {}", i == 0 ? "调用参数" : "　　　　", StringUtils.rightPad(
                        parameterNames[i] + "=" + (parameterValues[i] == null ? "" : parameterValues[i].toString()),
                        100));
            }

            final Object result = joinPoint.proceed();

            log.info("* {} : {}", "返回结果", StringUtils.rightPad(result == null ? "null" : result.toString(), 100));

            return result;
        } catch (final Throwable e) {
            log.error("出现异常", e);
            throw e;
        } finally {
            stopWatch.stop();
            log.info("* {} : {}", "调用耗时", StringUtils.rightPad(stopWatch.formatTime(), 100));
            log.info("===========================================================================");
        }
    }

}
