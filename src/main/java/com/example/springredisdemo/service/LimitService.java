package com.example.springredisdemo.service;

import com.example.springredisdemo.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LimitService {
    private static final String PRE = "limit";

    @Autowired
    private RedisUtils redisUtils;

    private DefaultRedisScript<Long> redisScript;

    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
    }

    /**
     * @param key       key
     * @param limit     限制次数
     * @param limitTime 限制时间，单位秒
     * @return true 表示超过阈值
     */
    public boolean limit(String key,int limit,int limitTime) {
        return limit(key, limit, limitTime,TimeUnit.SECONDS);
    }

    public boolean limit(String key, int limit, int limitTime, TimeUnit unit) {
        switch (unit) {
            case DAYS:
                limitTime = limitTime * 24 * 60 * 60;
                break;
            case HOURS:
                limitTime = limitTime * 60 * 60;
                break;
            case MINUTES:
                limitTime = limitTime * 60;
                break;
            case MILLISECONDS:
                limitTime = limitTime / 1000;
                break;
        }
        long nowTime = System.currentTimeMillis() / 1000;
        String uuid = UUID.randomUUID().toString();
        return redisUtils.execLimitLua(redisScript, Collections.singletonList(key),String.valueOf(limitTime),String.valueOf(nowTime),String.valueOf(limit),uuid);
    }

    /**
     * key生成
     * @param biz 业务
     * @param unionId 唯一id
     * @return
     */
    public String generateKey(int biz,String unionId) {
        return PRE + "_" + biz + "_" + unionId;
    }

}
