package com.badsector.qerb.modules.user.domain.port.out;

import java.util.Optional;

public interface PasswordResetTokenPort {
    void save(String token, String email);
    Optional<String> findEmailByToken(String token);
    void delete(String token);
}