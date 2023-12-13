package com.pdx.annotation;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/12
 * @Description: 用于防刷限流的注解 默认每秒限制5次
 */
public @interface CurrentLimit {

    /** 限流的key */
    String key() default "limit:";

    /** 周期,单位是秒 */
    int cycle() default 5;

    /** 请求次数 */
    int count() default 1;

    /** 默认提示信息 */
    String message() default "请勿重复点击";
}
