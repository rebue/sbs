package rebue.sbs.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.StringJoiner;

/**
 * 服务层日志拦截
 */
@Slf4j
@Aspect
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("${rebue.sbs.aop.svc.log.enabled:true}")
@Order(7)
public class SvcLogAopConfig {

    @Around("execution(public * *..svc..*Svc.*(..))")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String          clazzName       = joinPoint.getTarget().getClass().getSimpleName();
        final String          methodName      = joinPoint.getSignature().getName();
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String[]        parameterNames  = methodSignature.getParameterNames();
        final Object[]        parameterValues = joinPoint.getArgs();

        StringBuilder sb = new StringBuilder();
        sb.append("开始调用服务层");
        sb.append(clazzName);
        sb.append(".");
        sb.append(methodName);
        sb.append("，参数: ");
        final StringJoiner sj = new StringJoiner(", ");
        for (int i = 0; i < parameterNames.length; i++) {
            sj.add(parameterNames[i] + "=" + (parameterValues[i] == null ? "" : parameterValues[i].toString()));
        }

        log.info(StringUtils.rightPad(sb + sj.toString(), 73));

        try {
            // 调用
            final Object result = joinPoint.proceed();

            sb = new StringBuilder();
            sb.append("结束调用服务层");
            sb.append(clazzName);
            sb.append(".");
            sb.append(methodName);
            if (result != null) {
                sb.append("，返回: ");
                sb.append(result);
            }
            log.info(StringUtils.rightPad(sb.toString(), 73));
            return result;
        } catch (final Throwable e) {
            String sbErr = "调用服务层%s.%s出现异常".formatted(clazzName, methodName);
            log.error(sbErr, e);
            throw e;
        }

    }

}
