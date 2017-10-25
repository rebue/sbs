package com.zboss.sbs.druid;

import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;

@Configuration
@EnableConfigurationProperties(DruidStatProperties.class)
public class DruidStatConfig {
    /**
     * 注册一个Druid内置的StatViewServlet，用于展示Druid的统计信息。
     * 
     * @return
     */
    @Bean
    public ServletRegistrationBean DruidStatViewServlet(DruidStatProperties properties) {
        // Druid内置提供了一个StatViewServlet用于展示Druid的统计信息。这个StatViewServlet的用途包括：
        // - 提供监控信息展示的html页面
        // - 提供监控信息的JSON API
        // org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),
                properties.getUrl());

        // 添加初始化参数：initParams
        // 白名单 (没有配置或者为空，则允许所有访问)
        if (properties.getAllow() != null)
            servletRegistrationBean.addInitParameter("allow", properties.getAllow());
        // IP黑名单 (存在共同时，deny优先于allow) :
        // 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        if (properties.getDeny() != null)
            servletRegistrationBean.addInitParameter("deny", properties.getDeny());
        // 登录查看信息的账号密码
        servletRegistrationBean.addInitParameter("loginUsername", properties.getLoginUsername());
        servletRegistrationBean.addInitParameter("loginPassword", properties.getLoginPassword());
        // 是否能够重置数据(禁用HTML页面上的“Reset All”功能)
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean,添加请求过滤规则
     * 
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {
        // WebStatFilter用于采集web-jdbc关联监控的数据
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());

        // 添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");

        // 添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid2/*");
        return filterRegistrationBean;
    }

    /**
     * <pre>
     * 监听与jdbc关联的类，下面通过3个方法分别注入了3个bean 
     * 1.定义拦截器 
     * 2.定义切入点 
     * 3.定义通知类
     * </pre>
     * 
     * @return
     */
    @Bean
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }

    @Bean
    public JdkRegexpMethodPointcut druidStatPointcut(DruidStatProperties properties) {
        JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
        druidStatPointcut.setPatterns(properties.getPointcutPatterns().split(","));
        return druidStatPointcut;
    }

    @Bean
    public Advisor druidStatAdvisor(DruidStatProperties properties) {
        return new DefaultPointcutAdvisor(druidStatPointcut(properties), druidStatInterceptor());
    }

}
