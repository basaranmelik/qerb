package com.badsector.qerb.modules.user.infra.adapter.persistence.repository;

import com.badsector.qerb.modules.user.infra.adapter.persistence.entity.RefreshTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshTokenRedisEntity, String> {
    Optional<RefreshTokenRedisEntity> findByToken(String refreshToken);
    void delete(String refreshToken);
}