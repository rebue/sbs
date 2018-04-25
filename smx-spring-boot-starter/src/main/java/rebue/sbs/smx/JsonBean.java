package rebue.sbs.smx;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 初始化JsonParserBean
 * 
 * @author zbz
 *
 */
@Configuration
public class JsonBean {

    @Bean
    public JsonParser getJsonParser() {
        return JsonParserFactory.getJsonParser();
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

}
