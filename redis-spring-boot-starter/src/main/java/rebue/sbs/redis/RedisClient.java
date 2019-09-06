package rebue.sbs.redis;

import java.util.Map;

import rebue.wheel.protostuff.ProtostuffUtils;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;

public interface RedisClient {

    /**
     * 设置Key在多少秒后失效
     * 
     * @return 设置成功，返回true，失败则返回false，可能是key不存在
     */
    Boolean expire(final String key, final int seconds);

    /**
     * 检查key是否存在
     * 
     * @param key
     * @return
     */
    Boolean exists(String key);

    /**
     * 检查key是否存在
     * 
     * @param key
     * @return
     */
    Boolean exists(byte[] key);

    /**
     * 是否调用只有一次(默认Key只保留3天以供判断)
     */
    default Boolean isOnce(final String key) {
        return isOnce(key, 3 * 24 * 60 * 60);
    }

    /**
     * 是否调用只有一次
     * 
     * @param key
     * @param seconds
     *            保留多少秒的Key以供判断是否重复
     * @return
     */
    default Boolean isOnce(final String key, final Integer seconds) {
        final Long count = incr(key);
        if (count == 1) {
            expire(key, seconds);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 自增(如果没有找到key，会自动创建key并设置为1)
     * 
     * @param key
     * @return 自增后的值
     */
    Long incr(String key);

    /**
     * 自增(如果没有找到key，会自动创建key并设置为1)
     * 
     * @param key
     *            如果是第1次创建这个key，则为1
     * @param expireTime
     *            如果是第1次创建这个key，设置保持这个KV多少秒
     * @return 自增后的值
     */
    Long incr(String key, int expireTime) throws RedisSetException;

    /**
     * 设置KV
     * 
     * @param key
     * @param value
     * @throws RedisSetException
     *             设置错误时抛出异常
     */
    void set(String key, String value) throws RedisSetException;

    /**
     * 设置KV，并保持多少秒
     * 
     * @param key
     * @param value
     * @param expireTime
     *            保持这个KV多少秒
     * @throws RedisSetException
     *             设置错误时抛出异常
     */
    void set(String key, String value, int expireTime) throws RedisSetException;

    /**
     * 设置KV
     * 
     * @param key
     * @param value
     *            将value对象序列化后存储到redis中
     * @throws RedisSetException
     *             设置错误时抛出异常
     */
    void setObj(String key, Object value) throws RedisSetException;

    /**
     * 设置KV，并保持多少秒
     * 
     * @param key
     * @param value
     * @param expireTime
     *            保持这个KV多少秒
     * @throws RedisSetException
     *             设置错误时抛出异常
     */
    void setObj(String key, Object value, int expireTime) throws RedisSetException;

    /**
     * 获取key的值
     * 
     * @param key
     * @return 如果找不到key，返回null
     */
    String get(String key);

    /**
     * 获取long类型的key的值
     * 
     * @param key
     * @return 如果找不到key，返回null
     */
    Long getLong(String key);

    /**
     * 获取key的值
     * 
     * @param key
     * @return 如果找不到key，返回null
     */
    byte[] get(byte[] key);

    /**
     * 获取key的值，如果找到了，重新延长此key的缓存时间多少秒
     * 
     * @param key
     * @return 如果找不到key，返回null
     */
    String get(String key, int expireTime);

    /**
     * 获取Long类型的key值，如果找到了，重新延长此key的缓存时间多少秒
     * 
     * @param key
     * @return 如果找不到key，返回null
     */
    Long getLong(String key, int expireTime);

    /**
     * 获取key的值(反序列化为Object来获取)
     * 
     * @param key
     * @param clazz
     *            指定要反序列的对象的类
     * @return 如果找不到key，返回null
     */
    default <T> T getObj(final String key, final Class<T> clazz) {
        final byte[] result = get(key.getBytes());
        return result == null ? null : ProtostuffUtils.deserialize(result, clazz);
    }

    /**
     * 模糊查询
     * 
     * @param key
     *            模糊查询，可以用*和?作通配符
     * @param clazz
     *            Value的类
     * @return 如果找不到key，返回列表大小为0
     */
    <T> Map<String, T> listByWildcard(String key, Class<T> clazz);

    /**
     * 模糊查询
     * 
     * @param key
     *            模糊查询，可以用*和?作通配符
     * @return 如果找不到key，返回列表大小为0
     */
    default Map<String, String> listByWildcard(final String key) {
        return listByWildcard(key, String.class);
    }

    /**
     * 获取并删除key的值
     * 
     * @param key
     * @return 如果找到了，返回key的值；如果没找到，返回null
     */
    String pop(String key);

    /**
     * 获取并删除key的值
     * 
     * @param key
     * @return 如果找到了，返回key的值；如果没找到，返回null
     */
    <T> T popObj(String key, Class<T> clazz);

    /**
     * 精确删除
     * 
     * @return 删除的数量
     * 
     */
    Long del(String key);

    /**
     * 模糊删除
     * 
     */
    void delByWildcard(String key);

    /**
     * 添加元素到集合(Set)中
     * 
     * @param key
     * @param members
     *            要添加的一个或多个元素
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素
     */
    Long sadd(String key, String... members);

    /**
     * 从集合(Set)中随机获取元素
     * 
     * @param key
     * @return 随机获取的元素
     */
    String srandmember(String key);

    /**
     * 发布消息
     */
    Long publish(final String channel, final String message);

    /**
     * 发布消息
     */
    void subscribe(final JedisPubSub jedisPubSub, final String... channels);

    /**
     * 发布消息
     */
    Long publish(final String channel, final Object message);

    /**
     * 订阅消息
     */
    void subscribe(final BinaryJedisPubSub jedisPubSub, final String... channels);

    /**
     * 订阅消息
     */
    void subscribeByPatterns(final JedisPubSub jedisPubSub, final String... patterns);

    /**
     * 订阅消息
     */
    void subscribeByPatterns(final BinaryJedisPubSub jedisPubSub, final String... patterns);

}