package com.caomu.bootstrap.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.caomu.bootstrap.config.CaoMuProperties;
import com.caomu.bootstrap.constant.CommonConstant;
import com.caomu.bootstrap.domain.BaseUserDetail;
import com.caomu.bootstrap.domain.Result;
import com.caomu.bootstrap.token.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 登录成功、失败、注销、未登录回调的拦截器
 *
 * @author 刘斌
 */
@Component(value = "AuthenticationHandler")
public class AuthenticationHandler implements AuthenticationFailureHandler, AuthenticationSuccessHandler, AuthenticationEntryPoint, LogoutSuccessHandler, AccessDeniedHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationHandler.class);

    private final ObjectMapper objectMapper;

    private final RMapCache<Long, BaseUserDetail> authIdUserMap;

    private final TokenUtil tokenUtil;

    private final CaoMuProperties caoMuProperties;

    @Autowired
    public AuthenticationHandler(ObjectMapper objectMapper,
                                 RMapCache<Long, BaseUserDetail> authIdUserMap,
                                 TokenUtil tokenUtil,
                                 CaoMuProperties caoMuProperties) {
        this.objectMapper    = objectMapper;
        this.authIdUserMap   = authIdUserMap;
        this.tokenUtil       = tokenUtil;
        this.caoMuProperties = caoMuProperties;
    }

    /**
     * 登录失败回调
     * 可能是用户名密码错误
     * 或者是账号状态异常
     *
     * @param request   请求
     * @param response  响应
     * @param exception 异常
     * @throws IOException 异常
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        final Result result = new Result();
        result.setSucceeded(false);
        result.setMsg(exception.getMessage());
        writeResponse(response, result);
    }

    /**
     * 登录成功回调
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 异常
     * @throws IOException 异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        BaseUserDetail baseUserDetail = (BaseUserDetail) authentication.getPrincipal();
        final Result   result         = new Result();
        // 解析token
        Assert.isTrue(StringUtils.isNotBlank(baseUserDetail.getToken()), "token is blank");
        final String token = baseUserDetail.getToken();
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(caoMuProperties.getTokenSecret()))
                                     .build();
        DecodedJWT jwt = jwtVerifier.verify(token);
        // 过期时间
        final Date expiresAt = jwt.getExpiresAt();
        // 根据token的过期时间，计算出redis的过期时间
        final long seconds = Math.abs(ChronoUnit.SECONDS.between(expiresAt.toInstant(), Instant.now()));
        if (authIdUserMap.containsKey(baseUserDetail.getId())) {
            // 顶号
            LOGGER.info("重复登录,用户id：{}，ip：{}", baseUserDetail.getId(), request.getRemoteAddr());
        }
        // 权限数据存入redis
        authIdUserMap.put(baseUserDetail.getId(), baseUserDetail, seconds, TimeUnit.SECONDS);
        result.setObj(baseUserDetail);
        writeResponse(response, result);
    }


    /**
     * token无效
     *
     * @param request       请求
     * @param response      响应
     * @param authException 异常
     * @throws IOException io异常
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        LOGGER.error("尚未登录，用户ip：{}，路径：{}", request.getRemoteAddr(), request.getRequestURI());
        final Result result = new Result();
        result.setSucceeded(false);
        result.setMsg("尚未登录，请先登录。");
        writeResponse(response, result);
    }

    /**
     * 注销的成功回调
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 异常
     * @throws IOException 异常
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        final long userId = (long) authentication.getPrincipal();
        authIdUserMap.remove(userId);
        final Result result = new Result();
        result.setMsg("退出成功。");
        writeResponse(response, result);
    }


    /**
     * 权限不足 拦截器
     *
     * @param request               请求
     * @param response              响应
     * @param accessDeniedException 异常
     * @throws IOException 异常
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        LOGGER.error("权限不足，用户id：{}，路径：{}", tokenUtil.userIdFromSecurity(), request.getRequestURI());
        final Result result = new Result();
        result.setSucceeded(false);
        result.setMsg(accessDeniedException.getMessage());
        writeResponse(response, result);
    }

    /**
     * 向相应中写数据
     *
     * @param httpServletResponse 响应
     * @param result              数据
     * @throws IOException io异常
     */
    private void writeResponse(HttpServletResponse httpServletResponse,
                               Result result) throws IOException {
        httpServletResponse.setContentType(CommonConstant.RESPONSE_CONTENT_TYPE);
        PrintWriter out = httpServletResponse.getWriter();
        out.write(objectMapper.writeValueAsString(result));
        out.flush();
        out.close();
    }

}
