package rebue.sbs.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /**
     * 连接地址
     */
    private String endpoint  = "http://172.20.14.125:9000";
    /**
     * 用户名
     */
    private String accessKey = "minioadmin";
    /**
     * 密码
     */
    private String secretKey = "minioadmin";
}
