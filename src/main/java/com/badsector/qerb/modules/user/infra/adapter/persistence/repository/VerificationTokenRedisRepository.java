package com.badsector.qerb.modules.user.infra.adapter.persistence.repository;

import com.badsector.qerb.modules.user.domain.model.VerificationToken;
import com.badsector.qerb.modules.user.infra.adapter.persistence.entity.VerificationTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationTokenRedisRepository extends CrudRepository<VerificationTokenRedisEntity, String> {
}
