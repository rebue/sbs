package rebue.sbs.sb;

import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * 初始化Jackson的转换器 1. 让spring mvc支持json格式，并且统一编码为utf-8 2. 不显示为null的字段 3. Long to String
 *
 * @author zbz
 */
@Configuration(proxyBeanMethods = false)
public class JacksonConfig {
    /**
     * Parser that can read JSON formatted strings into Maps or Lists. 可以用来读取JSON字符串并解析到Map或List
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
        return setJackson2ObjectMapperBuilder(Jackson2ObjectMapperBuilder.json()).build();
    }

    /**
     * 自定义Jackson序列化时
     *
     * @return Jackson2ObjectMapperBuilderCustomizer 注入的对象
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return this::setJackson2ObjectMapperBuilder;
    }

    private Jackson2ObjectMapperBuilder setJackson2ObjectMapperBuilder(final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        // TODO 下面注释起来的地方是暂时没有条件测试的，等出现了情况或有了需求再打开注释进行测试
        return jackson2ObjectMapperBuilder//
            .featuresToEnable(
                // 反序列化时忽略大小写
                MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES
            // // 序列化BigDecimal时不使用科学计数法输出
            // JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN
            )
            .featuresToDisable(
                // 序列化时不按默认的时间格式'yyyy-MM-dd'T'HH:mm:ss.SSS’转换(按JavaTimeModule设置的格式)
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
            // // 对于空的对象转json的时候不抛出错误
            // SerializationFeature.FAIL_ON_EMPTY_BEANS,
            // // 禁用遇到未知属性抛出异常
            // DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
            )
            // 不转换值为null的项
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            // 全局转化long类型为String，避免js用number接收long类型时丢失精度问题
            .serializerByType(Long.TYPE, ToStringSerializer.instance)//
            .serializerByType(Long.class, ToStringSerializer.instance)
            .timeZone(TimeZone.getTimeZone("Asia/Shanghai"))//
            // 全局支持Java8的时间格式化
            .modules(new ParameterNamesModule())  //
            .modules(new Jdk8Module())  //
            .modules(new JavaTimeModule())   //
        ;
    }

}
