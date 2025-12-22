package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import com.badsector.qerb.modules.user.domain.model.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        Role role
) {
}
