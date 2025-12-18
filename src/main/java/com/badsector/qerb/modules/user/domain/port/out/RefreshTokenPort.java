package com.badsector.qerb.modules.user.domain.port.out;

import com.badsector.qerb.modules.user.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void delete(String refreshToken);
}
