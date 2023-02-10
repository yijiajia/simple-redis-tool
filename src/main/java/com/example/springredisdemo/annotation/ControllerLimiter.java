package com.example.springredisdemo.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerLimiter {

    /**
     * 限制次数
     * @return
     */
    int limit() default 1;

    /**
     * 限制时间
     */
    int limitTime() default 1;

    /**
     * 时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;


}
