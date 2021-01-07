package rebue.sbs.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.Ro;
import rebue.wheel.exception.RuntimeExceptionX;

/**
 * API层异常拦截
 */
@Aspect
@Configuration
@Order(2)
public class CtrlErrAopConfig {

    @Around("execution(public * *..ctrl..*Ctrl.*(..))")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (final IllegalArgumentException e) {
            if (StringUtils.isBlank(e.getMessage())) {
                return returnRoInCatch(new Ro<>(ResultDic.PARAM_ERROR, "参数错误 "));
            }
            else {
                return returnRoInCatch(new Ro<>(ResultDic.PARAM_ERROR, "参数错误: " + e.getMessage()));
            }
        } catch (final NullPointerException e) {
            if (StringUtils.isBlank(e.getMessage())) {
                return returnRoInCatch(new Ro<>(ResultDic.FAIL, "服务器出现空指针异常", null, "500", null));
            }
            else {
                return returnRoInCatch(new Ro<>(ResultDic.FAIL, "服务器出现空指针异常", e.getMessage(), "500", null));
            }
        } catch (final RuntimeExceptionX e) {
            return returnRoInCatch(new Ro<>(ResultDic.WARN, e.getMessage()));
        } catch (final RuntimeException e) {
            if (StringUtils.isBlank(e.getMessage())) {
                return returnRoInCatch(new Ro<>(ResultDic.FAIL, "服务器出现运行时异常", null, "500", null));
            }
            else {
                return returnRoInCatch(new Ro<>(ResultDic.FAIL, "服务器出现运行时异常", e.getMessage(), "500", null));
            }
        } catch (final Throwable e) {
            return returnRoInCatch(new Ro<>(ResultDic.FAIL, "服务器出现未定义的异常，请联系管理员", e.getMessage(), "500", null));
        }
    }

    private Mono<?> returnRoInCatch(final Ro<?> ro) {
        return Mono.create(callback -> callback.success(ro));
    }

}
