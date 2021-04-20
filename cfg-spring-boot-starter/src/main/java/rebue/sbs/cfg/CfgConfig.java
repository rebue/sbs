package rebue.sbs.cfg;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CfgConfig {

    @Bean
    @ConfigurationPropertiesBinding
    public StringToByteArrayConverter getStringToByteArrayConverter() {
        return new StringToByteArrayConverter();
    }
}
