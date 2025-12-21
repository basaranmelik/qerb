package com.badsector.qerb.modules.user.infra.adapter.redis.repository;

import com.badsector.qerb.modules.user.infra.adapter.redis.entity.RefreshTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshTokenRedisEntity, String> {
}