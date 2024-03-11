package rebue.sbs.feign;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.Contract;
import feign.RequestInterceptor;

@Configuration(proxyBeanMethods = false)
public class FeignConfig {
    private final static Logger _log = LoggerFactory.getLogger(FeignConfig.class);

    /**
     * 只有在这里才能控制feign的日志级别
     */
    @Bean
    feign.Logger.Level feignLoggerLevel() {
//        return Logger.Level.FULL;
        return feign.Logger.Level.BASIC;
    }

    /**
     * 传递接收到的请求头
     */
    @Bean
    public RequestInterceptor headerInterceptor() {
        _log.info("传递接收到的请求头");
        return requestTemplate -> {
            final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            final HttpServletRequest  request     = attributes.getRequest();
            final Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    final String name   = headerNames.nextElement();
                    final String values = request.getHeader(name);
                    requestTemplate.header(name, values);
                }
            }
        };
    }

    @Autowired(required = false)
    private final List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Bean
    public Contract feignContract(final FormattingConversionService feignConversionService) {
        // 在原配置类中是用ConversionService类型的参数，但ConversionService接口不支持addConverter操作，使用FormattingConversionService仍然可以实现feignContract配置。
        feignConversionService.addConverter(new EnumConverter());
        return new SpringMvcContract(parameterProcessors, feignConversionService);
    }

}
