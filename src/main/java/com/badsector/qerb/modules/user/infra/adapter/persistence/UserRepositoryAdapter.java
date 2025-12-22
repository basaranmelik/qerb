package com.badsector.qerb.modules.user.infra.adapter.persistence;

import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.port.out.UserRepositoryPort;
import com.badsector.qerb.modules.user.infra.adapter.persistence.entity.UserEntity;
import com.badsector.qerb.modules.user.infra.adapter.persistence.mapper.UserEntityMapper;
import com.badsector.qerb.modules.user.infra.adapter.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepo;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepo.findById(id).map(userEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email).map(userEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaRepo.findByPhone(phone).map(userEntityMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userEntityMapper.fromDomain(user);
        UserEntity saved = jpaRepo.save(entity);
        return userEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepo.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaRepo.existsByPhone(phone);
    }
}
