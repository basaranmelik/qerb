package com.badsector.qerb.modules.user.infra.adapter.redis.mapper;

import com.badsector.qerb.modules.user.domain.model.RefreshToken;
import com.badsector.qerb.modules.user.infra.adapter.redis.entity.RefreshTokenRedisEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshTokenRedisEntity toEntity(RefreshToken domain) {
        return RefreshTokenRedisEntity.builder()
                .token(domain.getToken())
                .userId(domain.getUserId())
                .build();
    }

    public RefreshToken toDomain(RefreshTokenRedisEntity entity) {
        return RefreshToken.builder()
                .token(entity.getToken())
                .userId(entity.getUserId())
                .build();
    }
}