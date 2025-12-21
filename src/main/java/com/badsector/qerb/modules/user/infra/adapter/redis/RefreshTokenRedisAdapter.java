package com.badsector.qerb.modules.user.infra.adapter.redis;

import com.badsector.qerb.modules.user.domain.model.RefreshToken;
import com.badsector.qerb.modules.user.domain.port.out.RefreshTokenPort;
import com.badsector.qerb.modules.user.infra.adapter.redis.entity.RefreshTokenRedisEntity;
import com.badsector.qerb.modules.user.infra.adapter.redis.mapper.RefreshTokenMapper;
import com.badsector.qerb.modules.user.infra.adapter.redis.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public Optional<RefreshToken> findByToken(String refreshToken) {
        return repository.findById(refreshToken)
                .map(mapper::toDomain);
    }

    @Override
    public void delete(String refreshToken) {
        repository.deleteById(refreshToken);
    }
}