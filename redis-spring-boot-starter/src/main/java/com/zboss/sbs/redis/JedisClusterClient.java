package com.zboss.sbs.redis;

import java.util.Map;
import java.util.Set;

import com.zboss.wheel.protostuff.ProtostuffUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisClusterClient implements RedisClient {

    private JedisCluster _jedisCluster;

    public JedisClusterClient(JedisCluster jedisCluster) {
        _jedisCluster = jedisCluster;
    }

    @Override
    public Boolean exists(String key) {
        return _jedisCluster.exists(key);
    }

    @Override
    public Boolean exists(byte[] key) {
        return _jedisCluster.exists(key);
    }

    @Override
    public Long incr(String key) {
        return _jedisCluster.incr(key);
    }

    @Override
    public Long incr(String key, int expireTime) throws RedisSetException {
        Long result = _jedisCluster.incr(key);
        if (result == 1)
            if (_jedisCluster.expire(key, expireTime) != 1)
                throw new RedisSetException();
        return result;
    }

    @Override
    public void set(String key, String value) throws RedisSetException {
        String result = _jedisCluster.set(key, value);
        if (!result.equals("OK"))
            throw new RedisSetException();
    }

    @Override
    public void set(String key, String value, int expireTime) throws RedisSetException {
        String result = _jedisCluster.setex(key, expireTime, value);
        if (!result.equals("OK"))
            throw new RedisSetException();
    }

    @Override
    public void setObj(String key, Object value) throws RedisSetException {
        String result = _jedisCluster.set(key.getBytes(), ProtostuffUtils.serialize(value));
        if (!result.equals("OK"))
            throw new RedisSetException();
    }

    @Override
    public void setObj(String key, Object value, int expireTime) throws RedisSetException {
        String result = _jedisCluster.setex(key.getBytes(), expireTime, ProtostuffUtils.serialize(value));
        if (!result.equals("OK"))
            throw new RedisSetException();
    }

    @Override
    public String get(String key) {
        String result = _jedisCluster.get(key);
        return result == "" || result == "nil" ? null : result;
    }

    @Override
    public byte[] get(byte[] key) {
        return _jedisCluster.get(key);
    }

    @Override
    public String get(String key, int expireTime) {
        String result = _jedisCluster.get(key);
        if (result != "nil" && _jedisCluster.expire(key, expireTime) == 1) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String pop(String key) {
        String result = _jedisCluster.get(key);
        if (result == null || _jedisCluster.del(key) == 0) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public <T> T popObj(String key, Class<T> clazz) {
        byte[] result = _jedisCluster.get(key.getBytes());
        if (result == null || _jedisCluster.del(key) == 0) {
            return null;
        } else {
            return ProtostuffUtils.deserialize(result, clazz);
        }
    }

    @Override
    public Long del(String key) {
        return _jedisCluster.del(key);
    }

    @Override
    public void delByWildcard(String key) {
        Map<String, JedisPool> jedises = _jedisCluster.getClusterNodes();
        for (JedisPool jedisPool : jedises.values()) {
            Jedis jedis = jedisPool.getResource();
            Set<String> keys = jedis.keys(key);
            jedis.del(keys.toArray(new String[keys.size()]));
        }
    }

    @Override
    public Long sadd(String key, String... members) {
        return _jedisCluster.sadd(key, members);
    }

    @Override
    public String srandmember(String key) {
        return _jedisCluster.srandmember(key);
    }
}
