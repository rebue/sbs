package com.zboss.sbs.redis;

import com.zboss.wheel.protostuff.ProtostuffUtils;

public interface RedisClient {

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
     * 获取key的值(反序列化为Object来获取)
     * 
     * @param key
     * @param clazz
     *            指定要反序列的对象的类
     * @return 如果找不到key，返回null
     */
    default <T> T getObj(String key, Class<T> clazz) {
        byte[] result = get(key.getBytes());
        return result == null ? null : ProtostuffUtils.deserialize(result, clazz);
    }

    /**
     * 获取并删除key的值
     * 
     * @param key
     * @return 如果找到了，返回key的值；如果没找到，返回null
     */
    String pop(String key);

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
}