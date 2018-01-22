package rebue.sbs.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 创建池化的生产者的Channel对象的工厂
 */
public class RabbitPooledProducerChannelFactory extends BasePooledObjectFactory<Channel> {

    private Connection _connection;

    public RabbitPooledProducerChannelFactory(Connection connection) throws IOException, TimeoutException {
        _connection = connection;
    }

    @Override
    public Channel create() throws Exception {
        Thread.sleep(1);    // 让出cpu占用，以防高并发下大批量创建对象时卡死
        Channel channel = _connection.createChannel();
        channel.confirmSelect();
        return channel;
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<Channel>(channel);
    }

    /**
     * 销毁对象前先关闭频道
     */
    @Override
    public void destroyObject(PooledObject<Channel> poolObject) throws Exception {
        poolObject.getObject().close();
    }

}
