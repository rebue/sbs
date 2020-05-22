package rebue.sbs.smx;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 初始化StringToLong转换器
 * 
 * @deprecated 暂时去掉，目前只在json环境下，没有用到这个转换器，没法测试
 */
@Deprecated
@Configuration
public class StringToLongConfig extends WebMvcConfigurationSupport {

    /**
     * 添加自定义的Converters和Formatters.
     */
    @Override
    protected void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(new StringToLongConverter());
    }
}
