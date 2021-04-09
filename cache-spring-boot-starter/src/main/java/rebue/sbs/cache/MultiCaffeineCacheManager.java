package rebue.sbs.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;

public class MultiCaffeineCacheManager extends CaffeineCacheManager implements InitializingBean {
    private Map<String, String>                         cacheSpecs = new HashMap<>();

    private final Map<String, Caffeine<Object, Object>> builders   = new HashMap<>();

    private CacheLoader<Object, Object>                 cacheLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (final Map.Entry<String, String> cacheSpecEntry : cacheSpecs.entrySet()) {
            builders.put(cacheSpecEntry.getKey(), Caffeine.from(cacheSpecEntry.getValue()));
        }
    }

    @Override
    protected Cache<Object, Object> createNativeCaffeineCache(final String name) {
        final Caffeine<Object, Object> builder = builders.get(name);
        if (builder == null) {
            return super.createNativeCaffeineCache(name);
        }

        if (cacheLoader != null) {
            return builder.build(cacheLoader);
        }
        else {
            return builder.build();
        }
    }

    public Map<String, String> getCacheSpecs() {
        return cacheSpecs;
    }

    public void setCacheSpecs(final Map<String, String> cacheSpecs) {
        this.cacheSpecs = cacheSpecs;
    }

    @Override
    public void setCacheLoader(final CacheLoader<Object, Object> cacheLoader) {
        super.setCacheLoader(cacheLoader);
        this.cacheLoader = cacheLoader;
    }
}
