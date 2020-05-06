package com.zzl.redisservice.controller;


import com.zzl.redisservice.entity.UserEntity;
import com.zzl.redisservice.util.RedisUtil;
import com.zzl.redisservice.util.RedissLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;


@RequestMapping("/redis")
@RestController
public class RedisController {

    private static int ExpireTime = 60;   // redis中存储的过期时间60s

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedissLockUtil redissLockUtil;

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
        Long store = redisUtil.decr(key, 1L);
        System.out.println("剩下库存数量：" + store);
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
