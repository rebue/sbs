package rebue.sbs.aop;

import javax.validation.ConstraintViolationException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.Ro;

/**
 * API层异常拦截
 */
@Aspect
@Configuration
@Order
public class ApiErrAopConfig {

    @Around("execution(public * *..api.*Api.*(..))")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (final DuplicateKeyException e) {
            return new Ro<>(ResultDic.FAIL, "操作数据库失败，唯一键重复：" + e.getCause().getMessage(), "500", null);
        } catch (final IllegalArgumentException e) {
            return new Ro<>(ResultDic.PARAM_ERROR, "参数不能为空");
        } catch (final ConstraintViolationException e) {
            return new Ro<>(ResultDic.PARAM_ERROR, e.getMessage().split(":")[1].trim());
        } catch (final Throwable e) {
            return new Ro<>(ResultDic.FAIL, "服务器出现未定义的异常，请联系管理员", "500", null);
        }
    }

}
