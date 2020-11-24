package com.caomu.bootstrap.interceptor;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.caomu.bootstrap.config.CaoMuProperties;
import com.caomu.bootstrap.constant.CommonConstant;

/**
 * token拦截器。
 *
 * @author liubin
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final Logger LOGGER = LoggerFactory.getLogger(TokenInterceptor.class);

    @Resource
    private CaoMuProperties caoMuProperties;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws IOException {
        // 从 http 请求头中取出 token
        String token = httpServletRequest.getHeader(CommonConstant.HEADER_TOKEN_KEY);

        // 执行认证
        if (token == null || StringUtils.isBlank(token)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token为空");
            LOGGER.error("token为空：{}", ((HandlerMethod) object).getShortLogMessage());
            return false;
        } else {
            try {
                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(caoMuProperties.getTokenSecret())).build();
                DecodedJWT jwt = jwtVerifier.verify(token);
                // 判断是否过期
                if (jwt.getExpiresAt().before(new Date())) {
                    httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "token过期");
                    LOGGER.error("token过期：{}", ((HandlerMethod) object).getShortLogMessage());
                    return false;
                }
            } catch (JWTDecodeException e) {
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "token校验失败");
                LOGGER.error("token校验失败：{}", ((HandlerMethod) object).getShortLogMessage());
                return false;
            } catch (TokenExpiredException e) {
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "token过期");
                LOGGER.error("token过期：{}", ((HandlerMethod) object).getShortLogMessage());
                return false;
            } catch (SignatureVerificationException e) {
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "token错误");
                LOGGER.error("token错误：{}", ((HandlerMethod) object).getShortLogMessage());
                return false;
            }
        }
        return true;
    }
}
