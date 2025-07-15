package org.example.trainingapp.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class RedisBlacklistCleanup {

    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void cleanupExpiredTokens() {                           //  cleaning up redis blacklist from expired tokens
        Set<String> keys = redisTemplate.keys("blacklisted:*");
        if (keys != null) {
            for (String key : keys) {
                String expirationStr = redisTemplate.opsForValue().get(key);
                if (expirationStr == null || expirationStr.isEmpty()) {
                    redisTemplate.delete(key);
                } else {
                    Instant expiration = Instant.parse(expirationStr);
                    if (Instant.now().isAfter(expiration)) {
                        redisTemplate.delete(key);
                    }
                }
            }
        }
    }
}

