package com.badsector.qerb.modules.user.domain.port.in;

import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.port.in.command.ChangePasswordCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.LoginCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.RegisterCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.UpdateProfileCommand;
import com.badsector.qerb.modules.user.domain.port.in.result.AuthResult;

public interface UserUseCase {
    void register(RegisterCommand cmd);
    AuthResult login(LoginCommand cmd);
    void verifyEmail(String tokenString);
    AuthResult refreshToken(String oldRefreshTokenString);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    void logout(String accessToken, String refreshToken);
    User getProfile(String username);
    User updateProfile(UpdateProfileCommand cmd);
    void changePassword(ChangePasswordCommand cmd);
}
