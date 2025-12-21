package com.badsector.qerb.modules.user.infra.adapter.redis.repository;

import com.badsector.qerb.modules.user.infra.adapter.redis.entity.VerificationTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRedisRepository extends CrudRepository<VerificationTokenRedisEntity, String> {
}
