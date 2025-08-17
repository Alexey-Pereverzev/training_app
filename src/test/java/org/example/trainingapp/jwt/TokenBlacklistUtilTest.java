package org.example.trainingapp.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TokenBlacklistUtilTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistUtil tokenBlacklistUtil;

    private static final String TOKEN = "test.jwt.token";
    private static final String PREFIX = "blacklisted:";


    @Test
    void whenBlacklistToken_shouldStoreInRedis() {
        // given
        Instant expirationTime = Instant.now().plusSeconds(3600);
        String expectedKey = PREFIX + TOKEN;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        // when
        tokenBlacklistUtil.blacklistToken(TOKEN, expirationTime);
        // then
        verify(valueOperations).set(expectedKey, expirationTime.toString());
    }


    @Test
    void whenIsTokenBlacklisted_inBlacklist_shouldReturnTrue() {
        // given
        String key = PREFIX + TOKEN;
        when(redisTemplate.hasKey(key)).thenReturn(true);
        // when
        boolean result = tokenBlacklistUtil.isTokenBlacklisted(TOKEN);
        // then
        assertTrue(result);
    }


    @Test
    void whenIsTokenBlacklisted_isNotInBlacklist_shouldReturnFalse() {
        // given
        String key = PREFIX + TOKEN;
        when(redisTemplate.hasKey(key)).thenReturn(false);
        // when
        boolean result = tokenBlacklistUtil.isTokenBlacklisted(TOKEN);
        // then
        assertFalse(result);
    }


    @Test
    void whenIsTokenBlacklisted_redisReturnsNull_shouldReturnFalse() {
        // given
        String key = PREFIX + TOKEN;
        when(redisTemplate.hasKey(key)).thenReturn(null);
        // when
        boolean result = tokenBlacklistUtil.isTokenBlacklisted(TOKEN);
        // then
        assertFalse(result);
    }
}
