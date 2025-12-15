package com.badsector.qerb.modules.user.infra.adapter.persistence;

import com.badsector.qerb.modules.user.domain.model.RefreshToken;
import com.badsector.qerb.modules.user.domain.port.out.RefreshTokenPort;
import com.badsector.qerb.modules.user.infra.adapter.persistence.entity.RefreshTokenRedisEntity;
import com.badsector.qerb.modules.user.infra.adapter.persistence.mapper.RefreshTokenMapper;
import com.badsector.qerb.modules.user.infra.adapter.persistence.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRedisAdapter implements RefreshTokenPort {

    private final RefreshTokenRedisRepository repository;
    private final RefreshTokenMapper mapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenRedisEntity entity = mapper.toEntity(refreshToken);
        RefreshTokenRedisEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(userId);
    }
}