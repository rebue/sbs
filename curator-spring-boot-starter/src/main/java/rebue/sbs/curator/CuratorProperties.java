package rebue.sbs.curator;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "curator")
public class CuratorProperties {
    /**
     * 是否启用
     */
    private Boolean     enabled           = true;
    /**
     * 连接字符串
     */
    private String      connectString     = "127.0.0.1:2181";
    /**
     * 连接超时
     */
    private Duration    connectionTimeout = Duration.ofSeconds(2);
    /**
     * 会话超时
     */
    private Duration    sessionTimeout    = Duration.ofSeconds(5);
    /**
     * 重试规则
     */
    private RetryPolicy retryPolicy       = new RetryPolicy();
}

@Data
class RetryPolicy {
    /**
     * initial amount of time to wait between retries
     */
    private Duration baseSleepTime = Duration.ofSeconds(1);
    /**
     * max number of times to retry
     */
    private int      maxRetries    = 3;
}
