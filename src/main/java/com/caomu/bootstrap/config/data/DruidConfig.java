package com.caomu.bootstrap.config.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.DbType;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.SQLUtils;

/**
 * 连接池配置
 *
 * @author 刘斌
 */
@Configuration
public class DruidConfig {

    /**
     * 性能监控
     *
     * @return 对象
     */
    @Bean
    public StatFilter statFilter() {
        final StatFilter statFilter = new StatFilter();
        statFilter.setDbType(DbType.mysql);
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        return statFilter;
    }

    /**
     * druid 打印sql
     *
     * @return 对象
     */
    @Bean
    public Slf4jLogFilter slf4jLogFilter() {
        final Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
        slf4jLogFilter.setStatementExecutableSqlLogEnable(true);
        slf4jLogFilter.setStatementLogEnabled(true);
        // close 语句
        slf4jLogFilter.setStatementCloseAfterLogEnabled(false);
        // 参数和类型语句
        slf4jLogFilter.setStatementParameterSetLogEnabled(false);
        // created 语句
        slf4jLogFilter.setStatementPrepareAfterLogEnabled(false);
        final SQLUtils.FormatOption statementSqlFormatOption = new SQLUtils.FormatOption(false, false);
        slf4jLogFilter.setStatementSqlFormatOption(statementSqlFormatOption);
        return slf4jLogFilter;
    }


    /**
     * 配置druid
     *
     * @return DruidDataSource
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DruidDataSource dataSource() {
        final DruidDataSource druidDataSource = new DruidDataSource();
        // 打印监控间隔时间
        druidDataSource.setTimeBetweenLogStatsMillis(5 * 60 * 1000);
        final List<Filter> filters = new ArrayList<>();
        filters.add(statFilter());
        filters.add(slf4jLogFilter());
        druidDataSource.setProxyFilters(filters);
        return druidDataSource;
    }
}
