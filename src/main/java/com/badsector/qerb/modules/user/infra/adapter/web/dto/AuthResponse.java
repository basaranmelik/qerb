package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing access and refresh tokens")
public record AuthResponse(

        @Schema(
                description = "JWT Access Token used for authenticating requests. Valid for a short period.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String accessToken,

        @Schema(
                description = "Refresh Token used to obtain a new Access Token when the current one expires.",
                example = "d9b2d63d-a233-4123-82ff-5a6789abcdef",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String refreshToken,

        @Schema(
                description = "Unique identifier of the authenticated user (UUID).",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String userId
) {
    public static AuthResponse from(com.badsector.qerb.modules.user.domain.port.in.result.AuthResult result) {
        return new AuthResponse(
                result.accessToken(),
                result.refreshToken(),
                result.userId().toString()
        );
    }
}