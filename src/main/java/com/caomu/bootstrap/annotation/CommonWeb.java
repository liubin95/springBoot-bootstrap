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
import com.caomu.bootstrap.config.GlobalExceptionHandler;
import com.caomu.bootstrap.config.GlobalResponseBodyHandler;
import com.caomu.bootstrap.config.SpringWebConfig;
import com.caomu.bootstrap.config.WebLogAspect;
import com.caomu.bootstrap.config.mybatisplus.MybatisPlusConfig;
import com.caomu.bootstrap.interceptor.OptionsInterceptor;
import com.caomu.bootstrap.interceptor.RequestIdInterceptor;
import com.caomu.bootstrap.interceptor.TokenInterceptor;
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
@Import({BeanConfig.class, GlobalExceptionHandler.class, GlobalResponseBodyHandler.class,
        SpringWebConfig.class, TokenInterceptor.class, OptionsInterceptor.class,
        RequestIdInterceptor.class, WebLogAspect.class, TokenUtil.class,
        MybatisPlusConfig.class})
public @interface CommonWeb {
}
