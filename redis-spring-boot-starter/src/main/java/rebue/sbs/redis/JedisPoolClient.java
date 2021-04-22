package rebue.sbs.redis;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import rebue.wheel.serialization.protostuff.ProtostuffUtils;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Slf4j
public class JedisPoolClient implements RedisClient {
    private final JedisPool _jedisPool;

    public JedisPoolClient(final JedisPool jedisPool) {
        _jedisPool = jedisPool;
    }

    private Jedis getJedis() {
        return _jedisPool.getResource();
    }

    @Override
    public Boolean expire(final String key, final int seconds) {
        try (Jedis jedis = getJedis()) {
            return jedis.expire(key, seconds) == 1 ? true : false;
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Boolean exists(final String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.exists(key);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Boolean exists(final byte[] key) {
        try (Jedis jedis = getJedis()) {
            return jedis.exists(key);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long incr(final String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.incr(key);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long incr(final String key, final int expireTime) {
        try (Jedis jedis = getJedis()) {
            final Long result = jedis.incr(key);
            if (result == 1) {
                if (jedis.expire(key, expireTime) != 1) {
                    throw new RedisSetException();
                }
            }
            return result;
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void set(final String key, final String value) {
        try (Jedis jedis = getJedis()) {
            final String result = jedis.set(key, value);
            if (!result.equals("OK")) {
                throw new RedisSetException();
            }
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void set(final String key, final String value, final int expireTime) {
        try (Jedis jedis = getJedis()) {
            final String result = jedis.setex(key, expireTime, value);
            if (!result.equals("OK")) {
                throw new RedisSetException();
            }
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void setObj(final String key, final Object value) {
        try (Jedis jedis = getJedis()) {
            final String result = jedis.set(key.getBytes(), ProtostuffUtils.serialize(value));
            if (!result.equals("OK")) {
                throw new RedisSetException();
            }
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void setObj(final String key, final Object value, final int expireTime) {
        try (Jedis jedis = getJedis()) {
            final String result = jedis.setex(key.getBytes(), expireTime, ProtostuffUtils.serialize(value));
            if (!result.equals("OK")) {
                throw new RedisSetException();
            }
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String get(final String key) {
        try (Jedis jedis = getJedis()) {
            final String result = jedis.get(key);
            return result == null || result == "" || result == "nil" ? null : result;
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long getLong(final String key) {
        try (Jedis jedis = getJedis()) {
            String result = jedis.get(key);
            if (result == null || result == "" || result == "nil") {
                return null;
            }
            result = result.replaceAll("\"", "");
            final Long longResult = Long.parseLong(result);
            return longResult;
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public byte[] get(final byte[] key) {
        try (Jedis jedis = getJedis()) {
            return jedis.get(key);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String get(final String key, final int expireTime) {
        try (Jedis jedis = getJedis()) {
            final String result = jedis.get(key);
            if (result != "nil" && jedis.expire(key, expireTime) == 1) {
                return result;
            }
            else {
                return null;
            }
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long getLong(final String key, final int expireTime) {
        try (Jedis jedis = getJedis()) {
            String result = jedis.get(key);
            if (result == null || result == "" || result == "nil" || jedis.expire(key, expireTime) != 1) {
                return null;
            }
            result = result.replaceAll("\"", "");
            final Long longResult = Long.parseLong(result);
            return longResult;
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> listByWildcard(final String key, final Class<T> clazz) {
        final List<T> result = new LinkedList<>();
        try (Jedis jedis = getJedis()) {
            for (final String item : jedis.keys(key)) {
                if ("java.lang.String".equals(clazz.getName())) {
                    result.add((T) get(item));
                }
                else {
                    result.add(getObj(item, clazz));
                }
            }
            return result;
        }
    }

    @Override
    public String pop(final String key) {
        try (Jedis jedis = getJedis()) {
            final String result = jedis.get(key);
            if (result == null || jedis.del(key) == 0) {
                return null;
            }
            else {
                return result;
            }
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public <T> T popObj(final String key, final Class<T> clazz) {
        try (Jedis jedis = getJedis()) {
            final byte[] result = jedis.get(key.getBytes());
            if (result == null || jedis.del(key) == 0) {
                return null;
            }
            else {
                return ProtostuffUtils.deserialize(result, clazz);
            }
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long del(final String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.del(key);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void delByWildcard(final String key) {
        try (Jedis jedis = getJedis()) {
            final Set<String> keys = jedis.keys(key);
            jedis.del(keys.toArray(new String[keys.size()]));
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long sadd(final String key, final String... members) {
        try (Jedis jedis = getJedis()) {
            return jedis.sadd(key, members);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String srandmember(final String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.srandmember(key);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long publish(final String channel, final String message) {
        try (Jedis jedis = getJedis()) {
            return jedis.publish(channel, message);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long publish(final String channel, final Object message) {
        try (Jedis jedis = getJedis()) {
            return jedis.publish(channel.getBytes(), ProtostuffUtils.serialize(message));
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        try (Jedis jedis = getJedis()) {
            jedis.subscribe(jedisPubSub, channels);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void subscribe(final BinaryJedisPubSub jedisPubSub, final String... channels) {
        final byte[][] bytesArray = new byte[channels.length][];
        for (int i = 0; i < bytesArray.length; i++) {
            bytesArray[i] = channels[i].getBytes();
        }

        try (Jedis jedis = getJedis()) {
            jedis.subscribe(jedisPubSub, bytesArray);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void subscribeByPatterns(final JedisPubSub jedisPubSub, final String... patterns) {
        try (Jedis jedis = getJedis()) {
            jedis.psubscribe(jedisPubSub, patterns);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void subscribeByPatterns(final BinaryJedisPubSub jedisPubSub, final String... patterns) {
        final byte[][] bytesArray = new byte[patterns.length][];
        for (int i = 0; i < bytesArray.length; i++) {
            bytesArray[i] = patterns[i].getBytes();
        }

        try (Jedis jedis = getJedis()) {
            jedis.psubscribe(jedisPubSub, bytesArray);
        } catch (final JedisConnectionException e) {
            log.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }
}
