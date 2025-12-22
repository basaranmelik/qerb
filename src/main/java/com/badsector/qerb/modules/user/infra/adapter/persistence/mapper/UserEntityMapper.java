package com.badsector.qerb.modules.user.infra.adapter.persistence.mapper;

import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.infra.adapter.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    public User toDomain(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .phone(userEntity.getPhone())
                .role(userEntity.getRole())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .deleted(userEntity.isDeleted())
                .enabled(userEntity.isEnabled())
                .build();
    }

    public UserEntity fromDomain(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deleted(user.isDeleted())
                .enabled(user.isEnabled())
                .build();
    }
}
