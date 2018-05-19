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
    private final static Logger        _log = LoggerFactory.getLogger(ConnectionFactory.class);

    private GenericObjectPool<Channel> _channelPool;

    public RabbitProducer(RabbitProperties properties) throws IOException, TimeoutException {
        // 获取连接
        Connection connection = RabbitConnectionFactory.newConnection(properties);

        // Channel池配置
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        // 配置Channel池保持最小空闲对象的数量
        config.setMinIdle(Runtime.getRuntime().availableProcessors());
        // 配置Channel池保持最大对象的数量
        config.setMaxTotal(properties.getChannelMaxTotal());

        // 创建Channel工厂
        RabbitPooledProducerChannelFactory factory = new RabbitPooledProducerChannelFactory(connection);
        _channelPool = new GenericObjectPool<>(factory, config);

    }

    /**
     * 声明Exchange
     */
    public void declareExchange(String exchangeName) throws Exception {
        _log.info("RabbitMQ声明Exchange: {}", exchangeName);
        // 初始化exchange
        Channel channel = _channelPool.borrowObject();
        try {
            // 声明Exchange
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
        } finally {
            _channelPool.returnObject(channel);// 有借有还，再借不难
        }
    }

    public void send(String exchangeName, Object msg) {
        send(exchangeName, ProtostuffUtils.serialize(msg));
    }

    private void send(String exchangeName, byte[] msg) {
        _log.info("生产者发送消息: {} - {}", exchangeName, new String(msg));
        Channel channel = null;
        try {
            channel = _channelPool.borrowObject();
            channel.basicPublish(exchangeName, "", true, MessageProperties.PERSISTENT_BASIC, msg);
            if (!channel.waitForConfirms(10000)) {
                String errorMsg = ("生产者发送消息不成功");
                _log.error("{}: {} - {}", errorMsg, exchangeName, new String(msg));
                throw new RuntimeException(errorMsg);
            }
            _log.info("生产者发送消息成功: {} - {}", exchangeName, new String(msg));
        } catch (Exception e) {
            _log.error("生产者发送消息出现异常", e);
            throw new RuntimeException(e);
        } finally {
            if (channel != null)
                _channelPool.returnObject(channel);// 有借有还，再借不难
        }
    }

}
