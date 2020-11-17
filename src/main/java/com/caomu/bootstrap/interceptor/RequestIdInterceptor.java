package com.caomu.bootstrap.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


    @Resource
    private IdentifierGenerator idGenerator;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        Object requestId = httpServletRequest.getAttribute(CommonConstant.REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = idGenerator.nextUUID(null);
        }
        httpServletRequest.setAttribute(CommonConstant.REQUEST_ID_HEADER, requestId);
        return true;
    }
}
