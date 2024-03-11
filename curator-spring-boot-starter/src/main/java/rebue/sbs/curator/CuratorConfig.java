package rebue.sbs.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
// XXX 启用属性类(也就是注入属性类，如果没有这一行，属性类要另外写注入，如在属性类上加注解@Compenent，或扫描)
@EnableConfigurationProperties(CuratorProperties.class)
public class CuratorConfig {
    @Bean
    public RetryPolicy retryPolicy(final CuratorProperties properties) {
        return new ExponentialBackoffRetry((int) properties.getRetryPolicy().getBaseSleepTime().toMillis(), properties.getRetryPolicy().getMaxRetries());
    }

    @Bean
    public CuratorFramework zookeeperClient(final CuratorProperties properties, final RetryPolicy retryPolicy) {
        final CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(properties.getConnectString())
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs((int) properties.getConnectionTimeout().toMillis())
                .sessionTimeoutMs((int) properties.getSessionTimeout().toMillis())
                .build();
        // 开始连接
        client.start();
        return client;
    }
}
