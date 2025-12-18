package com.badsector.qerb.modules.user.domain.port.in.result;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthResult (
        String accessToken,
        String refreshToken,
        UUID userId
)
{}
