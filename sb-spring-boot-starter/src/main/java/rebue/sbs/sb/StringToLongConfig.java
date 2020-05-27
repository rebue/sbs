package rebue.sbs.sb;

import org.springframework.context.annotation.Configuration;

/**
 * 初始化StringToLong转换器
 * 
 * @deprecated 暂时去掉，目前只在json环境下，没有用到这个转换器，没法测试
 */
@Deprecated
@Configuration
public class StringToLongConfig {// extends WebMvcConfigurationSupport {

    /**
     * 添加自定义的Converters和Formatters.
     */
//    @Override
//    protected void addFormatters(final FormatterRegistry registry) {
//        registry.addConverter(new StringToLongConverter());
//    }
}
