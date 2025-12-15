package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AuthResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("user_id")
        UUID userId
) {
    public static AuthResponse of(String accessToken, String refreshToken, UUID userId) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", userId);
    }
}