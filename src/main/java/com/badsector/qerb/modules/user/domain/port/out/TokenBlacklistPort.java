package com.badsector.qerb.modules.user.domain.port.out;

public interface TokenBlacklistPort {
    void blacklistToken(String token, Long durationInMillis);
    boolean isTokenBlacklisted(String token);
}
