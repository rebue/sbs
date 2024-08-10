package rebue.sbs.aop;

import java.sql.DataTruncation;
import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebInputException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.api.dic.HttpStatusCodeDic;
import rebue.wheel.api.exception.RuntimeExceptionX;
import rebue.wheel.api.ro.Rt;

/**
 * 控制器层异常拦截
 */
@Slf4j
@ControllerAdvice
@Order(5)
public class CtrlErrAopConfig {

    /**
     * 处理全局异常
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public Rt<?> errorHandler(Throwable e) {
        if (e instanceof NumberFormatException) {
            log.error("AOP拦截到字符串转数值的异常", e);
            final String[] errs = e.getMessage().split("\"");
            return Rt.illegalArgument("参数错误: \"" + errs[1] + "\"不是数值类型");
        } else if (e instanceof IllegalArgumentException) {
            log.error("AOP拦截到参数错误的异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return Rt.illegalArgument("参数错误");
            } else {
                return Rt.illegalArgument("参数错误: " + e.getMessage());
            }
        } else if (e instanceof DuplicateKeyException) {
            log.error("AOP拦截到关键字重复的异常", e);
            final String message = e.getCause().getMessage();
            log.debug("message: {}", message);
            // MySQL
            final int start = message.indexOf("'");
            final int end   = message.indexOf("'", start + 1) + 1;
            // final int start = message.lastIndexOf("(") + 1;
            // final int end = message.lastIndexOf(")");
            log.debug("start: {}, end: {}", start, end);
            return Rt.warn(message.substring(start, end) + "已存在");
        } else if (e instanceof ServerWebInputException serverWebInputException) {
            int statusCode = serverWebInputException.getStatusCode().value();
            try {
                Throwable cause = serverWebInputException.getCause().getCause().getCause();
                if (statusCode == 400 && cause instanceof IllegalArgumentException err) {
                    return Rt.illegalArgument("参数错误: " + err.getMessage());
                }
                HttpStatusCodeDic status = HttpStatusCodeDic.getItem(statusCode);
                return Rt.illegalArgument("请求出现错误: " + status.getDesc(), e.getMessage(), String.valueOf(statusCode));
            } catch (NullPointerException nullPointerException) {
                // 请求body为空时，上面的 serverWebInputException.getCause() 为 null，会报空指针异常
                log.warn("请求错误", serverWebInputException);
                HttpStatusCodeDic status = HttpStatusCodeDic.getItem(statusCode);
                return Rt.illegalArgument("请求错误: " + status.getDesc(), serverWebInputException.getMessage(), String.valueOf(statusCode));
                // } catch (IllegalArgumentException unknown) {
                // log.error("AOP拦截到未能识别的异常", unknown);
                // return Rt.fail("服务器出现未定义的异常，请联系管理员", e.getMessage(), String.valueOf(statusCode), null);
            }
        } else if (e instanceof ConstraintViolationException) {
            log.error("AOP拦截到违反参数约束的异常", e);
            final String[] errs = e.getMessage().split(":");
            String         msg  = "参数错误: ";
            if (errs.length == 1) {
                msg += errs[0].trim();
            } else if (errs.length == 2) {
                msg += errs[1].trim();
            } else if (errs.length == 3) {
                msg += errs[2].trim();
            } else {
                msg += e.getMessage();
            }
            return Rt.illegalArgument(msg);
        } else if (e instanceof DataIntegrityViolationException) {
            log.error("AOP拦截到违反数据库完整性的异常", e);
            final Throwable cause = e.getCause();
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                // 违反主外键约束
                return Rt.warn("该记录存在关联信息，请先解除关联", cause.getMessage());
            } else if (cause instanceof DataTruncation) {
                return Rt.warn("此操作违反了该字段最大长度的约束", cause.getMessage());
            } else {
                return Rt.warn("此操作违反了数据库完整性的约束", cause.getMessage());
            }
        } else if (e instanceof NullPointerException) {
            log.error("AOP拦截到空指针异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return Rt.fail("服务器出现空指针异常", null, "500", null);
            } else {
                return Rt.fail("服务器出现空指针异常", e.getMessage(), "500", null);
            }
        } else if (e instanceof RuntimeExceptionX) {
            log.warn("AOP拦截到自定义的运行时异常", e);
            return Rt.warn(e.getMessage());
        } else if (e instanceof RuntimeException) {
            log.error("AOP拦截到运行时异常", e);
            if (StringUtils.isBlank(e.getMessage())) {
                return Rt.fail("服务器出现运行时异常", null, "500", null);
            } else {
                return Rt.fail("服务器出现运行时异常", e.getMessage(), "500", null);
            }
        } else {
            log.error("AOP拦截到未能识别的异常", e);
            return Rt.fail("服务器出现未定义的异常，请联系管理员", e.getMessage(), "500", null);
        }
    }

}
