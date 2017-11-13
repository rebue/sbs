package rebue.sbs.druid;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.druid.stat")
public class DruidStatProperties {
    /**
     * 访问统计监控的url
     */
    private String url;

    /**
     * 白名单 (没有配置或者为空，则允许所有访问)
     */
    private String allow;

    /**
     * IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to
     * view this page.
     */
    private String deny;

    /**
     * 指定登录查看信息的账号
     */
    private String loginUsername;
    /**
     * 指定登录查看信息的密码
     */
    private String loginPassword;

    /**
     * 监听与jdbc关联的类的表达式
     */
    private String pointcutPatterns;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }

    public String getDeny() {
        return deny;
    }

    public void setDeny(String deny) {
        this.deny = deny;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getPointcutPatterns() {
        return pointcutPatterns;
    }

    public void setPointcutPatterns(String pointcutPatterns) {
        this.pointcutPatterns = pointcutPatterns;
    }

}
