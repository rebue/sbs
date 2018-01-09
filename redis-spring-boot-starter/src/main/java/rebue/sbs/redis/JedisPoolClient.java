package rebue.sbs.redis;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rebue.wheel.protostuff.ProtostuffUtils;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisPoolClient implements RedisClient {
    private static Logger _logger = LoggerFactory.getLogger(JedisPoolClient.class);

    private JedisPool     _jedisPool;

    public JedisPoolClient(JedisPool jedisPool) {
        _jedisPool = jedisPool;
    }

    private Jedis getJedis() {
        return _jedisPool.getResource();
    }

    @Override
    public Boolean expire(String key, int seconds) {
        try (Jedis jedis = getJedis()) {
            return jedis.expire(key, seconds) == 1 ? true : false;
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Boolean exists(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.exists(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Boolean exists(byte[] key) {
        try (Jedis jedis = getJedis()) {
            return jedis.exists(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long incr(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.incr(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long incr(String key, int expireTime) throws RedisSetException {
        try (Jedis jedis = getJedis()) {
            Long result = jedis.incr(key);
            if (result == 1)
                if (jedis.expire(key, expireTime) != 1)
                    throw new RedisSetException();
            return result;
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void set(String key, String value) throws RedisSetException {
        try (Jedis jedis = getJedis()) {
            String result = jedis.set(key, value);
            if (!result.equals("OK"))
                throw new RedisSetException();
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void set(String key, String value, int expireTime) throws RedisSetException {
        try (Jedis jedis = getJedis()) {
            String result = jedis.setex(key, expireTime, value);
            if (!result.equals("OK"))
                throw new RedisSetException();
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void setObj(String key, Object value) throws RedisSetException {
        try (Jedis jedis = getJedis()) {
            String result = jedis.set(key.getBytes(), ProtostuffUtils.serialize(value));
            if (!result.equals("OK"))
                throw new RedisSetException();
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void setObj(String key, Object value, int expireTime) throws RedisSetException {
        try (Jedis jedis = getJedis()) {
            String result = jedis.setex(key.getBytes(), expireTime, ProtostuffUtils.serialize(value));
            if (!result.equals("OK"))
                throw new RedisSetException();
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String get(String key) {
        try (Jedis jedis = getJedis()) {
            String result = jedis.get(key);
            return result == "" || result == "nil" ? null : result;
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public byte[] get(byte[] key) {
        try (Jedis jedis = getJedis()) {
            return jedis.get(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String get(String key, int expireTime) {
        try (Jedis jedis = getJedis()) {
            String result = jedis.get(key);
            if (result != "nil" && jedis.expire(key, expireTime) == 1) {
                return result;
            } else {
                return null;
            }
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String pop(String key) {
        try (Jedis jedis = getJedis()) {
            String result = jedis.get(key);
            if (result == null || jedis.del(key) == 0) {
                return null;
            } else {
                return result;
            }
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public <T> T popObj(String key, Class<T> clazz) {
        try (Jedis jedis = getJedis()) {
            byte[] result = jedis.get(key.getBytes());
            if (result == null || jedis.del(key) == 0) {
                return null;
            } else {
                return ProtostuffUtils.deserialize(result, clazz);
            }
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long del(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.del(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void delByWildcard(String key) {
        try (Jedis jedis = getJedis()) {
            Set<String> keys = jedis.keys(key);
            jedis.del(keys.toArray(new String[keys.size()]));
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long sadd(String key, String... members) {
        try (Jedis jedis = getJedis()) {
            return jedis.sadd(key, members);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String srandmember(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.srandmember(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long publish(final String channel, final String message) {
        try (Jedis jedis = getJedis()) {
            return jedis.publish(channel, message);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long publish(String channel, Object message) {
        try (Jedis jedis = getJedis()) {
            return jedis.publish(channel.getBytes(), ProtostuffUtils.serialize(message));
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        try (Jedis jedis = getJedis()) {
            jedis.subscribe(jedisPubSub, channels);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void subscribe(final BinaryJedisPubSub jedisPubSub, final String... channels) {
        byte[][] bytesArray = new byte[channels.length][];
        for (int i = 0; i < bytesArray.length; i++) {
            bytesArray[i] = channels[i].getBytes();
        }

        try (Jedis jedis = getJedis()) {
            jedis.subscribe(jedisPubSub, bytesArray);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void subscribeByPatterns(final JedisPubSub jedisPubSub, final String... patterns) {
        try (Jedis jedis = getJedis()) {
            jedis.psubscribe(jedisPubSub, patterns);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void subscribeByPatterns(final BinaryJedisPubSub jedisPubSub, final String... patterns) {
        byte[][] bytesArray = new byte[patterns.length][];
        for (int i = 0; i < bytesArray.length; i++) {
            bytesArray[i] = patterns[i].getBytes();
        }

        try (Jedis jedis = getJedis()) {
            jedis.psubscribe(jedisPubSub, bytesArray);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }
}
