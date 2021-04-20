package rebue.sbs.cache.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import rebue.sbs.cache.CacheManagerName;
import rebue.sbs.cache.CachePropertiesEx;
import rebue.sbs.cache.FstSerializer;

/**
 * 多缓存配置
 *
 * @author zbz
 *
 */
@Configuration
// 启用属性类(也就是注入属性类，如果没有这一行，属性类要另外写注入，如在属性类上加注解@Compenent，或扫描)
@EnableConfigurationProperties(CachePropertiesEx.class)
@ConditionalOnClass(RedisConnectionFactory.class)
public class RedisCacheConfig {
    /**
     * 注入Redis缓存管理
     *
     * Primary注解让此缓存管理为默认的缓存管理
     *
     * @param connectionFactory
     *
     * @return
     */
    @Primary
    @Bean(CacheManagerName.REDIS_CACHE_MANAGER)
    public RedisCacheManager cacheManager(final RedisConnectionFactory connectionFactory) {
        // return RedisCacheManager.create(connectionFactory);
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                // .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new FstSerializer())))
            .build();
    }

}
