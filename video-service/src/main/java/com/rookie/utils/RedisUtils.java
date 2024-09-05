package com.rookie.utils;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUtils {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private Gson gson;
    
    public void setValue(String key, Object value,Integer hours) {
        redisTemplate.opsForValue().set(key, gson.toJson(value));
        redisTemplate.expire(key,hours, TimeUnit.HOURS);
    }
    
    public <T> T getValue(String key,Class<T> Clazz){
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null){
            return null;
        }
        return gson.fromJson(value.toString(), Clazz);
    }
    
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
    
}
