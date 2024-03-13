package rebue.sbs.sb.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import rebue.wheel.api.dic.Dic;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * <pre>
 * 枚举类型的转换工厂
 * 负责在Spring MVC中将接收到的记录数字的字符串类型参数，转换为数字对应的枚举类型参数
 *
 * 注意：
 *   ConverterFactory可以将原类型转换为实现了一个相同接口的一组类，而Converter只能转换一种具体的类
 * </pre>
 */
@Slf4j
public class EnumConverterFactory implements ConverterFactory<String, Dic> {

    private final static Map<Class<?>, Converter<String, ?>> converterMap = new WeakHashMap<>();

    /**
     * 得到指定类的转换器
     *
     * @param targetType 实现了BaseEnum接口的类
     */
    @Override
    public <T extends Dic> Converter<String, T> getConverter(final Class<T> targetType) {
        log.info("获取IntegerStr转枚举类型的转换器：targetType={}", targetType.getName());
        @SuppressWarnings("unchecked")
        Converter<String, T> result = (Converter<String, T>) converterMap.get(targetType);
        if (result == null) {
            result = new IntegerStrToEnumConverter<>(targetType);
            converterMap.put(targetType, result);
        }
        return result;
    }

    /**
     * 将数字的字符串类型，转换为数字对应的枚举类型
     *
     * @param <T>
     */
    private static class IntegerStrToEnumConverter<T extends Dic> implements Converter<String, T> {
        private final Map<String, T> enumMap = new HashMap<>();

        /**
         * 构造方法 - 将所有的枚举项加入map中，以便转换的时候查找
         *
         * @param enumType 枚举类型
         */
        public IntegerStrToEnumConverter(final Class<T> enumType) {
            log.info("将枚举类型的所有项放入map中：enumType={}", enumType.getName());
            final T[] enums = enumType.getEnumConstants();
            for (final T enu : enums) {
                log.info("{}:{}({})", enu.getCode(), enu.getName(), enu.getDesc());
                enumMap.put(enu.getCode() + "", enu);
            }
        }

        @Override
        public T convert(final String source) {
            log.info("将{}转成枚举类型", source);
            final T result = enumMap.get(source);
            if (result == null) {
                final String  msg   = "枚举类型中没有对应" + source + "的项";
                StringBuilder sTemp = new StringBuilder();
                for (final String key : enumMap.keySet()) {
                    sTemp.append(key).append(",");
                }
                // 去掉最后的那个逗号
                sTemp = new StringBuilder(sTemp.substring(0, sTemp.length() - 1));
                log.error(msg + ": 枚举类型有{}", sTemp);
                throw new IllegalArgumentException(msg);
            }
            log.info("{}转成枚举类型{}的{}", source, result.getClass().getSimpleName(), ((Enum<?>) result).name());
            return result;
        }
    }

}
