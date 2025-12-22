package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Refresh token cannot be blank")
        String refreshToken
) {
}
