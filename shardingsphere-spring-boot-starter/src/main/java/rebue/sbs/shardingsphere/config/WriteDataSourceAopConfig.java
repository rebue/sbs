package rebue.sbs.shardingsphere.config;

import org.apache.shardingsphere.infra.hint.HintManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

import rebue.wheel.api.annotation.WriteDataSource;

/**
 * ShardingSphere强制路由拦截
 */
@Aspect
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("${rebue.sbs.shardingsphere.enabled:true}")
public class WriteDataSourceAopConfig {

    @Around("@annotation(writeDataSource)")
    public Object around(final ProceedingJoinPoint joinPoint, final WriteDataSource writeDataSource) throws Throwable {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setWriteRouteOnly();
            return joinPoint.proceed();
        }
    }

}
