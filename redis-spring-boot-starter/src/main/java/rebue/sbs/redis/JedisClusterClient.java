package rebue.sbs.redis;

import org.apache.commons.lang3.StringUtils;
import rebue.wheel.serialization.protostuff.ProtostuffUtils;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

import java.util.LinkedHashSet;
import java.util.Set;

public class JedisClusterClient implements RedisClient {

    private final JedisCluster _jedisCluster;

    public JedisClusterClient(final JedisCluster jedisCluster) {
        _jedisCluster = jedisCluster;
    }

    @Override
    public Boolean expire(final String key, final int seconds) {
        return _jedisCluster.expire(key, seconds) == 1 ? true : false;
    }

    @Override
    public Boolean exists(final String key) {
        return _jedisCluster.exists(key);
    }

    @Override
    public Boolean exists(final byte[] key) {
        return _jedisCluster.exists(key);
    }

    @Override
    public Long incr(final String key) {
        return _jedisCluster.incr(key);
    }

    @Override
    public Long incr(final String key, final int expireTime) {
        final Long result = _jedisCluster.incr(key);
        if (result == 1) {
            if (_jedisCluster.expire(key, expireTime) != 1) {
                throw new RedisSetException();
            }
        }
        return result;
    }

    @Override
    public void set(final String key, final String value) {
        final String result = _jedisCluster.set(key, value);
        if (!result.equals("OK")) {
            throw new RedisSetException();
        }
    }

    @Override
    public void set(final String key, final String value, final int expireTime) {
        final String result = _jedisCluster.setex(key, expireTime, value);
        if (!result.equals("OK")) {
            throw new RedisSetException();
        }
    }

    @Override
    public void setObj(final String key, final Object value) {
        final String result = _jedisCluster.set(key.getBytes(), ProtostuffUtils.serialize(value));
        if (!result.equals("OK")) {
            throw new RedisSetException();
        }
    }

    @Override
    public void setObj(final String key, final Object value, final int expireTime) {
        final String result = _jedisCluster.setex(key.getBytes(), expireTime, ProtostuffUtils.serialize(value));
        if (!result.equals("OK")) {
            throw new RedisSetException();
        }
    }

    @Override
    public String get(final String key) {
        final String result = _jedisCluster.get(key);
        return StringUtils.isBlank(result) || "nil".equalsIgnoreCase(result) ? null : result;
    }

    @Override
    public Long getLong(final String key) throws ClassCastException {
        String result = _jedisCluster.get(key);
        if (StringUtils.isBlank(result) || "nil".equalsIgnoreCase(result)) {
            return null;
        }
        result = result.replaceAll("\"", "");
        final Long longResult = Long.parseLong(result);
        return longResult;
    }

    @Override
    public byte[] get(final byte[] key) {
        return _jedisCluster.get(key);
    }

    @Override
    public String get(final String key, final int expireTime) {
        final String result = _jedisCluster.get(key);
        if (result != "" || result != "nil" || _jedisCluster.expire(key, expireTime) == 1) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public Long getLong(final String key, final int expireTime) {
        String result = _jedisCluster.get(key);
        if (StringUtils.isBlank(result) || "nil".equalsIgnoreCase(result) || _jedisCluster.expire(key, expireTime) != 1) {
            return null;
        }
        result = result.replaceAll("\"", "");
        final Long longResult = Long.parseLong(result);
        return longResult;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<T> listByWildcard(final String key, final Class<T> clazz) {
        final Set<T> result = new LinkedHashSet<>();
        for (final String item : _jedisCluster.keys(key)) {
            if ("java.lang.String".equals(clazz.getName())) {
                result.add((T) get(item));
            } else {
                result.add(getObj(item, clazz));
            }
        }
        return result;
    }

    @Override
    public String pop(final String key) {
        final String result = _jedisCluster.get(key);
        if (StringUtils.isBlank(result) || "nil".equalsIgnoreCase(result) || _jedisCluster.del(key) == 0) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public <T> T popObj(final String key, final Class<T> clazz) {
        final byte[] result = _jedisCluster.get(key.getBytes());
        if (result == null || _jedisCluster.del(key) == 0) {
            return null;
        } else {
            return ProtostuffUtils.deserialize(result, clazz);
        }
    }

    @Override
    public Long del(final String key) {
        return _jedisCluster.del(key);
    }

    @Override
    public void delByWildcard(final String key) {
        Set<String> keys = _jedisCluster.keys(key);
        _jedisCluster.del(keys.toArray(new String[keys.size()]));
    }

    @Override
    public Long sadd(final String key, final String... members) {
        return _jedisCluster.sadd(key, members);
    }

    @Override
    public String srandmember(final String key) {
        return _jedisCluster.srandmember(key);
    }

    @Override
    public Long publish(final String channel, final String message) {
        return _jedisCluster.publish(channel, message);
    }

    @Override
    public Long publish(final String channel, final Object message) {
        return _jedisCluster.publish(channel.getBytes(), ProtostuffUtils.serialize(message));
    }

    @Override
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        _jedisCluster.subscribe(jedisPubSub, channels);
    }

    @Override
    public void subscribe(final BinaryJedisPubSub jedisPubSub, final String... channels) {
        final byte[][] bytesArray = new byte[channels.length][];
        for (int i = 0; i < bytesArray.length; i++) {
            bytesArray[i] = channels[i].getBytes();
        }
        _jedisCluster.subscribe(jedisPubSub, bytesArray);
    }

    @Override
    public void subscribeByPatterns(final JedisPubSub jedisPubSub, final String... patterns) {
        _jedisCluster.psubscribe(jedisPubSub, patterns);
    }

    @Override
    public void subscribeByPatterns(final BinaryJedisPubSub jedisPubSub, final String... patterns) {
        final byte[][] bytesArray = new byte[patterns.length][];
        for (int i = 0; i < bytesArray.length; i++) {
            bytesArray[i] = patterns[i].getBytes();
        }
        _jedisCluster.psubscribe(jedisPubSub, bytesArray);
    }

}
