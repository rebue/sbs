package rebue.sbs.cfg;

import java.io.UnsupportedEncodingException;

import org.springframework.core.convert.converter.Converter;

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
