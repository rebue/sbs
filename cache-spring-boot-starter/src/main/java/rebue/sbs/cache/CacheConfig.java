package rebue.sbs.cache;

import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * 多缓存配置
 *
 * @author zbz
 *
 */
@Configuration
public class CacheConfig {
    @Bean(CacheManagerName.REDIS_CACHE_MANAGER)
    @Primary
    public RedisCacheManager cacheManager(final RedisConnectionFactory connectionFactory) {
        // return RedisCacheManager.create(connectionFactory);
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())))
            .build();
    }

    @Bean(CacheManagerName.CAFFEINE_CACHE_MANAGER)
    public CaffeineCacheManager caffeineCacheManager() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        return caffeineCacheManager;
    }
}
