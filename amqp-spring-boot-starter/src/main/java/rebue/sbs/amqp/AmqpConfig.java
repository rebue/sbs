package rebue.sbs.amqp;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;
import rebue.wheel.serialization.fst.FstAmqpMessageConverter;
import rebue.wheel.serialization.fst.FstUtils;
import rebue.wheel.serialization.kryo.KryoAmqpMessageConverter;
import rebue.wheel.serialization.kryo.KryoUtils;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ RabbitTemplate.class, Channel.class
})
@EnableConfigurationProperties(RabbitProperties.class)
@Import(RabbitAnnotationDrivenConfiguration.class)
public class AmqpConfig {
    @Bean
    public MessageConverter messageConverter() {
        // return new FstAmqpMessageConverter();
        return new KryoAmqpMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final RabbitTemplateConfigurer configurer, final ConnectionFactory connectionFactory, final MessageConverter messageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate();
        configurer.configure(rabbitTemplate, connectionFactory);

        // 设置消息转换器
        rabbitTemplate.setMessageConverter(messageConverter);

        // 设置处理消息发送不到队列的回调函数
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String sMessageBody;
            if (message == null || message.getBody() == null || message.getBody().length == 0) {
                sMessageBody = "";
            }
            else {
                try {
                    switch (message.getMessageProperties().getContentType()) {
                    case MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT:
                        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.getBody());
                            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                            sMessageBody = objectInputStream.readObject().toString();
                        }
                        break;
                    case FstAmqpMessageConverter.CONTENT_TYPE_FST:
                        sMessageBody = FstUtils.readObject(message.getBody()).toString();
                        break;
                    case KryoAmqpMessageConverter.CONTENT_TYPE_KRYO:
                        sMessageBody = KryoUtils.readObject(message.getBody()).toString();
                        break;
                    default:
                        sMessageBody = "不能识别的序列化方式";
                        break;
                    }
                } catch (final Exception e) {
                    sMessageBody = "反序列化失败-" + e.getMessage();
                }
            }

            final int          rightPadLen = 80;
            final StringJoiner sj          = new StringJoiner("\r\n");
            sj.add("消息未能发送到队列!!!!!!!!!");
            sj.add("****************************************** 未能发送的消息详情 *******************************************");
            sj.add("* exchange           : " + StringUtils.rightPad(exchange, rightPadLen));
            sj.add("* routing key        : " + StringUtils.rightPad(routingKey, rightPadLen));
            sj.add("* reply code         : " + StringUtils.rightPad(String.valueOf(replyCode), rightPadLen));
            sj.add("* reply text         : " + StringUtils.rightPad(replyText, rightPadLen));
            sj.add("* message properties : " + StringUtils.rightPad(message.getMessageProperties().toString(), rightPadLen));
            sj.add("* message body       : " + StringUtils.rightPad(sMessageBody, rightPadLen));
            sj.add("********************************************************************************************************");
            sj.add("");
            log.error(sj.toString());

            // TODO 发邮件
        });
        return rabbitTemplate;
    }

}
