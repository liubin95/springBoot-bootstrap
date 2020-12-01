package com.caomu.bootstrap.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.caomu.bootstrap.config.BeanConfig;
import com.caomu.bootstrap.config.CaoMuProperties;
import com.caomu.bootstrap.config.data.DruidConfig;
import com.caomu.bootstrap.config.data.MybatisPlusConfig;
import com.caomu.bootstrap.config.security.AuthenticationHandler;
import com.caomu.bootstrap.config.security.WebSecurityConfig;
import com.caomu.bootstrap.config.web.GlobalExceptionHandler;
import com.caomu.bootstrap.config.web.GlobalResponseBodyHandler;
import com.caomu.bootstrap.config.web.SpringWebConfig;
import com.caomu.bootstrap.config.web.WebLogAspect;
import com.caomu.bootstrap.filter.RequestIdFilter;
import com.caomu.bootstrap.token.TokenUtil;

/**
 * 通用的web
 *
 * @author 刘斌
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableConfigurationProperties({CaoMuProperties.class})
@Import({BeanConfig.class, GlobalExceptionHandler.class, GlobalResponseBodyHandler.class, SpringWebConfig.class, WebLogAspect.class, TokenUtil.class, MybatisPlusConfig.class, DruidConfig.class, AuthenticationHandler.class, WebSecurityConfig.class, RequestIdFilter.class})
public @interface CommonWeb {
}
