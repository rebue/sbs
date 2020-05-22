package rebue.sbs.smx;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 处理字符串转Long，当字符串有双引号时智能去除
 * 
 * @deprecated 暂时不用，实在想不起来什么情况会带双引号
 */
@Deprecated
public class ToLongDeserializer extends JsonDeserializer<Long> {
    /**
     * Singleton instance to use.
     */
    public final static ToLongDeserializer instance = new ToLongDeserializer();

    @Override
    public Long deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        String value = jsonParser.getText();
        if (StringUtils.isBlank(value)) {
            return null;
        }

        if (value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"') {
            value = value.substring(1, value.length() - 1);
        }

        return Long.valueOf(value);
    }

}
