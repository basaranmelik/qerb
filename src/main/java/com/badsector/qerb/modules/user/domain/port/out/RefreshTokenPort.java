package com.badsector.qerb.modules.user.domain.port.out;

import com.badsector.qerb.modules.user.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(UUID userId);
}
