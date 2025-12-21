package com.badsector.qerb.modules.user.infra.adapter.redis;

import com.badsector.qerb.modules.user.domain.port.out.PasswordResetTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRedisAdapter implements PasswordResetTokenPort {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "password_reset:";

    private static final long EXPIRATION_MINUTES = 15;

    @Override
    public void save(String token, String email) {
        String key = KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, Duration.ofMinutes(EXPIRATION_MINUTES));
    }
    @Override
    public Optional<String> findEmailByToken(String token) {
        String key = KEY_PREFIX + token;
        String email = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(email);
    }

    @Override
    public void delete(String token) {
        String key = KEY_PREFIX + token;
        redisTemplate.delete(key);
    }
}