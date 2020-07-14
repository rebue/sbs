package rebue.sbs.aop;

import javax.validation.ConstraintViolationException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;

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
            System.out.println(e);
            return new Ro<>(ResultDic.FAIL, e.getCause().getMessage().substring(16, 22) + "已存在");
        } catch (final IllegalArgumentException e) {
            return new Ro<>(ResultDic.PARAM_ERROR, "参数不能为空");
        } catch (final ConstraintViolationException e) {
            final String[] errs = e.getMessage().split(",");
            final StringBuilder sb = new StringBuilder();
            for (final String err : errs) {
                sb.append(err.split(":")[1].trim() + ",");
            }
            return new Ro<>(ResultDic.PARAM_ERROR, sb.deleteCharAt(sb.length() - 1).toString());
        } catch (final Throwable e) {
            return new Ro<>(ResultDic.FAIL, "服务器出现未定义的异常，请联系管理员", "500", null);
        }
    }

}
