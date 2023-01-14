package com.devember.devember.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final RedisTemplate<String, Object> redisBlackListTemplate;

    public void saveBlackList(String key, Object value, int time){
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(value.getClass()));
        redisBlackListTemplate.opsForValue().set(key, value, time, TimeUnit.MILLISECONDS);
    }

    public boolean hasKeyBlackList(String key){
        return redisBlackListTemplate.hasKey(key);
    }
}
