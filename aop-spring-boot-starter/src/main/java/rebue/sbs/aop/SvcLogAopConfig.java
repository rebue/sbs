package rebue.sbs.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Before("execution(public * *..svc.*Svc.*(..))")
    public void log(final JoinPoint joinPoint) throws Throwable {
        final String          clazzName       = joinPoint.getTarget().getClass().getSimpleName();
        final String          methodName      = joinPoint.getSignature().getName();
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String[]        parameterNames  = methodSignature.getParameterNames();
        final Object[]        parameterValues = joinPoint.getArgs();

        final StringBuilder   sb              = new StringBuilder();
        sb.append("调用服务层");
        sb.append(clazzName);
        sb.append("的方法");
        sb.append(methodName);
        sb.append(": ");
        for (int i = 0; i < parameterNames.length; i++) {
            sb.append(parameterNames[i]);
            sb.append("=");
            sb.append(parameterValues[i] == null ? "" : parameterValues[i].toString());
            sb.append(", ");
        }

        log.info(StringUtils.rightPad(sb.substring(0, sb.length() - 2), 73));
    }

}
