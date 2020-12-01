package com.caomu.bootstrap.config.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.caomu.bootstrap.config.BusinessRuntimeException;
import com.caomu.bootstrap.config.security.AuthenticationHandler;
import com.caomu.bootstrap.constant.CommonConstant;
import com.caomu.bootstrap.domain.Result;


/**
 * 全局异常捕获。
 *
 * @author liubin
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局异常捕获。业务异常
     *
     * @param exception 异常。
     * @return re
     */
    @ExceptionHandler(BusinessRuntimeException.class)
    public Result handleMyRuntimeException(BusinessRuntimeException exception) {
        final Result result = new Result();
        LOGGER.info("业务异常：", exception);
        result.setSucceeded(false);
        result.setMsg(exception.getMessage());
        return result;
    }

    /**
     * 全局异常捕获。参数校验异常
     * 表单提交的异常
     *
     * @param exception 异常。
     * @return re
     */
    @ExceptionHandler({BindException.class})
    public Result handleBindException(BindException exception) {
        final Result result = new Result();
        LOGGER.info("参数校验异常：", exception);
        result.setSucceeded(false);
        result.setMsg(dealObjectErrorList(exception.getBindingResult()
                                                   .getAllErrors()));
        return result;
    }

    /**
     * 全局异常捕获。参数校验异常
     * 请求体的异常
     *
     * @param exception 异常。
     * @return re
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        final Result result = new Result();
        LOGGER.info("参数校验异常：", exception);
        result.setSucceeded(false);
        result.setMsg(dealObjectErrorList(exception.getBindingResult()
                                                   .getAllErrors()));
        return result;
    }

    /**
     * 处理ObjectError的集合
     *
     * @param allErrors 错误集合
     * @return 拼接的字符串
     */
    private String dealObjectErrorList(List<ObjectError> allErrors) {
        final StringBuilder stringBuilder = new StringBuilder("参数校验异常：");
        allErrors.forEach(item -> stringBuilder.append(item.getDefaultMessage()));
        return stringBuilder.toString();
    }

    /**
     * 权限不足的异常 会在{@link AuthenticationHandler#handle} 中处理
     *
     * @param accessDeniedException 权限异常
     * @throws AccessDeniedException 权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException accessDeniedException) throws AccessDeniedException {
        throw accessDeniedException;
    }

    /**
     * 全局异常捕获。
     *
     * @param exception 异常。
     * @return re
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception exception) {
        final Result result = new Result();
        LOGGER.error("ApiException 异常抛出", exception);
        result.setSucceeded(false);
        result.setMsg(String.format("服务器异常，请重试，或者联系管理员。%s", MDC.get(CommonConstant.REQUEST_ID_HEADER)));
        return result;
    }
}
