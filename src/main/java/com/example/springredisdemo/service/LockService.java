package com.example.springredisdemo.service;

import com.example.springredisdemo.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class LockService {

    @Autowired
    private RedisUtils redisUtils;

    private DefaultRedisScript<Long> redisScript;


    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("unLock.lua")));
    }

    @Value("${redis.lock.sleep-time}")
    private int sleepTime;

    @Value("${redis.lock.time-out}")
    private int timeOut;

    /**
     * 在指定时间范围内获取锁，堵塞
     * @param key
     * @param value
     * @param blockTime 获取锁的堵塞时间
     * @return
     * @throws InterruptedException·
     */
    public boolean lock(String key,String value,int blockTime,AtomicInteger tryCount) throws InterruptedException {
        while(blockTime >= 0) {
            tryCount.incrementAndGet();
            if(redisUtils.setNx(key,value,timeOut)) {
                return true;
            }
            log.info("未获取锁，休眠后尝试再获取：value：" + value);
            blockTime = blockTime - sleepTime;
            Thread.sleep(sleepTime);
        }
        return false;
    }

    /**
     * 尝试获取锁，不堵塞
     * @param key
     * @param value
     * @return
     */
    public boolean tryLock(String key, String value) {
        if(redisUtils.setNx(key,value,timeOut)) {
            return true;
        }
        return false;
    }

    /**
     * 解锁
     * @param key
     * @param sign
     * @return
     */
    public boolean unLock(String key,String sign) {
        return redisUtils.execLimitLua(redisScript, Collections.singletonList(key),sign);
    }

}
