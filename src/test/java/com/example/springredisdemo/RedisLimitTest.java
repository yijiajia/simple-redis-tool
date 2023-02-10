package com.example.springredisdemo;

import com.example.springredisdemo.service.LimitService;
import com.example.springredisdemo.service.LockService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisLimitTest {

    int limit = 10;

    @Autowired
    private LimitService limitService;

    /**
     * 手机号 135***997 在 1小时内限制10次发送
     */
    @Test
    public void testLimitService() {
        String key = limitService.generateKey(1,"135*****997");
        for(int i=0;i<11;i++) {
            System.out.println("是否限流：" + limitService.limit(key,limit,10, TimeUnit.HOURS));
        }
    }

}
