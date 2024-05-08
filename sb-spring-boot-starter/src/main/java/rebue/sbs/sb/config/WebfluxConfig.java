package rebue.sbs.sb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration(proxyBeanMethods = false)
public class WebfluxConfig implements WebFluxConfigurer {
    private final ObjectMapper objectMapper;

    @Value("${spring.mvc.static-path-pattern:#{null}}")
    private       String   staticPathPattern;
    private final String[] staticLocations;

    @Autowired
    public WebfluxConfig(ObjectMapper objectMapper, WebProperties webProperties) {
        this.objectMapper = objectMapper;
        this.staticLocations = webProperties.getResources().getStaticLocations();
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (staticPathPattern != null && staticLocations != null) {
            registry.addResourceHandler(staticPathPattern)
                    .addResourceLocations(staticLocations);
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebFluxConfigurer.super.addCorsMappings(registry);
    }
}
