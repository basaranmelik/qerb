package com.badsector.qerb.modules.user.infra.persistence;

import com.badsector.qerb.modules.user.domain.User;
import com.badsector.qerb.modules.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepo;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepo.findById(id).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaRepo.findByPhone(phone).map(UserEntityMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntityMapper.fromDomain(user);
        UserEntity saved = jpaRepo.save(entity);
        return UserEntityMapper.toDomain(saved);
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
