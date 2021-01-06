package com.caomu.bootstrap.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.caomu.bootstrap.config.CaoMuProperties;
import com.caomu.bootstrap.constant.CommonConstant;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * token相关工具类
 *
 * @author 刘斌
 */
@Component
@Scope("prototype")
public class TokenUtil {

    private final CaoMuProperties caoMuProperties;

    public TokenUtil(CaoMuProperties caoMuProperties) {this.caoMuProperties = caoMuProperties;}

    /**
     * 保存对象信息，生成token
     *
     * @param userId   保存的用户id
     * @param duration 可以定义过期时间
     * @return token
     */
    public String generateToken(Long userId,
                                Duration duration) {

        final Map<String, Object> tokenHeader = new HashMap<>(2);
        tokenHeader.put("alg", "HMAC256");
        tokenHeader.put("typ", "JWT");
        //设置过期时间
        Instant inst = Instant.now()
                              .plus(duration);
        final Date expiresDate = Date.from(inst);
        return JWT.create()
                  .withHeader(tokenHeader)
                  .withClaim(CommonConstant.TOKEN_USER_KEY, userId)
                  .withIssuedAt(new Date())
                  .withExpiresAt(expiresDate)
                  .sign(Algorithm.HMAC256(caoMuProperties.getTokenSecret()));
    }

    /**
     * 保存对象信息，生成token，配置文件中的过期时间
     *
     * @param userId 保存的用户id
     * @return token
     */
    public String generateToken(Long userId) {
        return this.generateToken(userId, caoMuProperties.getTokenExpiresTime());
    }


    /**
     * 解析token
     *
     * @param token token
     * @return 对象
     */
    public Long resolveToken(String token) {
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(caoMuProperties.getTokenSecret()))
                                     .build();
        DecodedJWT         jwt = jwtVerifier.verify(token);
        Map<String, Claim> map = jwt.getClaims();
        return map.get(CommonConstant.TOKEN_USER_KEY)
                  .asLong();
    }

    /**
     * 获取Security上下文中用户id
     *
     * @return id
     */
    public Long userIdFromSecurity() {
        return (long) SecurityContextHolder.getContext()
                                           .getAuthentication()
                                           .getPrincipal();
    }

}