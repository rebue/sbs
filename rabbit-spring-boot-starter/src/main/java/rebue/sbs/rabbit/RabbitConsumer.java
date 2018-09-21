package rebue.sbs.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import rebue.wheel.protostuff.ProtostuffUtils;

public class RabbitConsumer {
    private final static Logger _log = LoggerFactory.getLogger(RabbitConsumer.class);

    private Connection          _connection;

    public RabbitConsumer(RabbitProperties properties) throws IOException, TimeoutException {
        // 获取连接
        _connection = RabbitConnectionFactory.newConnection(properties);
    }

    public <T> boolean bind(String exchangeName, String queueName, Class<T> msgClazz, RabbitMsgHandler<T> handler) {
        _log.info("创建消费者: Exchange-{},Queue-{}", exchangeName, queueName);
        try {
            Channel channel = _connection.createChannel();
            // 声明exchange和队列，主要为了防止消费者先运行此程序，exchange和队列还不存在时可创建exchange和队列。
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
            channel.queueDeclare(queueName, true, false, false, null);
            // 分列模式fanout在绑定时不用指定bindKey，因为要分发给所有队列
            channel.queueBind(queueName, exchangeName, "");
            // TODO : RabbitMqX : 设置批量接收处理消息的大小
//            channel.basicQos(20);
            // 指定消费队列 ,第二个参数false表示不自动应答
            channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
                    try {
                        T msg = ProtostuffUtils.deserialize(body, msgClazz);
                        _log.info("接收到RabbitMQ传来的消息: Message={},Exchange={},Queue={},ConsumerTag={},envelope={}", msg, exchangeName, queueName, consumerTag, envelope);
                        if (handler.handle(msg)) {
                            // 应答ACK（false表示仅仅应答当前的这条消息）
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        } else {
                            // 应答NACK，消息重新入列
                            channel.basicReject(envelope.getDeliveryTag(), true);
                        }
                    } catch (IOException e) {
                        _log.error("RabbitMQ出现IO异常，有可能是网络被断开", e);
                    }
                }
            });
            return true;
        } catch (IOException e) {
            _log.error("RabbitMQ出现IO异常，有可能是网络被断开", e);
            return false;
        }
    }

}
