package rebue.sbs.smx;

import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
public class JacksonConfig implements WebMvcConfigurer {

    /**
     * Parser that can read JSON formatted strings into Maps or Lists.
     * 可以用来读取JSON字符串并解析到Map或List
     */
    @Bean
    public JsonParser getJsonParser() {
        return JsonParserFactory.getJsonParser();
    }

    /**
     * 可以用来对JSON字符串与POJO对象进行相互转换
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        return Jackson2ObjectMapperBuilder.json().serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .timeZone(TimeZone.getTimeZone("Asia/Shanghai")).modules(new JavaTimeModule()).build();
    }

    /**
     * 自定义Jackson序列化时
     * 
     * @return Jackson2ObjectMapperBuilderCustomizer 注入的对象
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                // 不转换值为null的项
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                // 全局转化long类型为String，避免js用number接收long类型时丢失精度问题
                // TODO 未检验改成字符串后，Feign用Long接收会不会出问题
                .serializerByType(Long.TYPE, ToStringSerializer.instance)
                .serializerByType(Long.class, ToStringSerializer.instance)
//                // 将前端传过来的String反序列化Long时，如果有引号则去除(记不起应用场景，暂时去除)
//                .deserializerByType(Long.class, ToLongDeserializer.instance)
        ;
    }

}
