package rebue.sbs.feign;

import org.springframework.core.convert.converter.Converter;

import rebue.robotech.dic.EnumBase;

public class EnumConverter implements Converter<EnumBase, String> {

    @Override
    public String convert(final EnumBase source) {
        return String.valueOf(source.getCode());
    }
}
