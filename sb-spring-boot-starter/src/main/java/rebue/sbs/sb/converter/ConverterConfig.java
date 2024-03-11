package rebue.sbs.sb.converter;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * 初始化转换器
 */
@Configuration(proxyBeanMethods = false)
@EnableWebFlux
public class ConverterConfig implements WebFluxConfigurer {

    /**
     * 添加自定义的Converters和Formatters.
     */
    @Override
    public void addFormatters(final FormatterRegistry registry) {
        // 暂时去掉，目前只在json环境下，没有用到这个转换器，没法测试
        // registry.addConverter(new StringToLongConverter());
        registry.addConverterFactory(new EnumConverterFactory());
    }

}
