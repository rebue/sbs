package rebue.sbs.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;
import rebue.wheel.api.exception.RuntimeExceptionX;
import rebue.wheel.api.ro.Rt;

/**
 * API层异常拦截
 */
@Aspect
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("${rebue.aop.ctrl-err:true}")
@Order(2)
@Slf4j
public class CtrlErrAopConfig {

    @Around("execution(public * *..ctrl..*Ctrl.*(..))")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (final IllegalArgumentException e) {
            log.error("AOP拦截到参数错误的异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return returnRoInCatch(Rt.illegalArgument("参数错误 "));
            } else {
                return returnRoInCatch(Rt.illegalArgument("参数错误: " + e.getMessage()));
            }
        } catch (final NullPointerException e) {
            log.error("AOP拦截到空指针异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return returnRoInCatch(Rt.fail("服务器出现空指针异常", null, "500", null));
            } else {
                return returnRoInCatch(Rt.fail("服务器出现空指针异常", e.getMessage(), "500", null));
            }
        } catch (final RuntimeExceptionX e) {
            log.warn("AOP拦截到自定义的运行时异常", e);
            return returnRoInCatch(Rt.warn(e.getMessage()));
        } catch (final RuntimeException e) {
            log.error("AOP拦截到运行时异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return returnRoInCatch(Rt.fail("服务器出现运行时异常", null, "500", null));
            } else {
                return returnRoInCatch(Rt.fail("服务器出现运行时异常", e.getMessage(), "500", null));
            }
        } catch (final Throwable e) {
            log.error("AOP拦截到未能识别的异常", e);
            return returnRoInCatch(Rt.fail("服务器出现未定义的异常，请联系管理员", e.getMessage(), "500", null));
        }
    }

    private Mono<?> returnRoInCatch(final Rt<?> ro) {
        return Mono.create(callback -> callback.success(ro));
    }

}
