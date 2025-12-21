package com.badsector.qerb.modules.user.infra.adapter.redis.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@RedisHash(value = "verification_tokens", timeToLive = 86400)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationTokenRedisEntity {
   @Id
   private String token;
   @Indexed
   private UUID userId;
}
