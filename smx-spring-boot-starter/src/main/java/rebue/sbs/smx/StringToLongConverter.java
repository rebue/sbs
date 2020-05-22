package rebue.sbs.smx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * String转换Long类型
 * 
 * 在SpringMvc接收参数时，会将参数按约定映射到默认的对象属性中
 * 这个类处理String类型的参数转成Long类型的属性
 * 因为js的number型范围承受不了Long类型的长度，所以只能作为字符串进行处理
 * 所以转换时值带着双引号，转Long时要将其去除
 * 
 * @deprecated 暂时去掉，目前只在json环境下，没有用到这个转换器，没法测试
 *
 */
@Deprecated
public class StringToLongConverter implements Converter<String, Long> {
    private final static Logger _log = LoggerFactory.getLogger(StringToLongConfig.class);

    @Override
    public Long convert(String source) {
        _log.debug("String转Long类型: {}", source);

        if (source.charAt(0) == '\"' && source.charAt(source.length() - 1) == '\"') {
            source = source.substring(1, source.length() - 1);
        }
        return Long.valueOf(source);
    }

}
