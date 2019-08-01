package rebue.sbs.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import rebue.wheel.protostuff.ProtostuffUtils;

public class RabbitProducer {
    private final static Logger              _log = LoggerFactory.getLogger(ConnectionFactory.class);

    private final GenericObjectPool<Channel> _channelPool;

    /**
     * 默认发送消息超时判断的毫秒数(默认为10000毫秒)
     */
    private final Long                       _defaultSendTimeoutMs;

    public RabbitProducer(final RabbitProperties properties) throws IOException, TimeoutException {
        // 获取连接
        final Connection connection = RabbitConnectionFactory.newConnection(properties);

        // Channel池配置
        final GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
        // 配置Channel池保持最小空闲对象的数量
        config.setMinIdle(Runtime.getRuntime().availableProcessors());
        // 配置Channel池保持最大对象的数量
        config.setMaxTotal(properties.getChannelMaxTotal());
        // 默认发送消息超时判断的毫秒数(默认为10000毫秒)
        _defaultSendTimeoutMs = properties.getDefaultSendTimeoutMs();

        // 创建Channel工厂
        final RabbitPooledProducerChannelFactory factory = new RabbitPooledProducerChannelFactory(connection);
        _channelPool = new GenericObjectPool<>(factory, config);

    }

    /**
     * 声明Exchange
     */
    public void declareExchange(final String exchangeName) throws Exception {
        _log.info("RabbitMQ声明Exchange: {}", exchangeName);
        // 初始化exchange
        final Channel channel = _channelPool.borrowObject();
        try {
            // 声明Exchange
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
        } finally {
            _channelPool.returnObject(channel);// 有借有还，再借不难
        }
    }

    /**
     * 发送消息(如果超过配置的默认超时时间(不配置为10秒)，抛出运行时异常)
     * 如果想直接指定超时时间，请用 <b>send(String exchangeName, byte[] msg, Long timeoutMs)</b> 方法
     * 
     * @param exchangeName
     *            Exchage的名称
     * @param msg
     *            要发送的消息
     */
    public void send(final String exchangeName, final Object msg) {
        send(exchangeName, ProtostuffUtils.serialize(msg), _defaultSendTimeoutMs);
    }

    /**
     * 发送消息(超时会抛出RuntimeException)
     * 
     * @param exchangeName
     *            Exchage的名称
     * @param msg
     *            要发送的消息
     * @param timeoutMs
     *            判断超时的毫秒数(如果为0则永远不超时)
     */
    public void send(final String exchangeName, final Object msg, final Long timeoutMs) {
        send(exchangeName, ProtostuffUtils.serialize(msg), timeoutMs);
    }

    private void send(final String exchangeName, final byte[] msg, final Long timeoutMs) {
        _log.info("生产者发送消息: {} - {} - 超时: {}", exchangeName, new String(msg), timeoutMs);
        Channel channel = null;
        try {
            channel = _channelPool.borrowObject();
            channel.basicPublish(exchangeName, "", true, MessageProperties.PERSISTENT_BASIC, msg);
            if (!channel.waitForConfirms(timeoutMs)) {
                final String errorMsg = ("生产者发送消息不成功");
                _log.error("{}: {} - {}", errorMsg, exchangeName, new String(msg));
                throw new RuntimeException(errorMsg);
            }
            _log.info("生产者发送消息成功: {} - {}", exchangeName, new String(msg));
        } catch (final Exception e) {
            _log.error("生产者发送消息出现异常", e);
            throw new RuntimeException(e);
        } finally {
            if (channel != null) {
                _channelPool.returnObject(channel);// 有借有还，再借不难
            }
        }
    }

}
