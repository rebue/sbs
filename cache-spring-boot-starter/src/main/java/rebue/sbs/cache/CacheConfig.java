package rebue.sbs.cache;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;

/**
 * 多缓存配置
 *
 * @author zbz
 *
 */
@Configuration
@EnableConfigurationProperties(CachePropertiesEx.class)
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

    /**
     * 参考了org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration类的编写
     */
    @Bean(CacheManagerName.CAFFEINE_CACHE_MANAGER)
    // public CaffeineCacheManager caffeineCacheManager() {
    // // final MultiCaffeineCacheManager caffeineCacheManager = new MultiCaffeineCacheManager();
    // CaffeineCacheManagerBuilder
    // final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    // caffeineCacheManager.
    // caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
    // .expireAfterAccess(1, TimeUnit.SECONDS)
    // .maximumSize(1024));
    // return caffeineCacheManager;
    // }
    public CaffeineCacheManager cacheManager(final CachePropertiesEx cacheProperties, final CacheManagerCustomizers customizers,
                                             final ObjectProvider<Caffeine<Object, Object>> caffeine, final ObjectProvider<CaffeineSpec> caffeineSpec,
                                             final ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        final CaffeineCacheManager cacheManager = createCaffeineCacheManager(cacheProperties, caffeine, caffeineSpec, cacheLoader);
        final List<String>         cacheNames   = cacheProperties.getCacheNames();
        if (!CollectionUtils.isEmpty(cacheNames)) {
            cacheManager.setCacheNames(cacheNames);
        }
        return customizers.customize(cacheManager);
    }

    private CaffeineCacheManager createCaffeineCacheManager(final CachePropertiesEx cacheProperties,
                                                            final ObjectProvider<Caffeine<Object, Object>> caffeine, final ObjectProvider<CaffeineSpec> caffeineSpec,
                                                            final ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        final CaffeineCacheManager cacheManager = new FlexibleCaffeineCacheManager();
        setCacheBuilder(cacheProperties, caffeineSpec.getIfAvailable(), caffeine.getIfAvailable(), cacheManager);
        cacheLoader.ifAvailable(cacheManager::setCacheLoader);
        return cacheManager;
    }

    private void setCacheBuilder(final CachePropertiesEx cacheProperties, final CaffeineSpec caffeineSpec,
                                 final Caffeine<Object, Object> caffeine, final CaffeineCacheManager cacheManager) {
        final String specification = cacheProperties.getCaffeine().getSpec();
        if (StringUtils.hasText(specification)) {
            cacheManager.setCacheSpecification(specification);
        }
        else if (caffeineSpec != null) {
            cacheManager.setCaffeineSpec(caffeineSpec);
        }
        else if (caffeine != null) {
            cacheManager.setCaffeine(caffeine);
        }

        // XXX 读取specs配置并设置到缓存管理器中
        final Map<String, String> specs = cacheProperties.getCaffeine().getSpecs();
        if (specs != null && !specs.isEmpty()) {
            ((FlexibleCaffeineCacheManager) cacheManager).setCacheSpecs(specs);
        }
    }
}
