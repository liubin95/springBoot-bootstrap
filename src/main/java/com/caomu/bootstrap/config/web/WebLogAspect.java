package com.caomu.bootstrap.config.web;

import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * web日志打印。
 *
 * @author liubin
 */
@Aspect
@Component
public class WebLogAspect {


    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogAspect.class);

    private final Gson gson;

    public WebLogAspect(Gson gson) {this.gson = gson;}


    /**
     * 以 controller 包下定义的所有请求为切入点.
     */
    @Pointcut("execution(* com.caomu..*.controller..*.*(..))")
    public void webLog() {

    }

    /**
     * 在切点之前织入
     *
     * @param joinPoint joinPoint对象
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        // 打印请求相关参数

        // 打印请求 url
        LOGGER.info("URL            : {}", request.getRequestURL()
                                                  .toString());
        // 打印 Http method
        LOGGER.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        LOGGER.info("Class Method   : {}.{}", joinPoint.getSignature()
                                                       .getDeclaringTypeName(), joinPoint.getSignature()
                                                                                         .getName());
        // 打印请求的 IP
        LOGGER.info("IP             : {}", request.getRemoteAddr());
        //访问目标方法的参数：
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof HttpServletResponse) {
                continue;
            }
            if (arg instanceof HttpServletRequest) {
                continue;
            }
            // 打印请求入参
            LOGGER.info("Request Args   : {}", gson.toJson(arg));
        }
    }

}
