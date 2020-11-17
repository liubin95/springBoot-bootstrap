package com.caomu.bootstrap.config.mybatisplus;

import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;

/**
 * mybatis 的通用配置
 *
 * @author 刘斌
 */
@Configuration
public class MybatisConfig implements ConfigurationCustomizer {
    /**
     * mybatis 的通用配置
     *
     * @param configuration 配置
     */
    @Override
    public void customize(MybatisConfiguration configuration) {
    }
}
