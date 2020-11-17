package com.caomu.bootstrap.config;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.caomu.bootstrap.constant.CommonConstant;
import com.caomu.bootstrap.interceptor.OptionsInterceptor;
import com.caomu.bootstrap.interceptor.RequestIdInterceptor;
import com.caomu.bootstrap.interceptor.TokenInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;


/**
 * web配置类。
 *
 * @author liubin
 */
@Configuration
@EnableWebMvc
public class SpringWebConfig implements WebMvcConfigurer {


    @Resource
    private CaoMuProperties caoMuProperties;


    /**
     * token拦截器。
     */
    @Resource
    private TokenInterceptor tokenInterceptor;

    /**
     * options请求拦截器。
     */
    @Resource
    private OptionsInterceptor optionsInterceptor;

    /**
     * 请求加唯一id拦截器
     */
    @Resource
    private RequestIdInterceptor requestIdInterceptor;


    /**
     * 全局跨域。
     *
     * @param registry registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 对所有的URL配置
        registry.addMapping("/**");
    }

    /**
     * 注册拦截器。
     *
     * @param registry registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 对所有的URL配置
        registry.addInterceptor(optionsInterceptor).addPathPatterns("/**");
        registry.addInterceptor(requestIdInterceptor).addPathPatterns("/**");
        // 排除配置的接口
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**").excludePathPatterns(caoMuProperties.getTokenExcludeUrl());
    }

    /**
     * 入参转换
     *
     * @param registry 对象
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {

        // 不能替换为lambda表达式
        registry.addConverter(new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                return LocalDateTime.parse(source, CommonConstant.DATE_TIME_FORMATTER);
            }
        });

        registry.addConverter(new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                return LocalDate.parse(source, CommonConstant.DATE_FORMATTER);
            }
        });

        registry.addConverter(new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(String source) {
                return LocalTime.parse(source, CommonConstant.TIME_FORMATTER);
            }
        });
    }

    /**
     * 全局的消息转换
     *
     * @param converters 集合
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(customJackson2HttpMessageConverter());
    }

    /**
     * jackson json 转换器
     *
     * @return 对象
     */
    @Bean
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        // 序列化设置
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setTimeZone(TimeZone.getDefault());
        // 序列换成json时,将所有的long变成string
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // 日期序列化设置
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(CommonConstant.DATE_TIME_FORMATTER_STRING)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(CommonConstant.DATE_TIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(CommonConstant.DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(CommonConstant.TIME_FORMATTER));

        objectMapper.registerModule(simpleModule).registerModule(javaTimeModule);
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }
}
