package rebue.sbs.aop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("rebue.aop")
public class AopProperties {

    /**
     * 拦截控制层日志
     */
    private Boolean ctrlLog = false;
    /**
     * 拦截控制层错误
     */
    private Boolean ctrlErr = true;
    /**
     * 拦截API层日志
     */
    private Boolean apiLog  = false;
    /**
     * 拦截API层错误
     */
    private Boolean apiErr  = true;
    /**
     * 拦截服务层日志
     */
    private Boolean svcLog  = false;
    /**
     * 拦截消息订阅层日志
     */
    private Boolean subLog  = false;
    /**
     * 拦截消息订阅层错误
     */
    private Boolean subErr  = true;

}
