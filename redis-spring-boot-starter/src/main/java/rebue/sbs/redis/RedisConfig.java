package rebue.sbs.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

@Configuration
@EnableConfigurationProperties(RedisConfigProperties.class)
public class RedisConfig {

    @Bean
    public RedisClient redisClient(RedisConfigProperties properties) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(properties.getMaxTotal());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMaxWaitMillis(properties.getMaxWaitMillis());
        config.setTestOnBorrow(properties.getTestOnBorrow());
        switch (properties.getClusterType()) {
        case "Single":
            List<JedisShardInfo> jedisShardInfoList = new ArrayList<>();
            jedisShardInfoList.add(new JedisShardInfo(properties.getUrl()));
            return new JedisPoolClient(new ShardedJedisPool(config, jedisShardInfoList));
        case "RedisCluster":
            Set<HostAndPort> nodes = new HashSet<>();
            for (String node : properties.getClusterNodes()) {
                String[] ipPortPair = node.split(":");
                nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.parseInt(ipPortPair[1].trim())));
            }
            JedisClusterClient result = new JedisClusterClient(new JedisCluster(nodes,
                    properties.getConnectionTimeout(), properties.getSoTimeout(), properties.getMaxAttempts(), config));
            return result;
        }
        return null;
    }
}
