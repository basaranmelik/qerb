package com.badsector.qerb.modules.user.domain.port.out;

import com.badsector.qerb.modules.user.domain.model.VerificationToken;

public interface EmailPort {
    void sendVerificationEmail(String to, String token);
    void sendPasswordResetEmail(String to, String token);
}
