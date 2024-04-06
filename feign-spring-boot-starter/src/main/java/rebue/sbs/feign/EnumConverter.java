package rebue.sbs.feign;

//import org.springframework.core.convert.converter.Converter;
import rebue.wheel.api.dic.Dic;


/**
 * 升级版本后不知是否还需要使用
 *
 */
@Deprecated
public class EnumConverter {//implements Converter<Dic, String> {

//    @Override
    public String convert(final Dic source) {
        return String.valueOf(source.getCode());
    }
}
