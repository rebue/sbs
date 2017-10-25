package com.zboss.sbs.redis;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zboss.wheel.protostuff.ProtostuffUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisPoolClient implements RedisClient {
    private static Logger _logger = LoggerFactory.getLogger(JedisPoolClient.class);

    private ShardedJedisPool _shardedJedisPool;

    public JedisPoolClient(ShardedJedisPool shardedJedisPool) {
        _shardedJedisPool = shardedJedisPool;
    }

    private ShardedJedis getJedis() {
        return _shardedJedisPool.getResource();
    }

    @Override
    public Boolean exists(String key) {
        try (ShardedJedis shardedJedis = getJedis()) {
            return shardedJedis.exists(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Boolean exists(byte[] key) {
        try (ShardedJedis shardedJedis = getJedis()) {
            return shardedJedis.exists(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long incr(String key) {
        try (ShardedJedis shardedJedis = getJedis()) {
            return shardedJedis.incr(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long incr(String key, int expireTime) throws RedisSetException {
        try (ShardedJedis shardedJedis = getJedis()) {
            Long result = shardedJedis.incr(key);
            if (result == 1)
                if (shardedJedis.expire(key, expireTime) != 1)
                    throw new RedisSetException();
            return result;
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void set(String key, String value) throws RedisSetException {
        try (ShardedJedis shardedJedis = getJedis()) {
            String result = shardedJedis.set(key, value);
            if (!result.equals("OK"))
                throw new RedisSetException();
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void set(String key, String value, int expireTime) throws RedisSetException {
        try (ShardedJedis shardedJedis = getJedis()) {
            String result = shardedJedis.setex(key, expireTime, value);
            if (!result.equals("OK"))
                throw new RedisSetException();
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void setObj(String key, Object value) throws RedisSetException {
        try (ShardedJedis shardedJedis = getJedis()) {
            String result = shardedJedis.set(key.getBytes(), ProtostuffUtils.serialize(value));
            if (!result.equals("OK"))
                throw new RedisSetException();
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void setObj(String key, Object value, int expireTime) throws RedisSetException {
        try (ShardedJedis shardedJedis = getJedis()) {
            String result = shardedJedis.setex(key.getBytes(), expireTime, ProtostuffUtils.serialize(value));
            if (!result.equals("OK"))
                throw new RedisSetException();
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String get(String key) {
        try (ShardedJedis shardedJedis = getJedis()) {
            String result = shardedJedis.get(key);
            return result == "" || result == "nil" ? null : result;
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public byte[] get(byte[] key) {
        try (ShardedJedis shardedJedis = getJedis()) {
            return shardedJedis.get(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public String get(String key, int expireTime) {
        try (ShardedJedis shardedJedis = getJedis()) {
            String result = shardedJedis.get(key);
            if (result != "nil" && shardedJedis.expire(key, expireTime) == 1) {
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
        try (ShardedJedis shardedJedis = getJedis()) {
            String result = shardedJedis.get(key);
            if (result == null || shardedJedis.del(key) == 0) {
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
        try (ShardedJedis shardedJedis = getJedis()) {
            byte[] result = shardedJedis.get(key.getBytes());
            if (result == null || shardedJedis.del(key) == 0) {
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
        try (ShardedJedis shardedJedis = getJedis()) {
            return shardedJedis.del(key);
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public void delByWildcard(String key) {
        try (ShardedJedis shardedJedis = getJedis()) {
            Collection<Jedis> jedises = shardedJedis.getAllShards();
            for (Jedis jedis : jedises) {
                Set<String> keys = jedis.keys(key);
                jedis.del(keys.toArray(new String[keys.size()]));
            }
        } catch (JedisConnectionException e) {
            _logger.error("\n连接Redis服务器异常", e);
            throw e;
        }
    }

    @Override
    public Long sadd(String key, String... members) {
        try (ShardedJedis shardedJedis = getJedis()) {
            return shardedJedis.sadd(key, members);
        }
    }

    @Override
    public String srandmember(String key) {
        try (ShardedJedis shardedJedis = getJedis()) {
            return shardedJedis.srandmember(key);
        }
    }
}
