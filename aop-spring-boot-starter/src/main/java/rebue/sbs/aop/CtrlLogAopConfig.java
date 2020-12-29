package rebue.sbs.aop;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

/**
 * 控制器层日志拦截
 */
@Slf4j
@Aspect
@Configuration
@ConditionalOnWebApplication
@ConditionalOnExpression("${rebue.sbs.aop.log-aop.ctrl.enabled:true}")
@Order(1)
public class CtrlLogAopConfig {

    @Before("execution(public * *..ctrl..*Ctrl.*(..))")
    public void log(final JoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final HttpServletRequest       request                  = servletRequestAttributes.getRequest();

        final String                   requestMethod            = request.getMethod();
        final String                   requestURI               = request.getRequestURI();

        log.info(StringUtils.rightPad("控制器层接收到请求:" + requestMethod + " " + requestURI, 73));
    }

}
