/**
 * 缓存配置类
 *
 * XXX 复制并改写了下面的类
 * 1. org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
 * 2. org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration
 *
 * Copyright 2012-2019 the original author or authors.
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
package rebue.sbs.cache;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheCondition;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Conditional(CacheCondition.class)
// XXX 启用属性类(也就是注入属性类，如果没有这一行，属性类要另外写注入，如在属性类上加注解@Compenent，或扫描)
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    // ↓↓↓↓↓↓↓↓↓↓↓ 参考org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration ↓↓↓↓↓↓↓↓↓↓↓
    // XXX @Primary注解让此缓存管理为默认的缓存管理
    @Primary
    // XXX 指定 Bean 的名称
    @Bean(CacheManagerName.REDIS_CACHE_MANAGER)
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    RedisCacheManager cacheManager(final CacheProperties cacheProperties, final CacheManagerCustomizers cacheManagerCustomizers,
                                   final ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
                                   final ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                   final RedisConnectionFactory redisConnectionFactory, final ResourceLoader resourceLoader) {
        final RedisCacheManagerBuilder builder    = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(
            determineConfiguration(cacheProperties, redisCacheConfiguration, resourceLoader.getClassLoader()));
        final List<String>             cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        redisCacheManagerBuilderCustomizers.orderedStream().forEach(customizer -> customizer.customize(builder));
        return cacheManagerCustomizers.customize(builder.build());
    }

    private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(
                                                                                                final CacheProperties cacheProperties,
                                                                                                final ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
                                                                                                final ClassLoader classLoader) {
        return redisCacheConfiguration.getIfAvailable(() -> createConfiguration(cacheProperties, classLoader));
    }

    private org.springframework.data.redis.cache.RedisCacheConfiguration createConfiguration(
                                                                                             final CacheProperties cacheProperties, final ClassLoader classLoader) {
        final Redis                                                  redisProperties = cacheProperties.getRedis();
        org.springframework.data.redis.cache.RedisCacheConfiguration config          = org.springframework.data.redis.cache.RedisCacheConfiguration
            .defaultCacheConfig();
        config = config.serializeValuesWith(
            SerializationPair.fromSerializer(
                // XXX 这里改写用FST代替原来的JDK进行序列化
                // new JdkSerializationRedisSerializer(classLoader)//
                new FstRedisSerializer()//
            ));

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
    // ↑↑↑↑↑↑↑↑↑↑↑ 参考org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration ↑↑↑↑↑↑↑↑↑↑↑

    // ↓↓↓↓↓↓↓↓↓↓↓ 参考org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration ↓↓↓↓↓↓↓↓↓↓↓
    // XXX 指定 Bean 的名称
    @Bean(CacheManagerName.CAFFEINE_CACHE_MANAGER)
    @ConditionalOnClass({ Caffeine.class, CaffeineCacheManager.class
    })
    public CaffeineCacheManager cacheManager(final CacheProperties cacheProperties, final CacheManagerCustomizers customizers,
                                             final ObjectProvider<Caffeine<Object, Object>> caffeine, final ObjectProvider<CaffeineSpec> caffeineSpec,
                                             final ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        final CaffeineCacheManager cacheManager = createCaffeineCacheManager(cacheProperties, caffeine, caffeineSpec, cacheLoader);
        final List<String>         cacheNames   = cacheProperties.getCacheNames();
        if (!CollectionUtils.isEmpty(cacheNames)) {
            cacheManager.setCacheNames(cacheNames);
        }
        return customizers.customize(cacheManager);
    }

    private CaffeineCacheManager createCaffeineCacheManager(final CacheProperties cacheProperties,
                                                            final ObjectProvider<Caffeine<Object, Object>> caffeine, final ObjectProvider<CaffeineSpec> caffeineSpec,
                                                            final ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        // XXX 这里使用了自写的缓存管理器
        final CaffeineCacheManager cacheManager = new FlexibleCaffeineCacheManager();
        setCacheBuilder(cacheProperties, caffeineSpec.getIfAvailable(), caffeine.getIfAvailable(), cacheManager);
        cacheLoader.ifAvailable(cacheManager::setCacheLoader);
        return cacheManager;
    }

    private void setCacheBuilder(final CacheProperties cacheProperties, final CaffeineSpec caffeineSpec,
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
    // ↑↑↑↑↑↑↑↑↑↑↑ 参考org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration ↑↑↑↑↑↑↑↑↑↑↑
}
