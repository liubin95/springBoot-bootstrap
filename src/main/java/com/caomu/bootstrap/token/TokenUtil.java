package com.caomu.bootstrap.token;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.caomu.bootstrap.config.CaoMuProperties;
import com.caomu.bootstrap.constant.CommonConstant;
import com.caomu.bootstrap.domain.BaseEntity;
import com.google.gson.Gson;

/**
 * token相关工具类
 *
 * @author 刘斌
 */
@Component
@Scope("prototype")
public class TokenUtil<T extends BaseEntity> {

    @Resource
    private CaoMuProperties caoMuProperties;

    @Resource
    private Gson gson;

    private Type t;

    private TokenUtil() {
    }

    public TokenUtil(Type t) {
        Assert.notNull(t, "type can not be null");
        this.t = t;
    }

    /**
     * 保存对象信息，生成token
     *
     * @param object 保存的用户信息
     * @return token
     */
    public String generateToken(T object) {
        final Map<String, Object> tokenHeader = new HashMap<>(2);
        tokenHeader.put("alg", "HMAC256");
        tokenHeader.put("typ", "JWT");
        //设置过期时间
        final Instant inst = Instant.now().plus(caoMuProperties.getTokenExpiresTime());
        final Date expiresDate = Date.from(inst);
        return JWT.create().withHeader(tokenHeader).withClaim(CommonConstant.TOKEN_USER_KEY, gson.toJson(object)).withIssuedAt(new Date())
                .withExpiresAt(expiresDate).sign(Algorithm.HMAC256(caoMuProperties.getTokenSecret()));
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 对象
     */
    public T resolveToken(String token) {
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(caoMuProperties.getTokenSecret())).build();
        DecodedJWT jwt = jwtVerifier.verify(token);
        Map<String, Claim> map = jwt.getClaims();
        return gson.fromJson(map.get(CommonConstant.TOKEN_USER_KEY).asString(), t);
    }
}