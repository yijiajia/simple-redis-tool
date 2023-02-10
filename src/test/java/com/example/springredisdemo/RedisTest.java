package com.example.springredisdemo;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Value("${spring.redis.host}")
    private String ip;

    @Value("${spring.redis.port}")
    private String port;

    @Test
    public void testRedis() {
        Jedis jedis = new Jedis(ip, Integer.parseInt(port));
        System.out.println("redis 连接状态：" + jedis.ping());
    }
}
