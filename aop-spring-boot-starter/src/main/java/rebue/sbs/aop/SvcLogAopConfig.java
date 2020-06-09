package rebue.sbs.aop;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 服务层日志拦截
 */
@Slf4j
@Aspect
@Configuration
@ConditionalOnExpression("${rebue.sbs.aop.log-aop.svc.enabled:true}")
public class SvcLogAopConfig {

    @Around("execution(public * *..svc.*Svc.*(..))")
    public Object logSvc(final ProceedingJoinPoint joinPoint) throws Throwable {

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            final String          clazzName       = joinPoint.getTarget().getClass().getSimpleName();
            final String          methodName      = joinPoint.getSignature().getName();
            final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            final String[]        parameterNames  = methodSignature.getParameterNames();
            final Object[]        parameterValues = joinPoint.getArgs();

            StringBuilder sb = new StringBuilder();
            sb.append("\r\n----------------------- 服务层方法准备被调用 -----------------------\r\n");
            sb.append("* 方法: \r\n*     ");
            sb.append(clazzName);
            sb.append(".");
            sb.append(methodName);
            sb.append("\r\n* 参数:");
            for (int i = 0; i < parameterNames.length; i++) {
                sb.append("\r\n*     ");
                sb.append(parameterNames[i]);
                sb.append("=");
                sb.append(parameterValues[i] == null ? "" : parameterValues[i].toString());
            }
            sb.append("\r\n");
            sb.append(StringUtils.rightPad("-----------------------------------------------------------------", 100));
            log.info(sb.toString());

            log.info(StringUtils.rightPad("方法调用开始....", 76));

            // 调用
            final Object result = joinPoint.proceed();

            // 调用完成
            stopWatch.stop();

            sb = new StringBuilder();
            sb.append("方法调用结束!!!\r\n======================= 服务层方法被调用详情 =======================\r\n");
            sb.append("* 方法: \r\n*     ");
            sb.append(clazzName);
            sb.append(".");
            sb.append(methodName);
            sb.append("\r\n* 参数:");
            for (int i = 0; i < parameterNames.length; i++) {
                sb.append("\r\n*     ");
                sb.append(parameterNames[i]);
                sb.append("=");
                sb.append(parameterValues[i] == null ? "" : parameterValues[i].toString());
            }
            sb.append("\r\n* 返回:\r\n*    ");
            sb.append(result == null ? "null" : result.toString());
            sb.append("\r\n* 耗时:\r\n*    ");
            sb.append(stopWatch.formatTime());
            sb.append("\r\n");
            sb.append(StringUtils.rightPad("=================================================================", 100));
            log.info(sb.toString());

            return result;
        } catch (final Throwable e) {
            log.error("出现异常", e);
            throw e;
        }
    }

}
