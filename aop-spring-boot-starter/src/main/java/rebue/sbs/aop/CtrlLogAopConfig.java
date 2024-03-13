package rebue.sbs.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 控制器层日志拦截
 */
@Slf4j
@Aspect
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnExpression("${rebue.aop.ctrl-log:false}")
@Order(1)
public class CtrlLogAopConfig {

    @Before("execution(public * *..ctrl..*Ctrl.*(..))")
    public void before(final JoinPoint joinPoint) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method          method          = methodSignature.getMethod();
        String                requestMethods  = null;
        String                requestPaths    = null;
        if (method.isAnnotationPresent(RequestMapping.class)) {
            final RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            requestMethods = Stream.of(annotation.method()).map(RequestMethod::name).collect(Collectors.joining(","));
            requestPaths = Stream.concat(Stream.of(annotation.path()), Stream.of(annotation.value())).collect(Collectors.joining(","));
        } else if (method.isAnnotationPresent(GetMapping.class)) {
            final GetMapping annotation = method.getAnnotation(GetMapping.class);
            requestMethods = "GET";
            requestPaths = Stream.concat(Stream.of(annotation.path()), Stream.of(annotation.value())).collect(Collectors.joining(","));
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            final PostMapping annotation = method.getAnnotation(PostMapping.class);
            requestMethods = "POST";
            requestPaths = Stream.concat(Stream.of(annotation.path()), Stream.of(annotation.value())).collect(Collectors.joining(","));
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            final PutMapping annotation = method.getAnnotation(PutMapping.class);
            requestMethods = "PUT";
            requestPaths = Stream.concat(Stream.of(annotation.path()), Stream.of(annotation.value())).collect(Collectors.joining(","));
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            final DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
            requestMethods = "DELETE";
            requestPaths = Stream.concat(Stream.of(annotation.path()), Stream.of(annotation.value())).collect(Collectors.joining(","));
        }
        if (StringUtils.isNoneBlank(requestMethods, requestPaths)) {
            log.info(StringUtils.rightPad("控制器层接收到请求: [" + requestMethods + "]" + requestPaths, 73));
        }
    }
}
