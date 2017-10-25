package com.zboss.sbs.smx.enumconvert;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;



/**
 * FastJson自动化配置
 *
 * @author Storezhang
 */
@Configuration
@ConditionalOnWebApplication
public class FastJsonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter.class)
    public HttpMessageConverters customConverters() {
        // 定义一个转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        // 配置支持的MediaTypes，不配置会报错:'Content-Type' cannot contain wildcard type
        // '*'
        // 因为fastjson默认MediaType.ALL，也就会是*/*
        fastConverter.setSupportedMediaTypes(Arrays.asList(//
                new MediaType("application", "json", Charset.forName("utf-8")),//
                new MediaType("application", "*+json", Charset.forName("utf-8"))));

        // 添加fastjson的配置信息 比如 ：是否要格式化返回的json数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//            fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteEnumUsingToString);
        JSON.DEFAULT_GENERATE_FEATURE &= ~SerializerFeature.WriteEnumUsingName.mask;

//        fastJsonConfig.getSerializeConfig().put(BaseEnum.class, new EnumSerializer());

        fastJsonConfig.getParserConfig().putDeserializer(Enum.class, new EnumExDeserializer());
        fastJsonConfig.getParserConfig().putDeserializer(BaseEnum.class, new EnumExDeserializer());

        // 在转换器中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);

        HttpMessageConverter<?> converter = fastConverter;

        return new HttpMessageConverters(converter);
    }

//        @Bean
//        @ConditionalOnMissingBean(FastJsonHttpMessageConverter.class)
//        public FastJsonHttpMessageConverter converter() {
//            return new FastJsonHttpMessageConverter();
//        }
}
