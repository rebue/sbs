/**
 * 扩展DefaultRedisCacheWriter，扩展内容如下:
 * 1. 实现可指定key设置过期时间(在key中末尾加上?TTL=xxx)
 */
package rebue.sbs.cache;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.cache.DefaultRedisCacheWriterCopy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.Assert;

import rebue.wheel.serialization.kryo.KryoUtils;

public class RebueRedisCacheWriter extends DefaultRedisCacheWriterCopy {

    RebueRedisCacheWriter(final RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public Set<byte[]> keys(final String name) {
        Assert.notNull(name, "Name must not be null!");
        return execute(name, connection -> connection.keys("*".getBytes()));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listAll(final String cacheName) {
        return keys(cacheName).stream().map(key -> ((T) KryoUtils.readObject(get(cacheName, key)))).collect(Collectors.toList());
    }

    @Override
    public void put(final String name, byte[] key, final byte[] value, Duration ttl) {
        System.out.println(new String(key, Charset.forName("UTF-8")) + ":" + Arrays.toString(value));
        final String[] keySplit = new String(key, Charset.forName("UTF-8")).split("\\?");
        if (keySplit.length == 2) {
            key = keySplit[0].getBytes();
            final String[] params = keySplit[0].split("&");
            for (final String param : params) {
                final String[] kv = param.split("=");
                if (kv[0].equalsIgnoreCase("TTL")) {
                    ttl = Duration.parse(kv[1]);
                }
            }
        }
        super.put(name, key, value, ttl);
    }

    @Override
    public byte[] get(final String name, byte[] key) {
        final String[] keySplit = new String(key, Charset.forName("UTF-8")).split("\\?");
        if (keySplit.length == 2) {
            key = keySplit[0].getBytes();
        }
        return super.get(name, key);
    }

    @Override
    public byte[] putIfAbsent(final String name, byte[] key, final byte[] value, Duration ttl) {
        final String[] keySplit = new String(key, Charset.forName("UTF-8")).split("\\?");
        if (keySplit.length == 2) {
            key = keySplit[0].getBytes();
            final String[] params = keySplit[0].split("&");
            for (final String param : params) {
                final String[] kv = param.split("=");
                if (kv[0].equalsIgnoreCase("TTL")) {
                    ttl = Duration.parse(kv[1]);
                }
            }
        }
        return super.putIfAbsent(name, key, value, ttl);
    }

}
