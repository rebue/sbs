package rebue.sbs.cache;

import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

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
    public RedisCacheManager redisCacheManager(final RedisTemplate<String, String> redisTemplate) {
        return RedisCacheManager.builder().build();
    }

    @Bean(CacheManagerName.CAFFEINE_CACHE_MANAGER)
    public CaffeineCacheManager caffeineCacheManager() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        return caffeineCacheManager;
    }
}
