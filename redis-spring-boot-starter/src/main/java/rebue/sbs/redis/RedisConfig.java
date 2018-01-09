package rebue.sbs.redis;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableConfigurationProperties(RedisConfigProperties.class)
public class RedisConfig {
    private final static Logger _log = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public RedisClient redisClient(RedisConfigProperties properties) {
        _log.info("redis init: {}", properties);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(properties.getMaxTotal());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMaxWaitMillis(properties.getMaxWaitMillis());
        config.setTestOnBorrow(properties.getTestOnBorrow());
        switch (properties.getClusterType()) {
        case "Single":
            return new JedisPoolClient(new JedisPool(config, properties.getHost(), properties.getPort(),
                    properties.getConnectionTimeout(), properties.getPassword()));
        case "RedisCluster":
            Set<HostAndPort> nodes = new HashSet<>();
            for (String node : properties.getClusterNodes()) {
                String[] ipPortPair = node.split(":");
                nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.parseInt(ipPortPair[1].trim())));
            }
            JedisClusterClient result = new JedisClusterClient(
                    new JedisCluster(nodes, properties.getConnectionTimeout(), properties.getSoTimeout(),
                            properties.getMaxAttempts(), properties.getPassword(), config));
            return result;
        }
        return null;
    }
}
