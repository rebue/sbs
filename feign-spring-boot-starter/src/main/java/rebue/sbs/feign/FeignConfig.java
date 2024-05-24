package rebue.sbs.feign;

import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * OpenFeign的配置器
 */
@Configuration(proxyBeanMethods = false)
public class FeignConfig {
    /**
     * 因为使用了WebFlux，导致HttpMessageConvertersAutoConfiguration不会生效，手动注册一下
     *
     * @param converters 转换器
     * @return 转换器
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    /**
     * 解决Feign客户端调用接口报错的问题
     * webflux会导致报block()/blockFirst()/blockLast()异常
     */
    @Bean
    @Primary
    public LoadBalancerClient BlockingLoadBalancerClient(LoadBalancerClientFactory loadBalancerClientFactory) {
        return new CustomBlockingLoadBalancerClient(loadBalancerClientFactory);
    }

    // /**
    // * 只有在这里才能控制feign的日志级别
    // */
    // @Bean
    // feign.Logger.Level feignLoggerLevel() {
    //// return Logger.Level.FULL;
    // return feign.Logger.Level.BASIC;
    // }

    // /**
    // * 传递接收到的请求头
    // */
    // @Bean
    // public RequestInterceptor headerInterceptor() {
    // _log.info("传递接收到的请求头");
    // return requestTemplate -> {
    // final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    // if (attributes == null) {
    // return;
    // }
    // final HttpServletRequest request = attributes.getRequest();
    // final Enumeration<String> headerNames = request.getHeaderNames();
    // if (headerNames != null) {
    // while (headerNames.hasMoreElements()) {
    // final String name = headerNames.nextElement();
    // final String values = request.getHeader(name);
    // requestTemplate.header(name, values);
    // }
    // }
    // };
    // }
    //
    // @Autowired(required = false)
    // private final List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();
    //
    // @Bean
    // public Contract feignContract(final FormattingConversionService feignConversionService) {
    // // 在原配置类中是用ConversionService类型的参数，但ConversionService接口不支持addConverter操作，使用FormattingConversionService仍然可以实现feignContract配置。
    // feignConversionService.addConverter(new EnumConverter());
    // return new SpringMvcContract(parameterProcessors, feignConversionService);
    // }

}
