package com.zboss.sbs.druid;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@EnableConfigurationProperties(DruidDataSourceProperties.class)
public class DruidDataSourceConfig {
    @Bean
    public DataSource dataSource(DruidDataSourceProperties properties) throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setInitialSize(properties.getInitialSize());
        dataSource.setMinIdle(properties.getMinIdle());
        dataSource.setMaxActive(properties.getMaxActive());
        dataSource.setMaxWait(properties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        String validationQuery = properties.getValidationQuery();
        if (validationQuery != null && !"".equals(validationQuery)) {
            dataSource.setValidationQuery(validationQuery);
        }
        dataSource.setTestWhileIdle(properties.isTestWhileIdle());
        dataSource.setTestOnBorrow(properties.isTestOnBorrow());
        dataSource.setTestOnReturn(properties.isTestOnReturn());
        if (properties.isPoolPreparedStatements()) {
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                    properties.getMaxPoolPreparedStatementPerConnectionSize());
        }
        dataSource.setFilters(properties.getFilters());// 这是最关键的,否则SQL监控无法生效
        Map<String, String> connectionProperties = properties.getConnectionProperties();
        if (connectionProperties != null && !"".equals(connectionProperties)) {
            Properties connectProperties = new Properties();
            connectProperties.putAll(connectionProperties);
            dataSource.setConnectProperties(connectProperties);
        }
        dataSource.setUseGlobalDataSourceStat(properties.isUseGlobalDataSourceStat());
        dataSource.setRemoveAbandoned(properties.isRemoveAbandoned());
        dataSource.setRemoveAbandonedTimeout(properties.getRemoveAbandonedTimeout());
        dataSource.setLogAbandoned(properties.isLogAbandoned());

        return dataSource;
    }
}
