package rebue.sbs.smx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化StringToLong转换器
 */
@Configuration
public class StringToLongConfigurer {

    // 此方法位于一个有@Configuration注解的类中
    @Bean
    public StringToLongConverter getStringToLongConverter() {
        return new StringToLongConverter();
    }
}
