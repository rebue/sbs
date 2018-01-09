package rebue.sbs.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

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

}
