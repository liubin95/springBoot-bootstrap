package com.caomu.bootstrap.config.web;

import com.caomu.bootstrap.annotation.IgnoreFormattedReturn;
import com.caomu.bootstrap.constant.CommonConstant;
import com.caomu.bootstrap.domain.Result;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;


/**
 * 处理解析 {@link ResponseBodyAdvice} 统一返回包装器
 *
 * @author 刘斌
 */
@RestControllerAdvice
public class GlobalResponseBodyHandler implements ResponseBodyAdvice<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalResponseBodyHandler.class);

    @Resource
    private Gson gson;

    @Override
    public boolean supports(@Nullable MethodParameter returnType,
                            @Nullable Class<? extends HttpMessageConverter<?>> converterType) {

        return true;
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Override
    public Object beforeBodyWrite(Object body,
                                  @Nullable MethodParameter returnType,
                                  @Nullable MediaType selectedContentType,
                                  @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @Nullable ServerHttpRequest request,
                                  @Nullable ServerHttpResponse response) {
        // 开始打印请求日志
        LOGGER.info("Response Args  : {}", gson.toJson(body));
        LOGGER.info("请求完成，耗时{}毫秒", System.currentTimeMillis() - (Long.parseLong(MDC.get(CommonConstant.START_TIME))));
        MDC.clear();
        final Method method = Objects.requireNonNull(returnType)
                                     .getMethod();
        if (Objects.requireNonNull(method)
                   .isAnnotationPresent(IgnoreFormattedReturn.class)) {
            return body;
        }

        //返回值为空
        if (body == null) {
            return new Result();
        }
        //返回值为result
        if (body instanceof Result) {
            return body;
        }
        //返回值为string
        //Assert.isTrue(!(body instanceof String), "do not return String");
        final Result result = new Result();
        result.setObj(body);
        return result;
    }

}
