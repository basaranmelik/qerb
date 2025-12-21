package com.badsector.qerb.modules.user.infra.adapter.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@RedisHash(value = "refresh_tokens", timeToLive = 604800)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRedisEntity {

    @Id
    private String token;

    @Indexed
    private UUID userId;
}