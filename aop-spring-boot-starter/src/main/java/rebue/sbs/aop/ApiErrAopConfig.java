package rebue.sbs.aop;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import lombok.extern.slf4j.Slf4j;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.Ro;
import rebue.wheel.exception.RuntimeExceptionX;

/**
 * API层异常拦截
 */
@Slf4j
@Aspect
@Configuration
@Order(4)
public class ApiErrAopConfig {

    @Around("execution(public * *..api..*Api.*(..))")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (final DuplicateKeyException e) {
            log.error("AOP拦截关键字重复的异常", e);
            final String message = e.getCause().getMessage();
            final int    start   = message.indexOf("'");
            final int    end     = message.indexOf("'", start + 1) + 1;
            return new Ro<>(ResultDic.WARN, message.substring(start, end) + "已存在");
        } catch (final NumberFormatException e) {
            final String[] errs = e.getMessage().split("\"");
            return new Ro<>(ResultDic.PARAM_ERROR, "参数错误: \"" + errs[1] + "\"不是数值类型");
        } catch (final IllegalArgumentException e) {
            if (StringUtils.isBlank(e.getMessage())) {
                return new Ro<>(ResultDic.PARAM_ERROR, "参数错误");
            }
            else {
                return new Ro<>(ResultDic.PARAM_ERROR, "参数错误: " + e.getMessage());
            }
        } catch (final ConstraintViolationException e) {
            final String[]      errs = e.getMessage().split(",");
            final StringBuilder sb   = new StringBuilder();
            for (final String err : errs) {
                sb.append(err.split(":")[1].trim() + ",");
            }
            return new Ro<>(ResultDic.PARAM_ERROR, sb.deleteCharAt(sb.length() - 1).toString());
        } catch (final DataIntegrityViolationException e) {
            return returnSQL((SQLIntegrityConstraintViolationException) e.getCause());
        } catch (final NullPointerException e) {
            if (StringUtils.isBlank(e.getMessage())) {
                return new Ro<>(ResultDic.FAIL, "服务器出现空指针异常", null, "500", null);
            }
            else {
                return new Ro<>(ResultDic.FAIL, "服务器出现空指针异常", e.getMessage(), "500", null);
            }
        } catch (final RuntimeExceptionX e) {
            return new Ro<>(ResultDic.WARN, e.getMessage());
        } catch (final RuntimeException e) {
            if (StringUtils.isBlank(e.getMessage())) {
                return new Ro<>(ResultDic.FAIL, "服务器出现运行时异常", null, "500", null);
            }
            else {
                return new Ro<>(ResultDic.FAIL, "服务器出现运行时异常", e.getMessage(), "500", null);
            }
        } catch (final Throwable e) {
            return new Ro<>(ResultDic.FAIL, "服务器出现未定义的异常，请联系管理员", e.getMessage(), "500", null);
        }
    }

    private Ro<String> returnSQL(final SQLIntegrityConstraintViolationException e) {
        return new Ro<>(ResultDic.WARN, "此数据被其它数据关联引用，不能直接修改或删除", e.getMessage());
    }

}
