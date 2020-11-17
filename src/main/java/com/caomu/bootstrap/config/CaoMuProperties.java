package com.caomu.bootstrap.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 个性化配置
 *
 * @author 刘斌
 */
@ConfigurationProperties(prefix = "cao-mu")
@EnableConfigurationProperties(CaoMuProperties.class)
public class CaoMuProperties {
    /**
     * 排除token校验的路径
     */
    private List<String> tokenExcludeUrl = new ArrayList<>();

    /**
     * token的秘钥
     */
    private String tokenSecret = "caoMu2020";

    /**
     * token过期时间
     */
    private Duration tokenExpiresTime = Duration.ofHours(2);


    public List<String> getTokenExcludeUrl() {
        return tokenExcludeUrl;
    }

    public void setTokenExcludeUrl(List<String> tokenExcludeUrl) {
        this.tokenExcludeUrl = tokenExcludeUrl;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public Duration getTokenExpiresTime() {
        return tokenExpiresTime;
    }

    public void setTokenExpiresTime(Duration tokenExpiresTime) {
        this.tokenExpiresTime = tokenExpiresTime;
    }
}
