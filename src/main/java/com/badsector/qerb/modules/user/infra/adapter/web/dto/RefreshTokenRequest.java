package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh Token cannot be blank")
        @Schema(description = "The refresh token received during login", example = "d9b2d63d-a233-4123-...")
        String token
) {}