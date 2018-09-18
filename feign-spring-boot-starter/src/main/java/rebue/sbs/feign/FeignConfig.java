package rebue.sbs.feign;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
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

    @Bean
    public RequestInterceptor headerInterceptor() {
        _log.info("传递接收到的请求头");
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes == null)
                    return;
                HttpServletRequest request = attributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String values = request.getHeader(name);
                        requestTemplate.header(name, values);
                    }
                }
            }
        };
    }
}
