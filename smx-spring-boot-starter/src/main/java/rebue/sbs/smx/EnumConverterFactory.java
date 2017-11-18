package rebue.sbs.smx;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import rebue.wheel.baseintf.EnumBase;

/**
 * <pre>
 * 枚举类型的转换工厂
 * 负责在Spring MVC中将接收到的记录数字的字符串类型参数，转换为数字对应的枚举类型参数
 * 
 * 注意：
 *   onverterFactory可以将原类型转换为实现了一个相同接口的一组类，而Converter只能转换一种具体的类
 * </pre>
 */
public class EnumConverterFactory implements ConverterFactory<String, EnumBase> {
    private final static Logger                              _log         = LoggerFactory
            .getLogger(EnumConverterFactory.class);

    private final static Map<Class<?>, Converter<String, ?>> converterMap = new WeakHashMap<>();

    /**
     * 得到指定类的转换器
     * 
     * @param targetType
     *            实现了BaseEnum接口的类
     */
    @Override
    public <T extends EnumBase> Converter<String, T> getConverter(Class<T> targetType) {
        _log.info("获取IntegerStr转枚举类型的转换器：targetType={}", targetType.getName());
        @SuppressWarnings("unchecked")
        Converter<String, T> result = (Converter<String, T>) converterMap.get(targetType);
        if (result == null) {
            result = new IntegerStrToEnumConverter<T>(targetType);
            converterMap.put(targetType, result);
        }
        return result;
    }

    /**
     * 将数字的字符串类型，转换为数字对应的枚举类型
     * 
     * @param <T>
     */
    private class IntegerStrToEnumConverter<T extends EnumBase> implements Converter<String, T> {
        private Map<String, T> enumMap = new HashMap<>();

        /**
         * 构造方法 - 将所有的枚举项加入map中，以便转换的时候查找
         * 
         * @param enumType
         */
        public IntegerStrToEnumConverter(Class<T> enumType) {
            _log.info("将枚举类型的所有项放入map中：enumType={}", enumType.getName());
            T[] enums = enumType.getEnumConstants();
            for (T e : enums) {
                _log.info("{}:{}", e.getCode(), e.getClass().getName());
                enumMap.put(e.getCode() + "", e);
            }
        }

        @Override
        public T convert(String source) {
            _log.info("将{}转成枚举类型", source);
            T result = enumMap.get(source);
            if (result == null) {
                String msg = "枚举类型中没有对应" + source + "的项";
                _log.error(msg);
                throw new IllegalArgumentException(msg);
            }
            _log.info("{}转成枚举类型{}的{}", source, result.getClass().getSimpleName(), ((Enum<?>) result).name());
            return result;
        }
    }

}
