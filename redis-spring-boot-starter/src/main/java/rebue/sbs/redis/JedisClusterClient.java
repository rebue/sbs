package rebue.sbs.redis;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rebue.wheel.protostuff.ProtostuffUtils;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class JedisClusterClient implements RedisClient {

    private JedisCluster _jedisCluster;

    public JedisClusterClient(JedisCluster jedisCluster) {
        _jedisCluster = jedisCluster;
    }

    @Override
    public Boolean expire(final String key, final int seconds) {
        return _jedisCluster.expire(key, seconds) == 1 ? true : false;
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
        return result == null || result == "" || result == "nil" ? null : result;
    }

    @Override
    public Long getLong(String key) throws ClassCastException {
        String result = _jedisCluster.get(key);
        if (result == null || result == "" || result == "nil") {
            return null;
        }
        result = result.replaceAll("\"", "");
        Long longResult = Long.parseLong(result);
        return longResult;
    }

    @Override
    public byte[] get(byte[] key) {
        return _jedisCluster.get(key);
    }

    @Override
    public String get(String key, int expireTime) {
        String result = _jedisCluster.get(key);
        if (result != "" || result != "nil" || _jedisCluster.expire(key, expireTime) == 1) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public Long getLong(String key, int expireTime) {
        String result = _jedisCluster.get(key);
        if (result == null || result == "" || result == "nil" || _jedisCluster.expire(key, expireTime) != 1) {
            return null;
        }
        result = result.replaceAll("\"", "");
        Long longResult = Long.parseLong(result);
        return longResult;

    }

    @Override
    public Map<String, String> listByWildcard(String key) {
        Map<String, JedisPool> jedises = _jedisCluster.getClusterNodes();
        Map<String, String> result = new LinkedHashMap<>();
        for (JedisPool jedisPool : jedises.values()) {
            Jedis jedis = jedisPool.getResource();
            Long cursor = 0L;
            ScanParams scanParams = new ScanParams();
            scanParams.match(key);
            while (true) {
                ScanResult<String> scanResult = jedis.scan(cursor.toString(), scanParams);
                String stringCursor = scanResult.getStringCursor();
                cursor = Long.valueOf(stringCursor);
                if (cursor == 0)
                    break;
                List<String> keys = scanResult.getResult();
                // 遍历key
                for (String item : keys) {
                    result.put(item, get(item));
                }
            }
        }
        return result;
    }

    @Override
    public String pop(String key) {
        String result = _jedisCluster.get(key);
        if (result == null || result == "" || result == "nil" || _jedisCluster.del(key) == 0) {
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
        byte[][] bytesArray = new byte[channels.length][];
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
        byte[][] bytesArray = new byte[patterns.length][];
        for (int i = 0; i < bytesArray.length; i++) {
            bytesArray[i] = patterns[i].getBytes();
        }
        _jedisCluster.psubscribe(jedisPubSub, bytesArray);
    }

}
