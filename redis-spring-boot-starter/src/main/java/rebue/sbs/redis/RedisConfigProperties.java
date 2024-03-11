package rebue.sbs.redis;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisConfigProperties {

    /**
     * 密码
     */
    private String  password;
    /**
     * 最大实例数
     */
    private Integer maxTotal;
    /**
     * 最大空闲实例数
     */
    private Integer maxIdle;
    /**
     * (创建实例时)最大等待时间
     */
    private Long    maxWaitMillis;
    /**
     * (创建实例时)是否验证
     */
    private Boolean testOnBorrow;

    /**
     * 集群类型 (Single,RedisCluster)
     */
    private String       clusterType;
    /**
     * Single类型需要配置连接Redis的host和port
     */
    private String       host;
    /**
     * Single类型需要配置连接Redis的host和port
     */
    private Integer      port;
    /**
     * 集群类型需要配置服务器的节点
     */
    private List<String> clusterNodes;
    /**
     * 连接超时
     */
    private int          connectionTimeout;
    /**
     * 集群类型需要配置返回值的超时时间(单机时不用设置，默认为连接超时的时间)
     */
    private int          soTimeout;
    /**
     * 集群类型需要配置出现异常最大重试次数
     */
    private int          maxAttempts;

}
