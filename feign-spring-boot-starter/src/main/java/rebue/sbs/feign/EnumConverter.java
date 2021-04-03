package rebue.sbs.feign;

import org.springframework.core.convert.converter.Converter;

import rebue.robotech.dic.Dic;

public class EnumConverter implements Converter<Dic, String> {

    @Override
    public String convert(final Dic source) {
        return String.valueOf(source.getCode());
    }
}
