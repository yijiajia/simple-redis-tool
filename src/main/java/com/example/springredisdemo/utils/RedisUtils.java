package com.example.springredisdemo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Boolean setNx(String key,String value,int timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key,value,timeout, TimeUnit.SECONDS);
    }

    /**
     * 执行脚本
     * @param redisScript
     * @param keys
     * @param args
     * @return
     */
    public Boolean execLimitLua(RedisScript<Long> redisScript, List<String> keys,String... args) {
        try {
            Long result = redisTemplate.execute(redisScript,keys,args);
            return result != null && result.intValue() == 1;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
