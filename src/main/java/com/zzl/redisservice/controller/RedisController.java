package com.zzl.redisservice.controller;


import com.zzl.redisservice.entity.UserEntity;
import com.zzl.redisservice.util.RedisUtil;
import com.zzl.redisservice.util.RedissLockUtil;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RequestMapping("/redis")
@RestController
public class RedisController {

    private static int ExpireTime = 60;   // redis中存储的过期时间60s

    @Resource
    private RedisUtil redisUtil;

    @RequestMapping("set")
    public boolean redisset(String key, String value) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(Long.valueOf(1));
        userEntity.setGuid(String.valueOf(1));
        userEntity.setName("zhangsan");
        userEntity.setAge(String.valueOf(20));
        userEntity.setCreateTime(new Date());

        //return redisUtil.set(key,userEntity,ExpireTime);

        return redisUtil.set(key, value);
    }

    @RequestMapping("get")
    public Object redisget(String key) {
        return redisUtil.get(key);
    }

    @RequestMapping("expire")
    public boolean expire(String key) {
        return redisUtil.expire(key, ExpireTime);
    }

    /**
     * 删除库存
     *
     * @param key
     * @return
     */
    @RequestMapping("delete")
    public boolean delete(String key) {
        UUID uuid = UUID.randomUUID();
        redisUtil.setnx(key + "_store", uuid.toString(), 5);
        System.out.println("上锁：" + key + "_store");
        try {
            //这里处理时间太长。需要进行续约，解决不了
            Thread.sleep(20000);
            Integer store = (Integer) redisUtil.get(key);
            redisUtil.set(key, store - 1);
            System.out.println("剩下库存数量：" + store);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (uuid.equals(redisUtil.get(key + "_store"))) {
                System.out.println("释放锁：" + key + "_store");
                redisUtil.del(key);
            }

        }
        return true;
    }

    /**
     * redisson上锁 底层根据lua脚本进行续约
     *
     * @param key
     * @return
     */
    @RequestMapping("delete1")
    public boolean delete1(String key) {
        System.out.println("开始=============");
        RLock rLock = RedissLockUtil.lock(key + "_store1");
        System.out.println("上锁：" + key + "_store1");
        try {
            //rLock.lock();
            //Thread.sleep(60000);
            Integer store = (Integer) redisUtil.get(key);
            redisUtil.set(key, store - 1);
            System.out.println("剩下库存数量：" + (store - 1));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("释放锁");
            RedissLockUtil.unlock(rLock);
        }

        return true;
    }

    /**
     * 设置库存
     *
     * @param key
     * @param store
     * @return
     */
    @RequestMapping("setStore")
    public boolean setStore(String key, String store) {
        return redisUtil.set(key, store);
    }
}
