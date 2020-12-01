package com.caomu.bootstrap.config.security;

import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import com.caomu.bootstrap.config.CaoMuProperties;
import com.caomu.bootstrap.domain.BaseEntity;
import com.caomu.bootstrap.domain.BaseUserDetail;
import com.caomu.bootstrap.filter.RequestIdFilter;
import com.caomu.bootstrap.filter.TokenFilter;
import com.caomu.bootstrap.token.TokenUtil;

/**
 * spring-security 的相关配置
 *
 * @author 刘斌
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final AuthenticationFailureHandler failureHandler;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AccessDeniedHandler accessDeniedHandler;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil<BaseEntity> baseEntityTokenUtil;
    private final RMapCache<Long, BaseUserDetail> authIdUserMap;
    private final CaoMuProperties caoMuProperties;
    private final RequestIdFilter requestIdFilter;
    /**
     * 需要自己装配一个
     */
    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(PasswordEncoder passwordEncoder, TokenUtil<BaseEntity> baseEntityTokenUtil,
                             CaoMuProperties caoMuProperties, RMapCache<Long, BaseUserDetail> authIdUserMap,
                             RequestIdFilter requestIdFilter, UserDetailsService userDetailsService,
                             @Qualifier("AuthenticationHandler") AuthenticationFailureHandler failureHandler,
                             @Qualifier("AuthenticationHandler") AuthenticationSuccessHandler successHandler,
                             @Qualifier("AuthenticationHandler") AuthenticationEntryPoint authenticationEntryPoint,
                             @Qualifier("AuthenticationHandler") LogoutSuccessHandler logoutSuccessHandler,
                             @Qualifier("AuthenticationHandler") AccessDeniedHandler accessDeniedHandler) {
        this.passwordEncoder = passwordEncoder;
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.baseEntityTokenUtil = baseEntityTokenUtil;
        this.caoMuProperties = caoMuProperties;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authIdUserMap = authIdUserMap;
        this.requestIdFilter = requestIdFilter;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String[] strings = caoMuProperties.getTokenExcludeUrl()
                                                .toArray(new String[0]);
        http.authorizeRequests()
            // 需要排除的接口
            .antMatchers(strings)
            .permitAll()
            .anyRequest()
            .authenticated();

        // 添加JWT鉴权过滤器
        http.addFilterAfter(new TokenFilter(baseEntityTokenUtil, authIdUserMap, authenticationManager()), SecurityContextPersistenceFilter.class)
            // 请求id的过滤器
            .addFilterBefore(requestIdFilter, SecurityContextPersistenceFilter.class)
            // 不使用session
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 登录模块
            .formLogin()
            // 设置登录接口
            .loginProcessingUrl(caoMuProperties.getLoginProcessingUrl())
            .permitAll()
            .failureHandler(failureHandler)
            .successHandler(successHandler)
            // 没有登录
            .and()
            .exceptionHandling()
            // 未登录处理
            .authenticationEntryPoint(authenticationEntryPoint)
            // 权限不足
            .accessDeniedHandler(accessDeniedHandler)
            .and()
            //注销
            .logout()
            .logoutUrl(caoMuProperties.getLogoutUrl())
            .permitAll()
            .logoutSuccessHandler(logoutSuccessHandler);
        //开启跨域访问
        http.cors()
            .disable()
            //开启模拟请求，比如API POST测试工具的测试，不开启时，API POST为报403错误
            .csrf()
            .disable();
    }

    /**
     * 配置使用的UserDetailsService 和密码解析
     *
     * @param auth auth对象
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        auth.authenticationProvider(authProvider);
    }
}
