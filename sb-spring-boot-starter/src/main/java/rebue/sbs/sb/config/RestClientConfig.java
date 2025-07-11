package rebue.sbs.sb.config;

import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * RestClient配置
 * 
 * @Deprecated 2025-07-11 弃用，请使用RestClient.builder
 */
@Configuration(proxyBeanMethods = false)
@Deprecated(since = "2025-07-11")
public class RestClientConfig {
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);// 单位为ms
        factory.setConnectTimeout(5000);// 单位为ms
        return factory;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        // 支持中文编码
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;

    }

    @Bean
    public RestClient restClient(RestTemplate restTemplate) {
        return RestClient.create(restTemplate);
    }

    @Bean
    public RestClient.Builder restClientBuilder(RestTemplate restTemplate) {
        return RestClient.builder(restTemplate);
    }

    @Bean
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }
}
