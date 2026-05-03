package org.toanehihi.botcv.application.token.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Qualifier("customStringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "token_blacklist:";
    private static final String RESET_TOKEN_PREFIX = "reset_token:";
    private static final String VERIFICATION_TOKEN_PREFIX = "verify_token:";
    private static final long TOKEN_TTL_MINUTES = 15;

    @Override
    public void addToBlacklist(String token, long expiryTime) {
        String key = BLACKLIST_PREFIX + token;
        long ttl = expiryTime / 1000;
        if (ttl > 0) {
            redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.SECONDS);
            log.info("Token blacklisted with TTL: {} second", ttl);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        Boolean existed = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(existed);
    }

    @Override
    public void storeResetToken(String token, String email) {
        redisTemplate.opsForValue().set(RESET_TOKEN_PREFIX + token, email, TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public String getResetTokenEmail(String token) {
        return redisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + token);
    }

    @Override
    public void deleteResetToken(String token) {
        redisTemplate.delete(RESET_TOKEN_PREFIX + token);
    }

    @Override
    public void storeVerificationToken(String token, String email) {
        redisTemplate.opsForValue().set(VERIFICATION_TOKEN_PREFIX + token, email, TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public String getVerificationTokenEmail(String token) {
        return redisTemplate.opsForValue().get(VERIFICATION_TOKEN_PREFIX + token);
    }

    @Override
    public void deleteVerificationToken(String token) {
        redisTemplate.delete(VERIFICATION_TOKEN_PREFIX + token);
    }
}
