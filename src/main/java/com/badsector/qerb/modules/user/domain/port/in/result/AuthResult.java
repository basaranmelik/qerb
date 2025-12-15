package com.badsector.qerb.modules.user.domain.port.in.result;

import java.util.UUID;

public record AuthResult (
        String accessToken,
        String refreshToken,
        UUID userId
)
{}
