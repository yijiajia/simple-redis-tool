package com.example.springredisdemo.intercept;

import com.example.springredisdemo.annotation.ControllerLimiter;
import com.example.springredisdemo.service.LimitService;
import com.example.springredisdemo.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 根据Ip进行限流
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class ControllerLimiterAspect {

    @Autowired
    private LimitService limitService;

    @Pointcut("@annotation(com.example.springredisdemo.annotation.ControllerLimiter)")
    private void check() {}

    @Before("check()")
    public void before(JoinPoint joinPoint) throws Exception {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        ControllerLimiter rateLimiter = methodSignature.getMethod().getAnnotation(ControllerLimiter.class);
        if(rateLimiter != null) {
            int limit = rateLimiter.limit();
            int limitTime = rateLimiter.limitTime();
            TimeUnit unit = rateLimiter.unit();

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String ip = IpUtils.getRequestIp(request);

            String key = limitService.generateKey(1,ip);
            if(limitService.limit(key,limit,limitTime,unit)) {
                log.info("method has been limited；ip：{}，limit：{}，limitTime：{}",ip,limit,limitTime);
                throw new RuntimeException("method has been limited");
            }
        }
    }
}
