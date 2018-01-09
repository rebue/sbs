package rebue.sbs.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    RabbitProducer getProducer(RabbitProperties properties) throws IOException, TimeoutException {
        return new RabbitProducer(properties);
    }

    @Bean
    RabbitConsumer getConsumer(RabbitProperties properties) throws IOException, TimeoutException {
        return new RabbitConsumer(properties);
    }
}
