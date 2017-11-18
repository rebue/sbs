package rebue.sbs.smx;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * HttpMessageConverter使用Jackson自动化配置<br>
 * 主要让spring mvc支持text/json/xml几种格式，并且统一编码为utf-8
 *
 * @author zbz
 */
@Configuration
@ConditionalOnWebApplication
public class JacksonHttpMessageConverterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MappingJackson2HttpMessageConverter.class)
    public HttpMessageConverters customConverters() {
        // 让spring mvc支持text/json/xml几种格式，并且统一编码为utf-8
        MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
        httpMessageConverter.setSupportedMediaTypes(Arrays.asList(//
                new MediaType("text", "plain", Charset.forName("utf-8")), //
                new MediaType("application", "json", Charset.forName("utf-8")), //
                new MediaType("application", "xml", Charset.forName("utf-8"))//
        ));

        return new HttpMessageConverters(httpMessageConverter);
    }

}
