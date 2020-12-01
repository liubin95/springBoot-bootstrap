package com.caomu.bootstrap.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.caomu.bootstrap.constant.CommonConstant;
import com.caomu.bootstrap.domain.BaseEntity;
import com.caomu.bootstrap.domain.BaseUserDetail;
import com.caomu.bootstrap.token.TokenUtil;

/**
 * 解析token，并且填充SecurityContextHolder中的用户信息
 *
 * @author 刘斌
 */
public class TokenFilter extends BasicAuthenticationFilter {

    public static final Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);

    private final TokenUtil<BaseEntity> baseEntityTokenUtil;

    private final RMapCache<Long, BaseUserDetail> mapCache;

    public TokenFilter(TokenUtil<BaseEntity> baseEntityTokenUtil, RMapCache<Long, BaseUserDetail> authIdUserMap,
                       AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.mapCache = authIdUserMap;
        this.baseEntityTokenUtil = baseEntityTokenUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(CommonConstant.HEADER_TOKEN_KEY);
        if (StringUtils.isNotBlank(token)) {
            token = token.replace(CommonConstant.TOKEN_PREFIX, "");
            final BaseEntity entity = baseEntityTokenUtil.resolveToken(token);
            if (mapCache.containsKey(entity.getId())) {
                final BaseUserDetail baseUserDetail = mapCache.get(entity.getId());
                // 用户id多个token
                if (Objects.equals(baseUserDetail.getToken(), token)) {
                    final Authentication authentication = new UsernamePasswordAuthenticationToken(entity.getId(), null, baseUserDetail.getAuthorities());
                    //存放认证信息，如果没有走这一步，下面的doFilter就会提示登录了
                    SecurityContextHolder.getContext()
                                         .setAuthentication(authentication);
                } else {
                    LOGGER.error("用户多个token同时使用，用户id：{}", baseUserDetail.getId());
                }
            }
        }
        // 调用后续的Filter,如果上面的代码逻辑未能复原“session”，SecurityContext中没有想过信息，后面的流程会检测出"需要登录"
        filterChain.doFilter(request, response);
    }
}
