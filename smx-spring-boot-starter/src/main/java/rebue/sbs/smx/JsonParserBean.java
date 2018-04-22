package rebue.sbs.smx;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化JsonParserBean
 * 
 * @author zbz
 *
 */
@Configuration
public class JsonParserBean {

    @Bean
    public JsonParser getJsonParser() {
        return JsonParserFactory.getJsonParser();
    }

}
