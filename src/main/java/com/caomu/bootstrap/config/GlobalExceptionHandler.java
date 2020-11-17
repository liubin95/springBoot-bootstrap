package com.caomu.bootstrap.config;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        LOGGER.info("{}-业务异常：", getRequestId(), exception);
        result.setSucceeded(false);
        result.setMsg(exception.getMessage());
        return result;
    }

    /**
     * 全局异常捕获。参数校验异常@Valid
     *
     * @param exception 异常。
     * @return re
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolationException(ConstraintViolationException exception) {
        final Result result = new Result();
        LOGGER.info("{}-参数校验异常：", getRequestId(), exception);
        result.setSucceeded(false);
        result.setMsg("参数校验异常：" + exception.getMessage());
        return result;
    }

    /**
     * 全局异常捕获。参数校验异常@Validated
     *
     * @param exception 异常。
     * @return re
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleBindException(MethodArgumentNotValidException exception) {
        final Result result = new Result();
        LOGGER.info("{}-参数校验异常：", getRequestId(), exception);
        final List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        final StringBuilder stringBuilder = new StringBuilder("参数校验异常：");
        allErrors.forEach(item -> stringBuilder.append(item.getDefaultMessage()));
        result.setSucceeded(false);
        result.setMsg(stringBuilder.toString());
        return result;
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
        LOGGER.error("{}-ApiException 异常抛出", getRequestId(), exception);
        result.setSucceeded(false);
        result.setMsg("服务器异常，请重试，或者联系管理员。");
        return result;
    }

    /**
     * 获取request的id
     *
     * @return request的UUID
     */
    private Object getRequestId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        return request.getAttribute(CommonConstant.REQUEST_ID_HEADER);
    }
}