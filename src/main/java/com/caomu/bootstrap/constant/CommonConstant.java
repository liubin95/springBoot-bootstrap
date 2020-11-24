package com.caomu.bootstrap.constant;

import java.time.format.DateTimeFormatter;

/**
 * 常量
 *
 * @author 刘斌
 */
public interface CommonConstant {

    /**
     * 设置响应头，响应头名字（惯例是大写）
     */
    String REQUEST_ID_HEADER = "REQUEST_ID";

    /**
     * 请求开始时间
     */
    String START_TIME = "start_time";

    /**
     * token在header中的key
     */
    String HEADER_TOKEN_KEY = "token";


    /**
     * 日期-时间格式化-字符串
     */
    String DATE_TIME_FORMATTER_STRING = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期-时间格式化
     */
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_STRING);


    /**
     * 日期格式化
     */
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * 时间格式化
     */
    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");


    /**
     * token中用户的key
     */
    String TOKEN_USER_KEY = "customer";
}
