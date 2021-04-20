package rebue.sbs.cache.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;

import rebue.sbs.cache.CacheManagerName;
import rebue.sbs.cache.CachePropertiesEx;
import rebue.sbs.cache.FlexibleCaffeineCacheManager;

/**
 * 多缓存配置
 *
 * @author zbz
 *
 */
@Configuration
// 启用属性类(也就是注入属性类，如果没有这一行，属性类要另外写注入，如在属性类上加注解@Compenent，或扫描)
@EnableConfigurationProperties(CachePropertiesEx.class)
@ConditionalOnClass({ Caffeine.class, CaffeineCacheManager.class
})
public class CaffeineCacheConfig {

    /**
     * 参考了org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration类的编写
     */
    @Bean(CacheManagerName.CAFFEINE_CACHE_MANAGER)
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
