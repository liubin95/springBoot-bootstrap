package com.caomu.bootstrap.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.caomu.bootstrap.constant.CommonConstant;

/**
 * 请求添加id拦截器。
 *
 * @author liubin
 */
@Component
public class RequestIdFilter extends OncePerRequestFilter {


    private final IdentifierGenerator idGenerator;

    public RequestIdFilter(IdentifierGenerator idGenerator) {this.idGenerator = idGenerator;}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        MDC.put(CommonConstant.REQUEST_ID_HEADER, idGenerator.nextUUID(null));
        MDC.put(CommonConstant.START_TIME, String.valueOf(System.currentTimeMillis()));
        filterChain.doFilter(request, response);
    }


}
