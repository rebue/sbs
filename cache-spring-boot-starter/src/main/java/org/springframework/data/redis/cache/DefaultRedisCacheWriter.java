/*
 * XXX DefaultRedisCacheWriter在spring的代码中未声明为public，不能使用，所以从 spring-data-redis-2.3.9.RELEASE.jar 中复制过来
 *
 * 完全复制 org.springframework.data.redis.cache.DefaultRedisCacheWriter 类并改变如下:
 * 1. 类改为 public
 * 2. 构造函数改为 protected，方便继承
 * 3. execute 改为 protected，方便继承
 *
 * Copyright 2017-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.cache;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link RedisCacheWriter} implementation capable of reading/writing binary data from/to Redis in {@literal standalone}
 * and {@literal cluster} environments. Works upon a given {@link RedisConnectionFactory} to obtain the actual
 * {@link RedisConnection}. <br />
 * {@link DefaultRedisCacheWriter} can be used in
 * {@link RedisCacheWriter#lockingRedisCacheWriter(RedisConnectionFactory) locking} or
 * {@link RedisCacheWriter#nonLockingRedisCacheWriter(RedisConnectionFactory) non-locking} mode. While
 * {@literal non-locking} aims for maximum performance it may result in overlapping, non atomic, command execution for
 * operations spanning multiple Redis interactions like {@code putIfAbsent}. The {@literal locking} counterpart prevents
 * command overlap by setting an explicit lock key and checking against presence of this key which leads to additional
 * requests and potential command wait times.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author André Prata
 *
 * @since 2.0
 */
public class DefaultRedisCacheWriter implements RedisCacheWriter {

    private final RedisConnectionFactory connectionFactory;
    private final Duration               sleepTime;

    /**
     * XXX 构造函数改为 protected，方便继承
     *
     * @param connectionFactory must not be {@literal null}.
     */
    protected DefaultRedisCacheWriter(final RedisConnectionFactory connectionFactory) {
        this(connectionFactory, Duration.ZERO);
    }

    /**
     * @param connectionFactory must not be {@literal null}.
     * @param sleepTime         sleep time between lock request attempts. Must not be {@literal null}. Use {@link Duration#ZERO}
     *                          to disable locking.
     */
    DefaultRedisCacheWriter(final RedisConnectionFactory connectionFactory, final Duration sleepTime) {

        Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");
        Assert.notNull(sleepTime, "SleepTime must not be null!");

        this.connectionFactory = connectionFactory;
        this.sleepTime         = sleepTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.redis.cache.RedisCacheWriter#put(java.lang.String, byte[], byte[], java.time.Duration)
     */
    @Override
    public void put(final String name, final byte[] key, final byte[] value, @Nullable final Duration ttl) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(value, "Value must not be null!");

        execute(name, connection -> {

            if (shouldExpireWithin(ttl)) {
                connection.set(key, value, Expiration.from(ttl.toMillis(), TimeUnit.MILLISECONDS), SetOption.upsert());
            }
            else {
                connection.set(key, value);
            }

            return "OK";
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.redis.cache.RedisCacheWriter#get(java.lang.String, byte[])
     */
    @Override
    public byte[] get(final String name, final byte[] key) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");

        return execute(name, connection -> connection.get(key));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.redis.cache.RedisCacheWriter#putIfAbsent(java.lang.String, byte[], byte[], java.time.Duration)
     */
    @Override
    public byte[] putIfAbsent(final String name, final byte[] key, final byte[] value, @Nullable final Duration ttl) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(value, "Value must not be null!");

        return execute(name, connection -> {

            if (isLockingCacheWriter()) {
                doLock(name, connection);
            }

            try {

                boolean put;

                if (shouldExpireWithin(ttl)) {
                    put = connection.set(key, value, Expiration.from(ttl), SetOption.ifAbsent());
                }
                else {
                    put = connection.setNX(key, value);
                }

                if (put) {
                    return null;
                }

                return connection.get(key);
            } finally {

                if (isLockingCacheWriter()) {
                    doUnlock(name, connection);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.redis.cache.RedisCacheWriter#remove(java.lang.String, byte[])
     */
    @Override
    public void remove(final String name, final byte[] key) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");

        execute(name, connection -> connection.del(key));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.redis.cache.RedisCacheWriter#clean(java.lang.String, byte[])
     */
    @Override
    public void clean(final String name, final byte[] pattern) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(pattern, "Pattern must not be null!");

        execute(name, connection -> {

            boolean wasLocked = false;

            try {

                if (isLockingCacheWriter()) {
                    doLock(name, connection);
                    wasLocked = true;
                }

                final byte[][] keys = Optional.ofNullable(connection.keys(pattern)).orElse(Collections.emptySet())
                    .toArray(new byte[0][]);

                if (keys.length > 0) {
                    connection.del(keys);
                }
            } finally {

                if (wasLocked && isLockingCacheWriter()) {
                    doUnlock(name, connection);
                }
            }

            return "OK";
        });
    }

    /**
     * Explicitly set a write lock on a cache.
     *
     * @param name the name of the cache to lock.
     */
    void lock(final String name) {
        execute(name, connection -> doLock(name, connection));
    }

    /**
     * Explicitly remove a write lock from a cache.
     *
     * @param name the name of the cache to unlock.
     */
    void unlock(final String name) {
        executeLockFree(connection -> doUnlock(name, connection));
    }

    private Boolean doLock(final String name, final RedisConnection connection) {
        return connection.setNX(createCacheLockKey(name), new byte[0]);
    }

    private Long doUnlock(final String name, final RedisConnection connection) {
        return connection.del(createCacheLockKey(name));
    }

    boolean doCheckLock(final String name, final RedisConnection connection) {
        return connection.exists(createCacheLockKey(name));
    }

    /**
     * @return {@literal true} if {@link RedisCacheWriter} uses locks.
     */
    private boolean isLockingCacheWriter() {
        return !sleepTime.isZero() && !sleepTime.isNegative();
    }

    protected <T> T execute(final String name, final Function<RedisConnection, T> callback) {

        final RedisConnection connection = connectionFactory.getConnection();
        try {

            checkAndPotentiallyWaitUntilUnlocked(name, connection);
            return callback.apply(connection);
        } finally {
            connection.close();
        }
    }

    private void executeLockFree(final Consumer<RedisConnection> callback) {

        final RedisConnection connection = connectionFactory.getConnection();

        try {
            callback.accept(connection);
        } finally {
            connection.close();
        }
    }

    private void checkAndPotentiallyWaitUntilUnlocked(final String name, final RedisConnection connection) {

        if (!isLockingCacheWriter()) {
            return;
        }

        try {

            while (doCheckLock(name, connection)) {
                Thread.sleep(sleepTime.toMillis());
            }
        } catch (final InterruptedException ex) {

            // Re-interrupt current thread, to allow other participants to react.
            Thread.currentThread().interrupt();

            throw new PessimisticLockingFailureException(String.format("Interrupted while waiting to unlock cache %s", name),
                ex);
        }
    }

    private static boolean shouldExpireWithin(@Nullable final Duration ttl) {
        return ttl != null && !ttl.isZero() && !ttl.isNegative();
    }

    private static byte[] createCacheLockKey(final String name) {
        return (name + "~lock").getBytes(StandardCharsets.UTF_8);
    }
}
