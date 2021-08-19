package rebue.sbs.aop;

import java.sql.DataTruncation;
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
import rebue.wheel.api.exception.RuntimeExceptionX;

/**
 * API层异常拦截
 */
@Slf4j
@Aspect
@Configuration(proxyBeanMethods = false)
@Order(4)
public class ApiErrAopConfig {

    @Around("execution(public * *..api..*Api.*(..))")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (final DuplicateKeyException e) {
            log.error("AOP拦截到关键字重复的异常", e);
            final String message = e.getCause().getMessage();
            final int    start   = message.indexOf("'");
            final int    end     = message.indexOf("'", start + 1) + 1;
            return new Ro<>(ResultDic.WARN, message.substring(start, end) + "已存在");
        } catch (final NumberFormatException e) {
            log.error("AOP拦截到字符串转数值的异常", e);
            final String[] errs = e.getMessage().split("\"");
            return new Ro<>(ResultDic.PARAM_ERROR, "参数错误: \"" + errs[1] + "\"不是数值类型");
        } catch (final IllegalArgumentException e) {
            log.error("AOP拦截到参数错误的异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return new Ro<>(ResultDic.PARAM_ERROR, "参数错误");
            }
            else {
                return new Ro<>(ResultDic.PARAM_ERROR, "参数错误: " + e.getMessage());
            }
        } catch (final ConstraintViolationException e) {
            log.error("AOP拦截到违反参数约束的异常", e);
            final String[] errs = e.getMessage().split(":");
            String         msg;
            if (errs.length == 1) {
                msg = errs[0].trim();
            }
            else if (errs.length == 2) {
                msg = errs[1].trim();
            }
            else {
                msg = e.getMessage();
            }
            return new Ro<>(ResultDic.PARAM_ERROR, msg);
        } catch (final DataIntegrityViolationException e) {
            log.error("AOP拦截到违反数据库完整性的异常", e);
            final Throwable cause = e.getCause();
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                return new Ro<>(ResultDic.WARN, "该记录存在关联信息，请先解除关联", cause.getMessage());
            }
            else if (cause instanceof DataTruncation) {
                return new Ro<>(ResultDic.WARN, "此操作违反了该字段最大长度的约束", cause.getMessage());
            }
            else {
                return new Ro<>(ResultDic.WARN, "此操作违反了数据库完整性的约束", cause.getMessage());
            }
        } catch (final NullPointerException e) {
            log.error("AOP拦截到空指针异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return new Ro<>(ResultDic.FAIL, "服务器出现空指针异常", null, "500", null);
            }
            else {
                return new Ro<>(ResultDic.FAIL, "服务器出现空指针异常", e.getMessage(), "500", null);
            }
        } catch (final RuntimeExceptionX e) {
            log.warn("AOP拦截到自定义的运行时异常", e);
            return new Ro<>(ResultDic.WARN, e.getMessage());
        } catch (final RuntimeException e) {
            log.error("AOP拦截到运行时异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return new Ro<>(ResultDic.FAIL, "服务器出现运行时异常", null, "500", null);
            }
            else {
                return new Ro<>(ResultDic.FAIL, "服务器出现运行时异常", e.getMessage(), "500", null);
            }
        } catch (final Throwable e) {
            log.error("AOP拦截到未能识别的异常", e);
            return new Ro<>(ResultDic.FAIL, "服务器出现未定义的异常，请联系管理员", e.getMessage(), "500", null);
        }
    }

}
