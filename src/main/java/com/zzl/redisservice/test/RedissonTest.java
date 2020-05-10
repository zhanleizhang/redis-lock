package com.zzl.redisservice.test;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class RedissonTest implements Runnable {
    private static RedissonClient redisson;
    private static int count = 10000;

    private static void init() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setDatabase(10);
        redisson = Redisson.create(config);
    }

    @Override
    public void run() {
        RLock lock = redisson.getLock("leizi");
        lock.lock();
        try {
            Thread.sleep(3000);
            count--;
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        init();
        for (int i = 0; i < 100; i++) {
            new Thread(new RedissonTest()).start();
        }
    }
}

