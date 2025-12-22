package com.badsector.qerb.modules.user.infra.adapter.redis;

import com.badsector.qerb.modules.user.domain.port.out.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenBlacklistRedisAdapter implements TokenBlacklistPort {

    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    public void blacklistToken(String token, Long durationInMillis) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "true", Duration.ofMillis(durationInMillis));
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
