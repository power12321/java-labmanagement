package org.example.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 添加令牌到黑名单
    public void addToBlacklist(String token, long expiration) {
        redisTemplate.opsForValue().set("blacklist:" + token, "invalid", expiration, TimeUnit.MILLISECONDS);
    }

    // 检查令牌是否在黑名单中
    public boolean isInBlacklist(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }
}