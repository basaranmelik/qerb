package com.badsector.qerb.modules.user.domain.port.in;

import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.port.in.command.LoginCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.RegisterCommand;
import com.badsector.qerb.modules.user.domain.port.in.result.AuthResult;

public interface UserUseCase {
    void register(RegisterCommand cmd);
    AuthResult login(LoginCommand cmd);
    void verifyEmail(String tokenString);
    AuthResult refreshToken(String oldRefreshTokenString);
}
