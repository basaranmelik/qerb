package com.badsector.qerb.modules.user.domain.port.out;

import com.badsector.qerb.modules.user.domain.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenPort {
    void save(VerificationToken token);
    Optional<VerificationToken> findById(String token);
    void delete(String token);
}
