package rebue.sbs.cache;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class FstRedisSerializer implements RedisSerializer<Object> {

    private static FSTConfiguration configuration;

    public FstRedisSerializer() {
        configuration = FSTConfiguration.getDefaultConfiguration();
        configuration.setClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public byte[] serialize(final Object object) throws SerializationException {
        if (null == object) {
            return null;
        }
        return configuration.asByteArray(object);
    }

    @Override
    public Object deserialize(final byte[] bytes) throws SerializationException {
        if (null == bytes) {
            return null;
        }
        return configuration.asObject(bytes);
    }

}
