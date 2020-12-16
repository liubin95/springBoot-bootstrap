package com.caomu.bootstrap.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.caomu.bootstrap.constant.CommonConstant;
import com.caomu.bootstrap.domain.BaseEntity;
import com.caomu.bootstrap.domain.BaseUserDetail;
import com.caomu.bootstrap.token.TokenUtil;
import com.google.gson.*;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 对象配置
 *
 * @author 刘斌
 */
@Configuration
public class BeanConfig {


    private final RedissonClient redissonClient;

    public BeanConfig(RedissonClient redissonClient) {this.redissonClient = redissonClient;}


    @Bean
    public Gson gson() {

        return new GsonBuilder().setDateFormat(CommonConstant.DATE_TIME_FORMATTER_STRING)
                                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(CommonConstant.DATE_TIME_FORMATTER)))
                                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(CommonConstant.DATE_FORMATTER)))
                                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(CommonConstant.TIME_FORMATTER)))
                                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> LocalTime.parse(json.getAsString(), CommonConstant.TIME_FORMATTER))
                                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> LocalDate.parse(json.getAsString(), CommonConstant.DATE_FORMATTER))
                                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), CommonConstant.DATE_TIME_FORMATTER))
                                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                                .create();
    }

    @Bean
    public IdentifierGenerator idGenerator() {

        return new DefaultIdentifierGenerator();
    }

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public TokenUtil<BaseEntity> baseEntityTokenUtil() {

        return new TokenUtil<>(BaseEntity.class);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public RMapCache<Long, BaseUserDetail> authIdUserMap() {

        return redissonClient.getMapCache(CommonConstant.REDIS_AUTH_MAP_NAME);
    }

}
