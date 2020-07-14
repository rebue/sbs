package rebue.sbs.cfg;

import java.io.UnsupportedEncodingException;

import org.springframework.core.convert.converter.Converter;

/**
 * 字符串转byte[]的转换器
 * 
 * 用于读取.properties或yaml文件里的配置项，如果是字符串的，而接收类型是byte[]的，则用到此转换器
 * 此转换器将会按utf-8编码格式转成byte[]
 * 
 * @author zbz
 *
 */
public class StringToByteArrayConverter implements Converter<String, byte[]> {

    @Override
    public byte[] convert(final String source) {
        try {
            return source.getBytes("utf-8");
        } catch (final UnsupportedEncodingException e) {
            assert false;
            return null;
        }
    }

}
