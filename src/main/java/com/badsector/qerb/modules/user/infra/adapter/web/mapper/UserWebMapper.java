package com.badsector.qerb.modules.user.infra.adapter.web.mapper;

import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.UserResponse;

public class UserWebMapper {
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }
}
