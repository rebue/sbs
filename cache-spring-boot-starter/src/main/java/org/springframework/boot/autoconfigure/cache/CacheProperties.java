/**
 * 复制并改写了org.springframework.boot.autoconfigure.cache.CacheProperties类
 *
 * 1. 新增specs属性，支持不同名称的cache使用不同的spec
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
package org.springframework.boot.autoconfigure.cache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Configuration properties for the cache abstraction.
 *
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 * @author Ryon Day
 *
 * @since 1.3.0
 */
@ConfigurationProperties(prefix = "spring.cache")
public class CacheProperties {

    /**
     * Cache type. By default, auto-detected according to the environment.
     */
    private CacheType        type;

    /**
     * Comma-separated list of cache names to create if supported by the underlying cache
     * manager. Usually, this disables the ability to create additional caches on-the-fly.
     */
    private List<String>     cacheNames = new ArrayList<>();

    private final Caffeine   caffeine   = new Caffeine();

    private final Couchbase  couchbase  = new Couchbase();

    private final EhCache    ehcache    = new EhCache();

    private final Infinispan infinispan = new Infinispan();

    private final JCache     jcache     = new JCache();

    private final Redis      redis      = new Redis();

    public CacheType getType() {
        return type;
    }

    public void setType(final CacheType mode) {
        type = mode;
    }

    public List<String> getCacheNames() {
        return cacheNames;
    }

    public void setCacheNames(final List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public Caffeine getCaffeine() {
        return caffeine;
    }

    public Couchbase getCouchbase() {
        return couchbase;
    }

    public EhCache getEhcache() {
        return ehcache;
    }

    public Infinispan getInfinispan() {
        return infinispan;
    }

    public JCache getJcache() {
        return jcache;
    }

    public Redis getRedis() {
        return redis;
    }

    /**
     * Resolve the config location if set.
     *
     * @param config the config resource
     *
     * @return the location or {@code null} if it is not set
     *
     * @throws IllegalArgumentException if the config attribute is set to an unknown
     *                                  location
     */
    public Resource resolveConfigLocation(final Resource config) {
        if (config != null) {
            Assert.isTrue(config.exists(),
                () -> "Cache configuration does not exist '" + config.getDescription() + "'");
            return config;
        }
        return null;
    }

    /**
     * Caffeine specific cache properties.
     */
    public static class Caffeine {

        /**
         * The spec to use to create caches. See CaffeineSpec for more details on the spec
         * format.
         */
        private String spec;

        public String getSpec() {
            return spec;
        }

        public void setSpec(final String spec) {
            this.spec = spec;
        }

        /**
         * XXX 新增specs属性，支持不同名称的cache使用不同的spec
         */
        private Map<String, String> specs;

        public Map<String, String> getSpecs() {
            return specs;
        }

        public void setSpecs(final Map<String, String> specs) {
            this.specs = specs;
        }

    }

    /**
     * Couchbase specific cache properties.
     */
    public static class Couchbase {

        /**
         * Entry expiration. By default the entries never expire. Note that this value is
         * ultimately converted to seconds.
         */
        private Duration expiration;

        public Duration getExpiration() {
            return expiration;
        }

        public void setExpiration(final Duration expiration) {
            this.expiration = expiration;
        }

    }

    /**
     * EhCache specific cache properties.
     */
    public static class EhCache {

        /**
         * The location of the configuration file to use to initialize EhCache.
         */
        private Resource config;

        public Resource getConfig() {
            return config;
        }

        public void setConfig(final Resource config) {
            this.config = config;
        }

    }

    /**
     * Infinispan specific cache properties.
     */
    public static class Infinispan {

        /**
         * The location of the configuration file to use to initialize Infinispan.
         */
        private Resource config;

        public Resource getConfig() {
            return config;
        }

        public void setConfig(final Resource config) {
            this.config = config;
        }

    }

    /**
     * JCache (JSR-107) specific cache properties.
     */
    public static class JCache {

        /**
         * The location of the configuration file to use to initialize the cache manager.
         * The configuration file is dependent of the underlying cache implementation.
         */
        private Resource config;

        /**
         * Fully qualified name of the CachingProvider implementation to use to retrieve
         * the JSR-107 compliant cache manager. Needed only if more than one JSR-107
         * implementation is available on the classpath.
         */
        private String   provider;

        public String getProvider() {
            return provider;
        }

        public void setProvider(final String provider) {
            this.provider = provider;
        }

        public Resource getConfig() {
            return config;
        }

        public void setConfig(final Resource config) {
            this.config = config;
        }

    }

    /**
     * Redis-specific cache properties.
     */
    public static class Redis {

        /**
         * Entry expiration. By default the entries never expire.
         */
        private Duration timeToLive;

        /**
         * Allow caching null values.
         */
        private boolean  cacheNullValues = true;

        /**
         * Key prefix.
         */
        private String   keyPrefix;

        /**
         * Whether to use the key prefix when writing to Redis.
         */
        private boolean  useKeyPrefix    = true;

        public Duration getTimeToLive() {
            return timeToLive;
        }

        public void setTimeToLive(final Duration timeToLive) {
            this.timeToLive = timeToLive;
        }

        public boolean isCacheNullValues() {
            return cacheNullValues;
        }

        public void setCacheNullValues(final boolean cacheNullValues) {
            this.cacheNullValues = cacheNullValues;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(final String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isUseKeyPrefix() {
            return useKeyPrefix;
        }

        public void setUseKeyPrefix(final boolean useKeyPrefix) {
            this.useKeyPrefix = useKeyPrefix;
        }

    }

}
