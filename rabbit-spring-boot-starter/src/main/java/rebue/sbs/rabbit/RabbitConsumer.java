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

    private final Connection    _connection;

    public RabbitConsumer(final RabbitProperties properties) throws IOException, TimeoutException {
        // 获取连接
        _connection = RabbitConnectionFactory.newConnection(properties);
    }

    /**
     * 绑定交换机和队列(默认使用Fanout模式)
     *
     * @param <T>          消息的泛型类
     * @param exchangeName 交换机名称
     * @param queueName    队列名称
     * @param msgClazz     消息的类
     * @param handler      处理接收消息的方法
     *
     * @return 返回是否绑定成功
     */
    public <T> boolean bind(final String exchangeName, final String queueName, final Class<T> msgClazz, final RabbitMsgHandler<T> handler) {
        // 分列模式fanout在绑定时不用指定bindKey，因为要分发给所有队列，指定了也无效
        return bind(exchangeName, queueName, "", msgClazz, handler, BuiltinExchangeType.FANOUT);
    }

    /**
     * 绑定交换机和队列
     *
     * @param <T>          消息的泛型类
     * @param exchangeName 交换机名称
     * @param queueName    队列名称
     * @param routingKey   路由键
     * @param msgClazz     消息的类
     * @param handler      处理接收消息的方法
     *
     * @return 返回是否绑定成功
     */
    public <T> boolean bind(final String exchangeName, final String queueName, final String routingKey, final Class<T> msgClazz, final RabbitMsgHandler<T> handler,
                            final BuiltinExchangeType builtinExchangeType) {
        _log.info("创建消费者: Exchange-{},Queue-{}", exchangeName, queueName);
        try {
            final Channel channel = _connection.createChannel();
            // 声明exchange和队列，主要为了防止消费者先运行此程序，exchange和队列还不存在时可创建exchange和队列。
            channel.exchangeDeclare(exchangeName, builtinExchangeType, true);
            // exclusive queue会限制连接（只对首次声明它的连接（Connection）可见;会在其连接断开的时候自动删除）
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey);
            // TODO : RabbitMqX : 设置批量接收处理消息的大小
            // channel.basicQos(20);
            // 指定消费队列 ,autoAck为false表示不自动应答
            channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(final String consumerTag, final Envelope envelope, final BasicProperties properties, final byte[] body) {
                    try {
                        final T msg = ProtostuffUtils.deserialize(body, msgClazz);
                        _log.info("接收到RabbitMQ传来的消息: Message={},Exchange={},Queue={},ConsumerTag={},envelope={}", msg, exchangeName, queueName, consumerTag, envelope);
                        if (handler.handle(msg)) {
                            // 应答ACK（false表示仅仅应答当前的这条消息）
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        }
                        else {
                            // 应答NACK，消息重新入列
                            channel.basicReject(envelope.getDeliveryTag(), true);
                        }
                    } catch (final IOException e) {
                        _log.error("RabbitMQ出现IO异常，有可能是网络被断开", e);
                    }
                }
            });
            return true;
        } catch (final IOException e) {
            _log.error("RabbitMQ出现IO异常，有可能是网络被断开", e);
            return false;
        }
    }

}
