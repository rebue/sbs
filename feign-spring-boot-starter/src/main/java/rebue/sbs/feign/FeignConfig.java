package rebue.sbs.feign;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Logger;
import feign.codec.Decoder;

@Configuration
public class FeignConfig {

    /**
     * 只有在这里才能控制feign的日志级别
     */
    @Bean
    Logger.Level feignLoggerLevel() {
//        return Logger.Level.FULL;
        return Logger.Level.BASIC;
    }

//    @Bean
//    public Decoder feignDecoder() {
//        HttpMessageConverter<?> jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
//        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
//        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
//    }
//
//    private ObjectMapper customObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        // Customize as much as you want
//        return objectMapper;
//    }

}
