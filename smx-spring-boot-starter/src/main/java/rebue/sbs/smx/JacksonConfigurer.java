package rebue.sbs.smx;

import java.util.Arrays;
import java.util.List;

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
 * 初始化Jackson的转换器
 * 1. 让spring mvc支持json格式，并且统一编码为utf-8
 * 2. 不显示为null的字段
 * 3. Long to String
 * 
 * @author zbz
 *
 */
@Configuration
public class JacksonConfigurer implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
        // 让spring mvc支持text/json/xml几种格式，并且统一编码为utf-8
        httpMessageConverter.setSupportedMediaTypes(Arrays.asList(//
                // new MediaType(MediaType.TEXT_HTML, Charset.forName("utf-8")),//
                // new MediaType(MediaType.TEXT_PLAIN, Charset.forName("utf-8")),//
                MediaType.APPLICATION_JSON//
//                MediaType.APPLICATION_JSON_UTF8//
//                new MediaType(MediaType.APPLICATION_XML, Charset.forName("utf-8")),//
//                new MediaType(MediaType.MULTIPART_FORM_DATA, Charset.forName("utf-8")),//
//                new MediaType(MediaType.APPLICATION_FORM_URLENCODED, Charset.forName("utf-8"))//
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
        // 加到第一个位置
        converters.add(0, httpMessageConverter);
    }

}
