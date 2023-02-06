package com.gridians.gridians.domain.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> redisBlackListTemplate;

    public void save(String key, Object value, int time) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(value.getClass()));
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.MILLISECONDS);
    }

    public boolean hasKeyToken(String key) {
        return redisTemplate.hasKey(key);
    }

    public void saveBlackList(String key, Object value, int time) {
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(value.getClass()));
        redisBlackListTemplate.opsForValue().set(key, value, time, TimeUnit.MILLISECONDS);
    }

    public boolean hasKeyBlackList(String key) {
        return redisBlackListTemplate.hasKey(key);
    }
}
