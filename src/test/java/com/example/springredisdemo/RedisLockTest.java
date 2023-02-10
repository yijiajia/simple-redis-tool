package com.example.springredisdemo;

import com.example.springredisdemo.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisLockTest {

    String key = "lock_test";
    int blockTime = 1000;    // 最多堵塞 1s

    @Autowired
    private LockService lockService;

    @Test
    public void testLock() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int clientNum = 10;
        CountDownLatch countDownLatch = new CountDownLatch(10);
        ExecutorService service = Executors.newFixedThreadPool(10);
        for(int i = 0 ;i < clientNum; i++) {
            TryLockTask task = new TryLockTask(lockService,countDownLatch);
            service.submit(task);
        }
        Thread.sleep(10);
        countDownLatch.await();
        System.out.println("任务提交完成；执行时长：" + (System.currentTimeMillis() - startTime) + "ms");
//        Thread.currentThread().join();  // 堵塞线程，观察日志输出
    }

    public class TryLockTask implements Runnable {

        LockService service;
        AtomicInteger tryCount;
        CountDownLatch countDownLatch;
        public TryLockTask(LockService service,CountDownLatch countDownLatch) {
            this.service = service;
            tryCount = new AtomicInteger();
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                long startTime = System.currentTimeMillis();
                String name = Thread.currentThread().getName();
                log.info("{} ：准备获取锁",name);
                boolean hasLock = service.lock(key,name,blockTime,tryCount);
                if(!hasLock) {
                    log.info("{}：获取锁超时失败；获取次数为：{}" ,name,tryCount.intValue());
                    return;
                }

                Thread.sleep(100);    // 假装在工作
                log.info("{} : 干活中",name);

                boolean hasUnLock = service.unLock(key,name);
                if(!hasUnLock) {
                    log.info("{}: 解锁失败",name);
                    return;
                }
                countDownLatch.countDown();
                log.info("{}：干活完成；获取次数为：{}；运行时长：{}",name,tryCount.intValue(),(System.currentTimeMillis() - startTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
