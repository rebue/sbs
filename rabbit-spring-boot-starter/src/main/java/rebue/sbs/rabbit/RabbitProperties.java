package rebue.sbs.rabbit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitProperties {

    /**
     * RabbitMQ host.
     */
    private String  host            = "localhost";

    /**
     * RabbitMQ port.
     */
    private int     port            = 5672;

    /**
     * Login user to authenticate to the broker.
     */
    private String  username;

    /**
     * Login to authenticate against the broker.
     */
    private String  password;

    /**
     * Virtual host to use when connecting to the broker.
     */
    private String  virtualHost     = "/";

    /**
     * Channel池的最大的对象数量
     */
    private Integer channelMaxTotal = Runtime.getRuntime().availableProcessors() * 100;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public Integer getChannelMaxTotal() {
        return channelMaxTotal;
    }

    public void setChannelMaxTotal(Integer channelMaxTotal) {
        this.channelMaxTotal = channelMaxTotal;
    }

    @Override
    public String toString() {
        return "RabbitProperties [host=" + host + ", port=" + port + ", username=" + username + ", password=" + password
                + ", virtualHost=" + virtualHost + ", channelMaxTotal=" + channelMaxTotal + "]";
    }

}
