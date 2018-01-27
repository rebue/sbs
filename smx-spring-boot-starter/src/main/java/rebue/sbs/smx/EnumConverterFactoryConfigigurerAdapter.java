package rebue.sbs.smx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 自定义枚举转换工厂的配置适配器<br>
 * 主要让Spring MVC支持将枚举类型与整形进行互相转换(枚举类型要求必须实现EnumBase接口)
 * 
 * @author zbz
 *
 */
@Configuration
@ConditionalOnWebApplication
public class EnumConverterFactoryConfigigurerAdapter implements WebMvcConfigurer {
//public class EnumConverterFactoryConfigigurerAdapter extends WebMvcConfigurerAdapter {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumConverterFactory());
    }

}
