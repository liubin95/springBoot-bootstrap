package com.caomu.bootstrap.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.caomu.bootstrap.constant.CommonConstant;

/**
 * 请求添加id拦截器。
 *
 * @author liubin
 */
@Component
public class RequestIdInterceptor implements HandlerInterceptor {


    public static final Logger LOGGER = LoggerFactory.getLogger(RequestIdInterceptor.class);


    @Resource
    private IdentifierGenerator idGenerator;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        MDC.put(CommonConstant.REQUEST_ID_HEADER, idGenerator.nextUUID(null));
        MDC.put(CommonConstant.START_TIME, String.valueOf(System.currentTimeMillis()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LOGGER.info("请求完成，耗时{}毫秒", System.currentTimeMillis() - (Long.parseLong(MDC.get(CommonConstant.START_TIME))));
        MDC.clear();
    }
}
