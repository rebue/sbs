package rebue.sbs.smx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class StringToLongConverter implements Converter<String, Long> {
    private final static Logger _log = LoggerFactory.getLogger(StringToLongConfigurer.class);

    @Override
    public Long convert(String source) {
        _log.debug("String转Long类型: {}", source);

        if (source.charAt(0) == '\"' && source.charAt(source.length() - 1) == '\"') {
            source = source.substring(1, source.length() - 1);
        }
        return Long.valueOf(source);
    }

}
