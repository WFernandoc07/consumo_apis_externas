package com.codigo.consumo_apis_externas.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    public void saveInredis(String key, String value, int exp){
        stringRedisTemplate.opsForValue().set(key, value);
        stringRedisTemplate.expire(key, exp, TimeUnit.MINUTES);
    }

    public String getFromRedis(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void deleteFromRedis(String key){
        stringRedisTemplate.delete(key);
    }

}
