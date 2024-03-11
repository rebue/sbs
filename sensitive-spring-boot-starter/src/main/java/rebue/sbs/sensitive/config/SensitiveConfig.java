package rebue.sbs.sensitive.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rebue.sbs.sensitive.plugin.DesensitizePlugin;

/**
 * 敏感数据处理的配置器
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("${rebue.sbs.sensitive.enabled:true}")
public class SensitiveConfig {

    @Bean
    public DesensitizePlugin sensitivePlugin() {
        return new DesensitizePlugin();
    }
}
