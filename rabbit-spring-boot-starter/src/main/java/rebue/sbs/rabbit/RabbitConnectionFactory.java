package rebue.sbs.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitConnectionFactory {
    private final static Logger _log = LoggerFactory.getLogger(ConnectionFactory.class);

    public static Connection newConnection(RabbitProperties properties) throws IOException, TimeoutException {
        _log.info("新建RabbitMQ的连接: {}", properties);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setVirtualHost(properties.getVirtualHost());
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());

        // 设置网络断开自动恢复重连
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // 设置不重新声明交换器，队列等信息。
        connectionFactory.setTopologyRecoveryEnabled(false);

        // XXX : RabbitSpringBootStarter : 这里不指定消费者线程池大小，默认cpu核心*2，正常已经够用了
        return connectionFactory.newConnection();
    }

}
