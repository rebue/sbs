package rebue.sbs.redis;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfigProperties {

    /**
     * 密码
     */
    private String       password;
    /**
     * 最大实例数
     */
    private Integer      maxTotal;
    /**
     * 最大空闲实例数
     */
    private Integer      maxIdle;
    /**
     * (创建实例时)最大等待时间
     */
    private Long         maxWaitMillis;
    /**
     * (创建实例时)是否验证
     */
    private Boolean      testOnBorrow;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<String> getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(List<String> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public String toString() {
        return "RedisConfigProperties [password=" + password + ", maxTotal=" + maxTotal + ", maxIdle=" + maxIdle
                + ", maxWaitMillis=" + maxWaitMillis + ", testOnBorrow=" + testOnBorrow + ", clusterType=" + clusterType
                + ", host=" + host + ", port=" + port + ", clusterNodes=" + clusterNodes + ", connectionTimeout="
                + connectionTimeout + ", soTimeout=" + soTimeout + ", maxAttempts=" + maxAttempts + "]";
    }

}
