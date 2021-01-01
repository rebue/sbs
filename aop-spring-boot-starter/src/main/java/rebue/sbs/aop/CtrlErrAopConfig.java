package rebue.sbs.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.Ro;

/**
 * API层异常拦截
 */
@Slf4j
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
                return Mono.create(callback -> callback.success(new Ro<>(ResultDic.PARAM_ERROR, "参数错误: ")));
            }
            else {
                return Mono.create(callback -> callback.success(new Ro<>(ResultDic.PARAM_ERROR, "参数错误: " + e.getMessage())));
            }
        } catch (final NullPointerException e) {
            if (StringUtils.isBlank(e.getMessage())) {
                return Mono.create(callback -> callback.success(new Ro<>(ResultDic.FAIL, "空指针异常", null, "500", null)));
            }
            else {
                return Mono.create(callback -> callback.success(new Ro<>(ResultDic.FAIL, e.getMessage(), null, "500", null)));
            }
        } catch (final RuntimeException e) {
            if (StringUtils.isBlank(e.getMessage())) {
                return Mono.create(callback -> callback.success(new Ro<>(ResultDic.FAIL, "运行时异常", null, "500", null)));
            }
            else {
                return Mono.create(callback -> callback.success(new Ro<>(ResultDic.FAIL, e.getMessage(), null, "500", null)));
            }
        } catch (final Throwable e) {
            return Mono.create(callback -> callback.success(new Ro<>(ResultDic.FAIL, "服务器出现未定义的异常，请联系管理员", e.getMessage(), "500", null)));
        }
    }

}
