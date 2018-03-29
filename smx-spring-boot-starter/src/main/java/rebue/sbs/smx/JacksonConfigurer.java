package rebue.sbs.smx;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 初始化Jackson的配置
 * 1. 让spring mvc支持text/json/xml几种格式，并且统一编码为utf-8
 * 2. 不显示为null的字段
 * 3. Long to String
 * 
 * @author zbz
 *
 */
@Configuration
public class JacksonConfigurer implements WebMvcConfigurer {

    @Bean
    public JsonParser getJsonParser() {
        return JsonParserFactory.getJsonParser();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
        // 让spring mvc支持text/json/xml几种格式，并且统一编码为utf-8
        httpMessageConverter.setSupportedMediaTypes(Arrays.asList(//
                new MediaType("text", "plain", Charset.forName("utf-8")), //
                new MediaType("application", "json", Charset.forName("utf-8")), //
                new MediaType("application", "xml", Charset.forName("utf-8")),//
                new MediaType("multipart", "form-data", Charset.forName("utf-8")),//
                new MediaType("application", "x-www-from-urlencoded", Charset.forName("utf-8"))//
        ));

        ObjectMapper objectMapper = httpMessageConverter.getObjectMapper();
        // 不显示为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Long to String
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        httpMessageConverter.setObjectMapper(objectMapper);
        // 放到第一个
        converters.add(0, httpMessageConverter);
    }

}
